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

import java.util.concurrent.atomic.AtomicReference;

/**
 * Holds rate limit info for the two separate Hetzner API rate limit buckets.
 * <p>
 * The Hetzner Cloud API uses two independent rate limit buckets:
 * <ul>
 *   <li><b>General</b> (3600 req/hr):
 *       all endpoints except action paths</li>
 *   <li><b>Actions</b> (7200 req/hr):
 *       any URL path containing {@code /actions}</li>
 * </ul>
 */
public class RateLimitBuckets {
    private final AtomicReference<RateLimitSnapshot> generalBucket = new AtomicReference<>();
    private final AtomicReference<RateLimitSnapshot> actionsBucket = new AtomicReference<>();

    public RateLimitSnapshot getGeneral() {
        return generalBucket.get();
    }

    public RateLimitSnapshot getActions() {
        return actionsBucket.get();
    }

    void updateBucketForPath(String urlPath, RateLimitSnapshot incoming) {
        boolean isActions = urlPath != null && urlPath.contains("/actions");
        AtomicReference<RateLimitSnapshot> bucket = isActions ? actionsBucket : generalBucket;

        bucket.getAndUpdate(current -> {
            if (current == null) {
                return incoming;
            }
            // The reset timestamp can only increase over time. A lower value means that we
            // received stale data from an earlier request and can discard it.
            long currentReset = current.getEpochSecondsForRateLimitReplenished();
            long incomingReset = incoming.getEpochSecondsForRateLimitReplenished();
            if (incomingReset < currentReset) {
                return current;
            }
            if (incomingReset == currentReset) {
                // if both point at the same instant when the bucket will be full,
                // then the snapshot with the most requests remaining is the freshest one
                return incoming.getRequestsRemaining() > current.getRequestsRemaining() ?
                        incoming : current;
            }
            return incoming;
        });
    }
}
