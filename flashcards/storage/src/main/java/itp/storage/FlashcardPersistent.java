package itp.storage;

import java.io.File;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.FlashcardDeckManager;

/**
 * Handles saving and loading flashcard decks to/from JSON files.
 */
public class FlashcardPersistent {

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
    File dataDir = new File("data");
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
    File file = new File("data", username + ".json");
    
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
    File file = new File(username + ".json");
    return file.exists();
  }
}