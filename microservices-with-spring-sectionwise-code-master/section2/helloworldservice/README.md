### Creating Hello World Service using Spring Boot
---

**Description:** This project is a maven project to build a simple REST Service using Spring Boot. Below are the key steps that are followed while creating this project.

**Key steps:**
- Go to https://start.spring.io/
- Fill all the details required to generate a Spring Boot project and add dependencies **Spring Web**,**Spring Boot Actuator**. Click GENERATE which will download the maven project in a zip format
- Extract the downloaded maven project and import the same into Eclipse by following the steps mentioned in the course
- Visit **pom.xml** and make sure all the required dependencies are present in it like shown below,
### /pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		 xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
        	<version>3.1.0</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.eazybytes</groupId>
	<artifactId>helloworldservice</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>helloworldservice</name>
	<description>Demo project for Spring Boot</description>
	<properties>
		<java.version>17</java.version>
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
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>

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
-  Make sure all the required and associated libraries are downloaded under maven dependencies
-  Open the SpringBoot main class **HelloworldserviceApplication.java** . We can always identify the main class in a Spring Boot project by looking for the annotation **@SpringBootApplication**

### \src\main\java\com\eazybytes\helloworldservice\HelloworldserviceApplication.java

```java
package com.eazybytes.helloworldservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HelloworldserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloworldserviceApplication.class, args);
	}

}

```
-  Please create a new Rest Controller class **HelloWorldRestController.java** in the same package of **HelloworldserviceApplication.java** and write a new method **sayHello()** with **@GetMapping(value = "/hello")** configurations on top of it. Your **HelloWorldRestController.java** class should look like shown below,

### \src\main\java\com\eazybytes\helloworldservice\HelloWorldRestController.java

```java
package com.eazybytes.helloworldservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldRestController {

	@GetMapping(value = "/hello")
	public String sayHello() {
		return "Hello, Welcome to course on Microservices";
	}
}
```
-  Open the **application.properties** and make the following entry inside it which will expose the supported APIs by Actuator
### \src\main\resources\application.properties
```
management.endpoints.web.exposure.include=*
```
-  Go to your Spring Boot main class **HelloworldserviceApplication.java** and right click-> Run As -> Java Application. This will start your Spring Boot application                successfully at port 8080 which is a default port. Your can confirm the same by looking at the console logs.
-  Access the URL http://localhost:8080/hello inside your browser and you should be able to see the response from your REST service
-  Access the URL http://localhost:8080/actuator inside your browser and you should be able to see various paths related to health, metrics etc. which are exposed by Actuator

---
### HURRAY !!! Congratulations you successfully setup Helloworld service using Spring Boot
---
