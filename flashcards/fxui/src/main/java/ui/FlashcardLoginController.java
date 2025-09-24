package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

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

  public void updateUi(){
    if (showAlert) {
      alertMessage.setText(error);
      alertMessage.setVisible(true);
      showAlert = false;
    } else {
      alertMessage.setVisible(false);
    }

  }

  public void whenLoginButtonClicked() {
    String username = usernameField.getText();
    String password = passwordField.getText();

    if (username.isEmpty() || password.isEmpty()) {
      error = "Username and password\ncannot be empty";
      showAlert = true;
    // Add more validation
    } else {
      // Proceed to main scene with decks for this user
      showAlert = false;
    }
    updateUi();
  }
}
