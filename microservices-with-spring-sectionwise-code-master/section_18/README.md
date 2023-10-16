### Securing Microservices using OAuth2 Authorization code grant flow
---

**Description:** This repository has six maven projects with the names **accounts, loans, cards, configserver, eurekaserver, gatewayserver** which are continuation from the 
section17 repository. All these microservices will be leveraged to explore the securing microservices using **OAuth2 Authorization code grant flow**.
Below are the key steps that we will be following in this section18 repository,

**Key steps:**
- Register a client inside Keycloak that supports **Authorization code grant flow**.
- Create an end user details inside the Keycloak
- Open the **pom.xml** of the microservices **accounts** and make sure to add the below required dependencies of **Spring Security,OAuth2**. 
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
- In order to make our accounts microservice to act as an resource server & handle both Authentication & Authorization,
  like we discussed in the course, please create the classes **SecurityConfig.java**, 
  **KeycloakRoleConverter.java**. They should look like below,
### \accounts\src\main\java\com\eazybytes\accounts\config\SecurityConfig.java
```java
package com.eazybytes.accounts.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain web(HttpSecurity http) throws Exception {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());

         http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/sayHello").hasRole("ACCOUNTS")
            .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2ResourceServerCustomizer ->
                    oauth2ResourceServerCustomizer.jwt(jwtCustomizer -> jwtCustomizer.jwtAuthenticationConverter(jwtAuthenticationConverter)));
	http.csrf((csrf) -> csrf.disable());
        return http.build();
    }
}
```
### \accounts\src\main\java\com\eazybytes\accounts\config\KeycloakRoleConverter.java
```java
package com.eazybytes.accounts.config;

import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>>{

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
- Open the **application.properties** inside **accounts** microservices and add the following entry. Here we are providing the Keycloak  URI where my resource server can validate the access tokens received.
### \accounts\src\main\resources\application.properties
```
spring.security.oauth2.resourceserver.jwt.jwk-set-uri = http://localhost:7080/realms/master/protocol/openid-connect/certs
```
- Delete all the changes related to resource server that we did inside Spring Cloud Gateway
- Open the **pom.xml** of the microservices **gatewayserver** and make sure to add the below required dependencies of **Spring Security,OAuth2**. 
  ```xml
  <dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-oauth2-client</artifactId>
  </dependency>
  ```
- In order to make our gatewayserver microservice to act as an OAuth2 client, like we discussed in the course, please create the class **SecurityConfig.java**.
  It should look like below,
### \gatewayserver\src\main\java\com\eaztbytes\gatewayserver\config\SecurityConfig.java
```java
package com.eaztbytes.gatewayserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange(exchanges -> exchanges.pathMatchers("/eazybank/accounts/**").authenticated()
                        .pathMatchers("/eazybank/cards/**").authenticated()
                        .pathMatchers("/eazybank/loans/**").permitAll())
                .oauth2Login(Customizer.withDefaults());
        http.csrf((csrf) -> csrf.disable());
        return http.build();
    }


}
```
- Open the **application.properties** inside **gatewayserver** microservices and add the following properties,
### \gatewayserver\src\main\resources\application.properties
```
spring.security.oauth2.client.provider.keycloak.token-uri=http://localhost:7080/realms/master/protocol/openid-connect/token
spring.security.oauth2.client.provider.keycloak.authorization-uri=http://localhost:7080/realms/master/protocol/openid-connect/auth
spring.security.oauth2.client.provider.keycloak.userinfo-uri=http://localhost:7080/realms/master/protocol/openid-connect/userinfo
spring.security.oauth2.client.provider.keycloak.user-name-attribute=preferred_username
spring.security.oauth2.client.registration.eazybank-gateway.provider=keycloak
spring.security.oauth2.client.registration.eazybank-gateway.client-id=eazybank-gateway-ui
spring.security.oauth2.client.registration.eazybank-gateway.client-secret=zfoalJ1P4e4uTkkIJYQtm9MviTYJ6sqn
spring.security.oauth2.client.registration.eazybank-gateway.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.eazybank-gateway.redirect-uri={baseUrl}/login/oauth2/code/keycloak
```
- Please make sure to start all your microservices including **Zipkin, Keycloak** in the order mentioned in the course.
- Access the URL http://localhost:8072/eazybank/accounts/account/properties through browser and you can expect the login page of keycloak.
- Provide end user credentials in the browser and you can expect the response from the accounts microservice.
- Once the local testing is completed successfully, generate the latest docker image for **accounts, gatewayserver** microservice and push into docker hub.
- Install Keycloak Auth server inside K8s cluster using the below commands,
```
helm repo add bitnami https://charts.bitnami.com/bitnami
helm install keycloak bitnami/keycloak
```
- Update the Helm charts like we discussed in the course
- Deploy all the microservices using Helm charts and test the E2E flow using the steps mentioned in the course.
- Atlast, make sure to test Authorization changes as well by creating a new role **ACCOUNTS** inside Keycloak.
---
### HURRAY !!! Congratulations, you successfully secured your microservices using the OAuth2 Authorization code grant flow
---
