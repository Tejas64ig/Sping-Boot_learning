package com.example.lab.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AppConfig demonstrates the @Configuration and @Bean approach to bean creation.
 *
 * @Configuration tells Spring:
 *   "This class contains bean definitions. Process the @Bean methods inside it."
 *
 * Under the hood, Spring creates a special proxy (subclass) of this class at startup.
 * This proxy ensures that if you call a @Bean method multiple times within this class,
 * you always get the SAME singleton instance — not a new object each time.
 *
 * WHEN TO USE @Configuration + @Bean vs @Service + @Component:
 *
 * Use @Service / @Component / @Repository when:
 *   - The class is YOUR OWN class
 *   - You can add annotations directly to the source code
 *   - Spring can create the object with a no-arg or injectable constructor
 *
 * Use @Configuration + @Bean when:
 *   - The class belongs to a THIRD-PARTY LIBRARY (you cannot add @Service to it)
 *   - You need CUSTOM CONSTRUCTION LOGIC (e.g., configure an HTTP client with a timeout)
 *   - You want to wire together multiple objects in a specific way
 *
 * Real-world examples of @Bean usage:
 *   - ObjectMapper (custom Jackson JSON settings)
 *   - RestTemplate or WebClient (HTTP clients with timeouts and interceptors)
 *   - Database connection pools
 *   - Password encoders (BCryptPasswordEncoder in Spring Security)
 */
@Configuration
public class AppConfig {

    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

    /**
     * Defines a bean of type MessageFormatter.
     *
     * The method name "messageFormatter" becomes the bean's name by default.
     * You can override it with: @Bean(name = "customName")
     *
     * Spring calls this method ONCE during startup and stores the returned object.
     * Any class that needs a MessageFormatter gets this same instance injected.
     *
     * @return a configured MessageFormatter instance
     */
    @Bean
    public MessageFormatter messageFormatter() {
        log.info("AppConfig: Creating MessageFormatter bean");
        return new MessageFormatter("[ ", " ]");
    }

    /**
     * Inner class to demonstrate what a custom bean looks like.
     *
     * In real projects this would be a third-party class that you cannot annotate.
     * Here we simulate that scenario — imagine this class came from an external library.
     *
     * Because we cannot add @Component to it, we use @Bean in AppConfig instead.
     */
    public static class MessageFormatter {

        private final String prefix;
        private final String suffix;

        /**
         * Constructor injection in a plain Java class (no Spring annotations).
         * Spring calls this via the @Bean factory method above, passing the values.
         */
        public MessageFormatter(String prefix, String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
        }

        /**
         * Wraps a message with the configured prefix and suffix.
         *
         * @param message The raw message to format
         * @return The formatted message
         */
        public String format(String message) {
            return prefix + message + suffix;
        }
    }
}
