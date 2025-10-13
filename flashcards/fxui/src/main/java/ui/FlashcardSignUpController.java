package ui;

import java.io.IOException;

import app.LoginValidator;
import itp.storage.FlashcardPersistent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Controller for the flashcard sign-up page.
 * Handles user registration with validation and navigation to the main application.
 * @author @sofietw
 * @author @ailinat
 */
public class FlashcardSignUpController {
  @FXML private Text alertMessage;
  @FXML private TextField usernameField;
  @FXML private TextField passwordField;
  @FXML private TextField confirmPasswordField;
  @FXML private Button signInButton;

  private boolean showAlert = false;
  private String error = "";
  private LoginValidator loginValidator;
  
  /**
   * Initializes the controller after FXML loading.
   * Sets up the LoginValidator with persistence implementation and updates the UI.
   */
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
   * Handles the sign-in button click event.
   * Validates user input, checks for username uniqueness, verifies password confirmation,
   * creates a new user account, and navigates to the main application on success.
   * Shows appropriate error messages for validation failures.
   */
  @FXML
  public void whenSignInButtonClicked() {
    String username = usernameField.getText().trim();
    String password = passwordField.getText().trim();
    String confirmedPassword = confirmPasswordField.getText().trim();

    // if username or password field is empty
    if (username.isEmpty() || password.isEmpty() || confirmedPassword.isEmpty()) {
      error = "Username and password fields\ncannot be empty";
      showAlert = true;
      updateUi();
      return;
    }

    // if user exists, give alert to user
    else if (!loginValidator.isUsernameUnique(username)) {
      System.out.println("Username already exists " + username);
      error = "Username already exists,\ntry with another username";
      showAlert = true;
      updateUi(); 
    }

    else if (!loginValidator.equalPasswords(password, confirmedPassword)) {
      System.out.println("Passwords must be equal");
      error = "Passwords must be equal";
      showAlert = true;
      updateUi(); 
    }

    // username is unique and passwords match, user is created and navigated to main app 
    else {
      if (loginValidator.createUser(username, password)) System.out.println("User created: " + username);
      try {
        navigateToMainApp(username);
      } catch (IOException e) {
        error = "Failed to load main application";
        showAlert = true;
        updateUi();
      }
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
    Stage stage = (Stage) signInButton.getScene().getWindow();
    stage.setScene(new Scene(root));
    stage.show();
  }
}
