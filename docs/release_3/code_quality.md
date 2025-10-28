# Code Quality

Code quality has been a central focus throughout the development of our flashcard application. We have implemented several tools and practices to ensure maintainable, readable, and reliable code. In Release 3, we have significantly expanded our quality measures with the implementation of a REST API architecture and comprehensive user authentication system.

## Multi-Module Architecture

Our application follows a clean **multi-module Maven architecture** that promotes separation of concerns:

- **`core`**: Core business logic and domain models
- **`storage`**: Data persistence layer with file-based storage
- **`fxui`**: JavaFX user interface and client-side logic
- **`server`**: Spring Boot REST API server
- **`shared`**: Shared DTOs, constants, and utilities used across modules

This modular approach ensures loose coupling, high cohesion, and easier testing of individual components.

## Static Analysis with SpotBugs

We use **SpotBugs 4.9.3.0** for static code analysis to detect potential bugs and code quality issues. SpotBugs is configured in our Maven build process and helps identify:

- Potential null pointer exceptions
- Resource leaks
- Security vulnerabilities
- Performance issues
- Bad coding practices

SpotBugs has been particularly valuable in our REST API development, helping us identify potential issues in our Spring Boot controllers and service classes. For example, it reminds us to properly validate input parameters and handle null values in our API endpoints.

## REST API Quality Standards

Our Spring Boot REST API follows consistent design patterns and quality standards:

- **Controller design**: Clean separation of concerns with service layer delegation
- **Standardized responses**: All endpoints use `ApiResponse<T>` wrapper for consistent format
- **Input validation**: Multi-layer validation including null checks and business rules
- **Error handling**: Comprehensive exception handling with informative messages
- **Security measures**: Parameter validation and data sanitization to prevent attacks

Example of our REST controller pattern:

```java
@RestController
@RequestMapping(ApiEndpoints.USERS_V1)
public class UserController {
    
    @PostMapping(ApiEndpoints.USER_LOGIN)
    public ApiResponse<LoginResponseDto> logInUser(@RequestBody LoginRequestDto request) {
        try {
            Boolean login = userService.logInUser(request.getUsername(), request.getPassword());
            return new ApiResponse<>(true, "Login successful", responseDto);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Error logging in user: " + e.getMessage(), null);
        }
    }
}
```

## Authentication and Security

We implement robust security measures throughout our application:

- **Password hashing**: SHA-256 with random salt generation for secure password storage
- **Salt storage**: Each password uses unique salt to prevent rainbow table attacks
- **Password validation**: Enforced complexity requirements (8+ chars, uppercase, lowercase, numbers, special characters)
- **Secure UI components**: JavaFX PasswordField components hide user input
- **Input validation**: Multi-layer validation to prevent injection attacks
- **Error handling**: Informative feedback without security information leakage

Our authentication system includes user registration with username uniqueness validation, secure login with credential validation, and proper session management. The system automatically handles legacy password migration from plain text to hashed passwords for backward compatibility.

Example of our password security implementation:

```java
public class PasswordEncoder {
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    
    public static String encode(String password) {
        // Generate random salt and hash password with SHA-256
        return saltBase64 + ":" + hashBase64;
    }
    
    public static boolean matches(String password, String encodedPassword) {
        // Verify password against stored hash
    }
}
```

## Documentation with Javadoc

All public classes and methods are documented using **Javadoc** comments. Our documentation includes:

- **Class descriptions**: Purpose and responsibility of each class
- **Method documentation**: What the method does, parameters, return values, and exceptions
- **Parameter descriptions**: Clear explanation of each parameter's purpose
- **Return value documentation**: What the method returns and under what conditions
- **Usage examples**: Where appropriate, showing how to use complex methods

Our documentation standard has been enhanced for the REST API to include service layer and controller documentation.

Example of our enhanced Javadoc standard:

```java
/**
 * Controller for managing flashcards.
 * Uses the FlashcardService to handle business logic.
 * @see server.service.FlashcardService
 * @author ailinat
 * @author sofietw
 * @author marennod
 */
@RestController
@RequestMapping(ApiEndpoints.FLASHCARDS)
public class FlashcardController {

    /**
     * Creates a new flashcard with the provided question and answer.
     * 
     * @param username the username of the user who owns the deck
     * @param deckname the name of the deck to add the flashcard to
     * @param question the question text for the flashcard
     * @param answer the answer text for the flashcard
     * @return ApiResponse containing the created FlashcardDto on success,
     *         or error message on failure
     */
    @PostMapping(ApiEndpoints.FLASHCARD_CREATE)
    public ApiResponse<FlashcardDto> createFlashcard(...) {
        // Implementation
    }
}
```

## Naming Conventions

We follow consistent naming conventions throughout the codebase:

