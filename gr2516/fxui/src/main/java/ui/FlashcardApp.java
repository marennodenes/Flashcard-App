package ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FlashcardApp extends Application{
  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
      primaryStage.setTitle("Flashcards App");
      Scene scene = new Scene(FXMLLoader.load(getClass().getResource("FlashcardMainUI.fxml")));
      scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
      primaryStage.setScene(scene);
      primaryStage.show();
  }


}

