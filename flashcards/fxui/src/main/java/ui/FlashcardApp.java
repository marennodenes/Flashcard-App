package ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Main JavaFX application class for the Flashcards application.
 * Handles application startup and initial scene configuration.
 *
 * @author marennod
 * @author marieroe
 */
public class FlashcardApp extends Application {
  
  /**
   * Starts the JavaFX application and sets up the primary stage.
   * Loads the login UI as the initial scene and applies appropriate styling.
   *
   * @param primaryStage the primary stage for this application
   * @throws IOException if the FXML file cannot be loaded
   */
  @Override
  public void start(Stage primaryStage) throws IOException {
    preloadFonts();
    primaryStage.setTitle("Flashcards App");
    primaryStage.setResizable(false);
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

  /**
   * Preloads custom fonts used in the application to ensure they are available when needed.
   * Loads the Ubuntu Regular and Bold fonts from resources.
   * This ensures consistent font rendering across different platforms.
   */
  private void preloadFonts() {
    loadFont("/fonts/Ubuntu-Regular.ttf");
    loadFont("/fonts/Ubuntu-Bold.ttf");
  }

  /**
   * Loads a font from the specified resource path.
   *
   * @param path the path to the font resource
   */
  private void loadFont(String path) {
    try (InputStream stream = getClass().getResourceAsStream(path)) {
      if (stream != null) {
        Font.loadFont(stream, 14);
      } else {
        System.err.println("Could not find font resource: " + path);
      }
    } catch (IOException e) {
      System.err.println("Failed to load font " + path + ": " + e.getMessage());
    }
  }
}