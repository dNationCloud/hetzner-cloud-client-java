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

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

import static cloud.dnation.hetznerclient.TestHelper.resourceAsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BasicTest {
    private static MockWebServer ws;
    private static HetznerApi api;

    @BeforeClass
    public static void setUpBeforeClass() throws IOException {
        ws = new MockWebServer();
        ws.start();
        System.setProperty("cloud.dnation.hetznerclient.apiendpoint",
                "http://localhost:" + ws.getPort());
        api = ClientFactory.create(() -> "mock");
    }

    @AfterClass
    public static void tearDownAfterClass() throws IOException {
        ws.shutdown();
        ws.close();
    }

    @Test
    public void testGetNetworkById() throws IOException {
        ws.enqueue(new MockResponse()
                .setBody(resourceAsString("get-network-by-id.json"))
        );
        Call<GetNetworkByIdResponse> call = api.getNetworkById(10);
        GetNetworkByIdResponse result = call.execute().body();
        assertEquals("my-net-1", result.getNetwork().getName());
        assertEquals(10, (long)result.getNetwork().getId());
        assertEquals("10.132.0.0/14", result.getNetwork().getIpRange());
    }


    @Test
    public void testGetNetworkByIdInvalid() throws IOException {
        ws.enqueue(new MockResponse()
                .setBody(resourceAsString("get-network-by-id-invalid.json"))
                .setResponseCode(404)
        );
        Call<GetNetworkByIdResponse> call = api.getNetworkById(11);
        Response<GetNetworkByIdResponse> response = call.execute();
        assertEquals(404, response.code());
    }

    @Test
    public void testGetNetworksBySelector() throws IOException {
        ws.enqueue(new MockResponse().setBody(resourceAsString("get-networks-by-selector.json")));
        Call<GetNetworksBySelectorResponse> call = api.getNetworkBySelector("test=1");
        GetNetworksBySelectorResponse result = call.execute().body();
        assertEquals(1, result.getNetworks().size());
        assertEquals(12, (long) result.getNetworks().get(0).getId());
        assertEquals("my-net-2", result.getNetworks().get(0).getName());
        assertEquals("10.132.0.0/14", result.getNetworks().get(0).getIpRange());
        assertEquals("1", result.getMeta().getPagination().getTotalEntries());
    }

    @Test
    public void testGetNetworksBySelectorEmpty() throws IOException {
        ws.enqueue(new MockResponse().setBody(resourceAsString("get-networks-by-selector-empty.json")));
        Call<GetNetworksBySelectorResponse> call = api.getNetworkBySelector("test=invalid");
        GetNetworksBySelectorResponse result = call.execute().body();
        assertEquals(0, result.getNetworks().size());
        assertEquals("0", result.getMeta().getPagination().getTotalEntries());
    }

    @Test
    public void testGetPrimaryIpsBySelector() throws IOException {
        ws.enqueue(new MockResponse().setBody(resourceAsString("get-primary-ips-by-selector.json")));
        Call<GetAllPrimaryIpsResponse> call = api.getAllPrimaryIps("jenkins");
        GetAllPrimaryIpsResponse result = call.execute().body();
        assertEquals(1, result.getPrimaryIps().size());
        assertEquals("1.2.3.4", result.getPrimaryIps().get(0).getIp());
        assertNull(result.getPrimaryIps().get(0).getAssigneeId());
    }

    @Test
    public void testGetFirewallBySelector() throws IOException {
        ws.enqueue(new MockResponse().setBody(resourceAsString("get-firewalls-by-selector.json")));
        final Call<GetFirewallsBySelectorResponse> call = api.getFirewallsBySelector("any");
        final GetFirewallsBySelectorResponse result = call.execute().body();
        assertEquals(3029857349L, (long) result.getFirewalls().get(0).getId());
        assertEquals("::/0", result.getFirewalls().get(0).getRules().get(1).getSourceIps().get(1));
        assertEquals("in", result.getFirewalls().get(0).getRules().get(1).getDirection());
        assertEquals("22", result.getFirewalls().get(0).getRules().get(0).getPort());
    }

    @Test
    public void testGetFirewallById() throws IOException {
        ws.enqueue(new MockResponse().setBody(resourceAsString("get-firewall-by-id.json")));
        final Call<GetFirewallByIdResponse> call = api.getFirewallById(345676L);
        final GetFirewallByIdResponse result = call.execute().body();
        assertEquals(345676, (long) result.getFirewall().getId());
        assertEquals("::/0", result.getFirewall().getRules().get(1).getSourceIps().get(1));
        assertEquals("in", result.getFirewall().getRules().get(1).getDirection());
        assertEquals("22", result.getFirewall().getRules().get(0).getPort());
    }


    @Test
    public void testGetFirewallByIdInvalid() throws IOException {
        ws.enqueue(new MockResponse()
                .setBody(resourceAsString("get-firewall-by-id-invalid.json"))
                .setResponseCode(404)
        );
        Call<GetNetworkByIdResponse> call = api.getNetworkById(11);
        Response<GetNetworkByIdResponse> response = call.execute();
        assertEquals(404, response.code());
    }
}
