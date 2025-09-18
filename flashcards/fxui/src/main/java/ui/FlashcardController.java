package ui;

import app.Flashcard;
import app.FlashcardDeck;
import itp.storage.FlashcardPersistent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * Controller class for managing flashcard operations in the UI. */
public class FlashcardController {
  @FXML private TextField questionField;
  @FXML private TextField answerField;
  @FXML private ListView<Flashcard> listView;

  private final FlashcardDeck deck = new FlashcardDeck();
  private final FlashcardPersistent storage = new FlashcardPersistent(); 


  /**
   * Sets up the UI when loaded.
   * Loads existing flashcards from CSV file
   */
  @FXML public void initialize() {
    updateUi();
    loadFlashcardsFromFile();
  }

  /**
   * Loads flashcards from CSV file into the deck.
   */
  private void loadFlashcardsFromFile() {
    storage.readFromFile();  // Read from CSV file
    for (Flashcard card : storage.getFlashcards()) { 
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
    ObservableList<Flashcard> ob = FXCollections.observableArrayList(deck.getFlashcards());
    listView.setItems(ob);
  }

  /**
   * Adds a new flashcard when button is clicked.
   */
  public void whenGenerateButtonClicked() {
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

}
