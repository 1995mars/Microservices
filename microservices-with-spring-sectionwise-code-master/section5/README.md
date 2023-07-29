### Configuration management inside microservices network using Spring Cloud Config
---

**Description:** This repository has three maven projects with the names **accounts, loans, cards** which are continuation from the **section5** repository. 
A new microservices **'configserver'** is created in this section based on **Spring Cloud Config** which will act as a Config server. These three microservices **accounts, loans, cards** are updated to read the configurations/properties from the **'configserver'** microservices. Below are the key steps that are followed inside this **section7** where we focused on set up of **Config Server** inside our microservices network.

**Key steps:**
- Go to https://start.spring.io/
- Fill all the details required to generate a **configserver** Spring Boot project and add dependencies **Config Server**,**Spring Boot Actuator**. Click GENERATE which will download the **configserver** maven project in a zip format
- Extract the downloaded maven project of **configserver** and import the same into Eclipse by following the steps mentioned in the course
- Visit **pom.xml** of **configserver** and make sure all the required dependencies are present in it. Add **spring-boot-maven-plugin** plugin details along with docker image name details inside like we discussed in the course. This extra **spring-boot-maven-plugin** details will help us to generate a docker image using Buildpacks easily. Finally
your pom.xml should looks like shown below,

### configserver\pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.1.0</version>
		<relativePath /> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.eaztbytes</groupId>
	<artifactId>configserver</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>configserver</name>
	<description>Configuration Server for Bank Microservices</description>
	<properties>
		<java.version>17</java.version>
		<spring-cloud.version>2022.0.3</spring-cloud.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-config-server</artifactId>
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
-  Open the SpringBoot main class **ConfigserverApplication.java** . We can always identify the main class in a Spring Boot project by looking for the annotation **@SpringBootApplication**. On top of this main class, please add annotation **'@EnableConfigServer'**. This annotation will make your microservice to act as a Spring Cloud Config Server. After making the changes your **ConfigserverApplication.java** class should like below,

### \src\main\java\com\eaztbytes\configserver\ConfigserverApplication.java

```java
package com.eaztbytes.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigserverApplication.class, args);
	}

}

```
- Create a **config** folder under the path **'configserver\src\main\resources\'** and copy all the 9 property files related to **accounts, loans and cards** microservices like mentioned in the course. The 9 property files can be found at https://github.com/eazybytes/microservices-with-spring-sectionwise-code/tree/master/section7/configserver/src/main/resources/config
- Open the **application.properties** inside **configserver** microservices and make the following entries inside it which will help in reading the properties from a given classpath location. Please note that **encrypt.key** is optional and can be used only in the scenarios where you want **configserver** to handle the **encryption/decryption** of
the properties like we discussed in the course.
### \src\main\resources\application.properties
```
spring.application.name=configserver
spring.profiles.active=native
spring.cloud.config.server.native.search-locations=classpath:/config
server.port=8071
encrypt.key=eazybytes
```
-  Go to your Spring Boot main class **ConfigserverApplication.java** and right click-> Run As -> Java Application. This will start your Spring Boot application successfully at port 8071 which is the port we configured inside **application.properties**. Your can confirm the same by looking at the console logs.
- Access the URLs like http://localhost:8071/accounts/default, http://localhost:8071/loans/dev, http://localhost:8071/cards/prod inside your browser to randomly validate the
  properties being exposed by Config Server for all the three microservices **accounts, loans and cards**.
