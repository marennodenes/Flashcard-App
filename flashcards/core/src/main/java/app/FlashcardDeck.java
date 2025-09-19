package app;

import java.util.ArrayList;
import java.util.List;


/**
 * Manages flashcards for the application.
 */
public class FlashcardDeck {
  
  /** List of all flashcards. */
  private final List<Flashcard> deck = new ArrayList<>();
  private String deckName;

  /**
   * Gets all flashcards as an observable list.
   * 
   * 
   * @return observable list of flashcards
   */
  public List<Flashcard> getDeck() { //Changed from ObservableList to list
      return new ArrayList<>(deck);
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
  public void addFlashcard(String question, String answer) {
      if (question != null && !question.isBlank() && answer != null && !answer.isBlank()) {
          deck.add(new Flashcard(question, answer));
      }
  }
}
