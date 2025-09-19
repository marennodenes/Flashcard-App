package app;

import java.util.ArrayList;
import java.util.List;

public class FlashcardDeckManager {
  private List<FlashcardDeck> decks = new ArrayList<>();
  private static final int MAX_DECKS = 8;

  public List<FlashcardDeck> getDecks() {
    return new ArrayList<>(decks);
  }

  public void addDeck(FlashcardDeck deck) {
    if (deck == null) {
      throw new IllegalArgumentException("Deck cannot be null");
    }
    if (deck.getDeckName() == null || deck.getDeckName().trim().isEmpty()) {
      throw new IllegalArgumentException("Deck name cannot be empty");
    }
    if (decks.size() >= MAX_DECKS) {
      throw new IllegalArgumentException("You can only have up to " + MAX_DECKS + " decks");
    }
    for (FlashcardDeck flashcardDeck : decks) {
      if (flashcardDeck.getDeckName().equals(deck.getDeckName())){
        throw new IllegalArgumentException("Deck with this name already exists");
      }
    }
    decks.add(deck);
  }

  public void removeDeck(FlashcardDeck deck) {
    decks.remove(deck);
  }

}
