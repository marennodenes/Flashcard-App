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

  @FXML 
  public void initialize() {
    deckButtons = new Button[]{ deck_1, deck_2, deck_3, deck_4, deck_5, deck_6, deck_7, deck_8 };
    deleteButtons = new Button[]{ deleteDeck_1, deleteDeck_2, deleteDeck_3, deleteDeck_4,
                                     deleteDeck_5, deleteDeck_6, deleteDeck_7, deleteDeck_8 };

    hideAllDeckButtons();
    loadUserData();
    updateUi();
  }

  private void hideAllDeckButtons() {
    for (Button b : deckButtons) {
        b.setVisible(false);
        b.setDisable(true);
    }
    for (Button b : deleteButtons) {
        b.setVisible(false);
    }
  }

  public void updateUi(){
    usernameField.setText("user"); //until we have login implemented

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

    for (int i = 0; i < deckButtons.length; i++) {
      if (i < decks.size()) {
        FlashcardDeck deck = decks.get(i);

        deckButtons[i].setText(deck.getDeckName());
        deckButtons[i].setDisable(false);
        deckButtons[i].setVisible(true);

        deleteButtons[i].setVisible(true);

        deckButtons[i].setUserData(deck);
        deleteButtons[i].setUserData(deck);
      }
    }

    newDeckButton.setDisable(decks.size() >= 8);

    deckNameInput.clear();
  }

  /**
   * Loads user data from JSON file.
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
   */
  private void saveUserData() {
    try {
      storage.writeDeck(currentUsername, deckManager);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

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

  @FXML
  public void whenDeleteDeckButtonIsClicked(ActionEvent event){
    Button clickedButton = (Button) event.getSource();
    FlashcardDeck deck = (FlashcardDeck) clickedButton.getUserData();
    deckManager.removeDeck(deck);
    saveUserData();
    updateUi();
  }

  @FXML
  public void whenADeckIsClicked(ActionEvent event){
    try {
        Button clickedButton = (Button) event.getSource();
        FlashcardDeck selectedDeck = (FlashcardDeck) clickedButton.getUserData();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardListUI.fxml"));
        Parent root = loader.load();

        FlashcardDeckController controller = loader.getController();
        controller.setDeck(selectedDeck);  // send valgt deck

        Stage stage = (Stage) clickedButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

      } catch (IOException e) {
          e.printStackTrace();
    }
  }

  @FXML
  public void whenLogOut(ActionEvent event){
    //go to login scene when that is implemented
  }
}
