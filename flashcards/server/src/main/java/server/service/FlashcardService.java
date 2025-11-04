package server.service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import app.Flashcard;
import app.FlashcardDeck;
import app.FlashcardDeckManager;
import itp.storage.FlashcardPersistent;
import shared.ApiConstants;

/**
 * Service class for managing flashcard operations including retrieval,
 * creation, and deletion. This service acts as an intermediary between the API
 * layer and the persistence layer, handling business logic for flashcard
 * operations within decks.
 * 
 * The service provides functionality to: - Retrieve individual flashcards by
 * position within a deck - Get all flashcards from a specific deck - Create new
 * flashcards and add them to existing decks - Delete flashcards from decks by
 * index
 * 
 * All operations require a username and deck name to identify the target deck,
 * and use FlashcardPersistent for data storage and DeckService for deck
 * management.
 * 
 * @author chrsom
 * @author isamw
 * @see FlashcardPersistent
 * @see DeckService
 * @see ApiConstants
 */
@Service
public class FlashcardService {

  private final FlashcardPersistent persistent;
  private final DeckService deckService;

  public FlashcardService() {
    this.persistent = new FlashcardPersistent();
    this.deckService = new DeckService();
  }

  public FlashcardService(FlashcardPersistent persistent, DeckService deckService) {
    this.persistent = Objects.requireNonNull(persistent, "FlashcardPersistent cannot be null");
    this.deckService = Objects.requireNonNull(deckService, "DeckService cannot be null");
  }

  /**
   * Retrieves a specific flashcard from a given deck for a user.
   * 
   * @param username the username of the user who owns the deck
   * @param deckname the name of the deck to retrieve the flashcard from
   * @param number   the index of the flashcard to retrieve
   * @return the Flashcard object at the specified index in the deck
   * @throws IOException              if an error occurs while reading from
   *                                  persistent storage
   * @throws IllegalArgumentException if the specified deck or flashcard is not
   *                                  found
   */
  public Flashcard getFlashcard(String username, String deckname, int number) throws IOException {
  
    if (this.persistent.readDeck(username).getDecks().isEmpty()) {
      throw new IllegalArgumentException(ApiConstants.FLASHCARD_NOT_FOUND);
    } else if (!this.persistent.readDeck(username).getDecks().stream().map(mapper -> mapper.getDeckName())
        .anyMatch(name -> name.equals(deckname))) {
      throw new IllegalArgumentException(ApiConstants.DECK_NOT_FOUND);
    } else if (number < 1 || number >= 8) {
      throw new IllegalArgumentException(ApiConstants.FLASHCARD_NOT_FOUND);
    } else if (this.deckService.getDeck(username, deckname).getDeck().size() < number) {
      throw new IllegalArgumentException(ApiConstants.FLASHCARD_NOT_FOUND);
    } else {
      return this.deckService.getAllDecks(username).getDecks().stream().filter(x -> x.getDeckName().equals(deckname))
          .findFirst().get().getDeck().get(number - 1);
    }
  }

  /**
   * Retrieves all flashcards from a specific deck for a given user.
   * 
   * @param username the username of the user who owns the deck
   * @param deckname the name of the deck to retrieve flashcards from
   * @return a list of all flashcards in the specified deck
   * @throws IOException              if an error occurs while reading from
   *                                  persistent storage
   * @throws IllegalArgumentException if the specified deck is not found for the
   *                                  user
   */
  public List<Flashcard> getAllFlashcards(String username, String deckname) throws IOException {
    return this.deckService.getDeck(username, deckname).getDeck();
  }

  /**
   * Creates a new flashcard and adds it to the specified deck for a given user.
   * The flashcard is immediately persisted to storage after being added to the
   * deck.
   * 
   * @param username the username of the user who owns the deck
   * @param deckname the name of the deck to add the flashcard to
   * @param answer   the answer text for the flashcard
   * @param question the question text for the flashcard
   * @return the newly created Flashcard object
   * @throws IOException              if an error occurs while writing the updated
   *                                  deck to persistent storage
   * @throws IllegalArgumentException if the specified deck is not found for the
   *                                  user
   */
  public Flashcard createFlashcard(String username, String deckname, String answer, String question)
      throws IOException {
    Flashcard flashcard = new Flashcard(question, answer);
    this.deckService.getDeck(username, deckname).addFlashcard(flashcard);

    this.persistent.writeDeck(username, this.deckService.getAllDecks(username));
    return flashcard;
  }

  /**
   * Deletes a flashcard from a specific deck for a given user.
   * 
   * @param username the username of the user who owns the deck
   * @param deckname the name of the deck containing the flashcard to delete
   * @param number   the index of the flashcard to remove from the deck
   * @throws IOException if an error occurs while writing the updated deck to
   *                     persistent storage
   */
  public void deleteFlashcard(String username, String deckname, int number) throws IOException {
    FlashcardDeckManager manager = this.deckService.getAllDecks(username);
    FlashcardDeck deck = this.deckService.getDeck(username, deckname);
    deck.removeFlashcardByIndex(number);

    this.persistent.writeDeck(username, manager);
  }
}
