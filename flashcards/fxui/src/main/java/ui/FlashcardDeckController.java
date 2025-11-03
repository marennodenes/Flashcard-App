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
          + "?username=" + URLEncoder.encode(currentUsername, StandardCharsets.UTF_8);
      
      ApiResponse<FlashcardDeckDto> result = ApiClient.performApiRequest(
        url,
        "GET",
        null,
        new TypeReference<ApiResponse<FlashcardDeckDto>>() {}
      );

      if (result.isSuccess() && result.getData() != null) {
        currentDeck = result.getData();
      }
    } catch (Exception e) {
      ApiClient.showAlert("Load Error", "Could not load deck data: " + e.getMessage());
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
      return;
    }

    try {
      String url = ApiEndpoints.SERVER_BASE_URL + ApiEndpoints.FLASHCARDS + ApiEndpoints.FLASHCARD_CREATE
          + "?username=" + URLEncoder.encode(currentUsername, StandardCharsets.UTF_8)
          + "&deckname=" + URLEncoder.encode(currentDeck.getDeckName(), StandardCharsets.UTF_8)
          + "&question=" + URLEncoder.encode(q, StandardCharsets.UTF_8)
          + "&answer=" + URLEncoder.encode(a, StandardCharsets.UTF_8);
      
      ApiResponse<FlashcardDto> result = ApiClient.performApiRequest(
        url,
        "POST",
        null,
        new TypeReference<ApiResponse<FlashcardDto>>() {}
      );

      if (result.isSuccess()) {
        clearInputFields();
        updateUi();
      } else {
        ApiClient.showAlert("Create Error", result.getMessage());
      }
    } catch (Exception e) {
      ApiClient.showAlert("Create Error", "Failed to create flashcard: " + e.getMessage());
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

      if (result.isSuccess()) {
        updateUi();
      } else {
        ApiClient.showAlert("Delete Error", result.getMessage());
      }
    } catch (Exception e) {
      ApiClient.showAlert("Delete Error", "Failed to delete flashcard: " + e.getMessage());
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
    FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardMain.fxml"));
    Parent root = loader.load();
    
    // Send current username and refresh deck list
    FlashcardMainController mainController = loader.getController();
    mainController.setCurrentUsername(currentUsername);
    mainController.refreshDecks();
    
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
    if (currentDeck == null) {
      return;
    }
    
    FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardLearning.fxml"));
    Parent root = loader.load();

    FlashcardController controller = loader.getController();
    controller.setCurrentUsername(currentUsername);
    controller.setDeck(currentDeck);

    Stage stage = (Stage) startLearning.getScene().getWindow();
    stage.setScene(new Scene(root));
    stage.show();
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
      Scene scene = new Scene(root);
      scene.getStylesheets().add(getClass().getResource("FlashcardLogin.css").toExternalForm());
      stage.setScene(scene);
      stage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
