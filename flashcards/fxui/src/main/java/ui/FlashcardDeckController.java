package ui;

import java.io.IOException;

import app.Flashcard;
import app.FlashcardDeck;
import app.FlashcardDeckManager;
import itp.storage.FlashcardPersistent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller class for managing flashcard operations in the UI.
 */
public class FlashcardDeckController {
  @FXML private TextField questionField;
  @FXML private TextField answerField;
  @FXML private ListView<Flashcard> listView;
  @FXML private TextField usernameField;
  @FXML private TextField deckNameField;



  private FlashcardDeck flashcards;

  /**
   * Sets the deck to be displayed and edited in this controller.
   * Creates a copy of the original deck to avoid modifying the original.
   * 
   * @param originalDeck the deck to be displayed and edited
   */
  public void setDeck(FlashcardDeck originalDeck) {
    this.flashcards = new FlashcardDeck();
    this.flashcards.setDeckName(originalDeck.getDeckName());
    
    for (Flashcard card : originalDeck.getDeck()) {
        Flashcard newCard = new Flashcard(card.getQuestion(), card.getAnswer());
        this.flashcards.addFlashcard(newCard);
    }
    
    // Oppdater currentDeckName til det faktiske deck navnet
    currentDeckName = originalDeck.getDeckName();
    updateUi();
  }
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
   * Attempts to read the user's flashcard deck collection from persistent storage.
   * If reading fails, creates a new empty deck manager.
   */
  private void loadUserData() {
    try {
      deckManager = storage.readDeck(currentUsername);
    } catch (IOException e) {
      e.printStackTrace();
      deckManager = new FlashcardDeckManager();
    }
  }

  /**
   * Gets the current active deck.
   * Searches through all decks to find the one matching the current deck name.
   * 
   * @return the current active FlashcardDeck, or null if not found
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
   * Persists the current deck manager state to the storage system.
   * Prints stack trace if an IOException occurs during saving.
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
   * Shows all flashcards from the current deck in the ListView.
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
   * Creates a flashcard from the question and answer fields,
   * adds it to the current deck, saves to file, and updates the UI.
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
   * Switches to a different user's flashcard collection.
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
   * Updates the name of the current deck or creates a new deck if it doesn't exist.
   */
  public void changeDeck() {
    String newDeckName = deckNameField.getText().trim();
    if (!newDeckName.isEmpty()) {
      FlashcardDeck currentDeck = getCurrentDeck();
      
      if (currentDeck != null) {
        // Update existing deck name
        currentDeck.setDeckName(newDeckName);
      } else {
        // Create new deck if it doesn't exist
        FlashcardDeck newDeck = new FlashcardDeck();
        newDeck.setDeckName(newDeckName);
        deckManager.addDeck(newDeck);
      }
      
      currentDeckName = newDeckName;
      saveUserData();
      updateUi();
    }
  }
  
  /**
   * Clears the input fields.
   * Resets both question and answer text fields to empty.
   */
  private void clearInputFields() {
    questionField.clear();
    answerField.clear();
  } 

  /**
   * Handles the back button click event.
   * Navigates back to the main flashcard UI.
   * 
   * @throws IOException if the FXML file cannot be loaded
   */
  @FXML
  private void onBackButtonClicked() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardMainUI.fxml"));
    Parent root = loader.load();
    Stage stage = (Stage) questionField.getScene().getWindow(); // eller en annen UI-node
    stage.setScene(new Scene(root));
    stage.show();
  }
}
