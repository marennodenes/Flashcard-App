package ui;

import app.Flashcard;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FlashcardManager {
  private final ObservableList<Flashcard> flashcards = FXCollections.observableArrayList();

  public ObservableList<Flashcard> getFlashcards() {
      return flashcards;
  }

  public void addFlashcard(String question, String answer) {
      if (question != null && !question.isBlank() && answer != null && !answer.isBlank()) {
          flashcards.add(new Flashcard(question, answer));
      }
  }
}
