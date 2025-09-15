package ui;

import app.Flashcard;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Manages a collection of flashcards.
 */
public class FlashcardManager {
  private final List<Flashcard> flashcards = new ArrayList<>();

  /**
   * Returns the list of flashcards as an observable list.
   *
   * @return an observable list of flashcards.
   */
  public ObservableList<Flashcard> getFlashcards() {
      return FXCollections.observableList(new ArrayList<>(flashcards));
  }

  /**
   * Adds a new flashcard with the given question and answer.
   *
   * @param question the question for the flashcard.
   * @param answer   the answer for the flashcard.
   */
  public void addFlashcard(String question, String answer) {
      if (question != null && !question.isBlank() && answer != null && !answer.isBlank()) {
          flashcards.add(new Flashcard(question, answer));
      }
  }
}
