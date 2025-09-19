package ui;

import java.io.IOException;

import app.Flashcard;
import app.FlashcardDeck;
import itp.storage.FlashcardPersistent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Controller class for managing flashcard operations in the UI. */
public class FlashcardDeckController {
  @FXML private TextField questionField;
  @FXML private TextField answerField;
  @FXML private ListView<Flashcard> listView;
  @FXML private Text usernameField;

  private FlashcardDeck deck;
  private final FlashcardPersistent storage = new FlashcardPersistent(); 

  public void setDeck(FlashcardDeck originalDeck) {
    this.deck = new FlashcardDeck();
    this.deck.setDeckName(originalDeck.getDeckName());
    
    for (Flashcard card : originalDeck.getDeck()) {
        this.deck.addFlashcard(card.getQuestion(), card.getAnswer());
    }
    updateUi();
  }

  /**
   * Sets up the UI when loaded.
   * Loads existing flashcards from CSV file
   */
  @FXML public void initialize() {
    deck = new FlashcardDeck();
    loadDeckFromFile();
    updateUi();
  }

  /**
   * Loads deck from CSV file into the deck.
   */
  private void loadDeckFromFile() {
    storage.readFromFile();  // Read from CSV file
    for (Flashcard card : storage.getDeck()) { 
      deck.addFlashcard(card.getQuestion(), card.getAnswer()); // Add to deck
    }
  }

  private void saveFlashcardToFile(Flashcard card) {
    storage.addFlashcard(card); // Add to storage
    storage.writeToFile();      // Write to CSV file
  }

  /**
   * Updates the flashcard list display.
   */
  public void updateUi() {
    usernameField.setText("user");
    ObservableList<Flashcard> ob = FXCollections.observableArrayList(deck.getDeck());
    listView.setItems(ob);
  }

  /**
   * Adds a new flashcard when button is clicked.
   */
  public void whenGenerateButtonIsClicked() {
    String q = questionField.getText().trim();
    String a = answerField.getText().trim();

    if (!q.isEmpty() && !a.isEmpty()) {
      
      //add to memory
      deck.addFlashcard(q, a);

      //save to CSV file 
      Flashcard newFlashcard = new Flashcard(q, a);
      saveFlashcardToFile(newFlashcard);

      //clear input fields
      clearInputFields();

      //update UI
      updateUi();
    }

  }
  
  /**
   * Clears the input fields.
   */
  private void clearInputFields() {
    questionField.clear();
    answerField.clear();
  } 

  @FXML
  private void whenBackButtonIsClicked() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardMainUI.fxml"));
    Parent root = loader.load();
    Stage stage = (Stage) questionField.getScene().getWindow();
    stage.setScene(new Scene(root));
    stage.show();
  }

  @FXML private void whenStartLearningButtonIsClicked() {
    //go to learning scene when that is implemented
  }

  @FXML private void whenLogOut() {
    //go to login scene when that is implemented
  }
}
