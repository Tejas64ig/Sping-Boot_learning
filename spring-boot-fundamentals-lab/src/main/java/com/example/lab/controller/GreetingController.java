package com.example.lab.controller;

import com.example.lab.config.AppConfig.MessageFormatter;
import com.example.lab.service.GreetingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * GreetingController handles all HTTP requests related to greetings.
 *
 * @RestController is a combination of two annotations:
 *   @Controller     - Marks this class as a Spring MVC controller (handles HTTP requests)
 *   @ResponseBody   - Tells Spring to write the return value directly to the HTTP response body
 *                     as JSON (using Jackson), rather than treating it as a view name.
 *
 * @RequestMapping("/api") applies a base URL prefix to all endpoints in this class.
 * So a method mapped to "/hello" becomes accessible at "/api/hello".
 *
 * HOW A REQUEST REACHES THIS METHOD (the full journey):
 *   1. Browser/Postman sends: GET http://localhost:8080/api/hello
 *   2. Embedded Tomcat receives the TCP connection
 *   3. Tomcat passes the request to Spring's DispatcherServlet (the front controller)
 *   4. DispatcherServlet looks up which controller method handles GET /api/hello
 *   5. It finds our greet() method (because of @GetMapping("/hello"))
 *   6. Spring calls the method (injecting @RequestParam values from the URL)
 *   7. The return value (a Map) is serialized to JSON by Jackson
 *   8. The JSON is written into the HTTP response body
 *   9. Tomcat sends the HTTP response back to the client
 */
@RestController
@RequestMapping("/api")
public class GreetingController {

    private static final Logger log = LoggerFactory.getLogger(GreetingController.class);

    /**
     * These fields are final because we use constructor injection.
     *
     * final means:
     *   - These references can NEVER be reassigned after construction
     *   - The object will never be in a partially initialized state
     *   - This is thread-safe for reading (important in a multi-threaded web server)
     *
     * If GreetingService bean is missing, Spring will throw a startup error
     * immediately at launch — not a NullPointerException buried in a request.
     */
    private final GreetingService greetingService;
    private final MessageFormatter messageFormatter;

    /**
     * Constructor Injection — the preferred approach in Spring Boot.
     *
     * Spring sees this constructor and automatically provides the required beans.
     * Notice: we do NOT use @Autowired here. Since Spring Boot 4.3+, if a class
     * has exactly ONE constructor, @Autowired is implied and not needed.
     *
     * What Spring does when creating this controller:
     *   1. Looks up GreetingService in the ApplicationContext — finds it (registered via @Service)
     *   2. Looks up MessageFormatter in the ApplicationContext — finds it (registered via @Bean)
     *   3. Calls this constructor, passing both beans as arguments
     *   4. Stores the result as the controller bean, ready to handle HTTP requests
     *
     * COMPARE: If you used @Autowired on fields, the constructor would need to be:
     *   public GreetingController() {} // no-arg
     * Spring would create the object first (null fields), THEN inject — riskier.
     *
     * @param greetingService Spring injects the GreetingService singleton
     * @param messageFormatter Spring injects the MessageFormatter singleton
     */
    public GreetingController(GreetingService greetingService, MessageFormatter messageFormatter) {
        this.greetingService = greetingService;
        this.messageFormatter = messageFormatter;
        log.info("GreetingController created — dependencies injected by Spring");
    }

    /**
     * Handles GET requests to /api/hello
     *
     * Example URLs:
     *   GET /api/hello              → Uses default name "World"
     *   GET /api/hello?name=Tejas   → Uses name "Tejas"
     *
     * @GetMapping("/hello") maps this method to HTTP GET requests on /api/hello.
     *
     * @RequestParam(required = false, defaultValue = "World") means:
     *   - "name" is an OPTIONAL query parameter (not required in the URL)
     *   - If missing, it defaults to "World"
     *   - Spring automatically extracts it from the URL query string
     *
     * Return type Map<String, String>:
     *   Jackson (the JSON library) serializes a Map as a JSON object.
     *   { "key": "value", "key2": "value2" }
     *   This is a simple alternative to creating a dedicated Response class for basic cases.
     */
    @GetMapping("/hello")
    public Map<String, String> greet(
            @RequestParam(required = false, defaultValue = "World") String name) {

        log.debug("GET /api/hello called with name={}", name);

        String greeting = greetingService.greet(name);
        String formatted = messageFormatter.format(greeting);

        // Map.of() creates an immutable map — suitable for simple JSON responses
        return Map.of(
                "greeting", greeting,
                "formatted", formatted,
                "name", name
        );
    }

    /**
     * Handles GET requests to /api/hello/simple
     *
     * Returns a plain String instead of a Map.
     * Jackson wraps a plain String in quotes: "Hello from Spring Boot! Welcome, World!"
     *
     * This endpoint demonstrates that @RestController can return any serializable type.
     */
    @GetMapping("/hello/simple")
    public String simpleGreet() {
        log.debug("GET /api/hello/simple called");
        return greetingService.greet();
    }

    /**
     * Handles GET requests to /api/info
     *
     * Returns information about the running application context.
     * Useful for understanding what's loaded in the Spring container.
     */
    @GetMapping("/info")
    public Map<String, String> info() {
        return Map.of(
                "application", "Spring Boot Fundamentals Lab",
                "project", "Project 1",
                "status", "running",
                "description", "Demonstrating IoC, DI, Beans, and HTTP handling"
        );
    }
}
