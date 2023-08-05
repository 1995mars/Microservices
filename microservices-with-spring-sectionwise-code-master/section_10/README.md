### Monitoring Microservices Metrics & Health inside microservices network using Micrometer, Prometheus, Grafana
---

**Description:** This repository has six maven projects with the names **accounts, loans, cards, configserver, eurekaserver, gatewayserver** which are continuation from the 
section11 repository. All these microservices will be updated in this section to implement monitoring metrics & health inside microservices network using **micrometer, 
prometheus, grafana.**

**Key steps:**
- Open the **pom.xml** of the microservices **accounts, loans, cards** and make sure to add the below required dependencies of **micrometer,prometheus** in all of them. 
  ```xml
  <dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
  </dependency>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
  </dependency>
  ```
- Open the **AccountsApplication.java** and create a bean of type **TimedAspect** inside it like we discussed in the course. After making the changes your 
  **AccountsApplication.java** should look like shown below,
  ### \accounts\src\main\java\com\eazybytes\accounts\AccountsApplication.java
```java
  package com.eazybytes.accounts;

  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.boot.autoconfigure.domain.EntityScan;
  import org.springframework.cloud.context.config.annotation.RefreshScope;
  import org.springframework.cloud.openfeign.EnableFeignClients;
  import org.springframework.context.annotation.Bean;
  import org.springframework.context.annotation.ComponentScan;
  import org.springframework.context.annotation.ComponentScans;
  import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

  import io.micrometer.core.aop.TimedAspect;
  import io.micrometer.core.instrument.MeterRegistry;

  @SpringBootApplication
  @EnableFeignClients
  @RefreshScope
  @ComponentScans({ @ComponentScan("com.eazybytes.accounts.controller")})
  @EnableJpaRepositories("com.eazybytes.accounts.repository")
  @EntityScan("com.eazybytes.accounts.model")
  public class AccountsApplication {

    public static void main(String[] args) {
      SpringApplication.run(AccountsApplication.class, args);
    }

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

  }
```
- Open the **AccountsController.java** and create a custom metric for **"/myAccount"** API with the help of annotation **@Timed** like we discussed in the course.After making 
  the changes your **AccountsController.java** should look like shown below,
  ### \accounts\src\main\java\com\eazybytes\accounts\controller\AccountsController.java
```java
    
package com.eazybytes.accounts.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.eazybytes.accounts.config.AccountsServiceConfig;
import com.eazybytes.accounts.model.Accounts;
import com.eazybytes.accounts.model.Cards;
import com.eazybytes.accounts.model.Customer;
import com.eazybytes.accounts.model.CustomerDetails;
import com.eazybytes.accounts.model.Loans;
import com.eazybytes.accounts.model.Properties;
import com.eazybytes.accounts.repository.AccountsRepository;
import com.eazybytes.accounts.service.client.CardsFeignClient;
import com.eazybytes.accounts.service.client.LoansFeignClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.annotation.Timed;

/**
 * @author Eazy Bytes
 *
 */

@RestController
public class AccountsController {
	
	private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);
	
	@Autowired
	private AccountsRepository accountsRepository;

	@Autowired
	AccountsServiceConfig accountsConfig;
	
	@Autowired
	LoansFeignClient loansFeignClient;

	@Autowired
	CardsFeignClient cardsFeignClient;
	
	@PostMapping("/myAccount")
	@Timed(value = "getAccountDetails.time", description = "Time taken to return Account Details")
	public Accounts getAccountDetails(@RequestBody Customer customer) {

		Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
		if (accounts != null) {
			return accounts;
		} else {
			return null;
		}

	}
	
	@GetMapping("/account/properties")
	public String getPropertyDetails() throws JsonProcessingException {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		Properties properties = new Properties(accountsConfig.getMsg(), accountsConfig.getBuildVersion(),
				accountsConfig.getMailDetails(), accountsConfig.getActiveBranches());
		String jsonStr = ow.writeValueAsString(properties);
		return jsonStr;
	}
	
	@PostMapping("/myCustomerDetails")
	@CircuitBreaker(name = "detailsForCustomerSupportApp",fallbackMethod="myCustomerDetailsFallBack")
	@Retry(name = "retryForCustomerDetails", fallbackMethod = "myCustomerDetailsFallBack")
	public CustomerDetails myCustomerDetails(@RequestHeader("eazybank-correlation-id") String correlationid,@RequestBody Customer customer) {
		logger.info("myCustomerDetails() method started");
		Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
		List<Loans> loans = loansFeignClient.getLoansDetails(correlationid,customer);
		List<Cards> cards = cardsFeignClient.getCardDetails(correlationid,customer);

		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setAccounts(accounts);
		customerDetails.setLoans(loans);
		customerDetails.setCards(cards);
		logger.info("myCustomerDetails() method ended");
		return customerDetails;
	}
	
	private CustomerDetails myCustomerDetailsFallBack(@RequestHeader("eazybank-correlation-id") String correlationid,Customer customer, Throwable t) {
		Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
		List<Loans> loans = loansFeignClient.getLoansDetails(correlationid,customer);
		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setAccounts(accounts);
		customerDetails.setLoans(loans);
		return customerDetails;

	}
	
	@GetMapping("/sayHello")
	@RateLimiter(name = "sayHello", fallbackMethod = "sayHelloFallback")
	public String sayHello() {
		return "Hello, Welcome to EazyBank";
	}

	private String sayHelloFallback(Throwable t) {
		return "Hi, Welcome to EazyBank";
	}

}

```
- Start the microservices in the order of **configserver, eurekaserver, accounts**. 
- Once all the required microservices are started, access the URL http://localhost:8080/myAccount through Postman by passing the below request in JSON format. 
  ```json
  {
    "customerId": 1
  }
  ```
