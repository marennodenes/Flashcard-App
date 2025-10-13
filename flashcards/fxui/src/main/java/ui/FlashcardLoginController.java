package ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import app.LoginValidator;
import itp.storage.FlashcardPersistent;

/**
 * Controller for the Flashcard Login UI.
 * Handles user login and account creation.
 */
public class FlashcardLoginController {
  @FXML private Text alertMessage;
  @FXML private Button loginButton;
  @FXML private TextField usernameField;
  @FXML private TextField passwordField;
  @FXML private Button signUpButton;

  private boolean showAlert = false;
  private String error = "";
  private LoginValidator loginValidator;

  public void initialize() {
    // Initialize LoginValidator with persistence implementation
    loginValidator = new LoginValidator(new FlashcardPersistent());
    updateUi();
  }

  /**
   * Updates the UI elements based on the current state.
   * Shows or hides alert messages as needed.
   */
  public void updateUi(){
    if (showAlert) {
      alertMessage.setText(error);
      alertMessage.setVisible(true);
      showAlert = false;
    } else {
      alertMessage.setVisible(false);
    }

  }

  /**
   * Handles login button click event.
   * Validates username and password fields, then navigates to main app if valid.
   * Shows error message if fields are empty or invalid.
   */
  public void whenLoginButtonClicked() {
    String username = usernameField.getText().trim();
    String password = passwordField.getText().trim();

    // if username or password field is empty
    if (username.isEmpty() || password.isEmpty()) {
      error = "Username and password\ncannot be empty";
      showAlert = true;
      updateUi();
      return;
    }

    // if user exists, authenticate password and open main app
    else if (loginValidator.authenticateUser(username, password)) {
      System.out.println("User authenticated: " + username);
      try {
        navigateToMainApp(username);
      } catch (IOException e) {
        error = "Failed to load main application";
        showAlert = true;
        updateUi();
      }
    }

    //TODO
    // check if user exists, if not create new user and open main app
    else if (loginValidator.createUser(username, password)) {
      //her hopper den foreløpig inn
      System.out.println("User created: " + username);
      try {
        navigateToMainApp(username);
      } catch (IOException e) {
        error = "Failed to load main application";
        showAlert = true;
        updateUi();
      }
    }
    
    // if user exists but password is wrong
    else {
      error = "Wrong password";
      showAlert = true;
      updateUi();
      return;
    }

  }

  public void whenSignUpButtonClicked(){
    try{
      navigateToSignUpPage();
    }
    catch(IOException e){
      error = "Failed to load signup page";
      showAlert=true;
      updateUi();
    }
    

  }

  /**
   * Navigates to the main flashcard application.
   * Loads the FlashcardMainUI and passes the username to the controller.
   * 
   * @param username the logged-in username to pass to the main controller
   * @throws IOException if the FXML file cannot be loaded
   */
  private void navigateToMainApp(String username) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardMainUI.fxml"));
    Parent root = loader.load();
    
    // Get the controller and set the username
    FlashcardMainController mainController = loader.getController();
    mainController.setCurrentUsername(username);
    
    // Switch to the main scene
    Stage stage = (Stage) loginButton.getScene().getWindow();
    stage.setScene(new Scene(root));
    stage.show();
  }


  private void navigateToSignUpPage() throws IOException{
    FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardSignUpUI.fxml"));
    Parent root = loader.load();

    Stage stage = (Stage) signUpButton.getScene().getWindow();
    stage.setScene(new Scene(root));
    stage.show();
  }
}
