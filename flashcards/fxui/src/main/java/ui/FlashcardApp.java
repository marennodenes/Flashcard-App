package ui;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main JavaFX application class for the Flashcards application.
 * Handles application startup and initial scene configuration.
 * 
 * @author marennod
 * @author marieroe
 */
public class FlashcardApp extends Application{
  
  /**
   * Starts the JavaFX application and sets up the primary stage.
   * Loads the login UI as the initial scene and applies appropriate styling.
   *
   * @param primaryStage the primary stage for this application
   * @throws IOException if the FXML file cannot be loaded
   */
  @Override
  public void start(Stage primaryStage) throws IOException {
    primaryStage.setTitle("Flashcards App");
    Scene scene = SceneUtils.createScaledScene(loadLoginScene());
    scene.getStylesheets().add(getLoginStylesheet().toExternalForm());
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  /**
   * Loads the FXML login scene. Protected to allow testing with mock scenes.
   *
   * @return the loaded Parent node
   * @throws IOException if the FXML file cannot be loaded
   */
  protected Parent loadLoginScene() throws IOException {
    return FXMLLoader.load(getClass().getResource("/ui/FlashcardLogin.fxml"));
  }

  /**
   * Gets the URL for the login CSS stylesheet. Protected to allow testing.
   *
   * @return the URL to the CSS file
   */
  protected URL getLoginStylesheet() {
    return getClass().getResource("/ui/FlashcardLogin.css");
  }

  /**
   * Main entry point for the application.
   * Launches the JavaFX application.
   * 
   * @param args command line arguments
   */
  public static void main(String[] args) {
    Application.launch(args);
  }
}