- Stop the Config Server microservices which started at port 8071 earlier.  
- Open the **application.properties** inside **configserver** microservices and make the following entries inside it which will help in reading the properties from a given file system location. Please make sure to create the configured folder/filesystem in your system and copy all the 9 property files related to **accounts, loans and cards** microservices like mentioned in the course.
### \src\main\resources\application.properties
```
spring.application.name=configserver
spring.profiles.active=native
spring.cloud.config.server.native.search-locations=file:///C://config
server.port=8071
encrypt.key=eazybytes
```
-  Go to your Spring Boot main class **ConfigserverApplication.java** and right click-> Run As -> Java Application. This will start your Spring Boot application successfully at port 8071 which is the port we configured inside **application.properties**. Your can confirm the same by looking at the console logs.
- Access the URLs like http://localhost:8071/accounts/default, http://localhost:8071/loans/dev, http://localhost:8071/cards/prod inside your browser to randomly validate that properties are being read from configured file system by Config Server for all the three microservices **accounts, loans and cards**.
- Stop the Config Server microservices which started at port 8071 earlier.  
- Create Github repository and upload all the 9 property files related to **accounts, loans and cards** microservices in to it like mentioned in the course. You can refer to https://github.com/eazybytes/microservices-config as a sample reference.
- Open the **application.properties** inside **configserver** microservices and make the following entries inside it which will help in reading the properties from a given Github repository. 
### \src\main\resources\application.properties
```
spring.application.name=configserver
spring.profiles.active=git
spring.cloud.config.server.git.uri=https://github.com/eazybytes/microservices-config.git
spring.cloud.config.server.git.clone-on-start=true
spring.cloud.config.server.git.default-label=main
server.port=8071
encrypt.key=eazybytes
```
-  Go to your Spring Boot main class **ConfigserverApplication.java** and right click-> Run As -> Java Application. This will start your Spring Boot application successfully at port 8071 which is the port we configured inside **application.properties**. Your can confirm the same by looking at the console logs.
- Access the URLs like http://localhost:8071/accounts/default, http://localhost:8071/loans/dev, http://localhost:8071/cards/prod inside your browser to randomly validate that properties are being read from configured Github location by Config Server for all the three microservices **accounts, loans and cards**.
- Now in order to integrate individual microservices **accounts, loans and cards** with **configserver**, please update the **pom.xml** files inside these microservices with **<spring-cloud.version>** details, **spring-cloud-starter-config** dependency, **spring-cloud-dependencies** under **dependencyManagement**. After making the changes, your pom.xml files should like below. For more details please check the course videos.
	
### accounts\pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.1.0</version>
		<relativePath /> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.eaztbytes</groupId>
	<artifactId>accounts</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>
	<name>accounts</name>
	<description>Microservice for Accounts</description>
	<properties>
		<java.version>17</java.version>
		<spring-cloud.version>2022.0.3</spring-cloud.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-config</artifactId>
		</dependency>
		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<scope>provided</scope>
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
			</plugin>
		</plugins>
	</build>

</project>
```
### loans\pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.1.0</version>
		<relativePath /> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.eaztbytes</groupId>
	<artifactId>loans</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>
	<name>loans</name>
	<description>Microservice for Loans</description>
	<properties>
		<java.version>17</java.version>
		<spring-cloud.version>2022.0.3</spring-cloud.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-config</artifactId>
		</dependency>
		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<scope>provided</scope>
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
### cards\pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.1.0</version>
		<relativePath /> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.eaztbytes</groupId>
	<artifactId>cards</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>
	<name>cards</name>
	<description>Microservice for Cards</description>
	<properties>
		<java.version>17</java.version>
		<spring-cloud.version>2022.0.3</spring-cloud.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-config</artifactId>
		</dependency>
		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<scope>provided</scope>
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
-  Make sure all the required and associated libraries are downloaded under maven dependencies of **accounts**, **loans**, **cards** microservices.
-  Like we discussed in the course, add **@RefreshScope** annotation on top of **AccountsApplication.java, LoansApplication.java and CardsApplication.java**. This is completely optional and can be used in the scenarios where we want to refresh the properties loaded into the given microservice with out the need of restart. In order to refresh the properties we can invoke the POST API **/actuator/refresh** exposed by the actuator through Postman. This refresh API will be exposed only if we configure **management.endpoints.web.exposure.include=*** inside **application.properties** like mentioned in the below step.
-  Open the **application.properties** present inside **accounts**, **loans**, **cards** microservices and make sure to update the properties related to Config server details inside them. After making the changes, your **application.properties** files should like below. For more details please check the course videos.
### \accounts\src\main\resources\application.properties
```
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
server.port=8080

spring.application.name=accounts
spring.profiles.active=prod
spring.config.import=optional:configserver:http://localhost:8071
management.endpoints.web.exposure.include=*
```
### \loans\src\main\resources\application.properties
```
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
server.port=8090

spring.application.name=loans
spring.profiles.active=dev
spring.config.import=optional:configserver:http://localhost:8071/
management.endpoints.web.exposure.include=*
```
### \cards\src\main\resources\application.properties
```
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
server.port=9000

