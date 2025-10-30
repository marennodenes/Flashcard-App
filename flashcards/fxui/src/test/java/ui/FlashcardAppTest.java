package ui;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Test class for FlashcardApp.
 * Tests JavaFX application startup and initialization with actual code execution.
 * 
 * @author marennod
 * @author sofietw
 */
public class FlashcardAppTest {

    private static boolean javaFxInitialized = false;

    /**
     * Initializes JavaFX toolkit once before all tests.
     */
    @BeforeAll
    public static void initJavaFX() throws InterruptedException {
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
    public void testFlashcardAppInstantiation() {
        FlashcardApp app = new FlashcardApp();
        assertNotNull(app, "FlashcardApp instance should not be null");
    }

    /**
     * Tests the complete start method execution including all lines.
     * This achieves full code coverage by actually running the start() method.
     */
    @Test
    public void testStartMethodCompleteExecution() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> errorRef = new AtomicReference<>();
        AtomicReference<Stage> stageRef = new AtomicReference<>();
        AtomicReference<Boolean> allLinesExecuted = new AtomicReference<>(false);

        Platform.runLater(() -> {
            try {
                FlashcardApp app = new FlashcardApp();
                Stage stage = new Stage();
                stageRef.set(stage);
                
                // This will execute all lines in the start() method
                app.start(stage);
                
                // Verify all the lines were executed
                assertNotNull(stage.getTitle(), "Title should be set (line 38)");
                assertEquals("Flashcards App", stage.getTitle(), "Title should be 'Flashcards App' (line 38)");
                assertNotNull(stage.getScene(), "Scene should be created (line 39)");
                assertNotNull(stage.getScene().getRoot(), "FXML should be loaded (line 39)");
                assertFalse(stage.getScene().getStylesheets().isEmpty(), "Stylesheet should be added (line 40)");
                assertTrue(stage.isShowing(), "Stage should be showing (line 42)");
                
                allLinesExecuted.set(true);
                
            } catch (Throwable e) {
                errorRef.set(e);
            } finally {
                Stage stage = stageRef.get();
                if (stage != null && stage.isShowing()) {
                    stage.close();
                }
                latch.countDown();
            }
        });

        assertTrue(latch.await(15, TimeUnit.SECONDS), "Test should complete within timeout");
        
        Throwable error = errorRef.get();
        if (error != null) {
            fail("start() method failed: " + error.getMessage() + "\nCause: " + 
                 (error.getCause() != null ? error.getCause().getMessage() : "none"));
        }
        
        assertTrue(allLinesExecuted.get(), "All lines in start() should execute successfully");
    }

    /**
     * Tests that primaryStage.setTitle() is called with correct value.
     */
    @Test
    public void testSetTitle() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> titleRef = new AtomicReference<>();

        Platform.runLater(() -> {
            try {
                FlashcardApp app = new FlashcardApp();
                Stage stage = new Stage();
                app.start(stage);
                titleRef.set(stage.getTitle());
                stage.close();
            } catch (Exception e) {
                // Expected if resources not available
            } finally {
                latch.countDown();
            }
        });

        latch.await(10, TimeUnit.SECONDS);
        assertEquals("Flashcards App", titleRef.get(), "Stage title should be set correctly");
    }

    /**
     * Tests that Scene is created with FXMLLoader.
     */
    @Test
    public void testSceneCreation() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> sceneCreated = new AtomicReference<>(false);

        Platform.runLater(() -> {
            try {
                FlashcardApp app = new FlashcardApp();
                Stage stage = new Stage();
                app.start(stage);
                sceneCreated.set(stage.getScene() != null);
                stage.close();
            } catch (Exception e) {
                // Expected if resources not available
            } finally {
                latch.countDown();
            }
        });

        latch.await(10, TimeUnit.SECONDS);
        assertTrue(sceneCreated.get(), "Scene should be created from FXML");
    }

    /**
     * Tests that CSS stylesheet is added to scene.
     */
    @Test
    public void testStylesheetAddition() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> stylesheetAdded = new AtomicReference<>(false);

        Platform.runLater(() -> {
            try {
                FlashcardApp app = new FlashcardApp();
                Stage stage = new Stage();
                app.start(stage);
                
                if (stage.getScene() != null) {
                    stylesheetAdded.set(!stage.getScene().getStylesheets().isEmpty());
                }
                
                stage.close();
            } catch (Exception e) {
                // Expected if resources not available
            } finally {
                latch.countDown();
            }
        });

        latch.await(10, TimeUnit.SECONDS);
        assertTrue(stylesheetAdded.get(), "Stylesheet should be added to scene");
    }

    /**
     * Tests that primaryStage.setScene() is called.
     */
    @Test
    public void testSetScene() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> sceneSet = new AtomicReference<>(false);

        Platform.runLater(() -> {
            try {
                FlashcardApp app = new FlashcardApp();
                Stage stage = new Stage();
                app.start(stage);
                sceneSet.set(stage.getScene() != null);
                stage.close();
            } catch (Exception e) {
                // Expected if resources not available
            } finally {
                latch.countDown();
            }
        });

        latch.await(10, TimeUnit.SECONDS);
        assertTrue(sceneSet.get(), "Scene should be set on stage");
    }

    /**
     * Tests that primaryStage.show() is called.
     */
    @Test
    public void testShowStage() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> stageShown = new AtomicReference<>(false);

        Platform.runLater(() -> {
            try {
                FlashcardApp app = new FlashcardApp();
                Stage stage = new Stage();
                app.start(stage);
                stageShown.set(stage.isShowing());
                stage.close();
            } catch (Exception e) {
                // Expected if resources not available
            } finally {
                latch.countDown();
            }
        });

        latch.await(10, TimeUnit.SECONDS);
        assertTrue(stageShown.get(), "Stage should be shown");
    }

    /**
     * Tests that FXML resource exists.
     */
    @Test
    public void testFXMLResourceExists() {
        assertNotNull(
            FlashcardApp.class.getResource("/ui/FlashcardLogin.fxml"),
            "FlashcardLogin.fxml should exist"
        );
    }

    /**
     * Tests that CSS resource exists.
     */
    @Test
    public void testCSSResourceExists() {
        assertNotNull(
            FlashcardApp.class.getResource("/ui/FlashcardLogin.css"),
            "FlashcardLogin.css should exist"
        );
    }

    /**
     * Tests that main method exists.
     */
    @Test
    public void testMainMethodExists() throws NoSuchMethodException {
        assertNotNull(FlashcardApp.class.getMethod("main", String[].class));
    }

    /**
     * Tests that the class extends Application.
     */
    @Test
    public void testExtendsApplication() {
        assertTrue(javafx.application.Application.class.isAssignableFrom(FlashcardApp.class));
    }
}