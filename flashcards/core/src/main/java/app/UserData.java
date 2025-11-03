package app;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * Combined data structure that holds both user credentials and flashcard data.
 * This prevents user credentials from being overwritten when flashcards are saved.
 * 
 * @author parts of this class is generated with the help of claude.ai
 * @author sofietw
 * @author ailinat
 */
public class UserData {
  @JsonUnwrapped private User user; 

  private FlashcardDeckManager deckManager;

  /**
   * Default constructor for JSON deserialization.
   */
  public UserData() {}

  /**
   * Constructor without deckManager, initializes with empty deck manager.
   * 
   * @param user the user object connected to the deck manager
   */
  public UserData(User user) {
    this(user, new FlashcardDeckManager());
  }

  /**
   * Constructor with User object and deck manager.
   * Used for programmatic creation (not JSON deserialization).
   * 
   * @param user the user object connected to the deck manager
   * @param deckManager the flashcard deck manager for the user
   */
  public UserData(User user, FlashcardDeckManager deckManager) {
    this.user = user;
    if (deckManager == null) {
      this.deckManager = new FlashcardDeckManager();
    } else {
      // Create defensive copy to avoid storing externally mutable object
      this.deckManager = new FlashcardDeckManager();
      for (FlashcardDeck deck : deckManager.getDecks()) {
        try {
          this.deckManager.addDeck(deck);
        } catch (IllegalArgumentException e) {
          // Skip invalid decks to prevent partial initialization
          // This ensures constructor completes successfully
          System.err.println("Skipping invalid deck during UserData construction: " + e.getMessage());
        }
      }
    }
  }

  /** 
   * Gets the user.
   * 
   * @return the user
   */
  public User getUser() {
    return user;
  }

  /**
   * Returns a copy of the deck manager to prevent external modification.
   * Creates a defensive copy to avoid exposing internal representation.
   * 
   * @return a copy of the FlashcardDeckManager
   */
  @JsonProperty("deckManager")
  public FlashcardDeckManager getDeckManager() {
    FlashcardDeckManager copy = new FlashcardDeckManager();
    for (FlashcardDeck deck : deckManager.getDecks()) {
      try {
        copy.addDeck(deck);
      } catch (IllegalArgumentException e) {
        // Skip invalid decks
        System.err.println("Skipping invalid deck during getDeckManager: " + e.getMessage());
      }
    }
    return copy;
  }

  /**
   * Sets the deck manager using a defensive copy.
   * Creates a copy to avoid storing externally mutable objects.
   * 
   * @param deckManager the FlashcardDeckManager to set
   */
  public void setDeckManager(FlashcardDeckManager deckManager) {
    if (deckManager == null) {
      this.deckManager = new FlashcardDeckManager();
    } else {
      this.deckManager = new FlashcardDeckManager();
      for (FlashcardDeck deck : deckManager.getDecks()) {
        try {
          this.deckManager.addDeck(deck);
        } catch (IllegalArgumentException e) {
          // Skip invalid decks
          System.err.println("Skipping invalid deck during setDeckManager: " + e.getMessage());
        }
      }
    }
  }
}