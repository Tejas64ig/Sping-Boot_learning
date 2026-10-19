package com.example.lab;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Spring Boot application context load test.
 *
 * @SpringBootTest loads the FULL Spring ApplicationContext.
 * This test verifies that:
 *   - All @Bean definitions are valid
 *   - All required dependencies can be injected
 *   - application.properties is loadable and valid
 *   - The application can start without errors
 *
 * If this test fails, it means your Spring configuration is broken —
 * even before you send a single HTTP request.
 *
 * The contextLoads() method body is intentionally empty.
 * The real assertion is that the ApplicationContext starts up successfully.
 * If Spring cannot wire all the beans, this test will throw an exception and FAIL.
 */
@SpringBootTest
class LabApplicationTests {

    /**
     * Verifies that the Spring ApplicationContext loads without errors.
     * This is the most fundamental sanity check for any Spring Boot application.
     */
    @Test
    void contextLoads() {
        // If we reach this line, the ApplicationContext started successfully.
        // All beans were created and wired correctly.
    }
}
