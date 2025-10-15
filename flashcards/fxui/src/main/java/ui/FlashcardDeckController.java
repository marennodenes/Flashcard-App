package ui;

import java.io.IOException;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.core.type.TypeReference;
// import com.fasterxml.jackson.core.type.TypeReference;
// import dto.FlashcardDeckDto;
import app.Flashcard;
import app.FlashcardDeck;
import app.FlashcardDeckManager;
import itp.storage.FlashcardPersistent; //delete
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
 * Controller class for managing flashcard deck operations in the JavaFX UI.
 * 
 * <p>This controller handles the main deck management interface where users can:
 * <ul>
 *   <li>View and manage flashcards within a specific deck</li>
 *   <li>Create new flashcards with questions and answers</li>
 *   <li>Delete existing flashcards from the deck</li>
 *   <li>Navigate to the learning interface to study flashcards</li>
 *   <li>Save and load deck data using both REST API and local storage</li>
 * </ul>
 * 
 * <p>The controller integrates with both remote API endpoints and local file storage
 * for persistent data management, providing fallback mechanisms for offline usage.
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
  private FlashcardPersistent storage = new FlashcardPersistent();

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
   * Loads user data from remote API or local storage.
   * First attempts to retrieve the user's flashcard deck collection from the REST API.
   * If the API call fails or returns an error, falls back to reading from local JSON storage.
   * If both methods fail, creates a new empty deck manager.
   */
  private void loadUserData() {
    // Try to load from API first
    if (loadFromAPI()) {
      return; // Successfully loaded from API
    }
    
    // Fallback to local storage
    loadFromLocalStorage();
  }

  /**
   * Attempts to load deck data from the REST API.
   * 
   * @return true if successfully loaded from API, false otherwise
   */
  private boolean loadFromAPI() {
    try {
      HttpResponse<String> response = APIClient.performRequest(
        "http://localhost:8080/api/users/" + currentUsername + "/decks", 
        "GET", 
        null
      );

      if (response != null && response.statusCode() == 200) {
        ObjectMapper mapper = new ObjectMapper();
        deckManager = mapper.readValue(response.body(), FlashcardDeckManager.class);
        return true; // Successfully loaded and parsed
      }
    } catch (Exception e) {
      System.err.println("API call failed: " + e.getMessage());
    }
    
    return false; // Failed to load from API
  }

  /**
   * Loads deck data from local storage as fallback.
   * Creates empty deck manager if local storage also fails.
   */
  private void loadFromLocalStorage() {
    try {
      deckManager = storage.readDeck(currentUsername);
    } catch (Exception e) {
      System.err.println("Local storage fallback failed: " + e.getMessage());
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
   * Saves user data to remote API or local storage.
   * First attempts to save the deck manager to the REST API.
   * If the API call fails or returns an error, falls back to saving to local JSON storage.
   */
  private void saveUserData() {
    // Try to save to API first
    if (saveToAPI()) {
      return; // Successfully saved to API
    }
    
    // Fallback to local storage
    saveToLocalStorage();
  }

  /**
   * Attempts to save deck data to the REST API.
   * 
   * @return true if successfully saved to API, false otherwise
   */
  private boolean saveToAPI() {
    try {
      HttpResponse<String> response = APIClient.performRequest(
        "http://localhost:8080/api/users/" + currentUsername + "/decks", 
        "PUT", 
        deckManager
      );

      if (response != null && response.statusCode() == 200) {
        return true; // Successfully saved to API
      }
    } catch (Exception e) {
      System.err.println("API save failed: " + e.getMessage());
    }
    
    return false; // Failed to save to API
  }

  /**
   * Saves deck data to local storage as fallback.
   */
  private void saveToLocalStorage() {
    try {
      storage.writeDeck(currentUsername, deckManager);
    } catch (Exception e) {
      System.err.println("Local storage save failed: " + e.getMessage());
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
