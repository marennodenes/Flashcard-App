package shared;

public class ApiConstants {
  // User/Login/Signup
  public static final String USER_NOT_FOUND = "User not found.";
  public static final String USER_CREATED = "User created successfully.";
  public static final String USER_UPDATED = "User updated.";
  public static final String USER_ALREADY_EXISTS = "User already exists.";
  public static final String LOGIN_FAILED = "Login failed, invalid\nusername or password.";
  public static final String LOGIN_SUCCESS = "Login success.";
  public static final String INVALID_PASSWORD = "Invalid password";
  
  // Detailed password validation messages
  public static final String PASSWORD_TOO_SHORT = "Password must be at\nleast 8 characters long.";
  public static final String PASSWORD_MISSING_UPPERCASE = "Password must contain at\nleast one uppercase letter.";
  public static final String PASSWORD_MISSING_LOWERCASE = "Password must contain at\nleast one lowercase letter.";
  public static final String PASSWORD_MISSING_DIGIT = "Password must contain\nat least one digit.";
  public static final String PASSWORD_MISSING_SPECIAL = "Password must contain at\nleast one special character.";

  // Flashcard
  public static final String FLASHCARD_NOT_FOUND = "Flashcard not found.";
  public static final String FLASHCARD_CREATED = "Flashcard created successfully.";
  public static final String FLASHCARD_DELETED = "Flashcard deleted successfully.";

  // Deck
  public static final String DECK_NOT_FOUND = "Deck not found.";
  public static final String DECK_CREATED = "Deck created successfully.";
  public static final String DECK_DELETED = "Deck deleted successfully.";

  // General messages
  public static final String INVALID_REQUEST = "Invalid request.";
}
