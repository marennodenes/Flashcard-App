package ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.Flashcard;
import app.FlashcardDeck;
import app.FlashcardDeckManager;
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
/**
 * Controller for the flashcard learning interface.
 * Handles navigation between cards, flipping animations, and progress tracking.
 * @author marieroe
 */
public class FlashcardController {

    // Remove 'private' from these fields to make them package-private for test access
    @FXML Button backButton;
    @FXML Button nextCard;
    @FXML Button previousCard;
    @FXML Button card;
    @FXML ProgressBar progressBar;
    @FXML Text usernameField;
    @FXML Text decknameField;
    @FXML Text cardNumber;

  // Remove 'private' for test access
  List<Flashcard> deck = new ArrayList<>();
  int currentCardI;
  private String currentUsername;
  private FlashcardDeckManager deckManager;

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
   * Initializes the controller after FXML loading.
   * Sets up the initial card index and updates the UI if deck is not empty.
   */
  @FXML
  public void initialize(){
    currentCardI = 0;
    if (!deck.isEmpty()) {
      updateUi();
    }
  }

  /**
   * Updates the UI by setting the card text to display the current flashcard's question.
   * Resets the card to show the question side and updates deck name and username displays.
   * Only updates if the card button exists, the deck is not empty, and the current card index is valid.
   */
  public void updateUi() {
        if (decknameField != null) {
            String deckName = "";
            if (originalDeck != null && originalDeck.getDeckName() != null) {
                deckName = originalDeck.getDeckName();
            }
            decknameField.setText(deckName == null ? "" : deckName);
        }
        if (usernameField != null) {
            usernameField.setText(currentUsername == null || currentUsername.isEmpty() ? "" : currentUsername);
        }
        Flashcard current = getCurrentCard();
        if (card != null) {
            if (current == null) {
                card.setText("");
                card.setStyle(questionStyle.trim()); // Always set style for empty card
                isShowingAnswer = false; // Reset to question state
            } else {
                String text = isShowingAnswer ? current.getAnswer() : current.getQuestion();
                card.setText(text == null ? "" : text);
                String style = isShowingAnswer ? answerStyle.trim() : questionStyle.trim();
                card.setStyle(style);
            }
        }
        updateProgress();
    }

    public void updateProgress() {
        int deckSize = deck == null ? 0 : deck.size();
        int cardNum = (deckSize == 0 || currentCardI < 0) ? 0 : currentCardI + 1;
        if (cardNumber != null) {
            cardNumber.setText(String.valueOf(cardNum));
        }
        if (progressBar != null) {
            double progress = (deckSize == 0) ? 0.0 : ((double) cardNum / deckSize);
            progressBar.setProgress(progress);
        }
    }

    Flashcard getCurrentCard() {
        if (deck == null || deck.isEmpty() || currentCardI < 0 || currentCardI >= deck.size()) {
            return null;
        }
        Flashcard cardObj = deck.get(currentCardI);
        return cardObj == null ? null : cardObj;
    }

    private void goToNextCard() {
        if (deck == null || deck.isEmpty()) return;
        currentCardI = (currentCardI + 1) % deck.size();
        isShowingAnswer = false; // Reset to question when navigating to new card
        updateUi();
        updateProgress();
    }

    private void goToPreviousCard() {
        if (deck == null || deck.isEmpty()) return;
        currentCardI = (currentCardI - 1 + deck.size()) % deck.size();
        isShowingAnswer = false; // Reset to question when navigating to new card
        updateUi();
        updateProgress();
    }

  /**
   * Sets the deck manager and current deck to work with.
   * This ensures that changes are saved to the complete deck collection.
   * Creates defensive copies to prevent external modification.
   * 
   * @param deckManager the complete deck manager
   * @param selectedDeck the specific deck to work with
   */
  public void setDeckManager(FlashcardDeckManager deckManager, FlashcardDeck selectedDeck) {
    if (deckManager == null || selectedDeck == null) {
        this.deckManager = null;
        this.originalDeck = null;
        this.deck = new ArrayList<>();
        currentCardI = 0;
        updateUi();
        updateProgress();
        return;
    }
    // Create defensive copy of deck manager to prevent external modification
    this.deckManager = new FlashcardDeckManager();
    for (FlashcardDeck deck : deckManager.getDecks()) {
      this.deckManager.addDeck(deck);
    }
    // Create defensive copy of the selected deck
    this.originalDeck = new FlashcardDeck(selectedDeck.getDeckName());
    for (app.Flashcard card : selectedDeck.getDeck()) {
      if (card != null) {
        this.originalDeck.addFlashcard(new app.Flashcard(card.getQuestion(), card.getAnswer()));
      } else {
        this.originalDeck.addFlashcard(null);
      }
    }
    this.deck = new ArrayList<>(this.originalDeck.getDeck()); // Copy flashcards for learning
    // Assign numbers to non-null flashcards only
    for (int i = 0; i < this.deck.size(); i++) {
      Flashcard flashcard = this.deck.get(i);
      if (flashcard != null) {
        flashcard.setNumber(i + 1);
      }
    }
    currentCardI = 0;
    updateUi();
    updateProgress();
  }

