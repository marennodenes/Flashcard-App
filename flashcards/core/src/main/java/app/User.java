package app;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a user with a username and password.
 * Handles password encoding upon creation.
 *
 * @author ailinat
 * @author sofietw
 */
public class User {
  @JsonProperty("username") private String username;

  @JsonProperty("password") private String password;

  /**
   * Default constructor for JSON deserialization.
   */
  public User() {}

  /**
   * Constructor to create a User with encoded password.
   *
   * @param username the username
   * @param password the password
   */
  public User(String username, String password) {
    this.username = username;
    this.password = password;

    encodePassword();
  }

  /**
   * Gets the username of the user.
   *
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * Gets the password of the user.
   *
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Encodes the user's password using PasswordEncoder.
   * This method is called in the constructor to ensure the password is encoded upon user creation.
   */
  private void encodePassword() {
    this.password = PasswordEncoder.encode(this.password);
  }
}
