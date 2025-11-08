package ui;

import dto.FlashcardDeckDto;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import shared.ApiConstants;

/**
 * Controller for the flashcard learning interface.
 * Handles navigation between cards, flipping animations, and progress tracking.
 *
 * @author marieroe
 * @author isamw
 * 
 */
public class FlashcardController {

  private @FXML Button backButton;
  private @FXML Button nextButton; 
  private @FXML Button previousButton; 
  private @FXML Button card;
  private @FXML ProgressBar progressBar;
  private @FXML Text usernameField;
  private @FXML Text decknameField;
  private @FXML Text cardNumber;

  private List<FlashcardDto> deck = new ArrayList<>();
  private int currentCardI;
  private String currentUsername;

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

  private FlashcardDeckDto originalDeck;

  /**
   * Initializes the controller after FXML loading.
   * Sets up the initial card index and updates the UI if deck is not empty.
   */
  @FXML
  public void initialize() {
    currentCardI = 0;
    if (!deck.isEmpty()) {
      updateUi();
    }
  }

  /**
   * Updates the UI by setting the card text to display the current flashcard's question.
   * Resets the card to show the question side and updates deck name and username displays.
   * Only updates if the card button exists, the deck is not empty, and the 
   * current card index is valid.
   *
   * @see "docs/release_3/ai_tools.md"
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
      usernameField.setText(currentUsername == null || currentUsername.isEmpty() 
          ? "" : currentUsername);
    }
    FlashcardDto current = getCurrentCard();
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

  /**
   * Updates the progress bar and card number display.
   * Calculates the current position in the deck and updates the UI elements accordingly.
   * Handles null checks and edge cases like empty decks or invalid card indices.
   *
   * @see "docs/release_3/ai_tools.md"
   */
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

  /**
   * Sets the deck for the controller and updates UI/progress.
   * Creates a defensive copy of the provided deck to avoid external mutations.
   *
   * @param deck the deck DTO to set (can be null)
   */
  public void setDeck(FlashcardDeckDto deck) {
    if (deck == null) {
      this.originalDeck = null;
      this.deck = new ArrayList<>();
      currentCardI = 0;
    } else {
      // Create defensive copy
      List<FlashcardDto> deckList = new ArrayList<>(deck.getDeck());
      this.originalDeck = new FlashcardDeckDto(deck.getDeckName(), deckList);
      this.deck = new ArrayList<>(deckList);
      currentCardI = 0;
    }
    updateUi();
    updateProgress();
  }

