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
  public static final String LOGIN = BASE + "/auth/login";
  public static final String REGISTER = BASE + "/users/register";  // Used for user registration
  
  // Complete API URLs - Built from existing constants
  public static final String LOGIN_URL = SERVER_BASE_URL + LOGIN;
  public static final String REGISTER_URL = SERVER_BASE_URL + REGISTER;
  
  /**
   * Gets the complete URL for user deck operations.
   * @param username the username
   * @return complete URL for user deck operations
   */
  public static String getUserDecksUrl(String username) {
    return SERVER_BASE_URL + USERS + "/" + username + "/decks";
  }
}
