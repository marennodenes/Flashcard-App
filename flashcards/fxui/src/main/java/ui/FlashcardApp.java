package ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
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
   * Main entry point for the application.
   * Launches the JavaFX application.
   * 
   * @param args command line arguments
   */
  public static void main(String[] args) {
    Application.launch(args);
  }

  /**
   * Sets up and displays the primary stage with the login scene.
   * Configures the window title, loads the login UI, and applies styling.
   * 
   * @param primaryStage the primary stage for this application
   * @throws IOException if the FXML file cannot be loaded
   */
  @Override
  public void start(Stage primaryStage) throws IOException {
      primaryStage.setTitle("Flashcards App");
      Scene scene = new Scene(FXMLLoader.load(getClass().getResource("FlashcardLogin.fxml")));
      scene.getStylesheets().add(getClass().getResource("FlashcardLogin.css").toExternalForm());
      primaryStage.setScene(scene);
      primaryStage.show();
  }

}