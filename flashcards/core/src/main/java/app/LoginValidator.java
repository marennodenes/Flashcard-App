package app;

import java.io.IOException;

/**
 * Validates user login and registration operations.
 * Uses dependency injection for persistence to avoid circular dependencies.
 */
public class LoginValidator {

    private final UserPersistence persistence;
    
    /**
     * Creates a new LoginValidator with the specified persistence implementation.
     * @param persistence the persistence implementation to use
     */
    public LoginValidator(UserPersistence persistence) {
        if (persistence == null) {
            throw new IllegalArgumentException("UserPersistence cannot be null");
        }
        this.persistence = persistence;
    }

    public boolean createUser(String username, String password) {
      // Create a new user with the given username and password
      if (!persistence.userExists(username)) {
        User newUser = new User(username, password); 
        try {
          persistence.writeUserData(newUser);
          return true; // User created successfully
        } catch (IOException e) {
          e.printStackTrace();
          return false; // Failed to create user due to IO error
        }
      }

      return false; // User already exists
    }

    public boolean isUsernameUnique(String username) {
      // Check if the username is unique by checking if file exists
      return !persistence.userExists(username);
    }

    public boolean authenticateUser(String username, String password) {
      // Authenticate user by checking username and password
      User user = findUserByUsername(username);
      if (user == null) {
        return false; // User not found
      }
      return user.getPassword().equals(password); 
    }

    public User findUserByUsername(String username) {
      // Find user from persistent storage
      if (persistence.userExists(username)) {
        User user = persistence.readUserData(username);
        if (user != null) {
          return user;
        }
      }

      return null;
    }

}