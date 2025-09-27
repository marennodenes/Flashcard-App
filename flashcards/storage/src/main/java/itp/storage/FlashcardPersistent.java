package itp.storage;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import app.User;
import app.UserPersistence;
import app.FlashcardDeckManager;

/**
 * Handles saving and loading flashcard decks to/from JSON files.
 */
public class FlashcardPersistent implements UserPersistence {

  private final ObjectMapper objectMapper;

  public FlashcardPersistent() {
    this.objectMapper = new ObjectMapper();
  }

  /**
   * Saves flashcard deck manager to JSON file.
   * 
   * @param username the username for the filename
   * @param deckManager the deck manager to save
   * @throws IOException if saving fails
   */
  public void writeDeck(String username, FlashcardDeckManager deckManager) throws IOException {
    // Create data directory if it doesn't exist
    File dataDir = new File(System.getProperty("user.dir") + "/../storage/data/users");
    if (!dataDir.exists()) {
      boolean created = dataDir.mkdirs();
      if (!created) {
        throw new IOException("Failed to create data directory: " + dataDir.getAbsolutePath());
      }
    }
    
    File file = new File(dataDir, username + ".json");
    objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, deckManager);
  }

  /**
   * Loads flashcard deck manager from JSON file.
   * 
   * @param username the username for the filename
   * @return the deck manager, or new empty one if file doesn't exist
   * @throws IOException if loading fails
   */
  public FlashcardDeckManager readDeck(String username) throws IOException {
    
    File file = new File(System.getProperty("user.dir") + "/../storage/data/users", username + ".json");    
    if (file.exists()) {
      return objectMapper.readValue(file, FlashcardDeckManager.class);
    } else {
      return new FlashcardDeckManager(); // Return empty for new setup
    }
  }
  
  /**
   * Checks if flashcards file exists for a user.
   * 
   * @param username the username to check
   * @return true if file exists
   */
  public boolean dataExists(String username) {
    File file = new File(System.getProperty("user.dir") + "/../storage/data/users", username + ".json");
    return file.exists();
  }


  public User readUserData(String username) {
    File file = new File(System.getProperty("user.dir") + "/../storage/data/users", username + ".json");    
    if (file.exists()) {
      try {
        // First try to read as User object (new format)
        return objectMapper.readValue(file, User.class);
      } catch (Exception e) {
        // If that fails, it's probably an old format (FlashcardDeckManager only)
        // Return null to indicate no user credentials found
        System.out.println("File exists but doesn't contain user credentials: " + username);
        return null;
      }
    } 
    
    return null; // No user data found
  }

  public void writeUserData(User user) throws IOException {
    // Create data directory if it doesn't exist
    File dataDir = new File(System.getProperty("user.dir") + "/../storage/data/users");
    if (!dataDir.exists()) {
      boolean created = dataDir.mkdirs();
      if (!created) {
        throw new IOException("Failed to create data directory: " + dataDir.getAbsolutePath());
      }
    }
    
    File file = new File(dataDir, user.getUsername() + ".json");
    objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, user);
  
  }

  public boolean userExists(String username) {
    // Check if user credentials exist, not just if file exists
    User user = readUserData(username);
    return user != null;
  }
}