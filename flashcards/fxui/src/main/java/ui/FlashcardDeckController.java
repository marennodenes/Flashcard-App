package ui;

import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;

import app.Flashcard;
import app.FlashcardDeck;
import app.FlashcardDeckManager;
import dto.FlashcardDeckManagerDto;
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
import shared.ApiEndpoints;
import shared.ApiResponse;

/**
 * Controller for managing individual flashcard deck operations.
 * Handles adding, deleting, and viewing flashcards within a specific deck.
 * Provides navigation to the learning interface and back to the main deck list.
 * Uses REST API for data persistence instead of local storage.
 * 
 * @author marennod
 * @author marieroe
 * @author chrsom
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

  /**
   * Initializes the controller after FXML loading.
   * Sets up ListView selection listeners and updates the initial UI state.
   */
  @FXML 
  public void initialize() {
    // Enable delete button only when a card is selected
    listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      deleteCardButton.setDisable(newValue == null);
    });
    
    updateUi();
  }

  /**
   * Updates the flashcard list display.
   * Shows all flashcards from the current deck in the ListView.
   */
  public void updateUi() {
    username.setText(currentUsername);
    
    // Check if we have a deck manager and current deck
    if (deckManager == null) {
      // No data loaded yet, show empty state
      listView.setItems(FXCollections.observableArrayList());
      startLearning.setDisable(true);
      deleteCardButton.setDisable(true);
      clearInputFields();
      return;
    }
    
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
   * Sets the deck manager and current deck to work with.
   * This ensures that changes are saved to the complete deck collection.
   * Creates defensive copies to prevent external modification.
   * 
   * @param deckManager the complete deck manager
   * @param selectedDeck the specific deck to work with
   */
  public void setDeckManager(FlashcardDeckManager deckManager, FlashcardDeck selectedDeck) {
    // Create defensive copy of deck manager to prevent external modification
    this.deckManager = new FlashcardDeckManager();
    for (FlashcardDeck deck : deckManager.getDecks()) {
      FlashcardDeck deckCopy = new FlashcardDeck(deck.getDeckName());
      for (Flashcard card : deck.getDeck()) {
        deckCopy.addFlashcard(new Flashcard(card.getQuestion(), card.getAnswer()));
      }
      this.deckManager.addDeck(deckCopy);
    }
    
    this.currentDeckName = selectedDeck.getDeckName();
    
    // Ensure the deck exists in the manager (defensive programming)
    FlashcardDeck existingDeck = getCurrentDeck();
    if (existingDeck == null) {
      // If deck not found, add it to the manager
      FlashcardDeck newDeck = new FlashcardDeck(selectedDeck.getDeckName());
      for (Flashcard card : selectedDeck.getDeck()) {
        newDeck.addFlashcard(new Flashcard(card.getQuestion(), card.getAnswer()));
      }
      this.deckManager.addDeck(newDeck);
    }
    
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
   * Saves user data to remote API.
   * If the API call fails, shows error to user.
   */
  private void saveUserData() {
    ApiResponse<FlashcardDeckManagerDto> result = ApiClient.performApiRequest(
      ApiEndpoints.getUserDecksUrl(currentUsername),
      "PUT",
      deckManager,
      new TypeReference<ApiResponse<FlashcardDeckManagerDto>>() {}
    );

    if (!result.isSuccess()) {
      ApiClient.showAlert("Save Error", result.getMessage());
    }
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
   * Saves current data and sends the updated deck manager back to main controller.
   * 
   * @throws IOException if the FXML file cannot be loaded
   */
  @FXML
  public void whenBackButtonIsClicked() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardMain.fxml"));
    Parent root = loader.load();
    
    // Send current username and updated deck manager back to main controller
    FlashcardMainController mainController = loader.getController();
    mainController.setCurrentUsername(currentUsername);
    mainController.setDeckManager(deckManager);
    
    Stage stage = (Stage) questionField.getScene().getWindow();
    stage.setScene(new Scene(root));
    stage.show();
  }

  /**
   * Handles the event when the "Start Learning" button is clicked.
   * Navigates from the current scene to the flashcard learning page by loading
   * the FlashcardLearning.fxml file and switching the scene.
   * 
   * @throws IOException if the FXML file cannot be loaded or found
   */
  @FXML
  public void whenStartLearningButtonIsClicked() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardLearning.fxml"));
    Parent root = loader.load();

    FlashcardController controller = loader.getController();
    FlashcardDeck currentDeck = getCurrentDeck();
    System.out.println("DEBUG: Navigating to learning page with username: '" + currentUsername + "', deck: '" + (currentDeck != null ? currentDeck.getDeckName() : "null") + "'");
    if(currentDeck != null){
      controller.setCurrentUsername(currentUsername);  // Pass the logged-in username
      controller.setDeckManager(deckManager, currentDeck);  // Pass the deck manager and selected deck
    }

    Stage stage = (Stage) startLearning.getScene().getWindow();
    stage.setScene(new Scene(root));
    stage.show();
  }

  /**
   * Handles log out button click event.
   * Saves current user data before logging out, navigates back to the login screen,
   * and applies appropriate CSS styling.
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
