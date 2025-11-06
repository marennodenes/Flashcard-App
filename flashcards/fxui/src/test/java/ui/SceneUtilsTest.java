package ui;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.testfx.framework.junit5.ApplicationTest;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

/**
 * Unit tests for the {@link SceneUtils} utility class.
 * Verifies scene creation and scaling functionality.
 * 
 * @author marennod
 * @author chrsom
 * @see SceneUtils
 */
public class SceneUtilsTest extends ApplicationTest {

    private static final double SCALE_FACTOR = 2.0;
    private static final double DELTA = 0.01; // Tolerance for double comparisons

    @BeforeAll
    public static void setUpClass() {
        // Initialize JavaFX toolkit (handled by ApplicationTest)
    }

    /**
     * Tests that createScaledScene creates a scene with correct dimensions
     * when given a root with preferred size.
     */
    @Test
    public void testCreateScaledSceneWithPreferredSize() {
        // Arrange
        AnchorPane root = new AnchorPane();
        root.setPrefWidth(416.0);
        root.setPrefHeight(264.0);

        // Act
        Scene scene = SceneUtils.createScaledScene(root);

        // Assert
        assertNotNull(scene, "Scene should not be null");
        assertEquals(416.0 * SCALE_FACTOR, scene.getWidth(), DELTA, 
            "Scene width should be doubled");
        assertEquals(264.0 * SCALE_FACTOR, scene.getHeight(), DELTA, 
            "Scene height should be doubled");
    }

    /**
     * Tests that createScaledScene uses default dimensions when root has no preferred size.
     */
    @Test
    public void testCreateScaledSceneWithDefaultSize() {
        // Arrange
        AnchorPane root = new AnchorPane();
        // Don't set preferred size - should use defaults

        // Act
        Scene scene = SceneUtils.createScaledScene(root);

        // Assert
        assertNotNull(scene, "Scene should not be null");
        assertEquals(416.0 * SCALE_FACTOR, scene.getWidth(), DELTA, 
            "Scene width should use default and be doubled");
        assertEquals(264.0 * SCALE_FACTOR, scene.getHeight(), DELTA, 
            "Scene height should use default and be doubled");
    }

    /**
     * Tests that scaling is correctly applied to the root node.
     */
    @Test
    public void testApplyScalingToRoot() {
        // Arrange
        AnchorPane root = new AnchorPane();
        root.setPrefWidth(416.0);
        root.setPrefHeight(264.0);

        // Act
        SceneUtils.applyScaling(root);

        // Assert
        assertEquals(SCALE_FACTOR, root.getScaleX(), DELTA, 
            "ScaleX should be set to scale factor");
        assertEquals(SCALE_FACTOR, root.getScaleY(), DELTA, 
            "ScaleY should be set to scale factor");
    }

    /**
     * Tests that the scene root is wrapped in a StackPane for proper layout.
     */
    @Test
    public void testSceneRootIsWrappedInStackPane() {
        // Arrange
        AnchorPane root = new AnchorPane();
        root.setPrefWidth(416.0);
        root.setPrefHeight(264.0);

        // Act
        Scene scene = SceneUtils.createScaledScene(root);

        // Assert
        assertNotNull(scene.getRoot(), "Scene root should not be null");
        assertTrue(scene.getRoot() instanceof StackPane, 
            "Scene root should be wrapped in StackPane");
    }

    /**
     * Tests that translation is applied to center scaled content.
     */
    @Test
    public void testTranslationIsAppliedForCentering() {
        // Arrange
        AnchorPane root = new AnchorPane();
        root.setPrefWidth(416.0);
        root.setPrefHeight(264.0);

        // Act
        SceneUtils.createScaledScene(root);

        // Assert
        // Translation should be applied to compensate for scaling
        assertNotEquals(0.0, root.getTranslateX(), 
            "TranslateX should be set to center content");
        assertNotEquals(0.0, root.getTranslateY(), 
            "TranslateY should be set to center content");
    }

    /**
     * Tests that createScaledScene works with custom dimensions.
     */
    @Test
    public void testCreateScaledSceneWithCustomDimensions() {
        // Arrange
        AnchorPane root = new AnchorPane();
        root.setPrefWidth(600.0);
        root.setPrefHeight(400.0);

        // Act
        Scene scene = SceneUtils.createScaledScene(root);

        // Assert
        assertEquals(600.0 * SCALE_FACTOR, scene.getWidth(), DELTA, 
            "Scene width should match custom size doubled");
        assertEquals(400.0 * SCALE_FACTOR, scene.getHeight(), DELTA, 
            "Scene height should match custom size doubled");
    }

    /**
     * Tests that the private constructor throws UnsupportedOperationException
     * to prevent instantiation (for JaCoCo coverage).
     */
    @Test
    public void testPrivateConstructorThrowsException() {
        // Act & Assert
        Exception exception = assertThrows(
            Exception.class,
            () -> {
                // Use reflection to access private constructor
                var constructor = SceneUtils.class.getDeclaredConstructor();
                constructor.setAccessible(true);
                constructor.newInstance();
            },
            "Private constructor should throw exception"
        );

        // Reflection wraps the exception in InvocationTargetException
        assertTrue(exception.getCause() instanceof UnsupportedOperationException,
            "Cause should be UnsupportedOperationException");
        assertEquals("Utility class", exception.getCause().getMessage(), 
            "Exception message should be 'Utility class'");
    }

    /**
     * Tests that applyScaling doesn't throw when called multiple times.
     */
    @Test
    public void testApplyScalingMultipleTimes() {
        // Arrange
        AnchorPane root = new AnchorPane();

        // Act & Assert - should not throw
        assertDoesNotThrow(() -> {
            SceneUtils.applyScaling(root);
            SceneUtils.applyScaling(root); // Apply again
        });

        // Verify scale is still correct
        assertEquals(SCALE_FACTOR, root.getScaleX(), DELTA);
        assertEquals(SCALE_FACTOR, root.getScaleY(), DELTA);
    }

    /**
     * Tests that createScaledScene handles very small preferred sizes.
     */
    @Test
    public void testCreateScaledSceneWithSmallSize() {
        // Arrange
        AnchorPane root = new AnchorPane();
        root.setPrefWidth(100.0);
        root.setPrefHeight(50.0);

        // Act
        Scene scene = SceneUtils.createScaledScene(root);

        // Assert
        assertNotNull(scene, "Scene should not be null");
        assertEquals(100.0 * SCALE_FACTOR, scene.getWidth(), DELTA);
        assertEquals(50.0 * SCALE_FACTOR, scene.getHeight(), DELTA);
    }
}
