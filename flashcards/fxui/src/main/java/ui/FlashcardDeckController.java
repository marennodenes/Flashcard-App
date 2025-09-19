package ui;

import app.Flashcard;
import app.FlashcardDeck;
import app.FlashcardDeckManager;
import itp.storage.FlashcardPersistent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import java.io.IOException;

/**
 * Controller class for managing flashcard operations in the UI.
 */
public class FlashcardDeckController {
  @FXML private TextField questionField;
  @FXML private TextField answerField;
  @FXML private ListView<Flashcard> listView;
  @FXML private TextField usernameField;
  @FXML private TextField deckNameField;

  private FlashcardDeckManager deckManager;
  private FlashcardPersistent storage;
  private String currentUsername = "defaultUserName";
  private String currentDeckName = "My deck";

  /**
   * Sets up the UI when loaded.
   */
  @FXML 
  public void initialize() {
    storage = new FlashcardPersistent();
    loadUserData();
    updateUi();
  }

  /**
   * Loads user data from JSON file.
   */
  private void loadUserData() {
    try {
      deckManager = storage.readDeck(currentUsername);
      
      // Check if default deck exists, if not - create it
      if (getCurrentDeck() == null) {
        FlashcardDeck newDeck = new FlashcardDeck();
        newDeck.setDeckName(currentDeckName);
        deckManager.addDeck(newDeck);
        saveUserData();
      }
    } catch (IOException e) {
      e.printStackTrace();
      deckManager = new FlashcardDeckManager();
      FlashcardDeck newDeck = new FlashcardDeck();
      newDeck.setDeckName(currentDeckName);
      deckManager.addDeck(newDeck);
    }
  }

  /**
   * Gets the current active deck.
   */
  private FlashcardDeck getCurrentDeck() {
    for (FlashcardDeck deck : deckManager.getDecks()) {
      if (deck.getDeckName().equals(currentDeckName)) {
        return deck;
      }
    }
    return null;
  }

  /**
   * Saves user data to JSON file.
   */
  private void saveUserData() {
    try {
      storage.writeDeck(currentUsername, deckManager);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Updates the flashcard list display.
   */
  public void updateUi() {
    FlashcardDeck currentDeck = getCurrentDeck();
    if (currentDeck != null) {
      ObservableList<Flashcard> ob = FXCollections.observableArrayList(currentDeck.getDeck());
      listView.setItems(ob);
    }
  }

  /**
   * Adds a new flashcard when button is clicked.
   */
  public void whenGenerateButtonClicked() {
    String q = questionField.getText().trim();
    String a = answerField.getText().trim();

    if (!q.isEmpty() && !a.isEmpty()) {
      FlashcardDeck currentDeck = getCurrentDeck();
      if (currentDeck != null) {
        // Create flashcard and add to deck
        Flashcard newFlashcard = new Flashcard(q, a);
        currentDeck.addFlashcard(newFlashcard);
        
        // Save to file
        saveUserData();
        
        // Clear fields and update UI
        clearInputFields();
        updateUi();
      }
    }
  }
  
  /**
   * Changes username and loads their data.
   */
  public void changeUser() {
    String newUsername = usernameField.getText().trim();
    if (!newUsername.isEmpty()) {
      currentUsername = newUsername;
      loadUserData();
      updateUi();
    }
  }

  /**
   * Changes deck name.
   */
  public void changeDeck() {
    String newDeckName = deckNameField.getText().trim();
    if (!newDeckName.isEmpty()) {
      currentDeckName = newDeckName;
      
      // Create deck if it doesn't exist
      if (getCurrentDeck() == null) {
        FlashcardDeck newDeck = new FlashcardDeck();
        newDeck.setDeckName(currentDeckName);
        deckManager.addDeck(newDeck);
        saveUserData();
      }
      
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
