/*
 *     Copyright 2022 https://dnation.cloud
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

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Trimmed-down version of Hetzner public cloud API.
 * For full version, check <a href="https://docs.hetzner.cloud/">official documentation</a>
 */
public interface HetznerApi {
    /**
     * Get all images that matches given label expression.
     *
     * @param selector label expression to match
     * @return list of {@link ImageDetail} wrapped in {@link GetImagesBySelectorResponse}
     */
    @GET("/v1/images?type=snapshot&status=available")
    Call<GetImagesBySelectorResponse> getImagesBySelector(@Query("label_selector") String selector);

    /**
     * Get single image by providing image ID.
     *
     * @param id image ID to get
     * @return {@link ImageDetail} object wrapped in {@link GetImageByIdResponse}
     * @see <a href="https://docs.hetzner.cloud/#images-get-an-image">Official docs</a>
     */
    @GET("/v1/images/{id}")
    Call<GetImageByIdResponse> getImageById(@Path("id") Long id);

    /**
     * Get datacenters matching provided string.
     *
     * @param name name of datacenter to match against
     * @return list of matching datacenters in form of {@link DatacenterDetail}
     * wrapped in {@link GetDatacentersResponse}.
     */
    @GET("/v1/datacenters")
    Call<GetDatacentersResponse> getAllDatacentersWithName(@Query("name") String name);

    /**
     * Get all datacenters.
     *
     * @return list of datacenters in form of {@link DatacenterDetail} wrapped in {@link GetDatacentersResponse}.
     */
    @GET("/v1/datacenters")
    Call<GetDatacentersResponse> getAllDatacenters();

    /**
     * Get locations matching provided string.
     *
     * @param name name of location to match against
     * @return list of matching locations in form of {@link LocationDetail} wrapped in {@link GetLocationsResponse}.
     */
    @GET("/v1/locations")
    Call<GetLocationsResponse> getAllLocationsWithName(@Query("name") String name);

    /**
     * Get server types whose name is matching provided string.
     *
     * @param name name of server type to match against
     * @return list of matching server types in form of {@link ServerType} wrapped in {@link GetServerTypesResponse}.
     */
    @GET("/v1/server_types")
    Call<GetServerTypesResponse> getAllServerTypesWithName(@Query("name") String name);

    /**
     * Delete sever instance.
     *
     * @param id ID of server to delete
     * @return {@link ActionResponse} inner object {@link ActionDetail} can be used to get result of action.
     */
    @DELETE("/v1/servers/{id}")
    Call<ActionResponse> deleteServer(@Path("id") long id);

    /**
     * Get server detail for given ID.
     *
     * @param id ID of server to retrieve details for
     * @return GetServerByIdResponse
     */
    @GET("/v1/servers/{id}")
    Call<GetServerByIdResponse> getServer(@Path("id") long id);

    /**
     * Power-off server instance.
     *
     * @param id ID of server to power-off
     * @return {@link ActionResponse} inner object {@link ActionDetail} can be used to get result of action.
     */
    @POST("/v1/servers/{id}/actions/poweroff")
    Call<ActionResponse> powerOffServer(@Path("id") long id);

    /**
     * Create new server instance.
     *
     * @param request new server details
     * @return {@link CreateServerResponse}. Inner objects {@link ServerDetail} and {@link ActionDetail}
     * can be used to retrieve server details and operation result.
     */
    @POST("/v1/servers")
    Call<CreateServerResponse> createServer(@Body CreateServerRequest request);

    /**
     * Create new SSH key.
     *
     * @param request new SSH key details
     * @return {@link CreateSshKeyResponse} with details
     */
    @POST("/v1/ssh_keys")
    Call<CreateSshKeyResponse> createSshKey(@Body CreateSshKeyRequest request);

