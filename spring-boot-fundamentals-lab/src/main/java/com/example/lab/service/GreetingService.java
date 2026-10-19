package com.example.lab.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * GreetingService is a Spring-managed service bean.
 *
 * WHY @Service?
 * @Service is a specialization of @Component. Both work the same way technically:
 * they tell Spring "register this class as a bean in the ApplicationContext."
 * However, @Service carries semantic meaning — it signals to other developers
 * that this class contains business logic, not HTTP handling or data access.
 *
 * Other stereotype annotations:
 *   @Component   - General-purpose bean (use when none of the others fit)
 *   @Service     - Business logic layer
 *   @Repository  - Data access layer (also adds database exception translation)
 *   @Controller  - Web layer, handles HTTP requests
 *   @RestController - @Controller + @ResponseBody combined
 *
 * SINGLETON SCOPE (default):
 * Spring creates exactly ONE instance of GreetingService for the entire application.
 * This single instance is reused every time something needs a GreetingService.
 * This is why you should NOT store request-specific state (e.g., user input)
 * in instance variables of a @Service class.
 */
@Service
public class GreetingService {

    /**
     * Logger for this class.
     * SLF4J (Simple Logging Facade for Java) is the logging abstraction Spring Boot uses.
     * The actual logging implementation is Logback (included by spring-boot-starter-web).
     *
     * Log levels from most verbose to least:
     *   TRACE → DEBUG → INFO → WARN → ERROR
     * In application.properties we set: logging.level.com.example.lab=DEBUG
     * So TRACE won't show, but DEBUG, INFO, WARN, and ERROR will.
     */
    private static final Logger log = LoggerFactory.getLogger(GreetingService.class);

    /**
     * @Value injects the value of the property "app.greeting.message"
     * from application.properties into this field at startup time.
     *
     * If the property is missing, Spring will throw an error on startup.
     * You can provide a default with: @Value("${app.greeting.message:Default greeting}")
     *
     * This is how you make your application configurable without changing Java code.
     * In production, you can override this with environment variables or a separate
     * properties file, keeping your source code free of environment-specific values.
     */
    @Value("${app.greeting.message}")
    private String greetingMessage;

    @Value("${app.greeting.author}")
    private String author;

    /**
     * Returns a simple greeting string.
     *
     * This is pure business logic. The service does not know or care whether
     * the greeting will be returned as HTTP JSON, printed to console, or
     * used in an email. That separation is the point of the service layer.
     *
     * @param name The name to include in the greeting
     * @return A formatted greeting string
     */
    public String greet(String name) {
        log.debug("GreetingService.greet() called with name: {}", name);

        String response = greetingMessage + " Welcome, " + name + "! — " + author;

        log.info("Generated greeting for: {}", name);
        return response;
    }

    /**
     * Returns the default greeting without a specific name.
     * Demonstrates method overloading in a service.
     */
    public String greet() {
        return greet("World");
    }
}
