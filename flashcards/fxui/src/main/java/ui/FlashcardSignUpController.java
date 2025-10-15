package ui;

import java.io.IOException;
import java.net.http.HttpResponse;

import app.LoginValidator;
import dto.LoginRequestDto;
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
   * Validates user input and initiates the user registration process.
   */
  @FXML
  public void whenSignInButtonClicked() {
    String username = usernameField.getText().trim();
    String password = passwordField.getText().trim();
    String confirmedPassword = confirmPasswordField.getText().trim();

    // Validate input fields
    if (!validateInput(username, password, confirmedPassword)) {
      return;
    }

    // Attempt to create user
    createUser(username, password);
  }

  /**
   * Validates user input fields.
   * 
   * @param username the entered username
   * @param password the entered password
   * @param confirmedPassword the confirmed password
   * @return true if all validation passes, false otherwise
   */
  private boolean validateInput(String username, String password, String confirmedPassword) {
    // Check for empty fields
    if (username.isEmpty() || password.isEmpty() || confirmedPassword.isEmpty()) {
      showError("Username and password fields\ncannot be empty");
      return false;
    }

    // Check if passwords match
    if (!password.equals(confirmedPassword)) {
      System.out.println("Passwords must be equal");
      showError("Passwords must be equal");
      return false;
    }

    return true;
  }

  /**
   * Attempts to create a new user via REST API, with fallback to local storage.
   * 
   * @param username the username to create
   * @param password the password for the user
   */
  private void createUser(String username, String password) {
    try {
      if (tryCreateUserViaAPI(username, password)) {
        return; // Success via API
      }
      
      // API failed, fall back to local storage
      System.out.println("API failed, falling back to local storage");
      createUserWithFallback(username, password);
      
    } catch (Exception e) {
      // API call failed, fall back to local storage
      System.out.println("API error, falling back to local storage: " + e.getMessage());
      createUserWithFallback(username, password);
    }
  }

  /**
   * Try to create user via REST API
   * @param username the username for the new user
   * @param password the password for the user
   * @return true if user was successfully created via API, false if API call failed
   */
  private boolean tryCreateUserViaAPI(String username, String password) {
    HttpResponse<String> response = APIClient.performRequest(
      "http://localhost:8080/api/users/register", 
      "POST", 
      new LoginRequestDto(username, password)
    );

    if (response != null && response.statusCode() == 201) {
      // User created successfully via API
      System.out.println("User created via API: " + username);
      try {
        navigateToMainApp(username);
        return true;
      } catch (IOException e) {
        showError("Failed to load main application");
        return false;
      }
    } else if (response != null && response.statusCode() == 409) {
      // Username already exists
      showError("Username already exists,\ntry with another username");
      return false;
    }
    
    // API call failed or returned unexpected status
    return false;
  }

  /**
   * Shows an error message to the user.
   * 
   * @param message the error message to display
   */
  private void showError(String message) {
    error = message;
    showAlert = true;
    updateUi();
  }

  /**
   * Creates user using local storage as fallback when API is unavailable.
   * 
   * @param username the username to create
   * @param password the password for the user
   */
  private void createUserWithFallback(String username, String password) {
    try {
      // Check if user exists locally
      if (!loginValidator.isUsernameUnique(username)) {
        System.out.println("Username already exists " + username);
        showError("Username already exists,\ntry with another username");
        return;
      }

      // Create user locally
      if (loginValidator.createUser(username, password)) {
        System.out.println("User created locally: " + username);
        navigateToMainApp(username);
      } else {
        showError("Failed to create user account");
      }
    } catch (IOException e) {
      showError("Failed to load main application");
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
