package app;

import java.io.IOException;
import java.util.Objects;

/**
 * Validates user login and registration operations.
 * Uses dependency injection for persistence to avoid circular dependencies.
 */
public class LoginValidator {

  //when supresswarnings got added here it runs, but i am not sure that its the best solution..., so maybe go back and change this
    @SuppressWarnings("EI_EXPOSE_REP2")
    private final UserPersistence persistence;

    /**
     * Creates a new LoginValidator with the specified persistence implementation.
     * @param persistence the persistence implementation to use
     */
    @SuppressWarnings("CT_CONSTRUCTOR_THROW")
    public LoginValidator(UserPersistence persistence) {
        this.persistence = Objects.requireNonNull(persistence, "UserPersistence cannot be null");
    }


    public boolean createUser(String username, String password) {
        if (!persistence.userExists(username)) {
            User newUser = new User(username, password); 
            try {
                persistence.writeUserData(newUser);
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

    public boolean isUsernameUnique(String username) {
        return !persistence.userExists(username);
    }

    //adding password security here:
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
                    // Optionally upgrade to hashed password
                    try {
                        User updatedUser = new User(username, password);
                        persistence.writeUserData(updatedUser);
                        System.out.println("Password upgraded to hashed format for: " + username);
                    } catch (IOException e) {
                        System.err.println("Failed to upgrade password for: " + username);
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

    public User findUserByUsername(String username) {
        if (persistence.userExists(username)) {
            return persistence.readUserData(username);
        }
        return null;
    }
}
