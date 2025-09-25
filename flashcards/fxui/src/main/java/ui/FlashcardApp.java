package ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main app for flashcards.
 */
public class FlashcardApp extends Application{
  
  /**
   * Starts the app.
   */
  public static void main(String[] args) {
    Application.launch(args);
  }

  /**
   * Sets up the main window.
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