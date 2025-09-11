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

  @FXML public void initialize(){
    updateUI();
  }

  public void updateUI(){
    listView.setItems(manager.getFlashcards());
  }

  public void whenGenerateButtonClicked(){
    String q = questionField.getText().trim();
    String a = answerField.getText().trim();

    manager.addFlashcard(q, a);

    questionField.clear();
    answerField.clear();

    updateUI();
  }

}
