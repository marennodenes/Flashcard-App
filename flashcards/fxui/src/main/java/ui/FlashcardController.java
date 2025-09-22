package ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class FlashcardController {

  @FXML private Button backButton;
  @FXML private Button nextCard;
  @FXML private Button previousCard;


  /**
   * Handles the event when the "Back" button is clicked.
   * Navigates from the current scene to the flashcard list page by loading
   * the FlashcardListUI.fxml file and switching the scene.
   * 
   * @throws IOException if the FXML file cannot be loaded or found
   * @author Claude (AI Assistant) - Javadoc documentation
   */
  @FXML
  private void whenBackButtonIsClicked() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardListUI.fxml"));
    Parent root = loader.load();
    Stage stage = (Stage) nextCard.getScene().getWindow();
    stage.setScene(new Scene(root));
    stage.show();
  }

  @FXML
  private void whenNextCardButtonClicked() {
    //go to next card when that is implemented
  }

  @FXML
  private void whenPreviousCardButtonClicked() {
    //go to previous card when that is implemented
  }
}