- **Classes**: PascalCase (e.g., `UserController`, `FlashcardService`, `LoginValidator`)
- **Methods**: camelCase with descriptive names (e.g., `logInUser()`, `validatePassword()`, `createFlashcard()`)
- **Variables**: camelCase and descriptive (e.g., `currentUsername`, `encodedPassword`, `loginRequest`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `API_BASE_URL`, `PASSWORD_INVALID`)
- **API Endpoints**: Consistent REST naming (e.g., `/api/v1/users`, `/login`, `/register`)
- **DTO Classes**: Descriptive with "Dto" suffix (e.g., `LoginRequestDto`, `FlashcardDto`)

## Error Handling and Resilience

We implement comprehensive error handling throughout our application:

- **Client-side handling**: Robust HTTP error management with proper exception wrapping
- **Server-side patterns**: Consistent try-catch blocks in all REST controllers
- **Standardized responses**: All errors return `ApiResponse<T>` format for consistency
- **Informative messages**: Clear error descriptions without exposing security details
- **Input validation**: Multi-layer validation to prevent errors at the source
- **Graceful degradation**: UI remains functional even when API calls fail

Our error handling ensures users receive helpful feedback while maintaining system security and stability.

## Testing Strategy

Our testing approach includes:

- **Unit tests**: Testing individual methods and classes in isolation
- **Integration tests**: Testing interaction between components and API endpoints
- **Coverage goals**: Aiming for high test coverage of business logic
- **Test naming**: Descriptive test method names that explain what is being tested

Our comprehensive testing covers all architectural layers including core business logic, service layer with mocked dependencies, DTO classes for serialization validation, and full request-response cycle testing for API endpoints.

Example of our test documentation standard:

```java
/**
 * Unit tests for the {@link LoginRequestDto} class.
 * 
 * This test class verifies the correct construction and behavior of LoginRequestDto,
 * including username and password handling, and setter/getter methods.
 * @author marennod  
 * @author ailinat
 */
public class LoginRequestDtoTest {
    
    @Test
    void testConstructorWithUsernameAndPassword() {
        // Test implementation
    }
}
```

## Data Transfer Objects (DTOs)

We implement proper separation between internal models and external APIs using Data Transfer Objects:

- **Clear separation**: DTOs separate internal domain models from external API contracts
- **JSON serialization**: Proper annotations for JSON serialization/deserialization
- **Validation**: Comprehensive validation in constructors and setters
- **Mapper pattern**: Dedicated mapper classes for safe conversion between DTOs and domain objects
- **Type safety**: Strong typing throughout the API layer

This approach ensures our internal models can evolve independently from our public API contracts while maintaining data integrity and type safety.

## Code Structure and Organization

- **Package organization**: Clear separation between `core`, `storage`, `fxui`, `server`, and `shared` modules
- **Layered architecture**: Controller, service, and persistence layers with clear responsibilities
- **Single Responsibility Principle**: Each class has a focused purpose
- **Dependency injection**: Spring Boot's dependency injection for loose coupling
- **Separation of concerns**: Clean boundaries between UI, business logic, and data access

Our multi-module Maven structure promotes maintainability and testability while ensuring clear separation of concerns across all application layers.

## API Design Principles

Our REST API follows established design principles:

- **Resource-based URLs**: Clean, predictable endpoints like `/api/v1/users` and `/api/v1/flashcards`
- **HTTP method semantics**: GET for retrieval, POST for creation, PUT for updates, DELETE for removal
- **Stateless design**: No server-side session management for scalability
- **Consistent response format**: All endpoints use standardized `ApiResponse<T>` wrapper
- **Versioning strategy**: URL path versioning (`/api/v1/`) for future compatibility
- **RESTful conventions**: Following REST principles for predictable and maintainable APIs

This approach ensures our API is intuitive, scalable, and follows industry best practices.

## Maven Configuration for Quality

Our `pom.xml` files include quality plugins:

- **SpotBugs Maven Plugin**: Automated static analysis
- **Checkstyle Plugin**: Code style enforcement with Google Java Style
- **JaCoCo Plugin**: Test coverage reporting
- **Maven Surefire Plugin**: Test execution and reporting with JavaFX support
- **Spring Boot Maven Plugin**: REST API packaging and execution
- **JavaFX Maven Plugin**: Client application execution

Our build process includes quality gates ensuring compilation without warnings, code style compliance, no critical SpotBugs issues, and passing all tests with adequate coverage.

## Continuous Quality Improvement

We continuously improve code quality through:

- Regular code reviews and pull requests
- Refactoring when needed
- Updating documentation as code evolves
- Learning from SpotBugs findings to write better code
- Maintaining consistent coding standards across the team
- Security audits and performance monitoring
- Static analysis feedback to prevent similar issues

This comprehensive approach to code quality ensures our flashcard application with REST API architecture is maintainable, reliable, and easy to understand for future development.
