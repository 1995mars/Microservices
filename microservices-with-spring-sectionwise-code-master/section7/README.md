### Making Microservices Resilient using Resilience4j patterns
---

**Description:** This repository has five maven projects with the names **accounts, loans, cards, configserver, eurekaserver** which are continuation from the section8 repository. Below are the key steps that are followed inside this **section9** where we focused on making our microservices resilient using **Resilience4j** patterns.

**Key steps:**
- Open the **pom.xml** of **accounts** microservice and add the below **Resilience4j** related dependencies to it,

### accounts\pom.xml

```xml
<dependency>
	<groupId>io.github.resilience4j</groupId>
	<artifactId>resilience4j-spring-boot2</artifactId>
</dependency>
<dependency>
	<groupId>io.github.resilience4j</groupId>
	<artifactId>resilience4j-circuitbreaker</artifactId>
</dependency>
<dependency>
	<groupId>io.github.resilience4j</groupId>
	<artifactId>resilience4j-timelimiter</artifactId>
</dependency>
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```
-  In order to implement the Circuit Breaker pattern for **"/myCustomerDetails"** API inside **accounts** microservice, open the **AccountsController.java** class 
   and add **@CircuitBreaker** annotation on top of the **"/myCustomerDetails"** API method along with a fallback method details. Post that add a new fallbackMethod method in      the same class with the same parameters like myCustomerDetails() method along with a **Throwable t** parameter like we discussed in the course. 
-  Now in order to implement the Retry pattern for **"/myCustomerDetails"** API inside **accounts** microservice, open the **AccountsController.java** class 
   and add **@Retry** annotation on top of the **"/myCustomerDetails"** API method along with the fallback method details that we build in the previous method.
-  Next in order to implement the RateLimiter pattern for **"/myCustomerDetails"** API inside **accounts** microservice, open the **AccountsController.java** class 
   and add **@RateLimiter** annotation on top of the **"/sayHello"** API method along with a fallback method details. Post that add a new fallbackMethod method in the same 
   class with the same parameters like sayHello() method along with a **Throwable t** parameter like we discussed in the course.
-  After making all the above three changes, your **AccountsController.java** class should look like below,
### \accounts\src\main\java\com\eazybytes\accounts\controller\AccountsController.java
```java
/**
 * 
 */
package com.eazybytes.accounts.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

/**
 * @author Eazy Bytes
 *
 */

@RestController
public class AccountsController {
	
	@Autowired
	private AccountsRepository accountsRepository;

	@Autowired
	AccountsServiceConfig accountsConfig;
	
	@Autowired
	LoansFeignClient loansFeignClient;

	@Autowired
	CardsFeignClient cardsFeignClient;
	
	@PostMapping("/myAccount")
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
	public CustomerDetails myCustomerDetails(@RequestBody Customer customer) {
		Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
		List<Loans> loans = loansFeignClient.getLoansDetails(customer);
		List<Cards> cards = cardsFeignClient.getCardDetails(customer);

		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setAccounts(accounts);
		customerDetails.setLoans(loans);
		customerDetails.setCards(cards);
		
		return customerDetails;
	}
	
	private CustomerDetails myCustomerDetailsFallBack(Customer customer, Throwable t) {
		Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
		List<Loans> loans = loansFeignClient.getLoansDetails(customer);
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
- Open the **application.properties** inside **accounts** microservices and make the following entries inside it like we discussed in the course. These entries will help
  in configuring the parameters for **CircuitBreaker, Retry and RateLimiter** patterns. After making the changes, the new entries inside **application.properties** should like below,
### \accounts\src\main\resources\application.properties
```
resilience4j.circuitbreaker.configs.default.registerHealthIndicator= true
resilience4j.circuitbreaker.instances.detailsForCustomerSupportApp.minimumNumberOfCalls= 5
resilience4j.circuitbreaker.instances.detailsForCustomerSupportApp.failureRateThreshold= 50
resilience4j.circuitbreaker.instances.detailsForCustomerSupportApp.waitDurationInOpenState= 30000
resilience4j.circuitbreaker.instances.detailsForCustomerSupportApp.permittedNumberOfCallsInHalfOpenState=2

resilience4j.retry.configs.default.registerHealthIndicator= true
resilience4j.retry.instances.retryForCustomerDetails.maxRetryAttempts=3
resilience4j.retry.instances.retryForCustomerDetails.waitDuration=2000

resilience4j.ratelimiter.configs.default.registerHealthIndicator= true
resilience4j.ratelimiter.instances.sayHello.timeoutDuration=5000
resilience4j.ratelimiter.instances.sayHello.limitRefreshPeriod=5000
resilience4j.ratelimiter.instances.sayHello.limitForPeriod=1
```
- Start all the microservices except **cards** microservice.
- In order to test the **CircuitBreaker, Retry** pattern changes, invoke the endpoint http://localhost:8080/myCustomerDetails through Postman by passing the below request in JSON format. Since **cards** microservice is down, you should be able to see the response from fall back mechanisms configured as part of **CircuitBreaker, Retry** patterns.
```json
{
  "customerId": 1
}
```
- In order to test the **RateLimiter** pattern changes, invoke the endpoint http://localhost:8080/sayHello multiple times at a time through browser like we discussed in the
  course. You should be able to see the response from fall back mechanisms configured for **RateLimiter** pattern.
-  Stop all the running microservices inside your eclipse
---
### HURRAY !!! Congratulations, you successfully made your microservices Resilient using Resilience4j patterns.
---
