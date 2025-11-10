package app;

import java.io.IOException;
import java.util.Objects;

/**
 * Validates user login and registration operations.
 * Uses dependency injection for persistence to avoid circular dependencies.
 * Handles user creation, authentication, and uniqueness checks.
 * Also manages password security and migration from plain text to hashed passwords.
 *
 * @author sofietw
 * @author ailinat
 */
public class LoginValidator {

  private final UserPersistence persistence;

  /**
   * Creates a new LoginValidator with the specified persistence implementation.
   *
   * @param persistence the persistence implementation to use
   */
  public LoginValidator(UserPersistence persistence) {
    this.persistence = Objects.requireNonNull(persistence, "persistence cannot be null");
  }

  /**
   * Creates a new user if the username is unique.
   * Returns true if the user was created successfully, false otherwise.
   *
   * @param username the username of the new user
   * @param password the password of the new user
   * @return true if user created, false if username exists or error occurred
   */
  public boolean createUser(String username, String password) {
    if (!persistence.userExists(username)) {
      User newUser = new User(username, password);
      try {
        persistence.writeUserData(newUser);
        return true;
      } catch (IOException e) {
        e.printStackTrace();
        return false;
      }
    }
    return false;
  }

  /** 
   * Checks if the given username is unique (not already taken).
   *
   * @param username the username to check
   * @return true if username is unique, false if it already exists
   */
  public boolean isUsernameUnique(String username) {
    return !persistence.userExists(username);
  }

  /**
   * Checks if the provided passwords match when a new user signs up.
   *
   * @param password the password to check
   * @param confirmedPassword the password confirmation to check
   * @return true if the passwords match, false otherwise
   */
  public boolean equalPasswords(String password, String confirmedPassword) {
    return password.equals(confirmedPassword); 
  }

  /**
   * Authenticates a user by username and password.
   * If the stored password is in plain text (legacy), an exception is thrown.
   * Otherwise, the password is verified using the matches() method from PasswordEncoder.
   *
   * @param username the username of the user
   * @param password the password of the user
   * @return true if authentication is successful, false otherwise
   * @throws IllegalStateException if legacy plain text passwords are used
   */
  public boolean authenticateUser(String username, String password) throws IllegalStateException {
    User user = findUserByUsername(username);
    if (user != null) {
      // Check if password is hashed (contains colon) or plain text (legacy)
      if (user.getPassword().contains(":")) {
        boolean matches = PasswordEncoder.matches(password, user.getPassword());
        return matches;
      } else {
        throw new IllegalStateException("Legacy plain text passwords are no longer supported.");
      }
    }
    return false;
  }

  /**
   * Finds and returns a User by username.
   *
   * @param username the username to search for
   * @return the User if found, null otherwise
   */
  public User findUserByUsername(String username) {
    if (persistence.userExists(username)) {
      return persistence.readUserData(username);
    }
    return null;
  }
}