package shared;

public class ApiConstants {
  // User/Login/Signup
  public static final String USER_NOT_FOUND = "User not found.";
  public static final String USER_CREATED = "User created successfully.";
  public static final String USER_UPDATED = "User updated.";
  public static final String USER_ALREADY_EXISTS = "User already exists.";
  public static final String USER_RETRIEVED = "User retrieved successfully";
  public static final String USER_RETRIEVED_ERROR = "Error retrieving user: ";
  public static final String USER_EXSISTS = "User existence check successful";
  public static final String USER_EXSISTS_ERROR = "Error checking user existence: ";

  public static final String LOGIN_FAILED = "Login failed, invalid\nusername or password.";
  public static final String LOGIN_SUCCESS = "Login success.";
  public static final String LOGIN_RESPONSE = "Login response";
  public static final String LOGIN_RESPONSE_ERROR = "Error logging in user: ";
  public static final String INVALID_PASSWORD = "Invalid password";
  
  // Detailed password validation messages
  public static final String PASSWORD_TOO_SHORT = "Password must be at\nleast 8 characters long.";
  public static final String PASSWORD_MISSING_UPPERCASE = "Password must contain at\nleast one uppercase letter.";
  public static final String PASSWORD_MISSING_LOWERCASE = "Password must contain at\nleast one lowercase letter.";
  public static final String PASSWORD_MISSING_DIGIT = "Password must contain\nat least one digit.";
  public static final String PASSWORD_MISSING_SPECIAL = "Password must contain at\nleast one special character.";
  public static final String PASSWORD_VALIDATION_SUCCESSFUL = "Password validation successful";
  public static final String PASSWORD_VALIDATION_ERROR = "Error validating password: ";

  // Flashcard
  public static final String FLASHCARD_NOT_FOUND = "Flashcard not found.";
  public static final String FLASHCARD_CREATED = "Flashcard created successfully.";
  public static final String FLASHCARD_DELETED = "Flashcard deleted successfully.";
  public static final String FLASHCARD_DELETED_FAILED = "Failed to delete flashcard: ";
  public static final String FLASHCARD_FAILED = "Failed to create flashcard: ";
  public static final String FLASHCARD_RETRIEVED = "Flashcard retrieved successfully";
  public static final String FLASHCARDS_RETRIEVED = "Flashcards retrieved successfully";
  public static final String FLASHCARD_RETRIEVED_FAILED = "Failed to retrieve flashcard: ";
  public static final String FLASHCARDS_RETRIEVED_FAILED = "Failed to retrieve flashcards: ";


  // Deck
  public static final String DECK_NOT_FOUND = "Deck not found.";
  public static final String DECK_CREATED = "Deck created successfully.";
  public static final String DECK_DELETED = "Deck deleted successfully.";
  public static final String DECK_DELETED_ERROR = "Error deleting deck: ";
  public static final String DECK_RETRIEVED = "Deck retrieved successfully";
  public static final String DECKS_RETRIEVED = "Decks retrieved successfully";
  public static final String DECK_UPDATED = "Decks updated successfully";
  public static final String DECK_CREATED_ERROR = "Error creating deck: ";
  public static final String DECK_RETRIEVING_ERROR = "Error retrieving deck: ";
  public static final String DECKS_RETRIEVING_ERROR = "Error retrieving decks: ";
  public static final String DECK_UPDATED_ERROR = "Error updating decks: ";


  // General messages
  public static final String INVALID_REQUEST = "Invalid request.";
}
