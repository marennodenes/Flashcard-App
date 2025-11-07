package ui;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import dto.FlashcardDto;
import dto.FlashcardDeckDto;
import shared.ApiEndpoints;
import shared.ApiResponse;
import shared.ApiConstants;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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
  @FXML private ListView<FlashcardDto> listView;
  @FXML private Text username;
  @FXML private Button startLearning;
  @FXML private Button deleteCardButton;

  private FlashcardDeckDto currentDeck;
  private String currentUsername;

  /**
   * Initializes the controller after FXML loading.
   * Sets up ListView selection listeners and updates the initial UI state.
   */
  @FXML 
  public void initialize() {
    // Set up simple cell factory to use toString() from FlashcardDto
    listView.setCellFactory(list -> new ListCell<FlashcardDto>() {
      @Override
      protected void updateItem(FlashcardDto item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty || item == null ? null : item.toString());
      }
    });
    
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
    if (username != null) {
      username.setText(currentUsername);
    }
    
    // Load deck data from API
    loadDeckData();
    
    if (currentDeck != null) {
      ObservableList<FlashcardDto> ob = FXCollections.observableArrayList(currentDeck.getDeck());
      listView.setItems(ob);
      if (startLearning != null) {
        startLearning.setDisable(currentDeck.getDeck().isEmpty());
      }
    } else {
      listView.setItems(FXCollections.observableArrayList());
      if (startLearning != null) {
        startLearning.setDisable(true);
      }
    }

    if (deleteCardButton != null) {
      deleteCardButton.setDisable(listView.getSelectionModel().getSelectedItem() == null);
    }
    clearInputFields();
  }

  /**
   * Loads the current deck from the REST API.
   */
  private void loadDeckData() {
    if (currentUsername == null || currentUsername.isEmpty()) {
      currentDeck = null;
      return;
    }
    
    if (currentDeck == null || currentDeck.getDeckName() == null || currentDeck.getDeckName().isEmpty()) {
      return;
    }
    
    try {
      String url = ApiEndpoints.SERVER_BASE_URL + ApiEndpoints.DECKS + "/" 
          + URLEncoder.encode(currentDeck.getDeckName(), StandardCharsets.UTF_8)
          + "?username=" + URLEncoder.encode(currentUsername, StandardCharsets.UTF_8)
          + "&deckName=" + URLEncoder.encode(currentDeck.getDeckName(), StandardCharsets.UTF_8);
      
      ApiResponse<FlashcardDeckDto> result = ApiClient.performApiRequest(
        url,
        "GET",
        null,
        new TypeReference<ApiResponse<FlashcardDeckDto>>() {}
      );

      if (result != null && result.isSuccess() && result.getData() != null) {
        currentDeck = result.getData();
      } else if (result != null && !result.isSuccess()) {
        System.err.println(ApiConstants.SERVER_ERROR + ": " + result.getMessage());
        ApiClient.showAlert(ApiConstants.LOAD_ERROR, ApiConstants.FAILED_TO_LOAD_DECK_DATA);
      }
    } catch (Exception e) {
      System.err.println("Unexpected error: " + e.getMessage());
      ApiClient.showAlert("Error", ApiConstants.UNEXPECTED_ERROR);
    }
  }

  /**
   * Sets the deck to work with.
   * 
   * @param deck the deck DTO to work with
   */
  public void setDeck(FlashcardDeckDto deck) {
    if (deck != null) {
      // Create a copy to avoid external modification
      List<FlashcardDto> deckList = new ArrayList<>(deck.getDeck());
      this.currentDeck = new FlashcardDeckDto(deck.getDeckName(), deckList);
    } else {
      this.currentDeck = null;
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
   * Adds a new flashcard when button is clicked.
   * Creates a flashcard via REST API and reloads the deck data.
   */
  @FXML
  public void whenCreateButtonIsClicked() {
    String q = questionField.getText().trim();
    String a = answerField.getText().trim();

    // Only create card if both fields have content
    if (q.isEmpty() || a.isEmpty() || currentDeck == null) {
      // Log validation error to terminal only, no user notification
      System.err.println(ApiConstants.VALIDATION_ERROR + ": " + ApiConstants.FLASHCARD_QUESTION_ANSWER_EMPTY);
      return;
    }

    try {
      String url = ApiEndpoints.SERVER_BASE_URL + ApiEndpoints.FLASHCARDS + ApiEndpoints.FLASHCARD_CREATE
          + "?username=" + URLEncoder.encode(currentUsername, StandardCharsets.UTF_8)
          + "&deckname=" + URLEncoder.encode(currentDeck.getDeckName(), StandardCharsets.UTF_8)
          + "&question=" + URLEncoder.encode(q, StandardCharsets.UTF_8)
          + "&answer=" + URLEncoder.encode(a, StandardCharsets.UTF_8);
      
      // Send empty JSON object as body since ApiClient requires it for POST requests
      // Server doesn't use the body (uses URL parameters), but ApiClient validation requires it
      ApiResponse<FlashcardDto> result = ApiClient.performApiRequest(
        url,
        "POST",
        "{}", // Empty JSON object string
        new TypeReference<ApiResponse<FlashcardDto>>() {}
      );

      if (result != null && result.isSuccess()) {
        clearInputFields();
        updateUi();
      } else {
        String errorMsg = result != null ? result.getMessage() : ApiConstants.NO_RESPONSE_FROM_SERVER;
        
        // Check if it's a validation error (just log, no popup for validation)
        if (errorMsg != null && (errorMsg.contains("empty") || errorMsg.contains("invalid") || 
            errorMsg.contains("required") || errorMsg.contains("missing"))) {
          System.err.println(ApiConstants.VALIDATION_ERROR + ": " + errorMsg);
        } else {
          // Server error - log technical details, show popup to user
          System.err.println(ApiConstants.SERVER_ERROR + ": " + errorMsg);
          ApiClient.showAlert(ApiConstants.SERVER_ERROR, ApiConstants.FLASHCARD_FAILED_TO_CREATE);
        }
      }
    } catch (Exception e) {
      // Unknown error type - log technical details, show generic message to user
      System.err.println("Unexpected error: " + e.getMessage());
      ApiClient.showAlert("Error", ApiConstants.UNEXPECTED_ERROR);
    }
  }

  /**
   * Deletes the selected flashcard when delete button is clicked.
   * Deletes the flashcard via REST API and reloads the deck data.
   */
  @FXML
  public void whenDeleteCardButtonIsClicked() {
    FlashcardDto selectedCard = listView.getSelectionModel().getSelectedItem();

    if (selectedCard == null || currentDeck == null) {
      return;
    }

    try {
      String url = ApiEndpoints.SERVER_BASE_URL + ApiEndpoints.FLASHCARDS + ApiEndpoints.FLASHCARD_DELETE
          + "?username=" + URLEncoder.encode(currentUsername, StandardCharsets.UTF_8)
          + "&deckname=" + URLEncoder.encode(currentDeck.getDeckName(), StandardCharsets.UTF_8)
          + "&number=" + selectedCard.getNumber();
      
      ApiResponse<Void> result = ApiClient.performApiRequest(
        url,
        "DELETE",
        null,
        new TypeReference<ApiResponse<Void>>() {}
      );

      if (result != null && result.isSuccess()) {
        updateUi();
      } else {
        // Server error - log technical details, show popup to user
        String errorMsg = result != null ? result.getMessage() : ApiConstants.NO_RESPONSE_FROM_SERVER;
        System.err.println(ApiConstants.SERVER_ERROR + ": " + errorMsg);
        ApiClient.showAlert(ApiConstants.SERVER_ERROR, ApiConstants.FLASHCARD_FAILED_TO_DELETE);
      }
    } catch (Exception e) {
      // Unknown error type - log technical details, show generic message to user
      System.err.println("Unexpected error: " + e.getMessage());
      ApiClient.showAlert("Error", ApiConstants.UNEXPECTED_ERROR);
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
   * Refreshes the deck list in the main controller.
   * 
   * @throws IOException if the FXML file cannot be loaded
   */
  @FXML
  public void whenBackButtonIsClicked() throws IOException {
    try{
      FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardMain.fxml"));
      Parent root = loader.load();

      // Send current username and updated deck manager back to main controller
      FlashcardMainController mainController = loader.getController();
    mainController.setCurrentUsername(currentUsername);
    mainController.refreshDecks();

    Stage stage = (Stage) questionField.getScene().getWindow();
    stage.setScene(SceneUtils.createScaledScene(root));
    stage.show();
    } catch (IOException e) {
      System.err.println(ApiConstants.LOAD_ERROR + ": " + e.getMessage());
      ApiClient.showAlert(ApiConstants.LOAD_ERROR, ApiConstants.UNEXPECTED_ERROR);
    }
  }

  /**
   * Handles the event when the "Start Learning" button is clicked.
   * Navigates from the current scene to the flashcard learning page by loading
   * the FlashcardLearning.fxml file and switching the scene.
   * 
   * @throws IOException if the FXML file cannot be loaded or found
   */
  @FXML
  public void whenStartLearningButtonIsClicked() {
    if (currentDeck == null) {
      return;
    }
    
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardLearning.fxml"));
      Parent root = loader.load();

      FlashcardController controller = loader.getController();
      controller.setCurrentUsername(currentUsername);
      controller.setDeck(currentDeck);

      Stage stage = (Stage) startLearning.getScene().getWindow();
      stage.setScene(SceneUtils.createScaledScene(root));
      stage.show();
    } catch (IOException e) {
      System.err.println(ApiConstants.LOAD_ERROR + ": " + e.getMessage());
      ApiClient.showAlert(ApiConstants.LOAD_ERROR, ApiConstants.UNEXPECTED_ERROR);
    }
  }

  /**
   * Handles log out button click event.
   * Navigates back to the login screen and applies appropriate CSS styling.
   */
  @FXML 
  public void whenLogOut() {
    try {
      // Load login screen
      FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardLogin.fxml"));
      Parent root = loader.load();
      
      // Switch to login scene
      Stage stage = (Stage) questionField.getScene().getWindow();
      Scene scene = SceneUtils.createScaledScene(root);
      scene.getStylesheets().add(getClass().getResource("FlashcardLogin.css").toExternalForm());
      stage.setScene(scene);
      stage.show();
    } catch (IOException e) {
      System.err.println(ApiConstants.LOAD_ERROR + ": " + e.getMessage());
      ApiClient.showAlert(ApiConstants.LOAD_ERROR, ApiConstants.UNEXPECTED_ERROR);
    }
  }
}
