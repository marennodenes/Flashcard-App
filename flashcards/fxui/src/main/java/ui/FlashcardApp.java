package ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main JavaFX application class for the Flashcards application.
 * Handles application startup and primary stage setup.
 */
public class FlashcardApp extends Application{
  
  /**
   * Entry point for the application.
   * Launches the JavaFX application with the provided command line arguments.
   * 
   * @param args command line arguments passed to the application
   */
  public static void main(String[] args) {
    Application.launch(args);
  }

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
      Scene scene = new Scene(FXMLLoader.load(getClass().getResource("FlashcardLoginUI.fxml")));
      scene.getStylesheets().add(getClass().getResource("FlashcardLogin.css").toExternalForm());
      primaryStage.setScene(scene);
      primaryStage.show();
  }

}