package ui;

import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import app.FlashcardDeck;
import itp.storage.FlashcardPersistent;

import java.io.IOException;
import java.util.List;
import app.FlashcardDeckManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

/**
 * Controller for the main flashcard deck management interface.
 * Handles displaying, creating, and deleting flashcard decks for the logged-in user.
 * Provides navigation to individual deck views and handles user logout functionality.
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

  @FXML private Text noDecks;

  private FlashcardDeckManager deckManager = new FlashcardDeckManager();
  
  private FlashcardPersistent storage = new FlashcardPersistent();
  
  private String currentUsername = "defaultUserName";
  
  private boolean showAlert = false;
  
  private String error = "";

  private Button[] deckButtons;
  
  private Button[] deleteButtons;

  /**
   * Initializes the controller after FXML loading.
   * Sets up button arrays for deck and delete buttons, configures event handlers,
   * loads the current user's data from storage, and updates the UI display.
   */
  @FXML 
  public void initialize() {
    // Initialize button arrays for easier iteration
    deckButtons = new Button[]{ deck_1, deck_2, deck_3, deck_4, deck_5, deck_6, deck_7, deck_8 };
    deleteButtons = new Button[]{ deleteDeck_1, deleteDeck_2, deleteDeck_3, deleteDeck_4,
                                     deleteDeck_5, deleteDeck_6, deleteDeck_7, deleteDeck_8 };

    hideAllDeckButtons();
    loadUserData();
    updateUi();
  }

  /**
   * Updates the UI with current data and deck information.
   * Displays username, handles alert messages, shows/hides deck buttons based on available decks,
   * and configures button states and visibility.
   */
  public void updateUi(){
    usernameField.setText(currentUsername);

    List<FlashcardDeck> decks = deckManager.getDecks();

    if (showAlert) {
      alertMessage.setText(error);
      alertMessage.setVisible(true);
      showAlert = false;
    } else {
      alertMessage.setVisible(false);
    }

    noDecks.setVisible(decks.isEmpty());

    hideAllDeckButtons();

    // Show buttons for existing decks and hide unused ones
    for (int i = 0; i < deckButtons.length; i++) {
      if (i < decks.size()) {
        FlashcardDeck deck = decks.get(i);

        deckButtons[i].setText(deck.getDeckName());
        deckButtons[i].setDisable(false);
        deckButtons[i].setVisible(true);

        deleteButtons[i].setVisible(true);

        // Store deck reference in button for event handling
        deckButtons[i].setUserData(deck);
        deleteButtons[i].setUserData(deck);
      }
    }

    // Disable new deck button if maximum number of decks reached
    newDeckButton.setDisable(decks.size() >= 8);

    deckNameInput.clear();
  }

  /**
   * Hides and disables all deck and delete buttons.
   * Used to reset the UI state before showing only the relevant buttons.
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
   * Loads user data from JSON file or creates new deck manager if loading fails.
   * Attempts to read the user's flashcard deck collection from persistent storage.
   * If reading fails or file doesn't exist, initializes a new empty deck manager.
   */
  private void loadUserData() {
    try {
      deckManager = storage.readDeck(currentUsername);
    } catch (Exception e) {
      // If file doesn't exist or error reading, create new deck manager
      deckManager = new FlashcardDeckManager();
    }
  }

  /**
   * Saves user data to JSON file.
   * Persists the current deck manager state to the storage system.
   * Prints stack trace if an IOException occurs during saving.
   */
  private void saveUserData() {
    try {
      storage.writeDeck(currentUsername, deckManager);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Sets the current username and loads their data.
   * This method is called from the login controller to set the logged-in user.
   * 
   * @param username the username to set as current user
   */
  public void setCurrentUsername(String username) {
    if (username != null && !username.trim().isEmpty()) {
      this.currentUsername = username.trim();
      loadUserData();
      updateUi();
    }
  }

  /**
   * Creates a new deck with the entered name.
   * Reads the deck name from the input field, creates a new FlashcardDeck,
   * adds it to the deck manager, saves the data, and updates the UI.
   * Shows an error message if deck creation fails.
   * 
   * @param event the action event from clicking the new deck button
   */
  @FXML
  public void whenNewDeckButtonIsClicked(ActionEvent event){
    try {
      String deckName = deckNameInput.getText().trim();
      FlashcardDeck newDeck = new FlashcardDeck();
      newDeck.setDeckName(deckName);
      deckManager.addDeck(newDeck);
      saveUserData();
      updateUi();
    } catch (IllegalArgumentException e) {
      error = e.getMessage();
      showAlert = true;
      updateUi();
    }
  }

  /**
   * Deletes the selected deck.
   * Retrieves the deck from the clicked delete button's user data,
   * removes it from the deck manager, saves the updated data, and refreshes the UI.
   * 
   * @param event the action event from clicking a delete button
   */
  @FXML
  public void whenDeleteDeckButtonIsClicked(ActionEvent event){
    Button clickedButton = (Button) event.getSource();
    FlashcardDeck deck = (FlashcardDeck) clickedButton.getUserData();
    deckManager.removeDeck(deck);
    saveUserData();
    updateUi();
  }

    /**
   * Handles clicking on a deck button to navigate to the deck view.
   * Retrieves the selected deck from the button's user data, loads the FlashcardListUI,
   * passes the current username and complete deck manager to the controller, and switches scenes.
   * 
   * @param event the action event from clicking a deck button
   */
  @FXML
  public void whenADeckIsClicked(ActionEvent event) {
    try {
      Button clickedButton = (Button) event.getSource();
      FlashcardDeck selectedDeck = (FlashcardDeck) clickedButton.getUserData();

      FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardListUI.fxml"));
      Parent root = loader.load();

      FlashcardDeckController controller = loader.getController();
      controller.setCurrentUsername(currentUsername);  // Send current username
      controller.setDeckManager(deckManager, selectedDeck);  // Send complete deck manager and selected deck

      Stage stage = (Stage) clickedButton.getScene().getWindow();
      stage.setScene(new Scene(root));
      stage.show();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Handles log out button click event.
   * Saves current user data before logging out, loads the login screen,
   * applies the appropriate CSS styling, and switches to the login scene.
   * 
   * @param event the action event from clicking the log out button
   */
  @FXML
  public void whenLogOut(ActionEvent event){
    try {
      // Save current user data before logging out
      saveUserData();
      
      // Load login screen
      FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardLoginUI.fxml"));
      Parent root = loader.load();
      
      // Switch to login scene
      Stage stage = (Stage) logOutButton.getScene().getWindow();
      Scene scene = new Scene(root);
      scene.getStylesheets().add(getClass().getResource("FlashcardLogin.css").toExternalForm());
      stage.setScene(scene);
      stage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Sets the deck manager (used when returning from FlashcardDeckController).
   * This ensures that changes made in the deck view are preserved.
   * Creates a defensive copy to prevent external modification.
   * 
   * @param deckManager the updated deck manager
   */
  public void setDeckManager(FlashcardDeckManager deckManager) {
    // Create defensive copy of deck manager to prevent external modification
    this.deckManager = new FlashcardDeckManager();
    for (FlashcardDeck deck : deckManager.getDecks()) {
      FlashcardDeck deckCopy = new FlashcardDeck(deck.getDeckName());
      for (app.Flashcard card : deck.getDeck()) {
        deckCopy.addFlashcard(new app.Flashcard(card.getQuestion(), card.getAnswer()));
      }
      this.deckManager.addDeck(deckCopy);
    }
    updateUi();
  }
}
