### Securing Microservices using OAuth2 client credentials grant flow
---

**Description:** This repository has six maven projects with the names **accounts, loans, cards, configserver, eurekaserver, gatewayserver** which are continuation from the 
section13 repository. All these microservices will be leveraged to explore the securing microservices using **OAuth2 client credentials grant flow**.
Below are the key steps that we will be following in this section17 repository,

**Key steps:**
- Like we discussed in the course, install & setup the **Keycloak using docker command in your local system**.
- Register a client inside Keycloak that supports **Client Credentials grant flow**.
- Pass your client details which is created in the previous step as a request inside Postman & make sure to get an **access token from the keycloak**.
- Open the **pom.xml** of the microservices **gatewayserver** and make sure to add the below required dependencies of **Spring Security,OAuth2**. 
  ```xml
  <dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-security</artifactId>
  </dependency>
  <dependency>
	<groupId>org.springframework.security</groupId>
	<artifactId>spring-security-oauth2-resource-server</artifactId>
  </dependency>
  <dependency>
	<groupId>org.springframework.security</groupId>
	<artifactId>spring-security-oauth2-jose</artifactId>
  </dependency>
  ```
- In order to make our Spring Cloud Gateway to act as a Resource server & handle both Authentication & Authorization,
  like we discussed in the course, please create the classes **SecurityConfig.java**, 
  **KeycloakRoleConverter.java**. They should look like below,
### \gatewayserver\src\main\java\com\eaztbytes\gatewayserver\config\SecurityConfig.java
```java
package com.eaztbytes.gatewayserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange(exchanges -> exchanges.pathMatchers("/eazybank/accounts/**").hasRole("ACCOUNTS")
                        .pathMatchers("/eazybank/cards/**").authenticated()
                        .pathMatchers("/eazybank/loans/**").permitAll())
                .oauth2ResourceServer(oauth2ResourceServerCustomizer ->
                        oauth2ResourceServerCustomizer.jwt(jwtCustomizer -> jwtCustomizer.jwtAuthenticationConverter(grantedAuthoritiesExtractor())));
        http.csrf((csrf) -> csrf.disable());
        return http.build();
    }

    Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter =
                new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter
                (new KeycloakRoleConverter());
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

}
```
### \gatewayserver\src\main\java\com\eaztbytes\gatewayserver\config\KeycloakRoleConverter.java
```java
package com.eaztbytes.gatewayserver.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
        if (realmAccess == null || realmAccess.isEmpty()) {
            return new ArrayList<>();
        }
        Collection<GrantedAuthority> returnValue = ((List<String>) realmAccess.get("roles"))
                .stream().map(roleName -> "ROLE_" + roleName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return returnValue;

    }
}
```
- Open the **application.properties** inside **gatewayserver** microservices and add the following entry. Here we are providing the Keycloak  URI where my resource server can validate the access tokens received.
### \gatewayserver\src\main\resources\application.properties
```
spring.security.oauth2.resourceserver.jwt.jwk-set-uri = http://localhost:7080/realms/master/protocol/openid-connect/certs
```
- Please make sure to start all your microservices including **Zipkin, Keycloak** in the order mentioned in the course.
- Access the URL http://localhost:8072/eazybank/accounts/sayHello through browser and you can expect the 401 response as the accounts API paths are secured.
- Access the URL http://localhost:8072/eazybank/loans/loans/properties through browser and you can expect a succesfull response as there is no security for loans API paths.
- Now get an access token from keycloak using steps mentioned in the course & pass the same to Gateway server while trying to access the secured APIs. You can expect a successfull response.
- Once the local testing is completed successfully, generate the latest docker image for **gatewayserver** microservice and push into docker hub.
- Install Keycloak Auth server inside K8s cluster using the below commands,
```
helm repo add bitnami https://charts.bitnami.com/bitnami
helm install keycloak bitnami/keycloak
```
- Update the Helm charts like we discussed in the course
- Deploy all the microservices using Helm charts and test the E2E flow using the steps mentioned in the course.
- Atlast, make sure to test Authorization changes as well by creating a new role **ACCOUNTS** inside Keycloak.
---
### HURRAY !!! Congratulations, you successfully secured your microservices using the OAuth2 client credentials grant flow
---
