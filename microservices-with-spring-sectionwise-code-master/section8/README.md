### Handling Routing & Cross cutting concerns inside microservices network using Spring Cloud Gateway
---

**Description:** This repository has five maven projects with the names **accounts, loans, cards, configserver, eurekaserver** which are continuation from the section9 
repository. A new microservices **'gatewayserver'** is created in this section based on **Spring Cloud Gateway** which will help in handling routing & any other
cross cutting concerns inside microservices network. Below are the key steps that are followed inside this **section10** where we focused on set up of **Gateway Server** 
inside our microservices network.

**Key steps:**
- Go to https://start.spring.io/
- Fill all the details required to generate a **gatewayserver** Spring Boot project and add dependencies **Spring Cloud Routing**,**Spring Boot Actuator**, **Eureka Discovery Client**, **Config Client**, **Spring Boot DevTools**. Click GENERATE which will download the **gatewayserver** maven project in a zip format
- Extract the downloaded maven project of **gatewayserver** and import the same into Eclipse by following the steps mentioned in the course
- Visit pom.xml of **gatewayserver** and make sure all the required dependencies are present in it. Add **spring-boot-maven-plugin** plugin details along with docker image name details inside like we discussed in the course. This extra **spring-boot-maven-plugin** details will help us to generate a docker image using **Buildpacks** easily. Finally your pom.xml should looks like shown below,

### gatewayserver\pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.0.1</version>
		<relativePath /> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.eaztbytes</groupId>
	<artifactId>gatewayserver</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>gatewayserver</name>
	<description>Spring Boot project for Spring Cloud Gateway</description>
	<properties>
		<java.version>17</java.version>
		<spring-cloud.version>2022.0.0</spring-cloud.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-config</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-gateway</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<scope>runtime</scope>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>
	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-dependencies</artifactId>
				<version>${spring-cloud.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<configuration>
					<image>
						<name>eazybytes/${project.artifactId}</name>
					</image>
				</configuration>
			</plugin>
		</plugins>
	</build>

</project>
```
- Open the SpringBoot main class **GatewayserverApplication.java** . We can always identify the main class in a Spring Boot project by looking for the annotation **@SpringBootApplication**. Add a routing configurations by creating a @Bean **RouteLocator** like we discussed in the course. After making the changes your **GatewayserverApplication.java** class should like below,

### \gatewayserver\src\main\java\com\eaztbytes\gatewayserver\GatewayserverApplication.java

```java
package com.eaztbytes.gatewayserver;

import java.util.Date;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayserverApplication.class, args);
	}
	
	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
	    return builder.routes()
	        .route(p -> p
	            .path("/eazybank/accounts/**")
	            .filters(f -> f.rewritePath("/eazybank/accounts/(?<segment>.*)","/${segment}")
	            				.addResponseHeader("X-Response-Time",new Date().toString()))
	            .uri("lb://ACCOUNTS")).
	        route(p -> p
		            .path("/eazybank/loans/**")
		            .filters(f -> f.rewritePath("/eazybank/loans/(?<segment>.*)","/${segment}")
		            		.addResponseHeader("X-Response-Time",new Date().toString()))
		            .uri("lb://LOANS")).
	        route(p -> p
		            .path("/eazybank/cards/**")
		            .filters(f -> f.rewritePath("/eazybank/cards/(?<segment>.*)","/${segment}")
		            		.addResponseHeader("X-Response-Time",new Date().toString()))
		            .uri("lb://CARDS")).build();
	}

}
```
- Open the **application.properties** inside **gatewayserver** microservices and make the following entries inside it like we discussed in the course. After making the changes, your **application.properties** should like below,
### \gatewayserver\src\main\resources\application.properties
```
spring.application.name=gatewayserver

spring.config.import=optional:configserver:http://localhost:8071

management.endpoints.web.exposure.include=*

## Configuring info endpoint
info.app.name=Gateway Server Microservice
info.app.description=Eazy Bank Gateway Server Application
info.app.version=1.0.0
management.info.env.enabled = true
management.endpoint.gateway.enabled=true
spring.cloud.gateway.discovery.locator.enabled=true
spring.cloud.gateway.discovery.locator.lowerCaseServiceId=true

