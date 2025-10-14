package shared;

/**
 * Constants class containing API-related string literals and endpoint URLs.
 * This class provides centralized storage for commonly used messages, error responses,
 * and API endpoint paths used throughout the flashcard application.
 * 
 * This class cannot be instantiated as it only contains static constants.
 * 
 * @author isamw
 * @author marieroe
 */
public class ApiConstants {
  // Prevent instantiation
  private ApiConstants() {}

  // User/Login/Signup
  public static final String USER_NOT_FOUND = "User not found.";
  public static final String USER_CREATED = "User created successfully.";
  public static final String USER_UPDATED = "User updated.";
  public static final String USER_ALREADY_EXISTS = "User already exists.";
  public static final String LOGIN_FAILED = "Login failed, invalid username or password.";
  public static final String LOGIN_SUCCESS = "Login success.";
  public static final String PASSWORD_INVALID = "Password does not meet requirements.";

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
  public static final String OPERATION_FAILED = "Operation failed.";

  // URL
  public static final String BASE = "/api";
  public static final String USERS = BASE + "/users";
  public static final String SIGNUP = BASE + "/signup";
  public static final String LOGIN = BASE + "/login";
  public static final String DECKS = BASE + "/decks";
  public static final String DECK = BASE + "/deck";
}
