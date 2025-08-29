/*
 *     Copyright 2025 https://dnation.cloud
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

import com.google.common.base.Preconditions;
import lombok.experimental.UtilityClass;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

@UtilityClass
public class PagedResourceHelper {
    /**
     * Consume all items from paginated REST endpoint.
     *
     * @param labelSelector label selector to restrict items only to those that match selector
     * @param pageSupplier {@link BiFunction} that takes page index (zero based) and selector and produces {@link Response}
     * @param itemsGetter {@link Function} that takes response from pageSupplier and extracts list of items
     * @return all items combined to single list
     * @param <T> item type
     * @param <X> REST endpoint response type
     */
    public static <T extends IdentifiableResource, X extends AbstractSearchResponse> List<T> fetchItems(
            String labelSelector,
            BiFunction<Integer, String, Call<X>> pageSupplier, Function<X, List<T>> itemsGetter) throws IOException {
        final List<T> result = new ArrayList<>();
        for (int i = 0; ; i++) {
            final Response<X> page = pageSupplier.apply(i, labelSelector).execute();
            assertValidResponse(page);
            final X body = page.body();
            Objects.requireNonNull(body, "Missing response body");
            final Meta meta = body.getMeta();
            result.addAll(itemsGetter.apply(body));
            Objects.requireNonNull(meta, "Missing meta response object");
            Objects.requireNonNull(meta.getPagination(), "Missing pagination inside meta response object");
            if (meta.getPagination().getNextPage() == null) {
                // last page
                break;
            }
        }
        return result;
    }

    public static List<PrimaryIpDetail> getAllPrimaryIps(HetznerApi api, String labelSelector) throws IOException {
        return fetchItems(labelSelector, (pageId, sel) ->
                api.getPrimaryIpsBySelector(sel, pageId, 25), GetAllPrimaryIpsResponse::getPrimaryIps);
    }

    public static List<ServerDetail> getAllServers(HetznerApi api, String labelSelector) throws IOException {
        return fetchItems(labelSelector, (pageId, sel) ->
                api.getServersBySelector(sel, pageId, 25), GetServersBySelectorResponse::getServers);
    }

    private static <X extends AbstractSearchResponse> void assertValidResponse(Response<X> response) {
        Preconditions.checkArgument(response.isSuccessful(),
                "Invalid response code: %d", response.code());
    }
}