logging.level.com.eaztbytes.gatewayserver: DEBUG
```
- Like we discussed in the course please make sure to create a **gatewayserver.properties** with the below content inside the location where your Config Server is reading the properties,
### /gatewayserver.properties
```
server.port=8072
eureka.instance.preferIpAddress = true 
eureka.client.registerWithEureka = true
eureka.client.fetchRegistry = true
eureka.client.serviceUrl.defaultZone = http://localhost:8070/eureka/
```
- Please make sure to start all your microservices except **gatewayserver** in the order mentioned in the course.
- Go to your Spring Boot main class **GatewayserverApplication.java** and right click-> Run As -> Java Application. This will start your Spring Boot application successfully at port 8072 which is the port we configured inside **gatewayserver.properties**. Your can confirm the same by looking at the console logs.
- Access the URL http://localhost:8072/eazybank/accounts/myCusomerDetails through Postman by passing the below request in JSON format. You should get the response from the accounts microservices which has all the details related to account, loans and cards. Also validate if you received custom header **X-Response-Time** that we created in the response.
  ```json
  {
    "customerId": 1
  }
  ```
- In order to implement cross cutting concerns inside your microservices, like we discussed in the course, please create the classes **TraceFilter.java**, **ResponseTraceFilter.java**, **FilterUtility.java**. They all should look like below,
### \gatewayserver\src\main\java\com\eaztbytes\gatewayserver\filters\TraceFilter.java
```java
package com.eaztbytes.gatewayserver.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Order(1)
@Component
public class TraceFilter implements GlobalFilter {

	private static final Logger logger = LoggerFactory.getLogger(TraceFilter.class);
	
	@Autowired
	FilterUtility filterUtility;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
		if (isCorrelationIdPresent(requestHeaders)) {
			logger.debug("EazyBank-correlation-id found in tracing filter: {}. ",
					filterUtility.getCorrelationId(requestHeaders));
		} else {
			String correlationID = generateCorrelationId();
			exchange = filterUtility.setCorrelationId(exchange, correlationID);
			logger.debug("EazyBank-correlation-id generated in tracing filter: {}.", correlationID);
		}
		return chain.filter(exchange);
	}

	private boolean isCorrelationIdPresent(HttpHeaders requestHeaders) {
		if (filterUtility.getCorrelationId(requestHeaders) != null) {
			return true;
		} else {
			return false;
		}
	}

	private String generateCorrelationId() {
		return java.util.UUID.randomUUID().toString();
	}

}
```
### \gatewayserver\src\main\java\com\eaztbytes\gatewayserver\filters\ResponseTraceFilter.java
```java
package com.eaztbytes.gatewayserver.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import reactor.core.publisher.Mono;

@Configuration
public class ResponseTraceFilter {

	private static final Logger logger = LoggerFactory.getLogger(ResponseTraceFilter.class);

	@Autowired
	FilterUtility filterUtility;
	
	@Bean
	public GlobalFilter postGlobalFilter() {
		return (exchange, chain) -> {
			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
				String correlationId = filterUtility.getCorrelationId(requestHeaders);
				logger.debug("Updated the correlation id to the outbound headers. {}", correlationId);
				exchange.getResponse().getHeaders().add(filterUtility.CORRELATION_ID, correlationId);
			}));
		};
	}
}
```
### \gatewayserver\src\main\java\com\eaztbytes\gatewayserver\filters\FilterUtility.java
```java
package com.eaztbytes.gatewayserver.filters;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class FilterUtility {

	public static final String CORRELATION_ID = "eazybank-correlation-id";
	
	public String getCorrelationId(HttpHeaders requestHeaders) {
		if (requestHeaders.get(CORRELATION_ID) != null) {
			List<String> requestHeaderList = requestHeaders.get(CORRELATION_ID);
			return requestHeaderList.stream().findFirst().get();
		} else {
			return null;
		}
	}

	public ServerWebExchange setRequestHeader(ServerWebExchange exchange, String name, String value) {
		return exchange.mutate().request(exchange.getRequest().mutate().header(name, value).build()).build();
	}

	public ServerWebExchange setCorrelationId(ServerWebExchange exchange, String correlationId) {
		return this.setRequestHeader(exchange, CORRELATION_ID, correlationId);
	}

}
```
- Like we discussed in the course, update all the important classes like **AccountsController.java, LoansController.java, CardsController.java** to accept the **@RequestHeader("eazybank-correlation-id") String correlationid** as input inside the method parameters.
- Restart your **gatewayserver** microservice and invoke the REST API http://localhost:8072/eazybank/accounts/myCusomerDetails through Postman by passing the below request in JSON format. You should get the response from the accounts microservices which has all the details related to account, loans and cards. Also validate if you received custom header **eazybank-correlation-id** that we created in the response.
  ```json
  {
    "customerId": 1
  }
  ```
- Validate the logger statements of **gatewayserver** microservice to check if the **eazybank-correlation-id** value is logged properly or not.
- Stop all the microservices that are running inside the eclipse.
- Generate the docker images for all the microservices and push them into Docker hub by following the similar steps we discussed in the previous sections.
- Now write **docker-compose.yml** files inside **accounts/docker-compose** folder for each profile with the following content,

### \accounts\docker-compose\default\docker-compose.yml
	
```yaml
version: "3.8"