spring.application.name=cards
spring.profiles.active=default
spring.config.import=optional:configserver:http://localhost:8071
management.endpoints.web.exposure.include=*
```
-  Like we discussed in the course, under Accounts microservice project, please create the classes **AccountsServiceConfig.java**, **Properties.java** and update **AccountsController.java** to add a new GET API with the name **/account/properties**. After making the changes, these classes should like below.

### accounts\src\main\java\com\eazybytes\accounts\config\AccountsServiceConfig.java

```java
/**
 * 
 */
package com.eazybytes.accounts.config;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Eazy Bytes
 *
 */
@Configuration
@ConfigurationProperties(prefix = "accounts")
@Getter @Setter @ToString
public class AccountsServiceConfig {

	 private String msg;
	 private String buildVersion;
	 private Map<String, String> mailDetails;
	 private List<String> activeBranches;

}

```
### \accounts\src\main\java\com\eazybytes\accounts\model\Properties.java

```java
package com.eazybytes.accounts.model;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Properties {

	private String msg;
	private String buildVersion;
	private Map<String, String> mailDetails;
	private List<String> activeBranches;

	public Properties(String msg, String buildVersion, Map<String, String> mailDetails, List<String> activeBranches) {
		this.msg = msg;
		this.buildVersion = buildVersion;
		this.mailDetails = mailDetails;
		this.activeBranches = activeBranches;
	}

}
```
### \accounts\src\main\java\com\eazybytes\accounts\controller\AccountsController.java

```java
/**
 * 
 */
package com.eazybytes.accounts.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eazybytes.accounts.config.AccountsServiceConfig;
import com.eazybytes.accounts.model.Accounts;
import com.eazybytes.accounts.model.Customer;
import com.eazybytes.accounts.model.Properties;
import com.eazybytes.accounts.repository.AccountsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

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

}
```
-  Like we discussed in the course, under Loans microservice project, please create the classes **LoansServiceConfig.java**, **Properties.java** and update **LoansController.java** to add a new GET API with the name **/loans/properties**. After making the changes, these classes should like below.

### \loans\src\main\java\com\eazybytes\loans\config\LoansServiceConfig.java

```java
/**
 * 
 */
package com.eazybytes.loans.config;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Eazy Bytes
 *
 */
@Configuration
@ConfigurationProperties(prefix = "loans")
@Getter @Setter @ToString
public class LoansServiceConfig {

	 private String msg;
	 private String buildVersion;
	 private Map<String, String> mailDetails;
	 private List<String> activeBranches;

}
```
### \loans\src\main\java\com\eazybytes\loans\model\Properties.java

```java
package com.eazybytes.loans.model;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Properties {

	private String msg;
	private String buildVersion;
	private Map<String, String> mailDetails;
	private List<String> activeBranches;

	public Properties(String msg, String buildVersion, Map<String, String> mailDetails, List<String> activeBranches) {
		this.msg = msg;
		this.buildVersion = buildVersion;
		this.mailDetails = mailDetails;
		this.activeBranches = activeBranches;
	}

}
```
### \loans\src\main\java\com\eazybytes\loans\controller\LoansController.java

```java
/**
 * 
 */
package com.eazybytes.loans.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eazybytes.loans.config.LoansServiceConfig;
import com.eazybytes.loans.model.Customer;
import com.eazybytes.loans.model.Loans;
import com.eazybytes.loans.model.Properties;
import com.eazybytes.loans.repository.LoansRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * @author Eazy Bytes
 *
 */

@RestController
public class LoansController {

	@Autowired
	private LoansRepository loansRepository;
	
	@Autowired
	LoansServiceConfig loansConfig;

	@PostMapping("/myLoans")
	public List<Loans> getLoansDetails(@RequestBody Customer customer) {
		List<Loans> loans = loansRepository.findByCustomerIdOrderByStartDtDesc(customer.getCustomerId());
		if (loans != null) {
			return loans;
		} else {
			return null;
		}

	}
	