  /**
   * Sets the deck for the controller and updates UI/progress.
   * Defensive copy is not strictly necessary for test coverage, so this is simple.
   * @param deck the deck to set (can be null)
   */
  public void setDeck(FlashcardDeck deck) {
    if (deck == null) {
        this.originalDeck = null;
        this.deck = new ArrayList<>();
        currentCardI = 0;
    } else {
        // Defensive copy
        this.originalDeck = new FlashcardDeck(deck.getDeckName());
        for (Flashcard card : deck.getDeck()) {
            if (card != null) {
                this.originalDeck.addFlashcard(new Flashcard(card.getQuestion(), card.getAnswer()));
            } else {
                this.originalDeck.addFlashcard(null);
            }
        }
        this.deck = new ArrayList<>(this.originalDeck.getDeck());
        // Assign numbers to non-null flashcards only
        for (int i = 0; i < this.deck.size(); i++) {
            Flashcard flashcard = this.deck.get(i);
            if (flashcard != null) {
                flashcard.setNumber(i + 1);
            }
        }
        currentCardI = 0;
    }
    updateUi();
    updateProgress();
  }

  /**
   * Sets the current username for display in the UI.
   * 
   * @param username the username to set
   */
  public void setCurrentUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            this.currentUsername = "";
        } else {
            this.currentUsername = username.trim();
        }
        if (usernameField != null) {
            usernameField.setText(this.currentUsername);
        }
    }  

  /**
   * Handles the event when the "Back" button is clicked.
   * Navigates from the current scene to the flashcard deck list page by loading
   * the FlashcardListUI.fxml file and switching the scene.
   * Passes the current username and deck manager to maintain data consistency.
   * 
   * @throws IOException if the FXML file cannot be loaded or found
   */
  @FXML
  public void whenBackButtonIsClicked() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardDeck.fxml"));
    Parent root = loader.load();

    FlashcardDeckController controller = loader.getController();
    controller.setCurrentUsername(currentUsername);  // Send current username
    
    if (deckManager != null && originalDeck != null) {
      // Send the complete deck manager and current deck
      controller.setDeckManager(deckManager, originalDeck);
    }

    // Null check for nextCard to prevent crash
    if (nextCard != null && nextCard.getScene() != null) {
      Stage stage = (Stage) nextCard.getScene().getWindow();
      stage.setScene(new Scene(root));
      stage.show();
    } else {
      // Fallback: try to get stage from any available button
      if (backButton != null && backButton.getScene() != null) {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
      } else if (card != null && card.getScene() != null) {
        Stage stage = (Stage) card.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
      } else {
        // Could not get stage, do nothing or log error
        System.err.println("Error: No valid button to get stage for scene switch.");
      }
    }
  }



  /**
   * Handles next card button click.
   * Advances to the next card in the deck with looping behavior.
   */
  @FXML
  private void whenNextCardButtonClicked() {
        goToNextCard();
  }

  /** 
   * Handles previous card button click.
   * Moves to the previous card in the deck with looping behavior.
   */
  @FXML
  private void whenPreviousCardButtonClicked() {
        goToPreviousCard();
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
  void flipCard() {
        if (card == null) return;
        RotateTransition rotateOut = new RotateTransition(Duration.millis(150), card);
        rotateOut.setAxis(Rotate.X_AXIS);
        rotateOut.setFromAngle(0);
        rotateOut.setToAngle(90);

        RotateTransition rotateIn = new RotateTransition(Duration.millis(150), card);
        rotateIn.setAxis(Rotate.X_AXIS);
        rotateIn.setFromAngle(270);
        rotateIn.setToAngle(360);

        rotateOut.setOnFinished(e -> {
            Flashcard current = getCurrentCard();
            if (!isShowingAnswer) {
                String answer = (current != null && current.getAnswer() != null) ? current.getAnswer() : "";
                card.setText(answer);
                card.setWrapText(true);
                card.setStyle(answerStyle.trim());
            } else {
                String question = (current != null && current.getQuestion() != null) ? current.getQuestion() : "";
                card.setText(question);
                card.setWrapText(true);
                card.setStyle(questionStyle.trim());
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
      FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardLogin.fxml"));
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

  // /**
  //  * Updates the progress bar and card number display.
  //  * Shows current position in the deck and progress as a percentage.
  //  */
  // @FXML 
  // public void updateProgress() {
  //   progressBar.setProgress((currentCardI +1)/ (double) deck.size());
  //   cardNumber.setText(Integer.toString(currentCardI + 1));

  // }

  
  
}
