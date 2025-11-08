package ui;

import com.fasterxml.jackson.core.type.TypeReference;
import dto.LoginRequestDto;
import dto.UserDataDto;
import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import shared.ApiConstants;
import shared.ApiEndpoints;
import shared.ApiResponse;

/**
 * Controller for the flashcard sign-up page.
 * Handles user registration with validation and navigation to the main application.
 *
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
  public void updateUi() {
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
   * Shows inline errors for validation failures and popup errors for server issues.
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
      stage.setScene(SceneUtils.createScaledScene(root));
      stage.show();
    } catch (IOException e) {
      System.err.println(ApiConstants.LOAD_ERROR + ": " + e.getMessage());
      ApiClient.showAlert(ApiConstants.LOAD_ERROR, ApiConstants.UNEXPECTED_ERROR);
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
      showInlineError(ApiConstants.EMPTY_FIELDS);
      return false;
    }

    // Check if passwords match
    if (!password.equals(confirmedPassword)) {
      showInlineError(ApiConstants.PASSWORDS_NOT_EQUAL);
      return false;
    }

    return true;
  }

  /**
   * Attempts to create a new user via REST API.
   * Shows popup error if server connection fails, inline error for other server responses.
   *
   * @param username the username to create
   * @param password the password for the user
   */
  private void createUser(String username, String password) {
    ApiResponse<UserDataDto> result = null;
    try {
      result = ApiClient.performApiRequest(
        ApiEndpoints.REGISTER_URL,
        "POST", 
        new LoginRequestDto(username, password),
        new TypeReference<ApiResponse<UserDataDto>>() {}
      );
    } catch (RuntimeException e) {
      System.err.println(ApiConstants.SERVER_ERROR + ": " + e.getMessage());
      // Show popup when server can't be reached
      ApiClient.showAlert(ApiConstants.SERVER_ERROR, ApiConstants.SERVER_CONNECTION_ERROR);
      return;
    }

    if (result.isSuccess()) {
      navigateToMainApp(username);
    } else {
      // Handle different types of errors with specific text messages
      String errorMessage = result.getMessage();
      if (errorMessage != null && errorMessage.toLowerCase().contains("already exists")) {
        showInlineError(ApiConstants.USER_ALREADY_EXISTS);
      } else {
        //Show the error message directly as text
        showInlineError(errorMessage != null ? errorMessage : ApiConstants.SERVER_CONNECTION_ERROR);
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
  private void navigateToMainApp(String username) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/FlashcardMain.fxml"));
      Parent root = loader.load();
      
      // Get the controller and set the username
      FlashcardMainController mainController = loader.getController();
      mainController.setCurrentUsername(username);
      
      // Switch to the main scene
      Stage stage = (Stage) signInButton.getScene().getWindow();
      stage.setScene(SceneUtils.createScaledScene(root));
      stage.show();
    } catch (IOException e) {
      System.err.println(ApiConstants.LOAD_ERROR + ": " + e.getMessage());
      ApiClient.showAlert(ApiConstants.LOAD_ERROR, ApiConstants.UNEXPECTED_ERROR);
    }
  }
  
  /**
   * Shows an inline error message without popup.
   * Used for validation errors that should only appear as text.
   *
   * @param message the error message to display inline
   */
  private void showInlineError(String message) {
    // Update state and ensure the UI refresh runs on the JavaFX Application Thread.
    // Validation errors show as inline text only
    error = message;
    showAlert = true;
    if (Platform.isFxApplicationThread()) {
      updateUi();
    } else {
      Platform.runLater(this::updateUi);
    }
  }
}
