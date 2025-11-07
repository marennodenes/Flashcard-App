package app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@link User} functionality.
 * Tests user object creation and basic getter methods to ensure proper data handling.
 * 
 * @author isamw
 * @author chrsom
 * 
 * @see User
 * 
 */
public class UserTest {
  
  /**
   * Validates that:
   * - User object is created with correct username
   * - Username is retrievable
   * - Password is stored and accessible 
   * - Password is not null after creation
   */
  @Test
  public void testUser(){
    User user = new User("testuser", "testpass");
    assertEquals("testuser", user.getUsername());
    assertNotNull(user.getPassword());
  }
}
