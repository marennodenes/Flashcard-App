package ui;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
 * 
 * @see SceneUtils
 * 
 */
public class SceneUtilsTest extends ApplicationTest {

  private static final double SCALE_FACTOR = 2.0;
  private static final double DELTA = 0.01; // Tolerance for double comparisons

  /**
   * Initializes JavaFX toolkit before running tests.
   * This is handled by the ApplicationTest base class.
   */
  @BeforeAll
  public static void setUpClass() {
    // Initialize JavaFX toolkit (handled by ApplicationTest)
  }

  /**
   * Tests scene creation with explicit preferred dimensions.
   * Verifies that scene width and height are correctly scaled (doubled) from 416x264.
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
   * Tests scene creation when no preferred size is set on root.
   * Verifies that default dimensions (416x264) are used and properly scaled.
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
   * Tests direct scaling application to root node.
   * Verifies that both scaleX and scaleY are set to the scale factor (2.0).
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
   * Tests StackPane wrapper creation in scene hierarchy.
   * Verifies that the scene root is properly wrapped in a StackPane container.
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
   * Tests translation offset calculation for content centering.
   * Verifies that translateX and translateY are non-zero to compensate for scaling.
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
   * Tests scene creation with custom dimensions (600x400).
   * Verifies that arbitrary preferred sizes are correctly scaled by factor of 2.
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
   * Tests utility class instantiation prevention via private constructor.
   * Verifies that constructor throws UnsupportedOperationException when accessed via reflection.
   * 
   * @throws Exception if reflection fails (expected behavior wraps UnsupportedOperationException)
   * 
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
   * Tests idempotency of applyScaling method.
   * Verifies that calling applyScaling multiple times doesn't throw exceptions or change scale values.
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
   * Tests scene creation with small dimensions (100x50).
   * Verifies that scaling works correctly even with very small preferred sizes.
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
