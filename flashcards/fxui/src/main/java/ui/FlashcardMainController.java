package ui;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;

import dto.FlashcardDeckDto;
import dto.FlashcardDeckManagerDto;
import shared.ApiConstants;
import shared.ApiEndpoints;
import shared.ApiResponse;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Controller for the main flashcard deck management interface. Handles
 * displaying, creating, editing, and deleting flashcard decks. Provides
 * navigation to deck editing and learning interfaces. Integrates with REST API
 * for persistent data storage.
 * 
 * @author chrsom
 * @author marennod
 * @author marieroe
 * 
 */
public class FlashcardMainController {
  @FXML private Button deck_1;
  @FXML private Button deck_2;
  @FXML private Button deck_3;
  @FXML private Button deck_4;
  @FXML private Button deck_5;
  @FXML private Button deck_6;
  @FXML private Button deck_7;
  @FXML private Button deck_8;

  @FXML private Button deleteDeck_1;
  @FXML private Button deleteDeck_2;
  @FXML private Button deleteDeck_3;
  @FXML private Button deleteDeck_4;
  @FXML private Button deleteDeck_5;
  @FXML private Button deleteDeck_6;
  @FXML private Button deleteDeck_7;
  @FXML private Button deleteDeck_8;

  @FXML private TextField deckNameInput;

  @FXML private Button newDeckButton;
  @FXML private Button logOutButton;

  @FXML private Text usernameField;
  @FXML private Text alertMessage;
  @FXML private Text ex;
  @FXML private Text noDecks;

  private List<FlashcardDeckDto> decks = new ArrayList<>();

  private String currentUsername;

  private boolean showAlert = false;

  private String error = "";

  private Button[] deckButtons;
  private Button[] deleteButtons;

  /**
   * Initializes the controller after FXML loading. Sets up button arrays for deck
   * and delete buttons, configures event handlers, loads the current user's data
   * from storage, and updates the UI display.
   */
  @FXML
  public void initialize() {
    // Initialize button arrays for easier iteration
    deckButtons = new Button[] { deck_1, deck_2, deck_3, deck_4, deck_5, deck_6, deck_7, deck_8 };
    deleteButtons = new Button[] { deleteDeck_1, deleteDeck_2, deleteDeck_3, deleteDeck_4, deleteDeck_5, deleteDeck_6,
        deleteDeck_7, deleteDeck_8 };

    hideAllDeckButtons();
    // Don't load user data here - wait for setCurrentUsername to be called
    updateUi();
  }

  /**
   * Updates the UI with current data and deck information. Displays username,
   * handles alert messages, shows/hides deck buttons based on available decks,
   * and configures button states and visibility.
   */
  public void updateUi() {
    if (usernameField != null) {
      usernameField.setText(currentUsername);
    }

    if (showAlert) {
      if (alertMessage != null) {
        alertMessage.setText(error);
        alertMessage.setVisible(true);
      }
      if (ex != null) {
        ex.setVisible(true);
      }

      showAlert = false;
    } else {
      if (alertMessage != null) {
        alertMessage.setVisible(false);
      }
      if (ex != null) {
        ex.setVisible(false);
      }

    }
    if (noDecks != null) {
      noDecks.setVisible(decks.isEmpty());
    }

    hideAllDeckButtons();

    // Show buttons for existing decks and hide unused ones
    for (int i = 0; i < deckButtons.length; i++) {
      if (i < decks.size()) {
        FlashcardDeckDto deck = decks.get(i);
        if (deckButtons[i] != null) {
          deckButtons[i].setText(deck.getDeckName());
          deckButtons[i].setDisable(false);
          deckButtons[i].setVisible(true);
          // Store deck reference in button for event handling
          deckButtons[i].setUserData(deck);
        }

        // Show corresponding delete button
        if (deleteButtons[i] != null) {
          deleteButtons[i].setVisible(true);
          // Store deck reference in delete button for event handling
          deleteButtons[i].setUserData(deck);
        }
      }
    }

    // Disable new deck button if maximum number of decks reached
    if (newDeckButton != null) {
      newDeckButton.setDisable(decks.size() >= 8);
    }

    if (deckNameInput != null) {
      deckNameInput.clear();
    }
  }

  /**
   * Sets the decks list (used when returning from FlashcardDeckController).
   * Reloads the deck list from the API to ensure data is up-to-date.
   */
  public void refreshDecks() {
    loadUserData();
    updateUi();
  }
  
  /**
   * Sets the current username and loads their data. This method is called from
   * the login controller to set the logged-in user.
   * 
   * @param username the username to set as current user
   * 
   */
  public void setCurrentUsername(String username) {
    if (username != null && !username.trim().isEmpty()) {
      this.currentUsername = username.trim();
      loadUserData();
      updateUi();
    }
  }

