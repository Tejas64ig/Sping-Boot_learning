# Spring Boot Fundamentals Lab

> **Project 1 of 6** in my Spring Boot learning portfolio.

A hands-on lab for understanding what Spring Boot actually does under the hood — IoC, dependency injection, bean lifecycle, HTTP request handling, and application configuration.

---

## What I Learned

- What Spring Boot automates vs what plain Spring requires
- How the `ApplicationContext` (IoC container) manages objects (beans)
- The difference between `@Component`, `@Service`, `@Configuration`, and `@Bean`
- Why constructor injection is preferred over field injection
- How an HTTP request travels from the client to a Java method and back
- How `application.properties` decouples configuration from code
- How to initialize a Git repository and make meaningful commits

---

## Features

- `GET /api/hello` — Returns a greeting (configurable via properties)
- `GET /api/hello?name=Tejas` — Personalized greeting using a query parameter
- `GET /api/hello/simple` — Returns a plain string response
- `GET /api/info` — Returns application metadata
- Custom `MessageFormatter` bean registered via `@Configuration` + `@Bean`
- Greeting message configurable from `application.properties` without code changes

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 (Temurin LTS) | Language |
| Spring Boot | 3.3.5 | Framework |
| Spring Web | (via starter) | Embedded Tomcat + Spring MVC |
| Maven | 3.10.0 | Build tool |
| JUnit 5 | (via starter-test) | Testing |

---

## Prerequisites

- Java 21 JDK installed ([Eclipse Temurin](https://adoptium.net/temurin/releases/?version=21))
- Maven 3.9+ installed and on PATH
- Git

Verify your setup:
```bash
java -version    # Should show: openjdk version "21.x.x"
mvn -version     # Should show: Apache Maven 3.x.x, Java version: 21.x.x
git --version
```

---

## Setup and Run

```bash
# Clone the repository
git clone https://github.com/Tejas64ig/spring-boot-fundamentals-lab.git
cd spring-boot-fundamentals-lab

# Compile
mvn clean compile

# Run tests
mvn test

# Start the server
mvn spring-boot:run
```

Server starts at: `http://localhost:8080`

---

## Configuration

Edit `src/main/resources/application.properties` to customize:

```properties
# Server port (default: 8080)
server.port=8080

# Customize the greeting message (no code change needed)
app.greeting.message=Hello from Spring Boot!
app.greeting.author=Spring Boot Fundamentals Lab
```

> No secrets, passwords, or API keys are used in this project.

---

## API Endpoints

### `GET /api/hello`

Returns a greeting using the default name "World".

**Request:**
```
GET http://localhost:8080/api/hello
```

**Response `200 OK`:**
```json
{
  "greeting": "Hello from Spring Boot! Welcome, World! — Spring Boot Fundamentals Lab",
  "formatted": "[ Hello from Spring Boot! Welcome, World! — Spring Boot Fundamentals Lab ]",
  "name": "World"
}
```

---

### `GET /api/hello?name={name}`

Returns a personalized greeting.

**Request:**
```
GET http://localhost:8080/api/hello?name=Tejas
```

**Response `200 OK`:**
```json
{
  "greeting": "Hello from Spring Boot! Welcome, Tejas! — Spring Boot Fundamentals Lab",
  "formatted": "[ Hello from Spring Boot! Welcome, Tejas! — Spring Boot Fundamentals Lab ]",
  "name": "Tejas"
}
```

---

### `GET /api/hello/simple`

Returns a plain string greeting.

**Request:**
```
GET http://localhost:8080/api/hello/simple
```

**Response `200 OK`:**
```
"Hello from Spring Boot! Welcome, World! — Spring Boot Fundamentals Lab"
```

---

### `GET /api/info`

Returns application metadata.

**Response `200 OK`:**
```json
{
  "application": "Spring Boot Fundamentals Lab",
  "project": "Project 1",
  "status": "running",
  "description": "Demonstrating IoC, DI, Beans, and HTTP handling"
}
```

---

## Running Tests

```bash
mvn test
```

Expected output:
```
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

The `LabApplicationTests` context load test verifies that:
- All bean definitions are valid
- All dependencies can be injected
- The Spring ApplicationContext starts without errors

---

## Project Structure

```
spring-boot-fundamentals-lab/
├── pom.xml                          # Maven config: Spring Boot 3.3.5, Java 21
├── README.md
├── .gitignore
└── src/
    ├── main/
    │   ├── java/com/example/lab/
    │   │   ├── LabApplication.java          # Entry point (@SpringBootApplication)
    │   │   ├── config/
    │   │   │   └── AppConfig.java           # @Configuration + @Bean example
    │   │   ├── service/
    │   │   │   └── GreetingService.java     # @Service with @Value injection
    │   │   └── controller/
    │   │       └── GreetingController.java  # @RestController with 3 endpoints
    │   └── resources/
    │       └── application.properties       # Configurable greeting message
    └── test/
        └── java/com/example/lab/
            └── LabApplicationTests.java     # Context load test
```

---

## Key Design Decisions

**Constructor injection over field injection**
The controller uses constructor injection (`private final`) rather than `@Autowired` on fields. This makes dependencies explicit, allows the field to be `final`, and makes the class easier to test without Spring.

**`@Bean` for MessageFormatter**
`MessageFormatter` is registered via `@Bean` in `AppConfig` rather than `@Component`. This demonstrates the pattern used for third-party classes that cannot be annotated directly.

**`@Value` for configuration**
The greeting message is read from `application.properties` using `@Value`. Changing the message requires no code change — only a properties update and restart.

---

## Known Limitations

- No database — all data is in-memory (intentional for this learning stage)
- No input validation — added in Project 2
- No error handling for missing properties (Spring fails fast on startup if `@Value` property is missing)

---

## Suggested Git History

```
chore: initialize Spring Boot fundamentals lab project
feat: add greeting service with @Value property injection
feat: add @Bean configuration with MessageFormatter
feat: add REST controller with constructor injection
feat: expose GET /hello and /info endpoints
test: add context load test
docs: add README with setup and API documentation
```

---

*Part of a 6-project Spring Boot learning portfolio — [github.com/Tejas64ig](https://github.com/Tejas64ig)*
