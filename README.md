
# Flashcards Application

[open in Eclipse Che](https://che.stud.ntnu.no/#https://git.ntnu.no/IT1901-2025-groups/gr2516.git)

## Overview

This repository contains a JavaFX application for creating and studying flashcards. Users can create profiles, organize flashcards into decks, and track their learning progress through an interactive study interface.

## Building and running the project

This project uses Maven for building and running.

To build and run this project:

1. First change into the flashcards folder: `cd flashcards`
2. Run `mvn clean install` to build all modules and run tests
3. Navigate to the UI module: `cd fxui`
4. Run the application: `mvn javafx:run`

Alternatively, you can use the VS Code task "Run JavaFX App" which is configured in the workspace.

## Code structure

This structure helps separate concerns and makes the project easy to navigate.

[flashcards](/flashcards/)

- [core](/flashcards/core/) - Business logic and domain models
- [fxui](/flashcards/fxui/) - JavaFX user interface and controllers  
- [storage](/flashcards/storage/) - JSON persistence and file handling
- [config](/flashcards/config/) - Code quality configuration files (Checkstyle, SpotBugs)
- [.vscode](/.vscode/) - VS Code workspace settings and tasks
- [images](/images/) - Application screenshots and documentation images

## App design

<img src="images/FlashcardLogin_release2.png" alt="Login interface" width="400">
<img src="images/FlashcardMain_release2.png" alt="Main dashboard" width="400">
<img src="images/FlashcardList_release2.png" alt="Deck management" width="400">
<img src="images/FlashcardPage_release2.png" alt="Learning interface" width="400">

## Dependencies

- Java version 17+
- JavaFX version 21
- Maven version 3.9+
- JUnit 5 (Jupiter) for testing
- TestFX for UI testing
- SpotBugs 4.9.3.0 for static analysis
- Checkstyle for code style enforcement
- Jackson for JSON processing

## Test coverage

After running `mvn test`, you can generate test coverage reports via JaCoCo:

1. Navigate to a specific module: `cd core` (or fxui/storage)
2. Run `mvn jacoco:report`
3. Open the HTML report in `target/site/jacoco/index.html`

**Current test coverage:**

- **core**: 88 missed instructions | 76 missed branches
- **fxui**: Run `mvn jacoco:report` to generate coverage report
- **storage**: Run `mvn jacoco:report` to generate coverage report
- **total**: 88 missed instructions | 76 missed branches

Current coverage targets focus on core business logic and critical user workflows.

## Coding standards

We maintain high coding standards using:

- **SpotBugs**: Static analysis for bug detection (`mvn spotbugs:check`)
- **Checkstyle**: Code style enforcement following Google Java Style (`mvn checkstyle:check`)
- **Comprehensive Javadoc**: All public classes and methods are documented
- **Consistent naming**: Following Java conventions for classes, methods, and variables

To check code quality: `mvn verify` (runs tests, SpotBugs, and Checkstyle)

## Documentation

For detailed project documentation, navigate to [docs](/docs/):

- [Release 1](/docs/release_1/) - Initial implementation and basic functionality
- [Release 2](/docs/release_2/) - Enhanced features, user authentication, and quality improvements

## User stories

The application addresses these key user needs:

- As a user, I want to create a login profile, so that my flashcards are saved separately from other users
- As a user, I want to create a new deck, so that I can group related flashcards by subject
- As a user, I want to add flashcards with questions and answers, so that I can test my knowledge
- As a user, I want to study my flashcards with a flip interface, so that I can practice active recall
- As a user, I want to track my progress, so that I can see which cards I need to review

## Use of AI

As part of this project, we made use of AI-based tools to improve development efficiency and code quality. For detailed information about our AI usage, see [AI Tools documentation](/docs/release_2/ai_tools.md).

## Getting started

See the "Building and running the project" section above for quick setup instructions. For development, ensure you have Java 17+ and Maven installed.