  /**
   * Sets the current username for display in the UI.
   * Trims whitespace and handles null or empty inputs.
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
   * Performs the card flip animation and toggles between question and answer.
   * Uses JavaFX rotation transitions to create a smooth flip effect.
   *
   * @see "docs/release_3/ai_tools.md"
   */
  public void flipCard() {
    if (card == null) {
      return;
    }
    Duration animationDuration = Duration.millis(200);

    FadeTransition fadeOut = new FadeTransition(animationDuration, card);
    fadeOut.setFromValue(1.0);
    fadeOut.setToValue(0.0);

    FadeTransition fadeIn = new FadeTransition(animationDuration, card);
    fadeIn.setFromValue(0.0);
    fadeIn.setToValue(1.0);

    fadeOut.setOnFinished(e -> {
      FlashcardDto current = getCurrentCard();
      if (!isShowingAnswer) {
        String answer = (current != null && current.getAnswer() != null) ? current.getAnswer() : "";
        card.setText(answer);
        card.setWrapText(true);
        card.setStyle(answerStyle.trim());
      } else {
        String question = (current != null && current.getQuestion() != null) 
            ? current.getQuestion() : "";
        card.setText(question);
        card.setWrapText(true);
        card.setStyle(questionStyle.trim());
      }
      isShowingAnswer = !isShowingAnswer;
      fadeIn.play();
    });

    // Play the first half of the animation
    fadeOut.play();
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
  public void whenBackButtonIsClicked() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardDeck.fxml"));
      Parent root = loader.load();

      FlashcardDeckController controller = loader.getController();
      controller.setCurrentUsername(currentUsername);
      
      if (originalDeck != null) {
        controller.setDeck(originalDeck);
      }

      // Null check for nextButton to prevent crash
      if (nextButton != null && nextButton.getScene() != null) {
        Stage stage = (Stage) nextButton.getScene().getWindow();
        stage.setScene(SceneUtils.createScaledScene(root));
        stage.show();
      } else {
        // Fallback: try to get stage from any available button
        if (backButton != null && backButton.getScene() != null) {
          Stage stage = (Stage) backButton.getScene().getWindow();
          stage.setScene(SceneUtils.createScaledScene(root));
          stage.show();
        } else if (card != null && card.getScene() != null) {
          Stage stage = (Stage) card.getScene().getWindow();
          stage.setScene(SceneUtils.createScaledScene(root));
          stage.show();
        } else {
          // Could not get stage, do nothing or log error
          System.err.println(ApiConstants.NO_VALID_BUTTON_FOR_SCENE_SWITCH);
        }
      }
    } catch (IOException e) {
      System.err.println(ApiConstants.LOAD_ERROR + ": " + e.getMessage());
      ApiClient.showAlert(ApiConstants.LOAD_ERROR, ApiConstants.UNEXPECTED_ERROR);
    }
  }

  /**
   * Handles log out button click event.
   * Navigates back to the login screen.
   *
   * @param event the action event from clicking the log out button
   */
  @FXML
  public void whenLogOut(ActionEvent event) {
    try {
      // Load login screen
      FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardLogin.fxml"));
      Parent root = loader.load();
      
      // Switch to login scene
      Stage stage = (Stage) backButton.getScene().getWindow();
      Scene scene = SceneUtils.createScaledScene(root);
      scene.getStylesheets().add(getClass().getResource("FlashcardLogin.css").toExternalForm());
      stage.setScene(scene);
      stage.show();
    } catch (IOException e) {
      System.err.println(ApiConstants.LOAD_ERROR + ": " + e.getMessage());
      ApiClient.showAlert(ApiConstants.LOAD_ERROR, ApiConstants.UNEXPECTED_ERROR);
    }
  }

  /**
   * Retrieves the current flashcard from the deck.
   * Performs boundary and null checks to ensure safe access.
   *
   * @return the current flashcard, or null if the deck is empty, null, or the index is invalid
   */
  private FlashcardDto getCurrentCard() {
    if (deck == null || deck.isEmpty() || currentCardI < 0 || currentCardI >= deck.size()) {
      return null;
    }
    FlashcardDto cardObj = deck.get(currentCardI);
    return cardObj == null ? null : cardObj;
  }

  /**
   * Navigates to the next card in the deck.
   * Uses modulo arithmetic to wrap around to the first card after reaching the end.
   * Resets the card to show the question side and updates the UI and progress.
   */
  private void goToNextCard() {
    if (deck == null || deck.isEmpty()) {
      return;
    }

    currentCardI = (currentCardI + 1) % deck.size();
    isShowingAnswer = false; // Reset to question when navigating to new card
    updateUi();
    updateProgress();
  }

  /**
   * Navigates to the previous card in the deck.
   * Uses modulo arithmetic to wrap around to the last card when going before the first card.
   * Resets the card to show the question side and updates the UI and progress.
   */
  private void goToPreviousCard() {
    if (deck == null || deck.isEmpty()) {
      return;
    }
    
    currentCardI = (currentCardI - 1 + deck.size()) % deck.size();
    isShowingAnswer = false; // Reset to question when navigating to new card
    updateUi();
    updateProgress();
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
  private void whenCardButtonClicked() {
    if (!deck.isEmpty()) {
      flipCard();
    }
  }
  
}
