package server;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;


/**
 * Unit tests for the {@link ServerApplication} class.
 * 
 * <p>Tests basic application functionality to ensure the main class
 * and its methods are properly defined.
 *
 * @author chrsom
 * @author isamw
 * @see ServerApplication
 * 
 */
public class ServerApplicationTest {

  /**
   * Tests that the main method exists and can be referenced.
   *
   * <p>This verifies the basic structure of the application class
   * without starting the full Spring Boot application.
   */
  @Test
  void mainMethodExists() {
    assertDoesNotThrow(() -> {
      ServerApplication.class.getDeclaredMethod("main", String[].class);
    });
  }
}
