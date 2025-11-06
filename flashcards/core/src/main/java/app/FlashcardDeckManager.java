package app;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages a collection of flashcard decks.
 * Provides functionality to add, remove, and retrieve flashcard decks
 * with validation to ensure deck name uniqueness and enforce maximum deck limits.
 * 
 * @author isamw
 * @author chrsom
 * 
 */
public class FlashcardDeckManager {
  private List<FlashcardDeck> decks = new ArrayList<>();
  
  // Maximum number of decks allowed in the manager
  private static final int MAX_DECKS = 8;

  /**
   * Returns a copy of the list of all flashcard decks.
   * 
   * @return a new ArrayList containing all the flashcard decks
   * 
   */
  public List<FlashcardDeck> getDecks() {
    return new ArrayList<>(decks);
  }

  /**
   * Adds a new flashcard deck to the manager.
   * 
   * @param deck the flashcard deck to add
   * @throws IllegalArgumentException if deck is null, deck name is null/empty,
   *                                  maximum number of decks is reached, or
   *                                  a deck with the same name already exists
   * 
   */
  public void addDeck(FlashcardDeck deck) throws IllegalArgumentException {
    if (deck == null) {
      throw new IllegalArgumentException("Deck cannot be null");
    }
    if (deck.getDeckName() == null || deck.getDeckName().trim().isEmpty()) {
      throw new IllegalArgumentException("Deckname cannot be empty");
    }
    if (decks.size() >= MAX_DECKS) {
      throw new IllegalArgumentException("You can only have up to " + MAX_DECKS + " decks");
    }
    for (FlashcardDeck flashcardDeck : decks) {
      if (flashcardDeck.getDeckName().equals(deck.getDeckName())){
        throw new IllegalArgumentException("Deckname must be unique");
      }
    }
    decks.add(deck);
  }

  /**
   * Removes a flashcard deck from the manager.
   * 
   * @param deck the flashcard deck to remove
   * 
   */
  public void removeDeck(FlashcardDeck deck) {
    decks.remove(deck);
  }

  /**
   * Sets the list of decks in the manager, replacing any existing decks.
   * 
   * @param decks the new list of decks
   * 
   */
  public void setDecks(List<FlashcardDeck> decks) {
    this.decks = new ArrayList<>(decks);
  }

}
