package app;

import java.io.IOException;
import java.util.Objects;

/**
 * Validates user login and registration operations.
 * Uses dependency injection for persistence to avoid circular dependencies.
 * Handles user creation, authentication, and uniqueness checks.
 * Also manages password security and migration from plain text to hashed passwords.
 * @author parts of class is generated with the help of claude.ai
 * @author @sofietw
 * @author @ailinat
 */
public class LoginValidator {

    private final UserPersistence PERSISTENCE;

    /**
     * Creates a new LoginValidator with the specified persistence implementation.
     * @param persistence the persistence implementation to use
     */
    public LoginValidator(UserPersistence persistence) {
        this.PERSISTENCE = Objects.requireNonNull(persistence, "persistence cannot be null");
    }

    /**
    Creates a new user if the username is unique.
    * Returns true if the user was created successfully, false otherwise.
    * @param username the username of the new user
    * @param password the password of the new user
    * @return true if user created, false if username exists or error occurred
    */
    public boolean createUser(String username, String password) {
        if (!PERSISTENCE.userExists(username)) {
            User newUser = new User(username, password); 
            try {
                PERSISTENCE.writeUserData(newUser);
                System.out.println("User created successfully: " + username);
                return true;
            } catch (IOException e) {
              System.err.println("Failed to create user: " + username);
                e.printStackTrace();
                return false;
            }
        }
        System.out.println("User already exists: " + username);
        return false;
    }

    /** 
     * Checks if the given username is unique (not already taken).
     * @param username the username to check
     * @return true if username is unique, false if it already exists
    */
    public boolean isUsernameUnique(String username) {
        return !PERSISTENCE.userExists(username);
    }

    /**
     * Checks if the provided passwords match.
     * @param password the password to check
     * @param confirmedPassword the password confirmation to check
     * @return true if the passwords match, false otherwise
     */
    public boolean equalPasswords(String password, String confirmedPassword) {
        return password.equals(confirmedPassword); 
    }

    /**
     * Authenticates a user by username and password.
     * Supports both hashed passwords and legacy plain text passwords.
     * If a legacy password is used, it upgrades to a hashed password.
     * @param username the username of the user
     * @param password the password of the user
     * @return true if authentication is successful, false otherwise
     */
    public boolean authenticateUser(String username, String password) {
        User user = findUserByUsername(username);
        if (user != null) {
            // Check if password is hashed (contains colon) or plain text (legacy)
            if (user.getPassword().contains(":")) {
                // New format: hashed password
                boolean matches = PasswordEncoder.matches(password, user.getPassword());
                if (matches) {
                    System.out.println("User authenticated successfully: " + username);
                } else {
                    System.out.println("Authentication failed - wrong password: " + username);
                }
                return matches;
            } else {
                // Legacy format: plain text password
                boolean matches = user.getPassword().equals(password);
                if (matches) {
                    System.out.println("User authenticated with legacy password: " + username);
                    // Upgrades to hashed password
                    try {
                        User updatedUser = new User(username, password);
                        PERSISTENCE.writeUserData(updatedUser);
                        System.out.println("Password encode for: " + username);
                    } catch (IOException e) {
                        System.err.println("Failed to encode password for: " + username);
                    }
                } else {
                    System.out.println("Authentication failed - wrong legacy password: " + username);
                }
                return matches;
            }
        }
        System.out.println("Authentication failed - user not found: " + username);
        return false;
    }

    /**
     * Finds and returns a User by username.
     * @param username the username to search for
     * @return the User if found, null otherwise
     */
    public User findUserByUsername(String username) {
        if (PERSISTENCE.userExists(username)) {
            return PERSISTENCE.readUserData(username);
        }
        return null;
    }
}
