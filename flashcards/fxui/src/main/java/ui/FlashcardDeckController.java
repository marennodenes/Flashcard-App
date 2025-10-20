package ui;

import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;
import app.Flashcard;
import app.FlashcardDeck;
import app.FlashcardDeckManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import shared.ApiResponse;
import shared.ApiEndpoints;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Controller class for managing flashcard deck operations in the JavaFX UI.
 * 
 * <p>This controller handles the main deck management interface where users can:
 * <ul>
 *   <li>View and manage flashcards within a specific deck</li>
 *   <li>Create new flashcards with questions and answers</li>
 *   <li>Delete existing flashcards from the deck</li>
 *   <li>Navigate to the learning interface to study flashcards</li>
 *   <li>Save and load deck data using REST API</li>
 * </ul>
 * 
 * <p>The controller integrates with remote API endpoints for persistent data management.
 * If API calls fail, users are notified and the app continues with empty state.
 * 
 * @author chrsom
 * @author marennod
 * @author marieroe
 */
public class FlashcardDeckController {
  @FXML private TextField questionField;
  @FXML private TextField answerField;
  @FXML private ListView<Flashcard> listView;
  @FXML private Text username;
  @FXML private Button startLearning;
  @FXML private Button deleteCardButton;

  private FlashcardDeckManager deckManager;
  private String currentUsername = "defaultUserName";
  private String currentDeckName = "defaultDeckName";

  private FlashcardDeck currentActiveDeck;

  /**
   * Sets the current deck to work with.
   * Creates a defensive copy of the deck to avoid external modifications.
   * 
   * @param originalDeck the deck to set as current
   */
  /**
   * Sets the deck to be edited and synchronizes it with the deck manager.
   * Creates a copy of the original deck, loads user data, and ensures the deck
   * is properly integrated into the deck manager before saving.
   * 
   * @param originalDeck the deck to set as the current active deck
   */
  public void setDeck(FlashcardDeck originalDeck) {
    if (originalDeck == null) return;

    // Create a copy of the original deck to avoid modifying the original
    this.currentActiveDeck = new FlashcardDeck(originalDeck.getDeckName());
    for (Flashcard card : originalDeck.getDeck()) {
        this.currentActiveDeck.addFlashcard(new Flashcard(card.getQuestion(), card.getAnswer()));
    }
    this.currentDeckName = originalDeck.getDeckName();

    loadUserData();

    // Find and replace the deck in the manager, or add it if not found
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
    loadUserData();

    listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      deleteCardButton.setDisable(newValue == null);
    });

    updateUi();
  }

      /**
   * Loads user data from REST API.
   * Attempts to retrieve the user's flashcard deck collection from the REST API.
   * If the API call fails, shows error to user and creates empty deck manager.
   */
  private void loadUserData() {
    ApiResponse<FlashcardDeckManager> result = ApiClient.performApiRequest(
      ApiEndpoints.getUserDecksUrl(currentUsername),
      "GET",
      null,
      new TypeReference<FlashcardDeckManager>() {}
    );

    if (result.isSuccess() && result.getData() != null) {
      deckManager = result.getData();
    } else {
      ApiClient.showAlert("Load Error", result.getMessage());
      deckManager = new FlashcardDeckManager();
    }
  }

  /**
   * Saves user data to remote API.
   * If the API call fails, shows error to user.
   */
  private void saveUserData() {
    ApiResponse<String> result = ApiClient.performApiRequest(
      ApiEndpoints.getUserDecksUrl(currentUsername),
      "PUT",
      deckManager,
      new TypeReference<String>() {}
    );

    if (!result.isSuccess()) {
      ApiClient.showAlert("Save Error", result.getMessage());
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
  @FXML
  public void whenCreateButtonIsClicked() {
    String q = questionField.getText().trim();
    String a = answerField.getText().trim();

    // Only create card if both fields have content
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
   * Deletes the selected flashcard when delete button is clicked.
   * Removes the selected flashcard from the current deck, saves the data,
   * and updates the UI to reflect the changes.
   */
  @FXML
  public void whenDeleteCardButtonIsClicked() {
    int selectedIndex = listView.getSelectionModel().getSelectedIndex();
    FlashcardDeck currentDeck = getCurrentDeck();

    // Check if a card is selected and deck exists
    if (selectedIndex >= 0 && currentDeck != null) {
        boolean removed = currentDeck.removeFlashcardByIndex(selectedIndex);
        
        if (removed) {
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
    FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardMain.fxml"));
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
    FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardLearning.fxml"));
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
      FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardLogin.fxml"));
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
