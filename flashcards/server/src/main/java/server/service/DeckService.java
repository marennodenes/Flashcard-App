package server.service;

import app.FlashcardDeck;
import app.FlashcardDeckManager;
import itp.storage.FlashcardPersistent;
import java.io.IOException;
import java.util.Objects;
import org.springframework.stereotype.Service;
import shared.ApiConstants;

/**
 * Service class responsible for managing flashcard deck operations.
 *
 * <p>Provides high-level CRUD operations for flashcard decks, handling validation
 * and persistence through the FlashcardPersistent storage layer. Acts as an
 * intermediary between business logic and data storage.
 *
 * <p>Operations:
 * - Retrieve all decks or specific decks by name
 * - Create and delete flashcard decks
 * - Validate user existence and deck operations
 *
 * <p>Throws IllegalArgumentException for business 
 * logic violations and IOException for storage errors.
 *
 * @author chrsom
 * @author isamw
 * @see FlashcardPersistent
 * @see FlashcardDeckManager
 * @see FlashcardDeck
 * @see ApiConstants
 */
@Service
public class DeckService {
  private final FlashcardPersistent flashcardPersistent;

  /**
   * Constructs a DeckService with a default FlashcardPersistent instance.
   */
  public DeckService() {
    this.flashcardPersistent = new FlashcardPersistent();
  }

  /**
   * Constructs a DeckService with the specified FlashcardPersistent instance.
   *
   * @param flashcardPersistent the FlashcardPersistent instance to use for
   *                            storage operations
   */
  public DeckService(FlashcardPersistent flashcardPersistent) {
    this.flashcardPersistent = Objects.requireNonNull(
        flashcardPersistent, "FlashcardPersistent cannot be null");
  }

  /**
   * Retrieves all flashcard decks for a given user.
   *
   * @param username the username of the user whose decks to retrieve
   * @return a FlashcardDeckManager containing all decks for the user
   * @throws IOException              if an error occurs while reading from
   *                                  persistent storage
   * @throws IllegalArgumentException if the specified user does not exist
   */
  public FlashcardDeckManager getAllDecks(String username) throws IOException {
    if (flashcardPersistent.userExists(username) == false) {
      throw new IllegalArgumentException(ApiConstants.USER_NOT_FOUND);
    } else {
      return flashcardPersistent.readDeck(username);
    }
  }

  /**
   * Retrieves a specific flashcard deck by name for a given user.
   *
   * @param username the username of the user who owns the deck
   * @param deckname the name of the deck to retrieve
   * @return the FlashcardDeck with the specified name
   * @throws IOException if an error occurs while reading from persistent storage
   * @throws IllegalArgumentException if the user does not exist or the deck is not found
   */
  public FlashcardDeck getDeck(String username, String deckname) throws IOException {
    return getAllDecks(username).getDecks()
          .stream()
          .filter(deck -> deck.getDeckName().equals(deckname)).findFirst()
          .orElseThrow(() -> new IllegalArgumentException(ApiConstants.DECK_NOT_FOUND));
  }

  /**
   * Creates a new flashcard deck for a user and persists it to storage.
   *
   * @param username the username of the user to create the deck for
   * @param deckName the name of the deck to create and add to the user's collection
   * @throws IOException if an error occurs while writing to persistent storage
   * @throws IllegalArgumentException if the user does not exist
   */
  public FlashcardDeck createDeck(String username, String deckName) 
      throws IOException {
    FlashcardDeck deck = new FlashcardDeck(deckName);
    FlashcardDeckManager deckManager = getAllDecks(username);
    deckManager.addDeck(deck);
    flashcardPersistent.writeDeck(username, deckManager);
    return deck;
  }

  /**
   * Deletes a specific flashcard deck for a user and updates persistent storage.
   *
   * @param username the username of the user who owns the deck
   * @param deckname the name of the deck to delete
   * @throws IOException if an error occurs while writing to persistent storage
   * @throws IllegalArgumentException if the user does not exist or the deck is not found
   */
  public void deleteDeck(String username, String deckname) throws IOException {
    FlashcardDeckManager manager = getAllDecks(username);
    FlashcardDeck deckToRemove = manager.getDecks().stream()
        .filter(deck -> deck.getDeckName().equals(deckname))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(ApiConstants.DECK_NOT_FOUND));
    manager.removeDeck(deckToRemove);
    flashcardPersistent.writeDeck(username, manager);
  }

  /**
   * Updates all decks for a user and persists them to storage.
   *
   * @param username the username of the user
   * @param deckManager the FlashcardDeckManager containing all decks to save
   * @throws IOException if an error occurs while writing to persistent storage
   * @throws IllegalArgumentException if the user does not exist
   */
  public void updateAllDecks(String username, FlashcardDeckManager deckManager) 
      throws IOException {
    if (!flashcardPersistent.userExists(username)) {
      throw new IllegalArgumentException(ApiConstants.USER_NOT_FOUND);
    }
    FlashcardDeckManager manager = new FlashcardDeckManager();
    manager.setDecks(deckManager.getDecks());
    flashcardPersistent.writeDeck(username, manager);
  }
}
