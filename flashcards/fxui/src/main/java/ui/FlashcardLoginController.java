package ui;

import java.io.IOException;
// import java.net.http.HttpResponse;
// import com.fasterxml.jackson.core.type.TypeReference;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FlashcardLoginController {
  @FXML private Text alertMessage;
  @FXML private Button loginButton;
  @FXML private TextField usernameField;
  @FXML private TextField passwordField;

  private boolean showAlert = false;
  private String error = "";

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
      showAlert = false;
    } else {
      alertMessage.setVisible(false);
    }

  }

  /**
   * Handles login button click event.
   * Validates username and password fields, then navigates to main app if valid.
   * Shows error message if fields are empty.
   */
  public void whenLoginButtonClicked() {
    String username = usernameField.getText().trim();
    String password = passwordField.getText().trim();

    if (username.isEmpty() || password.isEmpty()) {
      error = "Username and password\ncannot be empty";
      showAlert = true;
      updateUi();
    } else {
      // Basic validation passed - proceed to main app
      try {
        navigateToMainApp(username);
      } catch (IOException e) {
        error = "Failed to load main app";
        showAlert = true;
        updateUi();
      }
    }
  }

  // public void whenLoginButtonClicked() {
  //   String username = usernameField.getText().trim();
  //   String password = passwordField.getText().trim();

  //   if (username.isEmpty() || password.isEmpty()) {
  //       error = "Username and password\ncannot be empty";
  //       showAlert = true;
  //       updateUi();
  //   } else {
  //       // Send login request to server
  //       HttpResponse<String> response = APIClient.performRequest(
  //           "http://localhost:8080/api/auth/login", 
  //           "POST", 
  //           new LoginRequestDTO(username, password)  // from shared module
  //       );

  //       if (response != null && response.statusCode() == 200) {
  //           try {
  //               // Parse login response
  //               LoginResponseDTO loginResponse = APIClient.parseResponse(
  //                   response.body(), 
  //                   new TypeReference<LoginResponseDTO>() {}
  //               );
                
  //               // Store auth token if needed
  //               // APIClient kunne ha en setAuthToken() metode
                
  //               navigateToMainApp(username);
  //           } catch (Exception e) {
  //               error = "Login failed";
  //               showAlert = true;
  //               updateUi();
  //           }
  //       } else {
  //           error = "Invalid username or password";
  //           showAlert = true;
  //           updateUi();
  //       }
  //   }

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
}
