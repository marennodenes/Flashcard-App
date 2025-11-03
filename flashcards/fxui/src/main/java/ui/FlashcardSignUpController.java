package ui;

import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;

import dto.LoginRequestDto;
import dto.UserDataDto;
import shared.ApiResponse;
import shared.ApiEndpoints;
import shared.ApiConstants;
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
 * @author sofietw
 * @author ailinat
 */
public class FlashcardSignUpController {
  @FXML private Text alertMessage;
  @FXML private Text ex;
  @FXML private TextField usernameField;
  @FXML private TextField passwordField;
  @FXML private TextField confirmPasswordField;
  @FXML private Button signInButton;
  @FXML private Button backButton;

  private boolean showAlert = false;
  private String error = "";

  // Test hook: allow tests to provide a custom FXMLLoader (so navigateToMainApp can be tested without loading real FXML)
  public static volatile java.util.function.Supplier<javafx.fxml.FXMLLoader> TEST_FXMLLOADER_SUPPLIER = null;
  
  /**
   * Initializes the controller after FXML loading.
   * Updates the UI to initial state.
   */
  public void initialize() {
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
      ex.setVisible(true);
      showAlert = false;
    } else {
      alertMessage.setVisible(false);
      ex.setVisible(false);
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
   * Handles the action when the back button is clicked.
   * This method loads the FlashcardLogin.fxml file and sets it as the current scene.
   * It retrieves the current stage from the back button's scene and updates the scene 
   * to display the login interface.
   *
   * @throws IOException if the FXML file cannot be loaded.
   */
  @FXML
  public void whenBackButtonIsClicked() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/FlashcardLogin.fxml"));
      Parent root = loader.load();
      Stage stage = (Stage) backButton.getScene().getWindow();
      stage.setScene(new Scene(root));
      stage.show();
    } catch (Exception e) {
      showError(ApiConstants.INVALID_REQUEST);
    }
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
      showError(ApiConstants.INVALID_REQUEST);
      return false;
    }

    // Check if passwords match
    if (!password.equals(confirmedPassword)) {
      System.out.println("Passwords must be equal");
      showError(ApiConstants.INVALID_PASSWORD);
      return false;
    }

    // Password policy: at least 8 chars, one uppercase, one digit, one special character
    if (password.length() < 8
        || !password.matches(".*[A-Z].*")
        || !password.matches(".*\\d.*")
        || !password.matches(".*[^a-zA-Z0-9].*")) {
      showError(ApiConstants.INVALID_PASSWORD);
      return false;
    }

    return true;
  }

  /**
   * Attempts to create a new user via REST API.
   * 
   * @param username the username to create
   * @param password the password for the user
   */
  private void createUser(String username, String password) {
    ApiResponse<UserDataDto> result = ApiClient.performApiRequest(
      ApiEndpoints.REGISTER_URL,
      "POST", 
      new LoginRequestDto(username, password),
      new TypeReference<ApiResponse<UserDataDto>>() {}
    );

    if (result.isSuccess()) {
      // User created successfully via API
      System.out.println("User created via API: " + username);
      try {
        navigateToMainApp(username);
      } catch (IOException e) {
        ApiClient.showAlert("Error", "Failed to load main application");
      }
    } else {
      // Handle different types of errors with specific text messages
      String errorMessage = result.getMessage();
      if (errorMessage.toLowerCase().contains("already exists")) {
        showError(ApiConstants.USER_ALREADY_EXISTS);
      } else {
        // For general server errors show a modal alert (kept out of inline error)
        ApiClient.showAlert("Registration Error", errorMessage);
      }
      System.out.println("Registration failed: " + errorMessage);
    }
  }

  /**
   * Shows an error message to the user.
   * 
   * @param message the error message to display
   */
  private void showError(String message) {
    // Update state and ensure the UI refresh runs on the JavaFX Application Thread.
    error = message;
    showAlert = true;
    if (javafx.application.Platform.isFxApplicationThread()) {
      updateUi();
    } else {
      javafx.application.Platform.runLater(this::updateUi);
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
    FXMLLoader loader;
    if (TEST_FXMLLOADER_SUPPLIER != null) {
      loader = TEST_FXMLLOADER_SUPPLIER.get();
    } else {
      loader = new FXMLLoader(getClass().getResource("/ui/FlashcardMain.fxml"));
    }
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