  /**
   * Creates a new deck with the entered name. Reads the deck name from the input
   * field, creates a new deck via REST API, reloads the deck list, and updates
   * the UI. Shows an error message if deck creation fails.
   * 
   * @param event the action event from clicking the new deck button
   * 
   */
  @FXML
  public void whenNewDeckButtonIsClicked(ActionEvent event) {
    try {
      String deckName = deckNameInput.getText().trim();
      if (deckName.isEmpty()) {
        // Validation error - show as inline text only, no popup
        showInlineError(ApiConstants.DECK_NAME_EMPTY);
        return;
      }

      if (currentUsername == null || currentUsername.isEmpty()) {
        // Internal error - show popup and log to terminal
        System.err.println(ApiConstants.SERVER_ERROR + ": No user logged in when creating deck");
        ApiClient.showAlert(ApiConstants.SERVER_ERROR, ApiConstants.DECK_FAILED_TO_CREATE);
        return;
      }

      String url = ApiEndpoints.SERVER_BASE_URL + ApiEndpoints.DECKS + "/"
          + URLEncoder.encode(deckName, StandardCharsets.UTF_8) + "?username="
          + URLEncoder.encode(currentUsername, StandardCharsets.UTF_8);

      // Send empty JSON object as body since ApiClient requires it for POST requests
      // Server doesn't use the body (uses URL parameters), but ApiClient validation
      // requires it
      ApiResponse<FlashcardDeckDto> result = ApiClient.performApiRequest(url, "POST", "{}", // Empty JSON object string
          new TypeReference<ApiResponse<FlashcardDeckDto>>() {
          });

      if (result != null && result.isSuccess()) {
        // Clear input field
        deckNameInput.clear();
        // Reload decks from API
        loadUserData();
        // Update UI to show new deck
        updateUi();
      } else {
        String errorMsg = result != null ? result.getMessage() : ApiConstants.NO_RESPONSE_FROM_SERVER;
        
        // Check if it's a validation error that should be shown as inline text (not popup)
        if (errorMsg != null && (errorMsg.equals(ApiConstants.DECK_ALREADY_EXISTS) || 
            errorMsg.equals(ApiConstants.DECK_LIMIT_REACHED) || 
            errorMsg.equals(ApiConstants.DECK_NAME_EMPTY))) {
          // Validation error - show as inline text only, no popup
          showInlineError(errorMsg);
        } else {
          // Server error - show popup and log to terminal
          System.err.println(ApiConstants.SERVER_ERROR + ": " + errorMsg);
          ApiClient.showAlert(ApiConstants.SERVER_ERROR, ApiConstants.DECK_FAILED_TO_CREATE);
        }
      }
    } catch (RuntimeException e) {
      // Network/API error - show popup and log to terminal
      String errorMsg = e.getMessage();
      if (e.getCause() != null) {
        errorMsg = e.getCause().getMessage();
      }
      System.err.println(ApiConstants.SERVER_ERROR + ": " + errorMsg);
      ApiClient.showAlert(ApiConstants.SERVER_ERROR, ApiConstants.DECK_FAILED_TO_CREATE);
    } catch (Exception e) {
      // General error - show popup and log to terminal
      System.err.println(ApiConstants.SERVER_ERROR + ": " + e.getMessage());
      ApiClient.showAlert(ApiConstants.SERVER_ERROR, ApiConstants.UNEXPECTED_ERROR);
    }
  }

  /**
   * Deletes the selected deck. Retrieves the deck from the clicked delete
   * button's user data, deletes it via REST API, reloads the deck list, and
   * refreshes the UI.
   * 
   * @param event the action event from clicking a delete button
   * 
   */
  @FXML
  public void whenDeleteDeckButtonIsClicked(ActionEvent event) {
    Button clickedButton = (Button) event.getSource();
    FlashcardDeckDto deck = (FlashcardDeckDto) clickedButton.getUserData();

    if (deck == null) {
      return;
    }

    try {
      String url = ApiEndpoints.SERVER_BASE_URL + ApiEndpoints.DECKS + "/"
          + URLEncoder.encode(deck.getDeckName(), StandardCharsets.UTF_8) + "?username="
          + URLEncoder.encode(currentUsername, StandardCharsets.UTF_8);

      ApiResponse<Void> result = ApiClient.performApiRequest(url, "DELETE", null,
          new TypeReference<ApiResponse<Void>>() {
          });

      if (result != null && result.isSuccess()) {
        loadUserData();
        updateUi();
      } else {
        String errorMsg = result != null ? result.getMessage() : ApiConstants.NO_RESPONSE_FROM_SERVER;
        // Server error - show popup and log to terminal
        System.err.println(ApiConstants.SERVER_ERROR + ": " + errorMsg);
        ApiClient.showAlert(ApiConstants.SERVER_ERROR, ApiConstants.DECK_FAILED_TO_DELETE);
      }
    } catch (Exception e) {
      // Network/API error - show popup and log to terminal
      System.err.println(ApiConstants.SERVER_ERROR + ": " + e.getMessage());
      ApiClient.showAlert(ApiConstants.SERVER_ERROR, ApiConstants.UNEXPECTED_ERROR);
    }
  }

