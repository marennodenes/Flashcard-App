package ui;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class FlashcardController {
  @FXML private TextField questionField;
  @FXML private TextField answerField;
  @FXML private ListView listView;

  @FXML public void initialize(){
    updateUI();
  }

  public void updateUI(){
    //set the flashcard in to the list
  }

}
