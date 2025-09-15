package ui;

import app.Flashcard;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Manages flashcards for the application.
 */
public class FlashcardManager {
  
  /** List of all flashcards. */
  private final List<Flashcard> flashcards = new ArrayList<>();

  /**
   * Gets all flashcards as an observable list.
   * 
   * @return observable list of flashcards
   */
  public ObservableList<Flashcard> getFlashcards() {
      return FXCollections.observableList(new ArrayList<>(flashcards));
  }

  /**
   * Adds a new flashcard.
   * 
   * @param question the question text
   * @param answer the answer text
   */
  public void addFlashcard(String question, String answer) {
      if (question != null && !question.isBlank() && answer != null && !answer.isBlank()) {
          flashcards.add(new Flashcard(question, answer));
      }
  }
}
