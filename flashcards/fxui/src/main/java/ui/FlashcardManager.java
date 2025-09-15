package ui;

import app.Flashcard;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FlashcardManager {
  private final List<Flashcard> flashcards = new ArrayList<>();

  public ObservableList<Flashcard> getFlashcards() {
      return FXCollections.observableList(new ArrayList<>(flashcards));
  }

  public void addFlashcard(String question, String answer) {
      if (question != null && !question.isBlank() && answer != null && !answer.isBlank()) {
          flashcards.add(new Flashcard(question, answer));
      }
  }
}
