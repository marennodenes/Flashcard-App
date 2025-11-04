package server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Unit tests for the {@link ServerApplication} class.
 * 
 * Tests basic application startup functionality to ensure
 * the Spring Boot application can initialize without errors.
 * 
 * @author chrsom
 * @author isamw
 * @author parts of class is generated with the help of claude.ai
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
