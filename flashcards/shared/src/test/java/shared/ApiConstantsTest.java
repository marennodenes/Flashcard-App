package shared;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the ApiConstants class.
 * This test class verifies that all constant values in ApiConstants
 * match their expected string values. It covers user, flashcard, deck, and general
 * message constants to ensure API responses and error messages remain consistent.
 *
 * @author marennod
 * @author ailinat
 */
public class ApiConstantsTest {

  /**
   * Tests user operation constants in ApiConstants.
   * Verifies that all user success and error messages match their expected values.
   */
  @Test
  public void testUserOperationConstants() {
    // User operation success messages
    assertEquals("User created successfully", ApiConstants.USER_CREATED);
    assertEquals("User retrieved successfully", ApiConstants.USER_RETRIEVED);
    assertEquals("User existence check successful", ApiConstants.USER_EXISTS);
    
    // User operation error messages
    assertEquals("User not found", ApiConstants.USER_NOT_FOUND);
    assertEquals("User already exists", ApiConstants.USER_ALREADY_EXISTS);
    assertEquals("Error creating user", ApiConstants.USER_CREATION_ERROR);
    assertEquals("Error retrieving user", ApiConstants.USER_RETRIEVED_ERROR);
    assertEquals("Error checking user existence", ApiConstants.USER_EXISTS_ERROR);
    assertEquals("Could not complete user operation\nPlease try again", 
        ApiConstants.USER_OPERATION_FAILED);
  }

  /**
   * Tests login operation constants in ApiConstants.
   * Verifies that all login success and error messages match their expected values.
   */
  @Test
  public void testLoginOperationConstants() {
    // Login success messages
    assertEquals("Login success", ApiConstants.LOGIN_SUCCESS);
    assertEquals("Login response", ApiConstants.LOGIN_RESPONSE);
    
    // Login error messages
    assertEquals("Login failed, invalid\nusername or password", 
        ApiConstants.LOGIN_FAILED);
    assertEquals("Error logging in user", ApiConstants.LOGIN_RESPONSE_ERROR);
    assertEquals("Login failed\nPlease check your credentials and try again", 
        ApiConstants.LOGIN_OPERATION_FAILED);
    assertEquals("Invalid password", ApiConstants.INVALID_PASSWORD);
  }

  /**
   * Tests password validation constants in ApiConstants.
   * Verifies that all password validation messages match their expected values.
   */
  @Test
  public void testPasswordValidationConstants() {
    // Password validation success messages
    assertEquals("Password validation successful", 
        ApiConstants.PASSWORD_VALIDATION_SUCCESSFUL);
    
    // Password validation error messages
    assertEquals("Password must be at\nleast 8 characters long", 
        ApiConstants.PASSWORD_TOO_SHORT);
    assertEquals("Password must contain at\nleast one uppercase letter", 
        ApiConstants.PASSWORD_MISSING_UPPERCASE);
    assertEquals("Password must contain at\nleast one lowercase letter", 
        ApiConstants.PASSWORD_MISSING_LOWERCASE);
    assertEquals("Password must contain\nat least one digit", 
        ApiConstants.PASSWORD_MISSING_DIGIT);
    assertEquals("Password must contain at\nleast one special character", 
        ApiConstants.PASSWORD_MISSING_SPECIAL);
    assertEquals("Error validating password", 
        ApiConstants.PASSWORD_VALIDATION_ERROR);
  }

  /**
   * Tests flashcard operation constants in ApiConstants.
   * Verifies that all flashcard success and error messages match their expected values.
   */
  @Test
  public void testFlashcardOperationConstants() {
    // Flashcard operation success messages
    assertEquals("Flashcard created successfully", ApiConstants.FLASHCARD_CREATED);
    assertEquals("Flashcard deleted successfully", ApiConstants.FLASHCARD_DELETED);
    assertEquals("Flashcard retrieved successfully", ApiConstants.FLASHCARD_RETRIEVED);
    assertEquals("Flashcards retrieved successfully", ApiConstants.FLASHCARDS_RETRIEVED);
    
    // Flashcard operation error messages
    assertEquals("Flashcard not found", ApiConstants.FLASHCARD_NOT_FOUND);
    assertEquals("Flashcard failed", ApiConstants.FLASHCARD_FAILED);
    assertEquals("Failed to delete flashcard", ApiConstants.FLASHCARD_FAILED_TO_DELETE);
    assertEquals("Failed to create flashcard", ApiConstants.FLASHCARD_FAILED_TO_CREATE);
    assertEquals("Failed to retrieve flashcard", ApiConstants.FLASHCARD_RETRIEVED_FAILED);
    assertEquals("Failed to retrieve flashcards", ApiConstants.FLASHCARDS_RETRIEVED_FAILED);
    assertEquals("Could not complete flashcard operation - Please try again", 
        ApiConstants.FLASHCARD_OPERATION_FAILED);
  }

