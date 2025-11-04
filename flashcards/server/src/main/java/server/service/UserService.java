package server.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import app.LoginValidator;
import app.User;
import itp.storage.FlashcardPersistent;
import shared.ApiConstants;


/**
 * Service class for managing user operations including retrieval,
 * creation, existence check, login, and password validation.
 * 
 * This service acts as an intermediary between the API layer and the
 * persistence layer, handling business logic for user-related operations.
 * 
 * The service provides functionality to:
 *   Retrieve user information by username
 *   Create new users with validation
 * 
 * Check if a user exists
 *   Log in users by validating credentials
 * 
 * Validate user passwords
 * 
 * All operations utilize FlashcardPersistent for data storage.
 * 
 * @author chrsom
 * @author isamw
 * @see FlashcardPersistent
 * @see ApiConstants
 */
@Service
public class UserService {

  private final FlashcardPersistent persistent;
  
  /**
   * Default constructor initializing with a new FlashcardPersistent instance.
   */
  public UserService() {
    this.persistent = new FlashcardPersistent();
  }

  /**
   * Retrieves user information for the specified username.
   * 
   * @param username the username of the user to retrieve
   * @return the User object associated with the given username
   * @throws IllegalArgumentException if the user does not exist
   */
  public User getUser(String username) {
    if (!persistent.userExists(username) || username.isEmpty()) {
      throw new IllegalArgumentException(ApiConstants.USER_NOT_FOUND);
    }

    return persistent.readUserData(username);
  }

  /**
   * Checks if a user exists with the given username.
   * 
   * @param username the username to check
   * @return true if the user exists, false otherwise
   * @throws IllegalArgumentException if the username is empty
   */
  public boolean userExists(String username) {
    if (username.isEmpty()) {
      throw new IllegalArgumentException(ApiConstants.INVALID_REQUEST);
    }
    return persistent.userExists(username);
  }

  /**
   * Creates a new user with the specified username and password.
   * 
   * @param username
   * @param password
   * @return the newly created User object
   * @throws IOException if an error occurs while writing user data
   * @throws IllegalArgumentException if the username or password is empty
   * @throws IllegalArgumentException if the user already exists
   * @throws IllegalArgumentException if the password is invalid
   */
  public User createUser(String username, String password) throws IOException { 
    LoginValidator validator = new LoginValidator(persistent);

    if (username.isEmpty() || password.isEmpty()) {
      throw new IllegalArgumentException(ApiConstants.INVALID_REQUEST);
    }

    if (!validator.isUsernameUnique(username)) {
      throw new IllegalArgumentException(ApiConstants.USER_ALREADY_EXISTS);
    }

    if (!isValidPassword(password)) {
      throw new IllegalArgumentException(ApiConstants.INVALID_PASSWORD);
    }

    User newUser = new User(username, password);
    persistent.writeUserData(newUser);
    return newUser;
  }

  /**
   * Creates a new user with detailed validation and error handling.
   * 
   * @param username the username for the new user
   * @param password the password for the new user
   * @return the created User object if successful, null if failed
   * @throws IllegalArgumentException with detailed error message if validation fails
   */
  public User createUserWithValidation(String username, String password) throws IllegalArgumentException {
    try {
      LoginValidator validator = new LoginValidator(persistent);

      if (username.isEmpty() || password.isEmpty()) {
        throw new IllegalArgumentException(ApiConstants.INVALID_REQUEST);
      }

      if (!validator.isUsernameUnique(username)) {
        throw new IllegalArgumentException(ApiConstants.USER_ALREADY_EXISTS);
      }

      String passwordError = validatePasswordDetailed(password);
      if (passwordError != null) {
        throw new IllegalArgumentException(passwordError);
      }

      User newUser = new User(username, password);
      persistent.writeUserData(newUser);
      return newUser;
      
    } catch (IOException e) {
      throw new IllegalArgumentException("Failed to save user data: " + e.getMessage());
    }
  }

  /**
   * Used to log in a user with the specified username and password.
   * Checks for empty fields and user existence before validating the password.
   * 
   * @param username the username of the user attempting to log in
   * @param password the password provided for login
   * @return true if login is successful, false otherwise
   * @throws IllegalArgumentException if the username is empty during user existence check
   */
  public boolean logInUser(String username, String password) {
    if (username.isEmpty() || password.isEmpty()) {
      return false;
    } else if (!userExists(username)) {
      return false;
    }

    return validatePassword(username, password);
  }

  /**
   * Validates the password for the given username.
   * 
   * @param username the username of the user
   * @param password the password to validate
   * @return true if the password is valid, false otherwise
   */
  public boolean validatePassword(String username, String password) {
    if (username.isEmpty() || password.isEmpty()) {
      return false;
    }
    return isValidPassword(password);
  }

  /**
   * Checks if the given password meets the security requirements.
   * 
   * @param password the password to check
   * @return true if the password is valid, false otherwise
   */
  public boolean isValidPassword(String password) {
    return validatePasswordDetailed(password) == null;
  }

  /**
   * Validates a password and returns an error message if invalid.
   * Returns the first validation error found.
   * 
   * @param password the password to validate
   * @return null if valid, or error message string if invalid
   */
  public String validatePasswordDetailed(String password) {
    if (password == null) {
      return ApiConstants.INVALID_PASSWORD;
    }
    
    if (password.length() < 8) {
      return ApiConstants.PASSWORD_TOO_SHORT;
    }
    
    if (!password.matches(".*[A-Z].*")) {
      return ApiConstants.PASSWORD_MISSING_UPPERCASE;
    }
    
    if (!password.matches(".*[a-z].*")) {
      return ApiConstants.PASSWORD_MISSING_LOWERCASE;
    }
    
    if (!password.matches(".*\\d.*")) {
      return ApiConstants.PASSWORD_MISSING_DIGIT;
    }
    
    if (!password.matches(".*[^a-zA-Z0-9].*")) {
      return ApiConstants.PASSWORD_MISSING_SPECIAL;
    }
    
    return null; // Password is valid
  }
}
