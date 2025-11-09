# Code Quality

Code quality has been a central focus throughout development. We implement several tools and practices to ensure maintainable, readable, and reliable code. Release 3 expanded quality measures with REST API architecture and comprehensive user authentication.

## Multi-Module Architecture

Our application follows a **multi-module Maven architecture** with separation of concerns:

- **`core`**: Core business logic and domain models
- **`storage`**: Data persistence layer with file-based storage
- **`fxui`**: JavaFX user interface and client-side logic
- **`server`**: Spring Boot REST API server
- **`shared`**: Shared DTOs, constants, and utilities

This ensures loose coupling, high cohesion, and easier testing.

## Static Analysis with SpotBugs

We use **SpotBugs** for static code analysis to detect potential bugs and quality issues:

- Potential null pointer exceptions
- Resource leaks
- Security vulnerabilities
- Performance issues
- Bad coding practices

SpotBugs has been valuable in REST API development, helping identify issues in Spring Boot controllers and service classes, particularly around input validation and null handling.

## REST API Quality Standards

Our Spring Boot REST API follows consistent design patterns and quality standards:

- **Controller design**: Clean separation of concerns with service layer delegation
- **Standardized responses**: All endpoints use `ApiResponse<T>` wrapper for consistent format
- **Input validation**: Multi-layer validation including null checks and business rules
- **Error handling**: Comprehensive exception handling with informative messages
- **Security measures**: Parameter validation and data sanitization to prevent attacks

We maintain three main controllers: `UserController` for authentication and user management, `FlashcardController` for individual flashcard operations, and `DeckController` for deck management.

Example REST controller pattern: [UserController.java:100-114](../../flashcards/server/src/main/java/server/controller/UserController.java#L100-L114)

## Authentication and Security

We implement robust security measures:

- **Password hashing**: SHA-256 with random salt generation
- **Password validation**: Enforced complexity requirements (8+ chars, uppercase, lowercase, numbers, special characters)
- **Secure UI components**: JavaFX PasswordField components
- **Input validation**: Multi-layer validation to prevent injection attacks

Our authentication system includes user registration with username uniqueness validation, secure login, and proper session management. Legacy password migration from plain text to hashed passwords is handled automatically.

Password security implementation: [PasswordEncoder.java](../../flashcards/core/src/main/java/app/PasswordEncoder.java)

## Documentation with Javadoc

All classes and methods are documented using **Javadoc** comments including class descriptions, method documentation with parameters, return values, and exceptions.

Javadoc examples: [FlashcardController.java:21-30](../../flashcards/server/src/main/java/server/controller/FlashcardController.java#L21-L30), [UserController.java:20-29](../../flashcards/server/src/main/java/server/controller/UserController.java#L20-L29)

## Naming Conventions

We follow consistent naming conventions throughout the codebase:

- **Classes**: PascalCase (e.g., `UserController`, `FlashcardService`, `LoginValidator`)
- **Methods**: camelCase with descriptive names (e.g., `logInUser()`, `validatePassword()`, `createFlashcard()`)
- **Variables**: camelCase and descriptive (e.g., `currentUsername`, `encodedPassword`, `loginRequest`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `API_BASE_URL`, `PASSWORD_INVALID`)
- **API Endpoints**: Consistent REST naming (e.g., `/api/v1/users`, `/login`, `/register`)
- **DTO Classes**: Descriptive with "Dto" suffix (e.g., `LoginRequestDto`, `FlashcardDto`)

## Error Handling and Resilience

We implement comprehensive error handling:

- **Client-side**: Robust HTTP error management with proper exception wrapping
- **Server-side**: Consistent try-catch blocks in all REST controllers
- **Standardized responses**: All errors return `ApiResponse<T>` format
- **Informative messages**: Clear descriptions without exposing security details
- **Multi-layer validation**: Prevent errors at the source
- **Graceful degradation**: UI remains functional when API calls fail

## Testing Strategy

Our testing approach includes:

- **Unit tests**: Individual methods and classes in isolation
- **Integration tests**: Component interaction and API endpoints
- **Coverage goals**: High test coverage of business logic
- **Test naming**: Descriptive method names explaining what is tested

Testing covers all architectural layers: core business logic, service layer with mocked dependencies, DTO serialization validation, and full request-response cycles.

Test examples: [LoginRequestDtoTest.java](../../flashcards/shared/src/test/java/dto/LoginRequestDtoTest.java), [DeckServiceTest.java](../../flashcards/server/src/test/java/server/service/DeckServiceTest.java)

## Data Transfer Objects (DTOs)

We implement proper separation between internal models and external APIs using DTOs:

- **Clear separation**: DTOs separate domain models from API contracts
- **JSON serialization**: Proper Jackson annotations for serialization/deserialization
- **Validation**: Comprehensive validation in constructors and setters
- **Mapper pattern**: Dedicated mapper classes for safe DTO/domain conversion
- **Type safety**: Strong typing throughout the API layer

This ensures internal models can evolve independently from public API contracts.

## Code Structure and Organization

- **Package organization**: Clear separation between `core`, `storage`, `fxui`, `server`, and `shared` modules
- **Layered architecture**: Controller, service, and persistence layers with clear responsibilities
- **Single Responsibility Principle**: Each class has a focused purpose
- **Dependency injection**: Spring Boot DI for loose coupling
- **Separation of concerns**: Clean boundaries between UI, business logic, and data access

## API Design Principles

Our REST API follows established design principles:

- **Resource-based URLs**: Clean endpoints like `/api/v1/users` and `/api/v1/flashcards`
- **HTTP method semantics**: GET for retrieval, POST for creation, PUT for updates, DELETE for removal
- **Stateless design**: No server-side session management
- **Consistent response format**: All endpoints use `ApiResponse<T>` wrapper
- **Versioning strategy**: URL path versioning (`/api/v1/`)
- **RESTful conventions**: Following REST principles for predictable APIs

## Maven Configuration for Quality

Our `pom.xml` files include quality plugins:

- **SpotBugs Maven Plugin**: Automated static analysis with HTML reporting
- **Checkstyle Plugin**: Code style enforcement using Google Java Style guidelines
- **JaCoCo Plugin**: Test coverage reporting and analysis
- **Maven Surefire Plugin**: Test execution and reporting with JavaFX support
- **Spring Boot Maven Plugin**: REST API packaging and execution
- **JavaFX Maven Plugin**: Client application execution

Our build process includes quality gates: compilation without warnings, code style compliance, no critical SpotBugs issues, and passing tests with adequate coverage. The codebase maintains zero checkstyle violations.

## Continuous Quality Improvement

We continuously improve code quality through:

- Regular code reviews and pull requests
- Refactoring when needed to maintain clean architecture
- Updating documentation as code evolves
- Learning from SpotBugs findings to write better code
- Maintaining consistent coding standards across the team
- Proactive checkstyle compliance and import order management
- Security audits and performance monitoring
- Static analysis feedback to prevent similar issues
- Standardization of development environment (Java 21) for consistency

Recent quality improvements include resolving all checkstyle warnings, standardizing import order across modules, and ensuring proper null-safety validation in service layer tests.
