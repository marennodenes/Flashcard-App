# Contributions

## Ailin Anjadatter Tinglum

Ailin was responsible for implementing the authentication system, developing the User and UserData classes along with LoginValidator for secure user management. She built the login and signup functionality, including FlashcardLoginController and FlashcardSignUpController in the fxui module, ensuring users could create accounts and authenticate securely.

In the server module, Ailin implemented the three REST API controllers (UserController, DeckController, and FlashcardController), establishing communication between frontend and backend. These controllers handle all HTTP requests for user authentication, deck management, and flashcard operations. She also developed all mapper classes in the shared module to handle data transformation between domain objects and DTOs, ensuring clean separation between layers.

In addition, Ailin contributed comprehensive test suites for the fxui module, helping maintain code quality and reliability. She also created system diagrams to document the application architecture, providing clear visual documentation for the team.

## Christian Sommer

Christian implemented FlashcardPersistent for release 1, establishing file-based data persistence using CSV, now changed to JSON. He wrote comprehensive tests for this class, covering read/write operations, error handling, and edge cases including special characters in filenames.

In the core module, Christian developed fundamental classes that form the backbone of the flashcard system. He created test suits for server layer classes, the testing required for the implemented business logic between controllers and persistence layers.

Christian configured JaCoCo for the project, setting up automated test coverage reporting to help maintain the team's 80% coverage target. This configuration has enabled ongoing code quality monitoring throughout development.

Early in the project, he set up the initial FlashcardController and UI components, establishing the foundation for the JavaFX interface before the REST API integration. This early work provided the team with a functional user interface to build upon in later development phases. And before the final release, he was responsible for cleaning up the fxui classes.

## Isa Marie Wisth

Isa took responsibility for implementing the server's business logic layer, developing all three service classes (UserService, DeckService, and FlashcardService) that handle core application operations. These services manage the logic between the REST controllers and the persistence layer, ensuring proper data validation and business rule enforcement.

In the shared module, Isa contributed to all DTO classes and developed key API utilities including ApiResponse, ApiEndpoints, and ApiConstants, establishing consistent communication patterns across the application. She also wrote comprehensive test suites for the core module, covering all fundamental classes including Flashcard, FlashcardDeck, FlashcardDeckManager, User, UserData, LoginValidator, and PasswordEncoder.

Early in the project, Isa set up the initial FlashcardController and UI components, establishing the foundation for the JavaFX interface. She also contributed to project documentation and created system diagrams to illustrate the application's architecture and design.

## Maren Nodenes-Fimland

Throughout the project, Maren played a key role in ensuring technical quality and effective teamwork. Acting as the team's debugger, she was responsible for identifying and fixing bugs, improving the application's overall stability and reliability.

She set up the REST API in the fxui module, enabling smooth communication between system components. Maren also configured JSON handling in the storage module and JaCoCo setup in the project. In addition, she developed fxui controllers and comprehensive unit tests for both the fxui and shared modules.

Beyond her technical work, Maren contributed to project documentation for Release 1 and Release 2, and created system diagrams to illustrate architecture and design in release 2. Always supportive and collaborative, Maren's technical skill, reliability, and teamwork made her an essential part of the project.

## Marie Kolstad Røed

Marie took primary responsibility for the application's user interface, designing and implementing all FXML files and CSS stylesheets that define the visual appearance and layout of the application. Her work created a cohesive and user-friendly interface across all screens.

In the fxui module, Marie developed most of the controller logic, including FlashcardController, FlashcardMainController, FlashcardDeckController, and FlashcardLoginController. She was responsible for iterating on these controllers throughout development, continuously fixing bugs and improving functionality. Her attention to detail ensured smooth user interactions and reliable UI behavior.

Marie also contributed to the shared module by developing all DTO classes (FlashcardDto, FlashcardDeckDto, FlashcardDeckManagerDto, UserDataDto, LoginRequestDto, and LoginResponseDto) along with key API utilities including ApiResponse, ApiEndpoints, and ApiConstants. Beyond her technical contributions, she played an important role in project documentation, helping maintain clear records of the team's progress and decisions.

## Sofie Tessem Wang

Sofie took on a crucial technical and organizational role in the project. Most significantly, she was responsible for migrating FlashcardPersistent from CSV to JSON format, configuring JSON serialization and deserialization with Jackson to enable more robust and flexible data storage. This foundational work supports all persistent data operations in the application.

As Scrum leader, Sofie organized and facilitated daily standup meetings, ensuring effective communication and coordination among team members throughout the development process. She contributed to the authentication system, working on both the core logic (UserData, LoginValidator, PasswordEncoder) and the UI components (FlashcardLoginController and FlashcardSignUpController).

In the server module, Sofie helped implement the REST API controllers alongside developing mapper classes in the shared module. She wrote comprehensive test suites for the fxui module, maintaining code quality standards. Additionally, she contributed to project documentation and created system diagrams to visualize the application architecture, helping the team maintain clear technical understanding throughout development.
