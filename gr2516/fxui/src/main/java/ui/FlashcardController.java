package ui;

import app.Flashcard;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class FlashcardController {
  @FXML private TextField questionField;
  @FXML private TextField answerField;
  @FXML private ListView<Flashcard> listView;
  private final FlashcardManager manager = new FlashcardManager();

  /**
   * Sets up the UI when loaded.
   */
  @FXML public void initialize(){
    updateUI();
  }

  /**
   * Updates the flashcard list display.
   */
  public void updateUI(){
    listView.setItems(manager.getFlashcards());
  }

   /**
   * Adds a new flashcard when button is clicked.
   */
  public void whenGenerateButtonClicked(){
    String q = questionField.getText().trim();
    String a = answerField.getText().trim();

    manager.addFlashcard(q, a);

    questionField.clear();
    answerField.clear();

    updateUI();
  }

}
