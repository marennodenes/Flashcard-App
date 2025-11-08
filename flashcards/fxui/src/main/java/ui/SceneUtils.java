package ui;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

/**
 * Utility class for creating and configuring JavaFX scenes with consistent scaling.
 *
 * This class provides helper methods to create scenes with 2x scaling applied
 * to all UI elements, making the application twice as large while maintaining
 * the same layout proportions.
 *
 * @author marennod
 * @author chrsom
 * 
 */
public final class SceneUtils {

  /** The scaling factor applied to all scenes */
  private static final double SCALE_FACTOR = 2.0;
  /** How much of the extra scaled size to offset the content (0-1). Increase to move content more down/right. */
  private static final double OFFSET_FACTOR = 1.0;

  /**
   * Private constructor to prevent instantiation.
   */
  private SceneUtils() {
    throw new UnsupportedOperationException("Utility class");
  }

  /**
   * Creates a scaled scene with the given root node.
   * The scene will have 2x scaling applied to make all UI elements larger.
   * The scene size will also be scaled to accommodate the larger content.
   *
   * @param root the root node of the scene
   * @return a new Scene with scaling applied and appropriate size
   * 
   */
  public static Scene createScaledScene(Parent root) {
    // Get the preferred size from the root
    double width = root.prefWidth(-1);
    double height = root.prefHeight(-1);

    // If no preferred size is set, use default
    if (width <= 0) width = 416.0;  // Default from FXML
    if (height <= 0) height = 264.0; // Default from FXML

    // Apply scaling to root
    applyScaling(root);

    // Wrap in StackPane with top-left alignment to preserve AnchorPane positioning
    StackPane container = new StackPane(root);
    StackPane.setAlignment(root, Pos.TOP_LEFT);

    // Compute offset based on extra space created by scaling and apply a factor to move further right/down
    double extraWidth = width * (SCALE_FACTOR - 1);
    double extraHeight = height * (SCALE_FACTOR - 1);
    root.setTranslateX(extraWidth * OFFSET_FACTOR);
    root.setTranslateY(extraHeight * OFFSET_FACTOR);

    // Create scene with scaled dimensions
    Scene scene = new Scene(container, width * SCALE_FACTOR, height * SCALE_FACTOR);

    return scene;
  }

  /**
   * Applies 2x scaling to the given parent node.
   * This method can be used to scale existing scenes or scene roots.
   *
   * @param root the parent node to scale
   * 
   */
  public static void applyScaling(Parent root) {
    root.setScaleX(SCALE_FACTOR);
    root.setScaleY(SCALE_FACTOR);
  }
}
