package ui;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Test suite for {@link FlashcardApp}.
 * Tests application startup, initialization, and resource loading.
 * 
 * @author marennod
 * @author sofietw
 * @see FlashcardApp
 */
public class FlashcardAppTest {

  private static boolean javaFxInitialized = false;
  private static final int TIMEOUT_SECONDS = 15;

  /**
   * Initializes JavaFX before running tests.
   * 
   * @throws InterruptedException if initialization is interrupted
   */
  @BeforeAll
  public static void initializeJavaFx() throws InterruptedException {
    if (!javaFxInitialized) {
      CountDownLatch latch = new CountDownLatch(1);
      
      new Thread(() -> {
        try {
          Platform.startup(() -> latch.countDown());
        } catch (IllegalStateException e) {
          latch.countDown();
        }
      }).start();
      
      latch.await(5, TimeUnit.SECONDS);
      javaFxInitialized = true;
    }
  }

  /**
   * Tests that FlashcardApp can be instantiated.
   */
  @Test
  public void testCanCreateFlashcardApp() {
    FlashcardApp app = new FlashcardApp();
    assertNotNull(app, "FlashcardApp should be created successfully");
  }

  /**
   * Tests that FlashcardApp extends JavaFX Application class.
   */
  @Test
  public void testExtendsJavaFxApplication() {
    assertTrue(Application.class.isAssignableFrom(FlashcardApp.class),
      "FlashcardApp should extend Application");
  }

  /**
   * Tests that the main method exists with correct signature.
   * 
   * @throws NoSuchMethodException if main method is not found
   */
  @Test
  public void testMainMethodExists() throws NoSuchMethodException {
    assertNotNull(FlashcardApp.class.getMethod("main", String[].class),
      "Main method should exist");
  }

  /**
   * Tests that FXML login resource exists in classpath.
   */
  @Test
  public void testLoginFxmlResourceExists() {
    URL resourceUrl = FlashcardApp.class.getResource("/ui/FlashcardLogin.fxml");
    assertNotNull(resourceUrl, "FlashcardLogin.fxml should exist");
  }

  /**
   * Tests that CSS stylesheet resource exists in classpath.
   */
  @Test
  public void testLoginCssResourceExists() {
    URL resourceUrl = FlashcardApp.class.getResource("/ui/FlashcardLogin.css");
    assertNotNull(resourceUrl, "FlashcardLogin.css should exist");
  }

  /**
   * Tests the getLoginStylesheet() method directly.
   * 
   * @throws InterruptedException if the test times out
   */
  @Test
  public void testGetLoginStylesheetMethod() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    AtomicReference<URL> urlRef = new AtomicReference<>();
    
    Platform.runLater(() -> {
      try {
        new FlashcardApp() {
          {
            URL url = getLoginStylesheet();
            urlRef.set(url);
          }
        };
      } finally {
        latch.countDown();
      }
    });
    
    assertTrue(latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS), 
      "Test should complete");
    assertNotNull(urlRef.get(), 
      "Stylesheet URL should not be null");
  }

  /**
   * Tests the complete start() method execution using mock resources.
   * 
   * @throws InterruptedException if the test times out
   */
  @Test
  public void testStartMethodWithMockResources() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    AtomicReference<Boolean> success = new AtomicReference<>(false);
    
    Platform.runLater(() -> {
      try {
        FlashcardApp app = createTestableApp();
        Stage stage = new Stage();
        
        try {
          app.start(stage);
          success.set(true);
        } finally {
          if (stage.isShowing()) {
            stage.close();
          }
        }
      } catch (Exception e) {
        System.err.println("Error in start(): " + e.getMessage());
      } finally {
        latch.countDown();
      }
    });

    assertTrue(latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS), 
      "Test should complete within timeout");
    assertTrue(success.get(), 
      "start() method should execute successfully");
  }

  /**
   * Creates a testable FlashcardApp with mock resource loading.
   * This allows the start() method to execute completely without path encoding issues.
   */
  private FlashcardApp createTestableApp() {
    return new FlashcardApp() {
      @Override
      protected Parent loadLoginScene() throws IOException {
        StackPane root = new StackPane();
        root.getChildren().add(new Label("Test Login"));
        return root;
      }
      
      @Override
      protected URL getLoginStylesheet() {
        try {
          return java.net.URI.create("file:///test.css").toURL();
        } catch (Exception e) {
          return null;
        }
      }
    };
  }
}
