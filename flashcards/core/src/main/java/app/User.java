package app;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a user with a username and password.
 * @author @ailinat
 */
public class User {
    @JsonProperty("username")private String username;

    @JsonProperty("password") private String password;

    /**
     * Default constructor for JSON deserialization.
     */
    public User() {}

    /**
     * Private constructor to create a User with encoded password.
     * @param username
     * @param password
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;

        encodePassword();
    }

    /**
     * Gets the username of the user.
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password of the user.
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    private void encodePassword() {
        this.password = PasswordEncoder.encode(this.password);
    }
}