    /**
     * Delete existing SSH key.
     *
     * @param id ID of SSH key to delete
     * @return {@link ErrorDetail} in case of error, empty response otherwise
     * See <a href="https://docs.hetzner.cloud/#ssh-keys-delete-an-ssh-key">API reference</a>
     */
    @DELETE("/v1/ssh_keys/{id}")
    Call<ErrorDetail> deleteSshKey(@Path("id") long id);

    /**
     * Find SSH keys matching given label selector
     *
     * @param selector label selector used to match keys
     * @return list of matched SSH keys
     * See <a href="https://docs.hetzner.cloud/#ssh-keys-get-all-ssh-keys">API reference</a>
     */
    @GET("/v1/ssh_keys")
    Call<GetSshKeysBySelectorResponse> getSshKeysBySelector(@Query("label_selector") String selector);

    /**
     * Get all server matching given label selector.
     *
     * @param selector label selector used to match servers
     * @param page     page index
     * @param perPage  number of records per page
     * @return paged list of servers
     */
    @GET("/v1/servers")
    Call<GetServersBySelectorResponse> getServersBySelector(@Query("label_selector") String selector,
                                                            @Query("page") int page,
                                                            @Query("per_page") int perPage);

    /**
     * Get all networks matching given label selector.
     *
     * @param selector label selector used to match servers
     * @return list of networks
     * see <a href="https://docs.hetzner.cloud/#networks-get-all-networks">API reference</a>
     */
    @GET("/v1/networks")
    Call<GetNetworksBySelectorResponse> getNetworkBySelector(@Query("label_selector") String selector);

    /**
     * Get network detail based on provided network ID.
     *
     * @param id network ID
     * @return details of network
     * see <a href="https://docs.hetzner.cloud/#networks-get-a-network">API reference</a>
     */
    @GET("/v1/networks/{id}")
    Call<GetNetworkByIdResponse> getNetworkById(@Path("id") long id);

    /**
     * Get placement groups, optionally filtered using label expression.
     *
     * @param selector Can be used to filter resources by labels.
     *                 The response will only contain resources matching the label selector.
     * @return returns matching placement groups.
     * see <a href="https://docs.hetzner.cloud/#placement-groups-get-all-placementgroups">API reference</a>
     */
    @GET("/v1/placement_groups")
    Call<GetPlacementGroupsResponse> getPlacementGroups(@Query("label_selector") String selector);

    /**
     * Get placement group detail based on provided placement group ID.
     *
     * @param id placement group ID
     * @return details of placement group
     * see <a href="https://docs.hetzner.cloud/#placement-groups-get-a-placementgroup">API reference</a>
     */
    @GET("/v1/placement_groups/{id}")
    Call<GetPlacementGroupByIdResponse> getPlacementGroupById(@Path("id") long id);

    /**
     * Get all Primary IP objects.
     *
     * @param selector Can be used to filter resources by labels.
     *                 The response will only contain resources matching the label selector.
     * @return returns all Primary IP objects.
     * see <a href="https://docs.hetzner.cloud/#primary-ips-get-all-primary-ips">API reference</a>
     */
    @GET("/v1/primary_ips")
    Call<GetAllPrimaryIpsResponse> getAllPrimaryIps(@Query("label_selector") String selector);


    /**
     * Get volume detail based on ID.
     *
     * @param id volume ID
     * @return volume detail
     * see <a href="https://docs.hetzner.cloud/#volumes-get-a-volume">API reference</a>
     */
    @GET("/v1/volumes/{id}")
    Call<GetVolumeByIdResponse> getVolumeById(@Path("id") long id);

    /**
     * Get all volumes.
     *
     * @param selector Can be used to filter resources by labels.
     *                 The response will only contain resources matching the label selector.
     * @return list of volumes
     * see <a href="https://docs.hetzner.cloud/#volumes-get-all-volumes">API reference</a>
     */
    @GET("/v1/volumes")
    Call<GetVolumesResponse> getVolumes(@Query("label_selector") String selector);
}
