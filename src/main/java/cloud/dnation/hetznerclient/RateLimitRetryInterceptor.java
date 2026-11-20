/*
 *     Copyright 2024 https://dnation.cloud
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cloud.dnation.hetznerclient;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Response;

import java.io.IOException;

/**
 * OkHttp {@link Interceptor} that retries requests receiving HTTP 429 (Too Many Requests)
 * with exponential backoff. Captures {@code RateLimit-*} headers into the appropriate
 * {@link RateLimitBuckets} bucket (general vs. actions) based on the request URL path.
 */
@Slf4j
class RateLimitRetryInterceptor implements Interceptor {
    private static final int MAX_RETRIES = 5;
    private static final long INITIAL_BACKOFF_MS = 1_000;
    private static final long MAX_BACKOFF_MS = 60_000;

    private final RateLimitBuckets rateLimitBuckets;

    RateLimitRetryInterceptor() {
        this(null);
    }

    RateLimitRetryInterceptor(RateLimitBuckets rateLimitBuckets) {
        this.rateLimitBuckets = rateLimitBuckets;
    }

    @Override
    @NonNull
    public Response intercept(Chain chain) throws IOException {
        okhttp3.Request request = chain.request();
        Response response = chain.proceed(request);
        int attempt = 0;

        while (response.code() == 429 && attempt < MAX_RETRIES) {
            attempt++;
            long waitMs = getBackoffMs(attempt);
            log.warn("HTTP 429 on {} {}, retry {}/{} after {} ms",
                    request.method(), request.url().encodedPath(), attempt, MAX_RETRIES, waitMs);
            response.close();
            try {
                Thread.sleep(waitMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Interrupted during rate-limit backoff", e);
            }
            response = chain.proceed(request);
        }
        captureRateLimitHeaders(request, response);
        return response;
    }

    private void captureRateLimitHeaders(okhttp3.Request request, Response response) {
        if (rateLimitBuckets == null) {
            return;
        }
        try {
            String limitStr = response.header("RateLimit-Limit");
            String remainingStr = response.header("RateLimit-Remaining");
            String resetStr = response.header("RateLimit-Reset");
            if (limitStr != null && remainingStr != null && resetStr != null) {
                RateLimitSnapshot snapshot = new RateLimitSnapshot(
                        Long.parseLong(resetStr),
                        Integer.parseInt(limitStr),
                        Integer.parseInt(remainingStr));
                rateLimitBuckets.updateBucketForPath(request.url().encodedPath(), snapshot);
            }
        } catch (NumberFormatException ignored) {
        }
    }

    private static long getBackoffMs(int attempt) {
        return Math.min(INITIAL_BACKOFF_MS * (1L << (attempt - 1)), MAX_BACKOFF_MS);
    }
}