	@GetMapping("/loans/properties")
	public String getPropertyDetails() throws JsonProcessingException {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		Properties properties = new Properties(loansConfig.getMsg(), loansConfig.getBuildVersion(),
				loansConfig.getMailDetails(), loansConfig.getActiveBranches());
		String jsonStr = ow.writeValueAsString(properties);
		return jsonStr;
	}

}
```
-  Like we discussed in the course, under Cards microservice project, please create the classes **CardsServiceConfig.java**, **Properties.java** and update **CardsController.java** to add a new GET API with the name **/cards/properties**. After making the changes, these classes should like below.

### \cards\src\main\java\com\eazybytes\cards\config\CardsServiceConfig.java

```java
/**
 * 
 */
package com.eazybytes.cards.config;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Eazy Bytes
 *
 */
@Configuration
@ConfigurationProperties(prefix = "cards")
@Getter @Setter @ToString
public class CardsServiceConfig {

	 private String msg;
	 private String buildVersion;
	 private Map<String, String> mailDetails;
	 private List<String> activeBranches;

}
```
### \cards\src\main\java\com\eazybytes\cards\model\Properties.java

```java
package com.eazybytes.cards.model;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Properties {

	private String msg;
	private String buildVersion;
	private Map<String, String> mailDetails;
	private List<String> activeBranches;

	public Properties(String msg, String buildVersion, Map<String, String> mailDetails, List<String> activeBranches) {
		this.msg = msg;
		this.buildVersion = buildVersion;
		this.mailDetails = mailDetails;
		this.activeBranches = activeBranches;
	}

}
```
### \cards\src\main\java\com\eazybytes\cards\controller\CardsController.java

```java
/**
 * 
 */
package com.eazybytes.cards.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eazybytes.cards.config.CardsServiceConfig;
import com.eazybytes.cards.model.Cards;
import com.eazybytes.cards.model.Customer;
import com.eazybytes.cards.model.Properties;
import com.eazybytes.cards.repository.CardsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * @author Eazy Bytes
 *
 */

@RestController
public class CardsController {

	@Autowired
	private CardsRepository cardsRepository;
	
	@Autowired
	CardsServiceConfig cardsConfig;

	@PostMapping("/myCards")
	public List<Cards> getCardDetails(@RequestBody Customer customer) {
		List<Cards> cards = cardsRepository.findByCustomerId(customer.getCustomerId());
		if (cards != null) {
			return cards;
		} else {
			return null;
		}

	}
	
	@GetMapping("/cards/properties")
	public String getPropertyDetails() throws JsonProcessingException {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		Properties properties = new Properties(cardsConfig.getMsg(), cardsConfig.getBuildVersion(),
				cardsConfig.getMailDetails(), cardsConfig.getActiveBranches());
		String jsonStr = ow.writeValueAsString(properties);
		return jsonStr;
	}

}

