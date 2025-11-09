# AI Tools

## Our Approach to AI in Development

We used AI as a supportive tool to enhance learning, productivity, and code quality rather than replace our own coding. Most code was written manually, but AI helped with guidance, troubleshooting, and best practices. **GitHub Copilot (Claude Sonnet 4.0)** assisted with dependency management in pom.xml files, Maven configuration, testing, generated Javadoc documentation, and bug fixing. **ChatGPT** supported project setup, Java syntax, and GitHub conventions. **Claude AI** improved test organization and configured JaCoCo for coverage reporting. We also used this to set up JLink and JPackage correctly, since none of the teammembers had experience with these technologies. Together, these tools enhanced our workflow and learning throughout the project.

## Classes we have used ai in:

- `PasswordEncoder`: Used claude.ai to identify best practice for secure password handling and hashing.
- `PasswordEncoderTest`: Used claude.ai verifying password encoding and matching functionality using JUnit 5.

**Fxui**
- `Flashcardcontroller` : AI helped correct event-handling syntax and structure methods like `updateUi()` and `flipCard()` for UI updates. In `flipCard()` it was the card rotation functionality we struggled with, where **GitHub Copilot** came to good use. 
- `FlashcardDeckController`
  - `whenCreateButtonIsClicked()`,  `whenDeleteCardButtonIsClicked()` and `loadDeckData()`: AI helped handle API endpoints and correct syntax.
- `ApiClientTest`: AI provided guidance on writing correct tests for API handling.
- `FlashcardDeckControllerTest`, `FlashcardLoginControllerTest`, `FlashcardMainControllerTest` and `FlashcardSignUpControllerTest`: Parts of the code were genereted with AI assistance for comprehensive test coverage and to implement headless tests, particularly in `initJavaFx()` methods.

### server

- `UserController`
    - `loginUser()`: **GitHub Copilot** suggested initial login logic and validation patterns.
- `DeckControllerTest`, `FlashcardControllerTest`, `UserControllerTest`: Claude AI assisted in comprehensive test coverage.
- `FlashcardServiceTest` and `UserServiceTest`:
  - `testGetFlashcard()`, `testGetFlashcardThrows()` and `testCreateUserWithValidation()`: AI assisted in generating tests for thorough coverage and handling mocking.

**Shared**
- `UserMapper`: AI was used to assist in implementing the complex UserMapper code, by clarifying complex mapping between domain and DTO objects. 
- `FlashcardDeckMapperTest`, `FlashcardMapperTest`, `UserMapperTest`: AI assisted with generating tests and handling mapper syntax for tests.

### storage

- `Flashcardpersistent`:
    - `readUserDataInternal()` and `writeUserDataInternal()`: AI guided logic for reading and writing to user data. 
- `FlashcardPersistentTest`: 
    - `cleanup()` : AI helped efficiently handle deletion of multiple test user files. 