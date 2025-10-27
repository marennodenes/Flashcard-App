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
public class ApiEndpoints {
  // Prevent instantiation
  private ApiEndpoints() {}

  // Server Configuration - Make configurable through properties
  public static final String SERVER_HOST = System.getProperty("server.host", "localhost");
  public static final String SERVER_PORT = System.getProperty("server.port", "8080");
  public static final String SERVER_BASE_URL = "http://" + SERVER_HOST + ":" + SERVER_PORT;

  // URL Paths
  public static final String BASE = "/api";
  public static final String USERS = BASE + "/users";
  public static final String LOGIN = BASE + "/v1/users/login";
  public static final String REGISTER = BASE + "/v1/users/register";
  
  // Flashcard endpoints
  public static final String FLASHCARDS = BASE + "/v1/flashcards";
  public static final String FLASHCARD_CREATE = "/create";
  public static final String FLASHCARD_GET = "/get";
  public static final String FLASHCARD_GET_ALL = "/get-all";
  public static final String FLASHCARD_DELETE = "/delete";

  // Deck endpoints
  public static final String DECKS = BASE + "/v1/decks";
  public static final String DECK_CREATE = "/create";
  public static final String DECK_GET = "/get";
  public static final String DECK_GET_ALL = "/get-all";
  public static final String DECK_DELETE = "/delete";

  // User endpoints
  public static final String USERS_V1 = BASE + "/v1/users"; // More consistent with other v1 endpoints
  public static final String USER_REGISTER = "/register";
  public static final String USER_LOGIN = "/login";
  public static final String USER_LOGOUT = "/logout";
  public static final String USER_VALIDATE_PASSWORD = "/validate-password";
  public static final String USER_PROFILE = "/profile";
  public static final String USER_FIND = "/find";
  
  // Complete API URLs - Built from existing constants
  public static final String LOGIN_URL = SERVER_BASE_URL + LOGIN;
  public static final String REGISTER_URL = SERVER_BASE_URL + REGISTER;
  
  /**
   * Gets the complete URL for user deck operations.
   * @param username the username
   * @return complete URL for user deck operations
   */
  public static String getUserDecksUrl(String username) {
    String fullUrl = SERVER_BASE_URL + DECKS + "?username=" + username;
    return fullUrl;
  }
}
