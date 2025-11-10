package ui;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

/**
 * Unit tests for the {@link SceneUtils} utility class.
 *
 * <p>Verifies scene creation, scaling functionality, and proper utility class behavior.
 * Tests cover scene dimensions, structure, scaling operations, and edge cases.
 *
 * @author marennod
 * @author chrsom
 *
 * @see SceneUtils
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
   * Tests scene creation with preferred and default dimensions.
   * 
   * <p>Verifies that {@link SceneUtils#createScaledScene(Parent)} properly scales
   * scenes with both explicit preferred sizes and default dimensions.
   */
  @Test
  public void testCreateScaledScene() {
    // Test with explicit preferred size
    AnchorPane rootWithSize = new AnchorPane();
    rootWithSize.setPrefWidth(416.0);
    rootWithSize.setPrefHeight(264.0);
    Scene sceneWithSize = SceneUtils.createScaledScene(rootWithSize);
    
    assertNotNull(sceneWithSize, "Scene should not be null");
    assertEquals(416.0 * SCALE_FACTOR, sceneWithSize.getWidth(), DELTA, 
        "Scene width should be doubled");
    assertEquals(264.0 * SCALE_FACTOR, sceneWithSize.getHeight(), DELTA, 
        "Scene height should be doubled");

    // Test with default size (no preferred size set)
    AnchorPane rootDefault = new AnchorPane();
    Scene sceneDefault = SceneUtils.createScaledScene(rootDefault);
    
    assertNotNull(sceneDefault, "Scene with defaults should not be null");
    assertEquals(416.0 * SCALE_FACTOR, sceneDefault.getWidth(), DELTA, 
        "Default scene width should be doubled");
    assertEquals(264.0 * SCALE_FACTOR, sceneDefault.getHeight(), DELTA, 
        "Default scene height should be doubled");
  }

  /**
   * Tests direct scaling application to root node.
   * 
   * <p>Verifies that {@link SceneUtils#applyScaling(Parent)} correctly sets
   * both scaleX and scaleY to the expected scale factor (2.0).
   */
  @Test
  public void testApplyScaling() {
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
   * Tests scene structure and positioning.
   * 
   * <p>Verifies that created scenes have proper StackPane wrapper and
   * correct translation offsets for centering scaled content.
   */
  @Test
  public void testSceneStructure() {
    // Arrange
    AnchorPane root = new AnchorPane();
    root.setPrefWidth(416.0);
    root.setPrefHeight(264.0);

    // Act
    Scene scene = SceneUtils.createScaledScene(root);

    // Assert - Scene structure
    assertNotNull(scene.getRoot(), "Scene root should not be null");
    assertTrue(scene.getRoot() instanceof StackPane,
        "Scene root should be wrapped in StackPane");
        
    // Assert - Translation for centering
    assertNotEquals(0.0, root.getTranslateX(),
        "TranslateX should be set to center content");
    assertNotEquals(0.0, root.getTranslateY(),
        "TranslateY should be set to center content");
  }

  /**
   * Tests scene scaling with various dimensions.
   * 
   * <p>Verifies correct scaling behavior for different size configurations
   * including custom dimensions and small sizes.
   */
  @Test
  public void testVariousSizes() {
    // Test custom dimensions (600x400)
    AnchorPane customRoot = new AnchorPane();
    customRoot.setPrefWidth(600.0);
    customRoot.setPrefHeight(400.0);
    Scene customScene = SceneUtils.createScaledScene(customRoot);
    
    assertEquals(600.0 * SCALE_FACTOR, customScene.getWidth(), DELTA,
        "Custom scene width should be doubled");
    assertEquals(400.0 * SCALE_FACTOR, customScene.getHeight(), DELTA,
        "Custom scene height should be doubled");

    // Test small dimensions (100x50)  
    AnchorPane smallRoot = new AnchorPane();
    smallRoot.setPrefWidth(100.0);
    smallRoot.setPrefHeight(50.0);
    Scene smallScene = SceneUtils.createScaledScene(smallRoot);
    
    assertNotNull(smallScene, "Small scene should not be null");
    assertEquals(100.0 * SCALE_FACTOR, smallScene.getWidth(), DELTA,
        "Small scene width should be doubled");
    assertEquals(50.0 * SCALE_FACTOR, smallScene.getHeight(), DELTA,
        "Small scene height should be doubled");
  }

  /**
   * Tests utility class instantiation prevention via private constructor.
   *
   * <p>Verifies that the private constructor throws {@link UnsupportedOperationException}
   * when accessed via reflection, ensuring proper utility class pattern implementation.
   *
   * @throws Exception if reflection fails (expected behavior wraps UnsupportedOperationException)
   */
  @Test
  public void testPrivateConstructor() {
    // Act & Assert
    Exception exception = assertThrows(
        Exception.class,
        () -> {
          // Use reflection to access private constructor
          var constructor = SceneUtils.class.getDeclaredConstructor();
          constructor.setAccessible(true);
          constructor.newInstance();
        },
        "Private constructor should throw exception");

    // Reflection wraps the exception in InvocationTargetException
    assertTrue(exception.getCause() instanceof UnsupportedOperationException,
        "Cause should be UnsupportedOperationException");
    assertEquals("Utility class", exception.getCause().getMessage(),
        "Exception message should be 'Utility class'");
  }

  /**
   * Tests multiple calls to applyScaling method.
   * 
   * <p>Verifies that repeated calls to {@link SceneUtils#applyScaling(Parent)}
   * don't throw exceptions or change previously set scale values (idempotency).
   */
  @Test
  public void testMultipleCalls() {
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
}