```
-  Check if your Config Server is running at port 8071, if not go to your Spring Boot main class **ConfigserverApplication.java** and right click-> Run As -> Java Application. This will start your Spring Boot application successfully at port 8071 which is the port we configured inside **application.properties**. Your can confirm the same by looking at the console logs.
-  Now go to the Spring Boot main classes and start all the three microservices by right click-> Run As -> Java Application. This will start your microservices successfully at port **8080,8090,9000** based on the ports configured inside **application.properties**. Your can confirm the same by looking at the console logs.
-  To validate if individual microservices like **accounts, loans & cards** are able to fetch properties from **configserver** based on the cofigured **spring.profiles.active**
   value, invoke the REST APIs http://localhost:8080/account/properties, http://localhost:8090/loans/properties, http://localhost:9000/cards/properties through browser. You        should get the properties related to a microservice based on the active profile configured.
-  Generate the docker images and push them into Docker hub by following the similar steps we discussed in the previous section.
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
      
  accounts:
    image: eazybytes/accounts:latest
    mem_limit: 700m
    ports:
      - "8080:8080"
    networks:
      - eazybank
    depends_on:
      - configserver
    deploy:
      restart_policy:
        condition: on-failure
        delay: 5s
        max_attempts: 3
        window: 120s
    environment:
      SPRING_APPLICATION_NAME: accounts
      SPRING_PROFILES_ACTIVE: default
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
  
  loans:
    image: eazybytes/loans:latest
    mem_limit: 700m
    ports:
      - "8090:8090"
    networks:
      - eazybank
    depends_on:
      - configserver
    deploy:
      restart_policy:
        condition: on-failure
        delay: 5s
        max_attempts: 3
        window: 120s
    environment:
      SPRING_APPLICATION_NAME: loans
      SPRING_PROFILES_ACTIVE: default
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
    
  cards:
    image: eazybytes/cards:latest
    mem_limit: 700m
    ports:
      - "9000:9000"
    networks:
      - eazybank
    depends_on:
      - configserver
    deploy:
      restart_policy:
        condition: on-failure
        delay: 5s
        max_attempts: 3
        window: 120s
    environment:
      SPRING_APPLICATION_NAME: cards
      SPRING_PROFILES_ACTIVE: default
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      
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
      
  accounts:
    image: eazybytes/accounts:latest
    mem_limit: 700m
    ports:
      - "8080:8080"
    networks:
      - eazybank
    depends_on:
      - configserver
    deploy:
      restart_policy:
        condition: on-failure
        delay: 5s
        max_attempts: 3
        window: 120s
    environment:
      SPRING_APPLICATION_NAME: accounts
      SPRING_PROFILES_ACTIVE: dev
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
  
  loans:
    image: eazybytes/loans:latest
    mem_limit: 700m
    ports:
      - "8090:8090"
    networks:
      - eazybank
    depends_on:
      - configserver
    deploy:
      restart_policy:
        condition: on-failure
        delay: 5s
        max_attempts: 3
        window: 120s
    environment:
      SPRING_APPLICATION_NAME: loans
      SPRING_PROFILES_ACTIVE: dev
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
    
  cards:
    image: eazybytes/cards:latest
    mem_limit: 700m
    ports:
      - "9000:9000"
    networks:
      - eazybank
    depends_on:
      - configserver
    deploy:
      restart_policy:
        condition: on-failure
        delay: 5s
        max_attempts: 3
        window: 120s
    environment:
      SPRING_APPLICATION_NAME: cards
      SPRING_PROFILES_ACTIVE: dev
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      
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
      
  accounts:
    image: eazybytes/accounts:latest
    mem_limit: 700m
    ports:
      - "8080:8080"
    networks:
      - eazybank
    depends_on:
      - configserver
    deploy:
      restart_policy:
        condition: on-failure
        delay: 5s
        max_attempts: 3
        window: 120s
    environment:
      SPRING_APPLICATION_NAME: accounts
      SPRING_PROFILES_ACTIVE: prod
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
  
  loans:
    image: eazybytes/loans:latest
    mem_limit: 700m
    ports:
      - "8090:8090"
    networks:
      - eazybank
    depends_on:
      - configserver
    deploy:
      restart_policy:
        condition: on-failure
        delay: 5s
        max_attempts: 3
        window: 120s
    environment:
      SPRING_APPLICATION_NAME: loans
      SPRING_PROFILES_ACTIVE: prod
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
    
  cards:
    image: eazybytes/cards:latest
    mem_limit: 700m
    ports:
      - "9000:9000"
    networks:
      - eazybank
    depends_on:
      - configserver
    deploy:
      restart_policy:
        condition: on-failure
        delay: 5s
        max_attempts: 3
        window: 120s
    environment:
      SPRING_APPLICATION_NAME: cards
      SPRING_PROFILES_ACTIVE: prod
      SPRING_CONFIG_IMPORT: configserver:http://configserver:8071/
      
networks:
  eazybank:
```
-  Based on the active profile that you want start the microservices, open the command line tool where the **docker-compose.yml** is present and run the docker compose command **"docker-compose up"** to start all the microservices containers with a single command. All the running containers can be validated by running a docker command **"docker ps"**.
-  To validate if individual microservices like **accounts, loans & cards** are able to fetch properties from **configserver** based on the started active profile
   value, invoke the REST APIs http://localhost:8080/account/properties, http://localhost:8090/loans/properties, http://localhost:9000/cards/properties through browser. You        should get the properties related to a microservice based on the active profile started.
-  Stop the three running containers by executing the docker compose command **"docker-compose down"** from the location where **docker-compose.yml** is present.
---
### HURRAY !!! Congratulations, you successfully set up Configuration management inside microservices network using Spring Cloud Config.
---
