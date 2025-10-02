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
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Controller class for managing flashcard operations in the UI.
 */
public class FlashcardDeckController {
  @FXML private TextField questionField;
  @FXML private TextField answerField;
  @FXML private ListView<Flashcard> listView;
  @FXML private Text username;
  @FXML private TextField deckNameField;
  @FXML private Button startLearning;
  @FXML private Button deleteCardButton;

  private FlashcardDeckManager deckManager;
  private FlashcardPersistent storage;
  private String currentUsername = "defaultUserName";
  private String currentDeckName = "defaultDeckName";

  private FlashcardDeck currentActiveDeck;

  /**
   * Sets the current deck to work with.
   * Creates a defensive copy of the deck to avoid external modifications.
   * 
   * @param originalDeck the deck to set as current
   */
  public void setDeck(FlashcardDeck originalDeck) {
    if (originalDeck == null) return;

    this.currentActiveDeck = new FlashcardDeck(originalDeck.getDeckName());
    for (Flashcard card : originalDeck.getDeck()) {
        this.currentActiveDeck.addFlashcard(new Flashcard(card.getQuestion(), card.getAnswer()));
    }
    this.currentDeckName = originalDeck.getDeckName();

    loadUserData();

    boolean foundDeck = false;
    for (int i = 0; i < deckManager.getDecks().size(); i++) {
        FlashcardDeck deck = deckManager.getDecks().get(i);
        if (deck.getDeckName().equals(originalDeck.getDeckName())) {
            deckManager.getDecks().set(i, this.currentActiveDeck);
            foundDeck = true;
            break;
        }
    }
    if (!foundDeck) {
        // NEW: add when missing
        deckManager.addDeck(this.currentActiveDeck);
    }

    saveUserData();
    updateUi();
  }

  /**
   * Sets the current username for loading user data.
   * 
   * @param username the username to set
   */
  public void setCurrentUsername(String username) {
    if (username != null && !username.trim().isEmpty()) {
      this.currentUsername = username.trim();
    }
  }

  /**
   * Sets up the UI when loaded.
   */
  @FXML 
  public void initialize() {
    storage = new FlashcardPersistent();
    loadUserData();

    listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      deleteCardButton.setDisable(newValue == null);
    });

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
    username.setText(currentUsername);
    FlashcardDeck currentDeck = getCurrentDeck();
    if (currentDeck != null) {
      ObservableList<Flashcard> ob = FXCollections.observableArrayList(currentDeck.getDeck());
      listView.setItems(ob);
      startLearning.setDisable(currentDeck.getDeck().isEmpty());
    } else {
      listView.setItems(FXCollections.observableArrayList());
      startLearning.setDisable(true);
    }

    deleteCardButton.setDisable(listView.getSelectionModel().getSelectedItem() == null);

    clearInputFields();
  }

  /**
   * Adds a new flashcard when button is clicked.
   * Creates a flashcard from the question and answer fields,
   * adds it to the current deck, saves to file, and updates the UI.
   */
  public void whenCreateButtonIsClicked() {
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

  public void whenDeleteCardButtonIsClicked() {
    int selectedIndex = listView.getSelectionModel().getSelectedIndex();
    FlashcardDeck currentDeck = getCurrentDeck();

    if (selectedIndex >= 0 && currentDeck != null) {
        boolean removed = currentDeck.removeFlashcardByIndex(selectedIndex);
        
        if (removed) {
            saveUserData();
            updateUi();
        }
    }
  }
  
  /**
   * Changes username and loads their data.
   * Switches to a different user's flashcard collection.
   */
  public void changeUser() {
    String newUsername = username.getText().trim();
    if (!newUsername.isEmpty()) {
      currentUsername = newUsername;
      loadUserData();
      updateUi();
    }
  }

  /**
   * Changes deck name.
   * Updates the name of the current deck or creates a new deck if it doesn't exist.
   * Note: Only works if deckNameField is present in the FXML.
   */
  public void changeDeck() {
    if (deckNameField != null) {
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
  public void whenBackButtonIsClicked() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardMainUI.fxml"));
    Parent root = loader.load();
    
    // Send current username back to main controller
    FlashcardMainController mainController = loader.getController();
    mainController.setCurrentUsername(currentUsername);
    
    Stage stage = (Stage) questionField.getScene().getWindow();
    stage.setScene(new Scene(root));
    stage.show();
  }

  /**
   * Handles the event when the "Start Learning" button is clicked.
   * Navigates from the current scene to the flashcard learning page by loading
   * the FlashcardPageUI.fxml file and switching the scene.
   * 
   * @throws IOException if the FXML file cannot be loaded or found
   * @author Claude (AI Assistant) - Javadoc documentation
   */
  @FXML
  public void whenStartLearningButtonIsClicked() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardPageUI.fxml"));
    Parent root = loader.load();

    FlashcardController controller = loader.getController();
    FlashcardDeck currentDeck = getCurrentDeck();
    if(currentDeck != null){
      controller.setCurrentUsername(currentUsername);  // Send current username
      controller.setDeck(currentDeck);
    }

    Stage stage = (Stage) startLearning.getScene().getWindow();
    stage.setScene(new Scene(root));
    stage.show();
  }

  /**
   * Handles log out button click event.
   * Navigates back to the login screen.
   */
  @FXML 
  public void whenLogOut() {
    try {
      // Save current user data before logging out
      saveUserData();
      
      // Load login screen
      FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardLoginUI.fxml"));
      Parent root = loader.load();
      
      // Switch to login scene
      Stage stage = (Stage) questionField.getScene().getWindow();
      Scene scene = new Scene(root);
      scene.getStylesheets().add(getClass().getResource("FlashcardLogin.css").toExternalForm());
      stage.setScene(scene);
      stage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