services:

  configserver:
    image: eazybytes/configserver:latest
    mem_limit: 700m
    ports:
      - "8071:8071"
    networks:
     - eazybank
   
  eurekaserver:
    image: eazybytes/eurekaserver:latest
    mem_limit: 700m
    ports:
      - "8070:8070"
    networks:
     - eazybank
    depends_on:
      - configserver
    deploy:
      restart_policy:
        condition: on-failure
        delay: 15s
        max_attempts: 3
        window: 120s
    environment:
      SPRING_PROFILES_ACTIVE: default
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      
  accounts:
    image: eazybytes/accounts:latest
    mem_limit: 700m
    ports:
      - "8080:8080"
    networks:
      - eazybank
    depends_on:
      - configserver
      - eurekaserver
    deploy:
      restart_policy:
        condition: on-failure
        delay: 30s
        max_attempts: 3
        window: 120s
    environment:
      SPRING_PROFILES_ACTIVE: default
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eurekaserver:8070/eureka/
  
  loans:
    image: eazybytes/loans:latest
    mem_limit: 700m
    ports:
      - "8090:8090"
    networks:
      - eazybank
    depends_on:
      - configserver
      - eurekaserver
    deploy:
      restart_policy:
        condition: on-failure
        delay: 30s
        max_attempts: 3
        window: 120s
    environment:
      SPRING_PROFILES_ACTIVE: default
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eurekaserver:8070/eureka/
    
  cards:
    image: eazybytes/cards:latest
    mem_limit: 700m
    ports:
      - "9000:9000"
    networks:
      - eazybank
    depends_on:
      - configserver
      - eurekaserver
    deploy:
      restart_policy:
        condition: on-failure
        delay: 30s
        max_attempts: 3
        window: 120s
    environment:
      SPRING_PROFILES_ACTIVE: default
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eurekaserver:8070/eureka/
   
  gatewayserver:
    image: eazybytes/gatewayserver:latest
    mem_limit: 700m
    ports:
      - "8072:8072"
    networks:
      - eazybank
    depends_on:
      - configserver
      - eurekaserver
      - cards
      - loans
      - accounts
    deploy:
      restart_policy:
        condition: on-failure
        delay: 45s
        max_attempts: 3
        window: 180s
    environment:
      SPRING_PROFILES_ACTIVE: default
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eurekaserver:8070/eureka/
      
networks:
  eazybank:
```
### \accounts\docker-compose\dev\docker-compose.yml
	
```yaml
version: "3.8"