- Open the URL http://localhost:8080/actuator/prometheus to validate if the custom metric **'getAccountDetails.time'** that we created is showing under metrics information.
- Stop all the microservices that are running inside the eclipse.
- Generate the docker images for the microservices **'accounts, loans, cards'** and push them into Docker hub by following the similar steps we discussed in the 
  previous sections.
- Like we discussed in the course, create a **prometheus.yml** inside the path and content like shown below,
  ### \accounts\docker-compose\monitoring\prometheus.yml
```yaml
  global:
  scrape_interval:     5s # Set the scrape interval to every 5 seconds.
  evaluation_interval: 5s # Evaluate rules every 5 seconds.
scrape_configs:
  - job_name: 'accounts'
    metrics_path: '/actuator/prometheus'
    static_configs:
    - targets: ['accounts:8080']
  - job_name: 'loans'
    metrics_path: '/actuator/prometheus'
    static_configs:
    - targets: ['loans:8090']
  - job_name: 'cards'
    metrics_path: '/actuator/prometheus'
    static_configs:
    - targets: ['cards:9000']
  ```
- Now in the same folder where **prometheus.yml** is present, create a **docker-compose.yml** file with the following content,
### \accounts\docker-compose\monitoring\docker-compose.yml
```yaml
version: "3.8"

services:

  grafana:
    image: "grafana/grafana:latest"
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_USER=admin
      - GF_SECURITY_ADMIN_PASSWORD=password
    networks:
     - eazybank
    depends_on:
      - prometheus  

  prometheus:
   image: prom/prometheus:latest
   ports:
      - "9090:9090"
   volumes:
    - ./prometheus.yml:/etc/prometheus/prometheus.yml
   networks:
    - eazybank
   
  zipkin:
    image: openzipkin/zipkin
    mem_limit: 700m
    ports:
      - "9411:9411"
    networks:
     - eazybank

  configserver:
    image: eazybytes/configserver:latest
    mem_limit: 700m
    ports:
      - "8071:8071"
    networks:
     - eazybank
    depends_on:
      - zipkin
    environment:
      SPRING_PROFILES_ACTIVE: default
      # SPRING_ZIPKIN_BASEURL: http://zipkin:9411/
      MANAGEMENT_ZIPKIN_TRACING_ENDPOINT: http://zipkin:9411/api/v2/spans
      
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
      SPRING_APPLICATION_NAME: eurekaserver
      SPRING_PROFILES_ACTIVE: default
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      # SPRING_ZIPKIN_BASEURL: http://zipkin:9411/
      MANAGEMENT_ZIPKIN_TRACING_ENDPOINT: http://zipkin:9411/api/v2/spans

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
      SPRING_APPLICATION_NAME: accounts
      SPRING_PROFILES_ACTIVE: default
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eurekaserver:8070/eureka/
      # SPRING_ZIPKIN_BASEURL: http://zipkin:9411/
      MANAGEMENT_ZIPKIN_TRACING_ENDPOINT: http://zipkin:9411/api/v2/spans
  
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
      SPRING_APPLICATION_NAME: loans
      SPRING_PROFILES_ACTIVE: default
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eurekaserver:8070/eureka/
      # SPRING_ZIPKIN_BASEURL: http://zipkin:9411/
      MANAGEMENT_ZIPKIN_TRACING_ENDPOINT: http://zipkin:9411/api/v2/spans
    
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
      SPRING_APPLICATION_NAME: cards
      SPRING_PROFILES_ACTIVE: default
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eurekaserver:8070/eureka/
      # SPRING_ZIPKIN_BASEURL: http://zipkin:9411/
      MANAGEMENT_ZIPKIN_TRACING_ENDPOINT: http://zipkin:9411/api/v2/spans
      
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
      SPRING_APPLICATION_NAME: gatewayserver
      SPRING_PROFILES_ACTIVE: default
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eurekaserver:8070/eureka/
      # SPRING_ZIPKIN_BASEURL: http://zipkin:9411/
      MANAGEMENT_ZIPKIN_TRACING_ENDPOINT: http://zipkin:9411/api/v2/spans

networks:
  eazybank:
```
- Open the command line tool where the docker-compose.yml is present and run the docker compose command **"docker-compose up"** to start all the microservices containers with a single command. All the running containers can be validated by running a docker command **"docker ps"**.
- Open the URL http://localhost:9090/targets/ inside a browser and validate all the details, graphs present inside prometheus like we discussed in the course.
- Open the URL http://localhost:3000/login/ inside a browser and enter the login details(**admin/password**) of Grafana like we discussed in the course. Inside Grafana provide prometheus details, build custom dashboards, alerts like we discussed in the course.
- Stop all the running containers by executing the docker compose command "docker-compose down" from the location where docker-compose.yml is present.

---
### HURRAY !!! Congratulations, you successfully implemented Metrics & Health monitoring inside microservices network using Micrometer, Prometheus, Grafana
---
