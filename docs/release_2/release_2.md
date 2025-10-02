# Release 2

I this second milestone, our primary goal was to deliver a fully functional base version of the app. The milestone introduces a user login system, per-user JSON data storage, a dedicated deck overview page, and a robust flashcard learning interface featuring card navigation and progress tracking. These enhancements establish a stable platform for future development and advanced learning features.

## User stories

The application addresses these key user needs:

- As a user, I want to create a login profile, so that my flashcards are saved separately from other users
- As a user, I want to create a new deck, so that I can group related flashcards by subject
- As a user, I want to add flashcards with questions and answers, so that I can test my knowledge
- As a user, I want to study my flashcards with a flip interface, so that I can practice active recall
- As a user, I want to track my progress, so that I can see which cards I need to review

## Key Focus Areas

### 1. User Authentication and Data Management

We implemented a complete user login system that allows multiple users to maintain separate flashcard collections.
The authentication flow guides users from login through the main application interface with proper username propagation.
User data is stored in JSON format with individual user files for data separation and privacy.

### 2. Enhanced Learning Interface

Developed a comprehensive flashcard learning system with card navigation, flip animations, and progress tracking.
Added functionality for users to mark cards as correct or incorrect, enabling future implementation of spaced repetition algorithms.
Implemented intuitive deck and flashcard management with creation, editing, and deletion capabilities.

### 3. Navigation and User Experience

Fixed critical navigation bugs that prevented users from accessing flashcard decks properly.
Implemented seamless navigation flow between login, main dashboard, deck editing, and learning interfaces.
Enhanced the user interface with proper state management and username display throughout the application.

## Project Module Separation

As in release_1 the project is divided into three distinct modules:

**[fxui (flashcards/fxui)](../flashcards/fxui):**  
This module handles all JavaFX user interface components and controllers.

**[core (flashcards/core)](../flashcards/core):**  
This module contains the business logic and domain models for flashcards and decks.

**[storage (flashcards/storage)](../flashcards/storage):**  
This module is responsible for JSON persistence and user data management.
This module handles all JavaFX user interface components and controllers.


## Learning System Implementation

We implemented a comprehensive learning interface that forms the core of our flashcard application.
The learning system allows users to navigate through flashcards with next/previous buttons and flip cards to reveal answers.
Users can track their progress by marking cards as correct or incorrect, with the system maintaining statistics for future enhancement.
The learning interface is seamlessly integrated with the deck management system, ensuring smooth transitions between editing and studying modes.

## Development Process

Our development approach emphasized pair programming and regular communication, as detailed in our [Teamwork](teamwork.md) documentation. We also made extensive use of AI tools for debugging and documentation, as outlined in our [AI Tools](ai_tools.md) report.

## Code Quality Report

As of this release, the project maintains high code quality standards through multiple tools and practices. For detailed information about our code quality approach, see our [Code Quality](code-quality.md) documentation.

Key quality measures include:
- Comprehensive Javadoc documentation for all major controllers
- SpotBugs static analysis with defensive programming practices
- Checkstyle compliance following Google Java Style guidelines
- Unit and integration testing with coverage goals
- Updated UML documentation reflecting current architecture

## Architecture Documentation

We have updated our UML documentation to accurately reflect the current application structure:
- **[Class diagrams](uml_documentation/class.puml)** show the relationships between domain models and controllers
- **[Package diagrams](uml_documentation/package.puml)** illustrate the three-module architecture
- **[Architecture diagrams](uml_documentation/Architecture.puml)** provide an overview of the application's component structure

For the current release, the application provides a solid foundation for flashcard-based learning with room for future enhancements such as spaced repetition algorithms and advanced statistics tracking.