services:

  configserver:
    image: eazybytes/configserver:latest
    mem_limit: 700m
    ports:
      - "8071:8071"
    networks:
     - eazybank
   
  eurekaserver:
    image: eazybytes/eurekaserver:latest
    mem_limit: 700m
    ports:
      - "8070:8070"
    networks:
     - eazybank
    depends_on:
      - configserver
    deploy:
      restart_policy:
        condition: on-failure
        delay: 15s
        max_attempts: 3
        window: 120s
    environment:
      SPRING_PROFILES_ACTIVE: dev
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      
  accounts:
    image: eazybytes/accounts:latest
    mem_limit: 700m
    ports:
      - "8080:8080"
    networks:
      - eazybank
    depends_on:
      - configserver
      - eurekaserver
    deploy:
      restart_policy:
        condition: on-failure
        delay: 30s
        max_attempts: 3
        window: 120s
    environment:
      SPRING_PROFILES_ACTIVE: dev
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eurekaserver:8070/eureka/
  
  loans:
    image: eazybytes/loans:latest
    mem_limit: 700m
    ports:
      - "8090:8090"
    networks:
      - eazybank
    depends_on:
      - configserver
      - eurekaserver
    deploy:
      restart_policy:
        condition: on-failure
        delay: 30s
        max_attempts: 3
        window: 120s
    environment:
      SPRING_PROFILES_ACTIVE: dev
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eurekaserver:8070/eureka/
    
  cards:
    image: eazybytes/cards:latest
    mem_limit: 700m
    ports:
      - "9000:9000"
    networks:
      - eazybank
    depends_on:
      - configserver
      - eurekaserver
    deploy:
      restart_policy:
        condition: on-failure
        delay: 30s
        max_attempts: 3
        window: 120s
    environment:
      SPRING_PROFILES_ACTIVE: dev
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eurekaserver:8070/eureka/
  
  gatewayserver:
    image: eazybytes/gatewayserver:latest
    mem_limit: 700m
    ports:
      - "8072:8072"
    networks:
      - eazybank
    depends_on:
      - configserver
      - eurekaserver
      - cards
      - loans
      - accounts
    deploy:
      restart_policy:
        condition: on-failure
        delay: 45s
        max_attempts: 3
        window: 180s
    environment:
      SPRING_PROFILES_ACTIVE: dev
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eurekaserver:8070/eureka/
    
networks:
  eazybank:
```
### \accounts\docker-compose\prod\docker-compose.yml
	
```yaml
version: "3.8"

services:

  configserver:
    image: eazybytes/configserver:latest
    mem_limit: 700m
    ports:
      - "8071:8071"
    networks:
     - eazybank
   
  eurekaserver:
    image: eazybytes/eurekaserver:latest
    mem_limit: 700m
    ports:
      - "8070:8070"
    networks:
     - eazybank
    depends_on:
      - configserver
    deploy:
      restart_policy:
        condition: on-failure
        delay: 15s
        max_attempts: 3
        window: 120s
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      
  accounts:
    image: eazybytes/accounts:latest
    mem_limit: 700m
    ports:
      - "8080:8080"
    networks:
      - eazybank
    depends_on:
      - configserver
      - eurekaserver
    deploy:
      restart_policy:
        condition: on-failure
        delay: 30s
        max_attempts: 3
        window: 120s
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eurekaserver:8070/eureka/
  
  loans:
    image: eazybytes/loans:latest
    mem_limit: 700m
    ports:
      - "8090:8090"
    networks:
      - eazybank
    depends_on:
      - configserver
      - eurekaserver
    deploy:
      restart_policy:
        condition: on-failure
        delay: 30s
        max_attempts: 3
        window: 120s
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eurekaserver:8070/eureka/
    
  cards:
    image: eazybytes/cards:latest
    mem_limit: 700m
    ports:
      - "9000:9000"
    networks:
      - eazybank
    depends_on:
      - configserver
      - eurekaserver
    deploy:
      restart_policy:
        condition: on-failure
        delay: 30s
        max_attempts: 3
        window: 120s
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eurekaserver:8070/eureka/
  
  gatewayserver:
    image: eazybytes/gatewayserver:latest
    mem_limit: 700m
    ports:
      - "8072:8072"
    networks:
      - eazybank
    depends_on:
      - configserver
      - eurekaserver
      - cards
      - loans
      - accounts
    deploy:
      restart_policy:
        condition: on-failure
        delay: 45s
        max_attempts: 3
        window: 180s
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eurekaserver:8070/eureka/
      
networks:
  eazybank:
```
-  Based on the active profile that you want start the microservices, open the command line tool where the **docker-compose.yml** is present and run the docker compose command **"docker-compose up"** to start all the microservices containers with a single command. All the running containers can be validated by running a docker command **"docker ps"**.  
-  To test the Spring Cloud Gateway changes, invoke the REST API http://localhost:8072/eazybank/accounts/myCusomerDetails through Postman by passing the below request in JSON format. You should get the response from the accounts microservices which has all the details related to account, loans and cards. Also validate if you received custom headers **X-Response-Time, eazybank-correlation-id** that we created in the response.
  ```json
  {
    "customerId": 1
  }
  ```
-  Stop all the running containers by executing the docker compose command **"docker-compose down"** from the location where **docker-compose.yml** is present.
---
### HURRAY !!! Congratulations, you successfully handled Routing & Cross cutting concerns inside microservices network using Spring Cloud Gateway.
---
