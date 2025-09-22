package ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.Flashcard;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Controller class for managing flashcard UI interactions.
 * Handles navigation between flashcards, displaying questions and answers,
 * and managing the flashcard deck state.
 */
public class FlashcardController {

  @FXML private Button backButton;
  @FXML private Button nextCard;
  @FXML private Button previousCard;
  @FXML private Button card;

  private List<Flashcard> deck = new ArrayList<>();
  //private HashMap<String, FlashcardDeck> allDecks;

  private int currentCardI;


  public void initialize(){
    //mocked cards and deck for implementing the methods before file reading is working corectly:
    Flashcard tryCard = new Flashcard("Qusestion", "answer");
    Flashcard tryCard1 = new Flashcard("Qusestion1", "answer1");
    Flashcard tryCard2 = new Flashcard("Qusestion2", "answer2");
    Flashcard tryCard3 = new Flashcard("Qusestion3", "answer3");
    Flashcard tryCard4 = new Flashcard("Qusestion4", "answer4");
    Flashcard tryCard5 = new Flashcard("Qusestion5", "answer5");
    deck.add(tryCard);
    deck.add(tryCard1);
    deck.add(tryCard2);
    deck.add(tryCard3);
    deck.add(tryCard4);
    deck.add(tryCard5);
    if (card != null) {
      card.setText(deck.get(0).getQuestion());
    }
    currentCardI = 0;
    updateUi();
  }


  /**
   * Updates the UI by setting the card text to display the current flashcard's question.
   * Only updates if the card button exists, the deck is not empty, and the current card index is valid.
   * 
   * Github Copilot Claude Sonnet 4
   */
  public void updateUi() {
    if (card != null && !deck.isEmpty() && currentCardI >= 0 && currentCardI < deck.size()) {
      card.setText(deck.get(currentCardI).getQuestion());
    }
  }


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
    
    if (currentCardI < deck.size()) {
      currentCardI += 1;
      updateUi();
    } 

    if (currentCardI == deck.size()) {
      currentCardI = 0;
      updateUi();
    }
  }

  @FXML
  private void whenPreviousCardButtonClicked() {
    //go to previous card when that is implemented
    if (currentCardI > 0) {
      currentCardI -= 1;
      updateUi();
    } 

    if (currentCardI == 0) {
      currentCardI = 0;
      updateUi();
    }
  }

  @FXML
  private void whenCardButtonClicked(){
    if (card.getText().equals(deck.get(currentCardI).getQuestion())) {
      card.setText(deck.get(currentCardI).getAnswer());
    } else {
      card.setText(deck.get(currentCardI).getQuestion());
    }
  }
  
}
