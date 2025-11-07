package ui;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Setup test class that configures JavaFX headless environment.
 * This test runs first to ensure proper headless configuration 
 * is applied before other JavaFX tests run.
 */
public class AaSetupFxuiTest {

  static {
      // Set headless properties before any JavaFX initialization
      System.setProperty("java.awt.headless", "true");
      System.setProperty("testfx.robot", "glass");
      System.setProperty("testfx.headless", "true");
      System.setProperty("prism.order", "sw");
      System.setProperty("prism.text", "t2k");
      System.setProperty("glass.platform", "Monocle");
      System.setProperty("monocle.platform", "Headless");
      System.setProperty("prism.verbose", "false");
      System.setProperty("javafx.animation.framerate", "1");
  }

  @BeforeAll
  public static void setupHeadless() {
      System.out.println("JavaFX headless environment configured");
  }

  @Test
  public void testHeadlessSetup() {
      // This test ensures the setup runs
      assertTrue(true, "Headless setup completed");
  }
  
  private static void assertTrue(boolean condition, String message) {
      if (!condition) {
          throw new AssertionError(message);
      }
  }

}
