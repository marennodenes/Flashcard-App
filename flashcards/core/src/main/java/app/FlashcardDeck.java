package app;

import java.util.ArrayList;
import java.util.List;


/**
 * Manages flashcards for the application.
 */
public class FlashcardDeck {
  
  /** List of all flashcards. */
  @JsonProperty("deck")
  private final List<Flashcard> deck;

  @JsonProperty("deckName")
  private String deckName;

  public FlashcardDeck(){
    this.deck = new ArrayList<>();
  }

  public FlashcardDeck(String deckName){
    this.deckName = deckName;
    this.deck = new ArrayList<>();
  }

  /**
   * Gets all flashcards as an observable list.
   * 
   * 
   * @return observable list of flashcards
   */
  public List<Flashcard> getDeck() { //Changed from ObservableList to list
      return new ArrayList<>(deck);
  }

  public void setDeck(List<Deck> deck){
    this.deck = new ArrayList<>(deck);
  }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

  /**
   * Adds a new flashcard.
   * 
   * @param question the question text
   * @param answer the answer text
   */
  public void addFlashcard(Flashcard flashcard) {
        flashcard.setNumber(deck.size() + 1);
        deck.add(flashcard);
      
  }
}
