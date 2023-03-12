# Hetzner Cloud API Client

[![Sonarcloud](https://sonarcloud.io/api/project_badges/measure?project=dNationCloud_hetzner-cloud-client-java&metric=alert_status)](https://sonarcloud.io/project/overview?id=dNationCloud_hetzner-cloud-client-java)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=dNationCloud_hetzner-cloud-client-java&metric=coverage)](https://sonarcloud.io/summary/new_code?id=dNationCloud_hetzner-cloud-client-java)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=dNationCloud_hetzner-cloud-client-java&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=dNationCloud_hetzner-cloud-client-java)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=dNationCloud_hetzner-cloud-client-java&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=dNationCloud_hetzner-cloud-client-java)



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
