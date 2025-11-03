package server;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link ServerApplication} class.
 * 
 * Tests basic application startup functionality to ensure
 * the Spring Boot application can initialize without errors.
 * 
 * @author chrsom
 * @author isamw
 */
public class ServerApplicationTest {

   /**
   * Tests that the main method runs without throwing exceptions.
   * 
   * Verifies the application can start successfully with default configuration.
   */
  @Test
  void mainMethodRuns() {
    assertDoesNotThrow(() -> ServerApplication.main(new String[]{}));
  }
}
