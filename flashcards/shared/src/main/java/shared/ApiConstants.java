package shared;

/**
 * Constants for API responses, error messages, and UI validation messages.
 * This class contains all constant strings used throughout the application
 * for consistent messaging between client and server components.
 * 
 * @author marieroe
 * @author isamw
 * 
 */
public class ApiConstants {

  /** User operation success messages */
  public static final String USER_CREATED = "User created successfully";
  public static final String USER_RETRIEVED = "User retrieved successfully";
  public static final String USER_EXISTS = "User existence check successful";
  
  /** User operation error messages */
  public static final String USER_NOT_FOUND = "User not found";
  public static final String USER_ALREADY_EXISTS = "User already exists";
  public static final String USER_CREATION_ERROR = "Error creating user";
  public static final String USER_RETRIEVED_ERROR = "Error retrieving user";
  public static final String USER_EXISTS_ERROR = "Error checking user existence";
  public static final String USER_OPERATION_FAILED = "Could not complete user operation\nPlease try again";

  /** Login success messages */
  public static final String LOGIN_SUCCESS = "Login success";
  public static final String LOGIN_RESPONSE = "Login response";
  
  /** Login error messages */
  public static final String LOGIN_FAILED = "Login failed, invalid\nusername or password";
  public static final String LOGIN_RESPONSE_ERROR = "Error logging in user";
  public static final String LOGIN_OPERATION_FAILED = "Login failed\nPlease check your credentials and try again";
  public static final String INVALID_PASSWORD = "Invalid password";

  /** Password validation success messages */
  public static final String PASSWORD_VALIDATION_SUCCESSFUL = "Password validation successful";
  
  /** Password validation error messages */
  public static final String PASSWORD_TOO_SHORT = "Password must be at\nleast 8 characters long";
  public static final String PASSWORD_MISSING_UPPERCASE = "Password must contain at\nleast one uppercase letter";
  public static final String PASSWORD_MISSING_LOWERCASE = "Password must contain at\nleast one lowercase letter";
  public static final String PASSWORD_MISSING_DIGIT = "Password must contain\nat least one digit";
  public static final String PASSWORD_MISSING_SPECIAL = "Password must contain at\nleast one special character";
  public static final String PASSWORD_VALIDATION_ERROR = "Error validating password";

  /** Flashcard operation success messages */
  public static final String FLASHCARD_CREATED = "Flashcard created successfully";
  public static final String FLASHCARD_DELETED = "Flashcard deleted successfully";
  public static final String FLASHCARD_RETRIEVED = "Flashcard retrieved successfully";
  public static final String FLASHCARDS_RETRIEVED = "Flashcards retrieved successfully";
  
  /** Flashcard operation error messages */
  public static final String FLASHCARD_NOT_FOUND = "Flashcard not found";
  public static final String FLASHCARD_FAILED = "Flashcard failed";
  public static final String FLASHCARD_FAILED_TO_DELETE = "Failed to delete flashcard";
  public static final String FLASHCARD_FAILED_TO_CREATE = "Failed to create flashcard";
  public static final String FLASHCARD_RETRIEVED_FAILED = "Failed to retrieve flashcard";
  public static final String FLASHCARDS_RETRIEVED_FAILED = "Failed to retrieve flashcards";
  public static final String FLASHCARD_OPERATION_FAILED = "Could not complete flashcard operation - Please try again";

  /** Deck operation success messages */
  public static final String DECK_CREATED = "Deck created successfully";
  public static final String DECK_DELETED = "Deck deleted successfully";
  public static final String DECK_RETRIEVED = "Deck retrieved successfully";
  public static final String DECK_UPDATED = "Decks updated successfully";
  public static final String DECKS_RETRIEVED = "Decks retrieved successfully";
  
  /** Deck operation error messages */
  public static final String DECK_NOT_FOUND = "Deck not found";
  public static final String DECK_ALREADY_EXISTS = "Deck name already exists";
  public static final String DECK_LIMIT_REACHED = "Max number of decks reached";
  public static final String DECK_FAILED_TO_CREATE = "Failed to create deck";
  public static final String DECK_FAILED_TO_DELETE = "Failed to delete deck";
  public static final String DECK_DELETED_ERROR = "Error deleting deck";
  public static final String DECK_CREATED_ERROR = "Error creating deck";
  public static final String DECK_RETRIEVING_ERROR = "Error retrieving deck";
  public static final String DECK_UPDATED_ERROR = "Error updating decks";
  public static final String DECKS_RETRIEVING_ERROR = "Error retrieving decks";
  public static final String DECK_OPERATION_FAILED = "Could not complete deck operation - Please try again";
  public static final String DECK_UPDATE_FAILED = "Could not update deck - Please try again";

  /** Client-side validation messages */
  public static final String EMPTY_FIELDS = "Username and password\ncannot be empty";
  public static final String PASSWORDS_NOT_EQUAL = "Passwords must be equal";
  public static final String DECK_NAME_EMPTY = "Deck name cannot be empty";
  public static final String FLASHCARD_QUESTION_ANSWER_EMPTY = "Flashcard question and answer cannot be empty";

  /** System error messages */
  public static final String SERVER_ERROR = "Server Error";
  public static final String LOAD_ERROR = "Load Error";
  public static final String VALIDATION_ERROR = "Validation Error";
  public static final String UNEXPECTED_ERROR = "An unexpected error occurred - Please try again";
  public static final String LOG_UNEXPECTED_ERROR = "Unexpected error";
  
  /** Connection and data messages */
  public static final String INVALID_REQUEST = "Invalid request";
  public static final String FAILED_TO_LOAD_DATA = "Could not load data";
  public static final String NO_RESPONSE_FROM_SERVER = "No response from server";
  public static final String SERVER_CONNECTION_ERROR = "Could not connect to server - Please check that the server is running";
  public static final String NO_VALID_BUTTON_FOR_SCENE_SWITCH = "No valid button to get stage for scene switch";
}