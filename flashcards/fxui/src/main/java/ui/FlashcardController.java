package ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.Flashcard;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
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

  
  private List<Flashcard> deck = new ArrayList<>();
  //private HashMap<String, FlashcardDeck> allDecks;

  private int currentCardI;

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
    if (card.getText().equals(deck.get(currentCardI).getQuestion())) {
      card.setText(deck.get(currentCardI).getAnswer());
      card.setStyle("""
            -fx-background-color: #5c7b80; /* ny bakgrunn for svar */
            -fx-effect: dropshadow(gaussian, #89b9bf, 0, 1, 5, 5); /* ny shadow for svar */
            -fx-border-width: 2;
            -fx-background-radius: 3;
            -fx-font-weight: bold;
        """);
    } else {
      card.setText(deck.get(currentCardI).getQuestion());
      card.setStyle("""
            -fx-background-color: #89b9bf; /* spørsmål default */
            -fx-effect: dropshadow(gaussian, #5c7b80, 0, 1, 5, 5); /* shadow default */
            -fx-border-width: 2;
            -fx-background-radius: 3;
            -fx-font-weight: bold;
        """);
    }
  }

  @FXML
  public void whenLogOut(ActionEvent event){
    //go to login scene when that is implemented
  }

  @FXML
  private void updateProgress() {
    progressBar.setProgress((currentCardI +1)/ (double) deck.size());
  }
  
}
