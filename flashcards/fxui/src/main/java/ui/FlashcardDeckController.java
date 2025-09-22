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
  @FXML private Text usernameField;
  @FXML private TextField deckNameField;
  @FXML private Button startLearning;
  @FXML private Button deleteCardButton;

  private FlashcardDeck deck;
  private FlashcardDeckManager deckManager;
  private FlashcardPersistent storage;
  private String currentUsername = "defaultUserName";
  private String currentDeckName = "My deck";

  public void setDeck(FlashcardDeck originalDeck) {
    this.deck = new FlashcardDeck();
    this.deck.setDeckName(originalDeck.getDeckName());
    
    for (Flashcard card : originalDeck.getDeck()) {
        Flashcard newCard = new Flashcard(card.getQuestion(), card.getAnswer());
        this.deck.addFlashcard(newCard);
    }
    updateUi();
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
    usernameField.setText("user");
    FlashcardDeck currentDeck = getCurrentDeck();
    if (currentDeck != null) {
      ObservableList<Flashcard> ob = FXCollections.observableArrayList(currentDeck.getDeck());
      listView.setItems(ob);
    } else {
      listView.setItems(FXCollections.observableArrayList());
    }

    deleteCardButton.setDisable(listView.getSelectionModel().getSelectedItem() == null);

    clearInputFields();
  }

  /**
   * Adds a new flashcard when button is clicked.
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

  @FXML
  private void whenBackButtonIsClicked() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardMainUI.fxml"));
    Parent root = loader.load();
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
  private void whenStartLearningButtonIsClicked() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardPageUI.fxml"));
    Parent root = loader.load();
    Stage stage = (Stage) startLearning.getScene().getWindow();
    stage.setScene(new Scene(root));
    stage.show();
  }

  @FXML private void whenLogOut() {
    //go to login scene when that is implemented
  }
}
