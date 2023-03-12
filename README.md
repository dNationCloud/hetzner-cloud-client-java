# Hetzner Cloud API Client

This is client library for [Hetzner cloud](https://www.hetzner.com/cloud), based on [Retrofit HTTP client](https://github.com/square/retrofit).

### Usage

Declare dependency in `pom.xml`

```xml
<dependency>
    <groupId>cloud.dnation.integration</groupId>
    <artifactId>hetzner-cloud-client-java</artifactId>
    <version>1.3.0</version>
</dependency>
```

Instantiate client and make an API call
```java
import cloud.dnation.hetznerclient.*;

HetznerApi api = ClientFactory.create("my-token-123456");
Response<GetServerByIdResponse> response = api.getServer(123456).execute();

if (response.isSuccessful()) {
    System.out.println(response.body());
}
```
