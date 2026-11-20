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

/**
 * Immutable snapshot of Hetzner API rate limit state from a single
 * response's {@code RateLimit-*} headers.
 */
public class RateLimitSnapshot {

    private final long epochSecondsForRateLimitReplenished;
    private final int rateLimitMaxPerHour;
    private final int requestsRemaining;

    public RateLimitSnapshot(long epochSecondsForRateLimitReplenished,
                             int rateLimitMaxPerHour,
                             int requestsRemaining) {
        this.epochSecondsForRateLimitReplenished = epochSecondsForRateLimitReplenished;
        this.rateLimitMaxPerHour = rateLimitMaxPerHour;
        this.requestsRemaining = requestsRemaining;
    }

    public long getEpochSecondsForRateLimitReplenished() {
        return epochSecondsForRateLimitReplenished;
    }

    public int getRateLimitMaxPerHour() {
        return rateLimitMaxPerHour;
    }

    public int getRequestsRemaining() {
        return requestsRemaining;
    }

    public long getEpochSecondsOfRequest() {
        double gainedPerSecond = (double) rateLimitMaxPerHour / 3600;
        double secondsUntilFull = (rateLimitMaxPerHour - requestsRemaining) / gainedPerSecond;
        return Math.round(epochSecondsForRateLimitReplenished - secondsUntilFull);
    }
}
