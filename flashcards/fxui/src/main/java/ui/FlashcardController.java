package ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.Flashcard;
import javafx.animation.RotateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import app.FlashcardDeck;

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
  @FXML private ProgressBar progressBar;
  @FXML private Text usernameField;
  @FXML private Text decknameField;
  @FXML private Text cardNumber;

  
  private List<Flashcard> deck = new ArrayList<>();
  //private HashMap<String, FlashcardDeck> allDecks;

  private int currentCardI;
  private String currentUsername = "defaultUserName";

  private String questionStyle = """
            -fx-background-color: #89b9bf; /* spørsmål default */
            -fx-effect: dropshadow(gaussian, #5c7b80, 0, 1, 5, 5);
            -fx-border-width: 2;
            -fx-background-radius: 3;
            -fx-font-weight: bold;
        """;
  
  private String answerStyle = """
                -fx-background-color: #5c7b80;
                -fx-effect: dropshadow(gaussian, #89b9bf, 0, 1, 5, 5);
                -fx-border-width: 2;
                -fx-background-radius: 3;
                -fx-font-weight: bold;
            """;

  private boolean isShowingAnswer = false;

  private FlashcardDeck originalDeck;

  public void setDeck(FlashcardDeck deck) {
    // bug fix for spotbugs
    // this.originalDeck = deck; - old
    this.originalDeck = new FlashcardDeck(deck.getDeckName());
    for (Flashcard card : deck.getDeck()) {
        this.originalDeck.addFlashcard(new Flashcard(card.getQuestion(), card.getAnswer()));
    }
    
    this.deck = new ArrayList<>(deck.getDeck()); // Copy flashcards from the deck
    currentCardI = 0;
    updateUi();

    updateProgress();
}


  public void initialize(){
    currentCardI = 0;
    /* updateUi(); */
    if (!deck.isEmpty()) {
      updateUi();
    }
  }


  /**
   * Updates the UI by setting the card text to display the current flashcard's question.
   * Only updates if the card button exists, the deck is not empty, and the current card index is valid.
   * 
   * Github Copilot Claude Sonnet 4
   */
  public void updateUi() {
    isShowingAnswer = false;
    decknameField.setText(originalDeck.getDeckName());
    usernameField.setText(currentUsername);
    if (card != null && !deck.isEmpty() && currentCardI >= 0 && currentCardI < deck.size()) {
      card.setText(deck.get(currentCardI).getQuestion());
      card.setStyle(questionStyle);
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

    FlashcardDeckController controller = loader.getController();
    if(originalDeck!= null){
      controller.setDeck(originalDeck);

    }

    Stage stage = (Stage) nextCard.getScene().getWindow();
    stage.setScene(new Scene(root));
    stage.show();
  }



  @FXML
  private void whenNextCardButtonClicked() {
    
   if (!deck.isEmpty()) {
      currentCardI = (currentCardI + 1) % deck.size(); // making it a loop
      updateUi();
    }

    updateProgress();

  }

  @FXML
  private void whenPreviousCardButtonClicked() {
    
    if (!deck.isEmpty()) {
      currentCardI = (currentCardI - 1 + deck.size()) % deck.size(); // making it a loop
      updateUi();
    }

    updateProgress();
  }

  @FXML
  private void whenCardButtonClicked(){
    if (!deck.isEmpty()) {
      flipCard();
    }
  }

  private void flipCard() {
    RotateTransition rotateOut = new RotateTransition(Duration.millis(150), card);
    rotateOut.setAxis(Rotate.X_AXIS);
    rotateOut.setFromAngle(0);
    rotateOut.setToAngle(90);

    RotateTransition rotateIn = new RotateTransition(Duration.millis(150), card);
    rotateIn.setAxis(Rotate.X_AXIS);
    rotateIn.setFromAngle(270);
    rotateIn.setToAngle(360);

    rotateOut.setOnFinished(e -> {
        // Toggle between question and answer
        if (!isShowingAnswer) {
            card.setText(deck.get(currentCardI).getAnswer());
            card.setStyle(answerStyle);
        } else {
            card.setText(deck.get(currentCardI).getQuestion());
            card.setStyle(questionStyle);
        }
        isShowingAnswer = !isShowingAnswer;
        rotateIn.play();
    });

    rotateOut.play();
  }

  @FXML
  public void whenLogOut(ActionEvent event){
    //go to login scene when that is implemented
  }

  @FXML
  private void updateProgress() {
    progressBar.setProgress((currentCardI +1)/ (double) deck.size());
    cardNumber.setText(Integer.toString(currentCardI + 1));

  }
  
}
