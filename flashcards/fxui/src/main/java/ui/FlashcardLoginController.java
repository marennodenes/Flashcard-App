package ui;

import java.io.IOException;

import com.fasterxml.jackson.core.type.TypeReference;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import dto.LoginRequestDto;
import shared.ApiResponse;
import shared.ApiEndpoints;
import dto.LoginResponseDto;
/**
 * Controller for the Flashcard Login UI. Handles user login.
 * 
 * @author marieroe
 * @author sofietw
 * @author ailinat
 */
public class FlashcardLoginController {
  @FXML
  private Text alertMessage;
  @FXML
  private Text ex;
  @FXML
  private Button loginButton;
  @FXML
  private TextField usernameField;
  @FXML
  private TextField passwordField;
  @FXML
  private Button signUpButton;

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
   * Updates the UI elements based on the current state. Shows or hides alert
   * messages as needed.
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
   * Handles login button click event. Validates username and password fields,
   * then navigates to main app if valid. Shows error message if fields are empty
   * or invalid.
   */
  @FXML
  public void whenLoginButtonClicked() {
    String username = usernameField.getText().trim();
    String password = passwordField.getText().trim();

    // Validate that both fields have content
    if (username.isEmpty() || password.isEmpty()) {
      error = "Username and password\ncannot be empty";
      showAlert = true;
      updateUi();
    } else {
      // Send login request to server
      ApiResponse<LoginResponseDto> result = ApiClient.performApiRequest(
        ApiEndpoints.LOGIN_URL, 
        "POST",
        new LoginRequestDto(username, password),
        new TypeReference<LoginResponseDto>() {}
      );

      if (result.isSuccess() && result.getData() != null) {
        LoginResponseDto loginResponse = result.getData();
        
        // Check if server confirmed login success
        if (loginResponse.isSuccess()) {
          try {
            navigateToMainApp(username);
            return; // Exit early on success
          } catch (IOException e) {
            error = "Failed to load main application";
          }
        } else {
          // Use server's specific error message
          error = loginResponse.getMessage();
        }
      } else {
        error = result.getMessage();
      }
      
      // Show error (only reached if login failed)
      showAlert = true;
      updateUi();
    }
  }

  /**
   * Handles the sign-up button click event. Navigates to the sign-up page where
   * users can create new accounts. Shows error message if the sign-up page fails
   * to load.
   */
  @FXML
  public void whenSignUpButtonClicked() {
    try {
      navigateToSignUpPage();
    } catch (IOException e) {
      error = "Failed to load signup page";
      showAlert = true;
      updateUi();
    }
  }

  /**
   * Navigates to the sign-up page. Loads the FlashcardSignUpUI FXML file and
   * switches to the sign-up scene.
   * 
   * @throws IOException if the FXML file cannot be loaded
   */
  private void navigateToSignUpPage() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardSignUp.fxml"));
    Parent root = loader.load();

    Stage stage = (Stage) signUpButton.getScene().getWindow();
    stage.setScene(new Scene(root));
    stage.show();
  }

  /**
   * Navigates to the main flashcard application. Loads the FlashcardMainUI and
   * passes the username to the controller.
   * 
   * @param username the logged-in username to pass to the main controller
   * @throws IOException if the FXML file cannot be loaded
   */
  private void navigateToMainApp(String username) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardMain.fxml"));
    Parent root = loader.load();

    // Get the controller and set the username
    FlashcardMainController mainController = loader.getController();
    mainController.setCurrentUsername(username);

    // Switch to the main scene
    Stage stage = (Stage) loginButton.getScene().getWindow();
    stage.setScene(new Scene(root));
    stage.show();
  }
}
