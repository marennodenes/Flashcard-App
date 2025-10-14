package app;

/**
 * Represents a user with a username and password.
 * @author @ailinat
 */
public class User {
    private String username;
    private String password;

    /**
     * Constructor for User object with the specified username and password.
     * @param username the username of the user
     * @param password the password of the user
    */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
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
}
