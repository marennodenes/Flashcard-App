package app;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Manages flashcards for the application.
 */
@JsonPropertyOrder({"deckName", "flashcards"})
public class FlashcardDeck {
  
  @JsonProperty("deckName")
  private String deckName;

  /** List of all flashcards. */
  @JsonProperty("flashcards")
  private List<Flashcard> deck;

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

  public void setDeck(List<Flashcard> deck){
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

  public boolean removeFlashcardByIndex(int index) {
    if (index >= 0 && index < deck.size()) {
        deck.remove(index);
        // Oppdater nummerering for alle kort etter det slettede
        for (int i = index; i < deck.size(); i++) {
            deck.get(i).setNumber(i + 1);
        }
        return true;
    }
    return false;
  }

  /**
   * Checks if the text displayed on the card is a question or answer
   * 
   * @param current the question text to check
   * @return true if the question exists in the deck, false otherwise
   */
  public boolean isQuestion(String current){
    for (Flashcard card : deck) {
        if (card.getQuestion().equals(current)) {
            return true;
        }
    }
    return false;
  }
  
}
