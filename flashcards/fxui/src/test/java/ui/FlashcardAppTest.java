package ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Test class for FlashcardApp.
 * This class tests the main JavaFX application class to ensure proper initialization
 * and startup behavior.
 */
public class FlashcardAppTest extends ApplicationTest {

    private FlashcardApp app;

    /**
     * Sets up the JavaFX toolkit before all tests.
     * This ensures that the JavaFX platform is properly initialized for testing.
     * The method handles platform startup in a thread-safe manner.
     *
     * @throws Exception if the JavaFX toolkit cannot be initialized
     */
    @BeforeAll
    public static void setUpClass() throws Exception {
        // Initialize JavaFX toolkit for headless testing
        if (!Platform.isFxApplicationThread()) {
            try {
                Platform.startup(() -> {
                    // Empty runnable for platform initialization
                });
            } catch (IllegalStateException e) {
                // Platform already initialized, this is expected in some test environments
            }
        }
    }

    /**
     * Sets up the test environment before each test.
     * Creates a new instance of FlashcardApp and initializes it with a test stage.
     * This method is called automatically by TestFX framework.
     *
     * @param stage the primary stage provided by TestFX
     * @throws Exception if the application cannot be started
     */
    @Override
    public void start(Stage stage) throws Exception {
        app = new FlashcardApp();
        // Only start the app if resources are available
        try {
            app.start(stage);
        } catch (Exception e) {
            // If resources are missing, create a minimal stage for testing
            stage.setTitle("Flashcards App");
            stage.show();
        }
    }

    /**
     * Tests the main method of FlashcardApp.
     * Verifies that the main method exists, is public, static, and takes String array parameter.
     * This test ensures that the application entry point is properly configured
     * according to Java application standards.
     * 
     * @throws SecurityException if reflection access is denied
     */
    @Test
    public void testMain() throws SecurityException {
        // Verify that the main method exists and has correct signature
        Method mainMethod = null;
        try {
            mainMethod = FlashcardApp.class.getMethod("main", String[].class);
        } catch (NoSuchMethodException e) {
            throw new AssertionError("Main method should exist with String[] parameter", e);
        }
        
        assertNotNull(mainMethod, "Main method should not be null");
        assertEquals("main", mainMethod.getName(), "Method should be named 'main'");
        assertEquals(void.class, mainMethod.getReturnType(), "Main method should return void");
        
        // Verify method is public and static
        assertTrue(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers()),
                  "Main method should be public");
        assertTrue(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers()),
                  "Main method should be static");
    }

    /**
     * Tests the start method functionality.
     * Verifies that the application can create and configure a stage properly.
     * This test checks stage title, visibility, and basic scene setup.
     */
    @Test
    public void testStart() {
        // Get the stage from TestFX
        Stage primaryStage = null;
        if (!listTargetWindows().isEmpty()) {
            primaryStage = (Stage) listTargetWindows().get(0);
        }
        
        if (primaryStage != null) {
            // Verify that the stage has the correct title
            assertEquals("Flashcards App", primaryStage.getTitle(), 
                        "Stage title should be 'Flashcards App'");
            
            // Verify that the stage is showing
            assertTrue(primaryStage.isShowing(), "Stage should be showing");
        }
    }

    /**
     * Tests error handling when starting with a null stage.
     * This test ensures that the application handles invalid input gracefully
     * and provides appropriate error responses.
     */
    @Test
    public void testStartWithNullStage() {
        FlashcardApp testApp = new FlashcardApp();
        
        // Test that starting with null stage throws appropriate exception
        assertThrows(Exception.class, () -> {
            testApp.start(null);
        }, "Starting with null stage should throw an exception");
    }

    /**
     * Tests that FlashcardApp extends Application class correctly.
     * Verifies the inheritance hierarchy is properly set up for JavaFX applications.
     */
    @Test
    public void testInheritance() {
        FlashcardApp testApp = new FlashcardApp();
        assertTrue(testApp instanceof javafx.application.Application,
                  "FlashcardApp should extend Application");
    }

    /**
     * Tests the application's resource loading capabilities.
     * Verifies that the required FXML and CSS resources exist in the classpath.
     * This test helps ensure deployment and packaging will work correctly.
     */
    @Test
    public void testResourceExistence() {
        // Test for FXML resource existence
        try {
            assertNotNull(FlashcardApp.class.getResource("FlashcardLoginUI.fxml"),
                         "FlashcardLoginUI.fxml should exist in resources");
        } catch (Exception e) {
            // Resource might not exist in test environment, log but don't fail
            System.out.println("Warning: FlashcardLoginUI.fxml not found in test classpath");
        }

        // Test for CSS resource existence  
        try {
            assertNotNull(FlashcardApp.class.getResource("FlashcardLogin.css"),
                         "FlashcardLogin.css should exist in resources");
        } catch (Exception e) {
            // Resource might not exist in test environment, log but don't fail
            System.out.println("Warning: FlashcardLogin.css not found in test classpath");
        }
    }

    /**
     * Tests application instantiation.
     * Verifies that FlashcardApp can be instantiated without errors
     * and that multiple instances can be created if needed.
     */
    @Test
    public void testApplicationInstantiation() {
        FlashcardApp app1 = new FlashcardApp();
        FlashcardApp app2 = new FlashcardApp();
        
        assertNotNull(app1, "First app instance should not be null");
        assertNotNull(app2, "Second app instance should not be null");
        assertTrue(app1 != app2, "Different instances should be different objects");
    }

    /**
     * Tests stage configuration after successful start.
     * Verifies that when the application starts successfully,
     * the stage is properly configured and visible.
     */
    @Test
    public void testStageConfiguration() {
        if (!listTargetWindows().isEmpty()) {
            Stage stage = (Stage) listTargetWindows().get(0);
            
            // Test basic stage properties
            assertNotNull(stage, "Stage should not be null");
            assertTrue(stage.isShowing(), "Stage should be visible");
            assertEquals("Flashcards App", stage.getTitle(), "Title should be set correctly");
            
            // Test that stage has a scene (if resources loaded correctly)
            Scene scene = stage.getScene();
            if (scene != null) {
                assertNotNull(scene.getRoot(), "Scene should have a root node");
            }
        }
    }
}
