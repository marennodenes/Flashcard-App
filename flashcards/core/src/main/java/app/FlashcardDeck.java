package app;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Manages flashcards for the application.
 * A flashcard deck contains a name and a list of flashcards.
 * Each flashcard has a question and an answer.
 * 
 * @author isamw
 * @author chrsom
 */
@JsonPropertyOrder({"deckName", "flashcards"})
public class FlashcardDeck {
  
  @JsonProperty("deckName")
  private String deckName;

  // List of all flashcards in this deck.
  @JsonProperty("flashcards")
  private List<Flashcard> deck;

  /**
   * Default constructor that creates an empty deck.
   */
  public FlashcardDeck(){
    this.deck = new ArrayList<>();
  }

  /**
   * Constructor that creates a deck with a specified name.
   * 
   * @param deckName the name of the deck
   */
  public FlashcardDeck(String deckName){
    this.deckName = deckName;
    this.deck = new ArrayList<>();
  }

  /**
   * Gets all flashcards as a list.
   *
   * @return list of flashcards
   */
  public List<Flashcard> getDeck() {
    return new ArrayList<>(deck);
  }

  /**
   * Sets the flashcards in the deck.
   *
   * @param deck list of flashcards to set
   */
  public void setDeck(List<Flashcard> deck){
    this.deck = new ArrayList<>(deck);
  }

  /**
   * Gets the name of the deck.
   * 
   * @return the deck name
   */
  public String getDeckName() {
    return deckName;
  }

  /**
   * Sets the name of the deck.
   * 
   * @param deckName the name to set for the deck
   */
  public void setDeckName(String deckName) {
    this.deckName = deckName;
  }

  /**
   * Adds a new flashcard to the deck
   * 
   * @param question the question text
   * @param answer the answer text
   */
  public void addFlashcard(Flashcard flashcard) {
    if (flashcard != null) {
      flashcard.setNumber(deck.size() + 1);
    }

    deck.add(flashcard);
  }

  /**
   * Removes a flashcard from the deck at the specified index.
   * After removal, all subsequent flashcards have their numbers updated
   * to maintain sequential numbering.
   * 
   * @param index the index of the flashcard to remove (0-based)
   * @return true if the flashcard was successfully removed, false if index is invalid
   */
  public boolean removeFlashcardByIndex(int index) {
    if (index >= 0 && index < deck.size()) {
        deck.remove(index);
        //Updates numbers
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
   * @return true if the question exists, false otherwise
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
