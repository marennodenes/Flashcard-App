package app;

import java.io.IOException;

/**
 * Interface for user data persistence operations.
 * This allows LoginValidator to use storage without direct dependencies.
 * @author sofietw
 * @author ailinat
 * 
 */
public interface UserPersistence {
  
  /**
   * Checks if a user exists in storage.
   * 
   * @param username the username to check
   * @return true if user exists, false otherwise
   * 
   */
  boolean userExists(String username);
  
  /**
   * Reads user data from storage.
   * 
   * @param username the username to read
   * @return User object or null if not found
   * 
   */
  User readUserData(String username);
  
  /**
   * Writes user data to storage.
   * 
   * @param user the user data to write
   * @throws IOException if writing fails
   * 
   */
  void writeUserData(User user) throws IOException;
}