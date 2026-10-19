package com.example.lab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Spring Boot Fundamentals Lab application.
 *
 * @SpringBootApplication is a convenience annotation that combines:
 *
 *   1. @SpringBootConfiguration
 *      Marks this class as a source of bean definitions for the application context.
 *      It is a specialization of @Configuration.
 *
 *   2. @EnableAutoConfiguration
 *      Tells Spring Boot to automatically configure your application based on the
 *      dependencies present on the classpath. For example, since spring-boot-starter-web
 *      is on the classpath, Spring Boot automatically:
 *        - Starts an embedded Tomcat server on port 8080
 *        - Configures Spring MVC to handle HTTP requests
 *        - Sets up Jackson to convert Java objects to JSON
 *
 *   3. @ComponentScan
 *      Tells Spring to scan this package (com.example.lab) and all sub-packages
 *      for classes annotated with @Component, @Service, @Repository, @Controller, etc.
 *      Any class found is automatically registered as a Spring bean (managed object).
 *
 * Without Spring Boot, you would need to:
 *   - Configure and start a Tomcat server manually
 *   - Write XML or Java configuration for every component
 *   - Manage library versions and conflicts yourself
 *
 * Spring Boot automates all of that through its "convention over configuration" philosophy.
 */
@SpringBootApplication
public class LabApplication {

    /**
     * The main method is the standard Java entry point.
     *
     * SpringApplication.run() does the following:
     *   1. Creates the Spring ApplicationContext (the IoC container that manages all beans)
     *   2. Performs component scanning to discover beans
     *   3. Creates and wires all beans (dependency injection)
     *   4. Starts the embedded Tomcat web server
     *   5. Registers all controllers with the web server so they can receive HTTP requests
     *
     * After this method completes its startup sequence, your application is running
     * and ready to accept HTTP requests on http://localhost:8080
     */
    public static void main(String[] args) {
        SpringApplication.run(LabApplication.class, args);
    }
}
