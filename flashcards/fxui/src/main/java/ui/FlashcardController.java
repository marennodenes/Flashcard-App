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
 * Controller for the flashcard learning interface.
 * Handles navigation between cards, flipping animations, and progress tracking.
 * @author marieroe
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

  /**
   * Sets the deck to be used for learning.
   * Creates a defensive copy to avoid external modifications and initializes the UI.
   * 
   * @param deck the flashcard deck to set for learning
   */
  public void setDeck(FlashcardDeck deck) {
    this.originalDeck = new FlashcardDeck(deck.getDeckName());
    for (Flashcard card : deck.getDeck()) {
        this.originalDeck.addFlashcard(new Flashcard(card.getQuestion(), card.getAnswer()));
    }
    
    this.deck = new ArrayList<>(deck.getDeck()); // Copy flashcards from the deck
    currentCardI = 0;
    updateUi();

    updateProgress();
  }

  /**
   * Sets the current username for display in the UI.
   * 
   * @param username the username to set
   */
  public void setCurrentUsername(String username) {
    if (username != null && !username.trim().isEmpty()) {
      this.currentUsername = username.trim();
    }
  }

  /**
   * Initializes the controller after FXML loading.
   * Sets up the initial card index and updates the UI if deck is not empty.
   */
  public void initialize(){
    currentCardI = 0;
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
      card.setWrapText(true);
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
      controller.setCurrentUsername(currentUsername);  // Send current username
      controller.setDeck(originalDeck);
    }

    Stage stage = (Stage) nextCard.getScene().getWindow();
    stage.setScene(new Scene(root));
    stage.show();
  }



  /**
   * Handles next card button click.
   * Advances to the next card in the deck with looping behavior.
   */
  @FXML
  private void whenNextCardButtonClicked() {
    
   if (!deck.isEmpty()) {
      currentCardI = (currentCardI + 1) % deck.size(); // making it a loop
      updateUi();
    }

    updateProgress();

  }

  /** 
   * Handles previous card button click.
   * Moves to the previous card in the deck with looping behavior.
   */
  @FXML
  private void whenPreviousCardButtonClicked() {
    
    if (!deck.isEmpty()) {
      currentCardI = (currentCardI - 1 + deck.size()) % deck.size(); // making it a loop
      updateUi();
    }

    updateProgress();
  }

  /**
   * Handles card button click to flip between question and answer.
   * Triggers the card flip animation if deck is not empty.
   */
  @FXML
  private void whenCardButtonClicked(){
    if (!deck.isEmpty()) {
      flipCard();
    }
  }

  /**
   * Performs the card flip animation and toggles between question and answer.
   * Uses JavaFX rotation transitions to create a smooth flip effect.
   */
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
            card.setWrapText(true);
            
            card.setStyle(answerStyle);
        } else {
            card.setText(deck.get(currentCardI).getQuestion());
            card.setWrapText(true);
            card.setStyle(questionStyle);
        }
        isShowingAnswer = !isShowingAnswer;
        rotateIn.play();
    });

    rotateOut.play();
  }

  /**
   * Handles log out button click event.
   * Navigates back to the login screen.
   * 
   * @param event the action event from clicking the log out button
   */
  @FXML
  public void whenLogOut(ActionEvent event){
    try {
      // Load login screen
      FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardLoginUI.fxml"));
      Parent root = loader.load();
      
      // Switch to login scene
      Stage stage = (Stage) backButton.getScene().getWindow();
      Scene scene = new Scene(root);
      scene.getStylesheets().add(getClass().getResource("FlashcardLogin.css").toExternalForm());
      stage.setScene(scene);
      stage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Updates the progress bar and card number display.
   * Shows current position in the deck and progress as a percentage.
   */
  @FXML
  private void updateProgress() {
    progressBar.setProgress((currentCardI +1)/ (double) deck.size());
    cardNumber.setText(Integer.toString(currentCardI + 1));

  }

  
  
}