  /**
   * Tests deck operation constants in ApiConstants.
   * Verifies that all deck success and error messages match their expected values.
   */
  @Test
  public void testDeckOperationConstants() {
    // Deck operation success messages
    assertEquals("Deck created successfully", ApiConstants.DECK_CREATED);
    assertEquals("Deck deleted successfully", ApiConstants.DECK_DELETED);
    assertEquals("Deck retrieved successfully", ApiConstants.DECK_RETRIEVED);
    assertEquals("Decks updated successfully", ApiConstants.DECK_UPDATED);
    assertEquals("Decks retrieved successfully", ApiConstants.DECKS_RETRIEVED);

    // Deck operation error messages.
    assertEquals("Deck not found", ApiConstants.DECK_NOT_FOUND);
    assertEquals("Deck name already exists", ApiConstants.DECK_ALREADY_EXISTS);
    assertEquals("Max number of decks reached", ApiConstants.DECK_LIMIT_REACHED);
    assertEquals("Failed to create deck", ApiConstants.DECK_FAILED_TO_CREATE);
    assertEquals("Failed to delete deck", ApiConstants.DECK_FAILED_TO_DELETE);
    assertEquals("Error deleting deck", ApiConstants.DECK_DELETED_ERROR);
    assertEquals("Error creating deck", ApiConstants.DECK_CREATED_ERROR);
    assertEquals("Error retrieving deck", ApiConstants.DECK_RETRIEVING_ERROR);
    assertEquals("Error updating decks", ApiConstants.DECK_UPDATED_ERROR);
    assertEquals("Error retrieving decks", ApiConstants.DECKS_RETRIEVING_ERROR);
    assertEquals("Could not complete deck operation - Please try again", 
        ApiConstants.DECK_OPERATION_FAILED);
    assertEquals("Could not update deck - Please try again", 
        ApiConstants.DECK_UPDATE_FAILED);
  }

  /**
   * Tests general constants in {@link ApiConstants}.
   * Verifies that all general error and request messages match their expected values.
   */
  @Test
  public void testGeneralConstants() {
    assertEquals("Invalid request", ApiConstants.INVALID_REQUEST);
    assertEquals("Could not load data", ApiConstants.FAILED_TO_LOAD_DATA);
    assertEquals("Server Error", ApiConstants.SERVER_ERROR);
    assertEquals("Load Error", ApiConstants.LOAD_ERROR);
    assertEquals("An unexpected error occurred - Please try again", 
        ApiConstants.UNEXPECTED_ERROR);
    
    // Additional general constants
    assertEquals("Unexpected error", ApiConstants.LOG_UNEXPECTED_ERROR);
    assertEquals("Validation Error", ApiConstants.VALIDATION_ERROR);
    assertEquals("No response from server", ApiConstants.NO_RESPONSE_FROM_SERVER);
    assertEquals("Could not connect to server - Please check that the server is running", 
        ApiConstants.SERVER_CONNECTION_ERROR);
    assertEquals("No valid button to get stage for scene switch", 
        ApiConstants.NO_VALID_BUTTON_FOR_SCENE_SWITCH);
  }

  /**
   * Tests UI validation constants in ApiConstants.
   * Verifies that all client-side validation messages match their expected values.
   */
  @Test
  public void testUiValidationConstants() {
    assertEquals("Username and password\ncannot be empty", ApiConstants.EMPTY_FIELDS);
    assertEquals("Passwords must be equal", ApiConstants.PASSWORDS_NOT_EQUAL);
    assertEquals("Deck name cannot be empty", ApiConstants.DECK_NAME_EMPTY);
    assertEquals("Flashcard question and answer cannot be empty", 
        ApiConstants.FLASHCARD_QUESTION_ANSWER_EMPTY);
    assertEquals("Username can only contain\nletters and numbers", 
        ApiConstants.INVALID_USERNAME);
  }

  /**
  * Tests general system constants in ApiConstants.
  * Verifies that all system error and connection messages match their expected values.
  */
  @Test
  public void testGeneralSystemConstants() {
    // System error messages
    assertEquals("Server Error", ApiConstants.SERVER_ERROR);
    assertEquals("Load Error", ApiConstants.LOAD_ERROR);
    assertEquals("Validation Error", ApiConstants.VALIDATION_ERROR);
    assertEquals("An unexpected error occurred - Please try again", 
        ApiConstants.UNEXPECTED_ERROR);
    assertEquals("Unexpected error", ApiConstants.LOG_UNEXPECTED_ERROR);
    
    // Connection and data messages
    assertEquals("Invalid request", ApiConstants.INVALID_REQUEST);
    assertEquals("Could not load data", ApiConstants.FAILED_TO_LOAD_DATA);
    assertEquals("No response from server", ApiConstants.NO_RESPONSE_FROM_SERVER);
    assertEquals("Could not connect to server - Please check that the server is running", 
        ApiConstants.SERVER_CONNECTION_ERROR);
    assertEquals("No valid button to get stage for scene switch", 
        ApiConstants.NO_VALID_BUTTON_FOR_SCENE_SWITCH);
  }
}