  /**
   * Handles clicking on a deck button to navigate to the deck view. Retrieves the
   * selected deck from the button's user data, loads the FlashcardListUI, passes
   * the current username and selected deck DTO to the controller, and switches
   * scenes.
   * 
   * @param event the action event from clicking a deck button
   * 
   */
  @FXML
  public void whenADeckIsClicked(ActionEvent event) {
    try {
      Button clickedButton = (Button) event.getSource();
      FlashcardDeckDto selectedDeck = (FlashcardDeckDto) clickedButton.getUserData();

      if (selectedDeck == null) {
        return;
      }

      FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardDeck.fxml"));
      Parent root = loader.load();

      FlashcardDeckController controller = loader.getController();
      controller.setCurrentUsername(currentUsername); // Send current username
      controller.setDeck(selectedDeck); // Send selected deck DTO

      Stage stage = (Stage) clickedButton.getScene().getWindow();
      stage.setScene(SceneUtils.createScaledScene(root));
      stage.show();

    } catch (IOException e) {
      System.err.println(ApiConstants.LOAD_ERROR + ": " + e.getMessage());
      ApiClient.showAlert(ApiConstants.LOAD_ERROR, ApiConstants.UNEXPECTED_ERROR);
    }
  }

  /**
   * Handles log out button click event. Loads the login screen, applies the
   * appropriate CSS styling, and switches to the login scene.
   * 
   * @param event the action event from clicking the log out button
   * 
   */
  @FXML
  public void whenLogOut(ActionEvent event) {
    try {
      // Load login screen
      FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardLogin.fxml"));
      Parent root = loader.load();

      // Switch to login scene
      Stage stage = (Stage) logOutButton.getScene().getWindow();
      Scene scene = SceneUtils.createScaledScene(root);
      scene.getStylesheets().add(getClass().getResource("FlashcardLogin.css").toExternalForm());
      stage.setScene(scene);
      stage.show();
    } catch (IOException e) {
      System.err.println(ApiConstants.LOAD_ERROR + ": " + e.getMessage());
      ApiClient.showAlert(ApiConstants.LOAD_ERROR, ApiConstants.UNEXPECTED_ERROR);
    }
  }



  /**
   * Shows an inline error message without popup.
   * Used for validation errors that should only appear as text.
   * Hides and disables all deck and delete byttons. Used to reset the UI state before showing onlu the relevant buttons
   * 
   * @param message the error message to display inline
   * 
   */
  private void hideAllDeckButtons() {
    for (Button b : deckButtons) {
      b.setVisible(false);
      b.setDisable(true);
    }
    for (Button b : deleteButtons) {
      b.setVisible(false);
    }
  }

  /**
   * Loads user data from REST API. Attempts to retrieve the user's flashcard deck
   * collection from the REST API. If the API call fails, creates a new empty deck
   * list.
   */
  private void loadUserData() {
    try {
      ApiResponse<FlashcardDeckManagerDto> result = ApiClient.performApiRequest(
          ApiEndpoints.getUserDecksUrl(currentUsername), "GET", null,
          new TypeReference<ApiResponse<FlashcardDeckManagerDto>>() {
          });

      if (result != null && result.isSuccess() && result.getData() != null) {
        decks = new ArrayList<>(result.getData().getDecks());
      } else {
        if (result != null && !result.isSuccess()) {
          System.err.println(ApiConstants.SERVER_ERROR + ": " + result.getMessage());
          ApiClient.showAlert(ApiConstants.LOAD_ERROR, ApiConstants.FAILED_TO_LOAD_DATA);
        }
        decks = new ArrayList<>();
      }
    } catch (Exception e) {
      System.err.println("Unexpected error: " + e.getMessage());
      ApiClient.showAlert(ApiConstants.LOAD_ERROR, ApiConstants.UNEXPECTED_ERROR);
      decks = new ArrayList<>();
    }
  }
  
  /**
   * Private method to show error messages on the UI
   * 
   * @param message the error message to show
   * 
   */
  private void showInlineError(String message) {
    error = message;
    showAlert = true;
    updateUi();
  }
}
