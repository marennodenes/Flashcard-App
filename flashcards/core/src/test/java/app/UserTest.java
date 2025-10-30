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
 */
public class UserTest {
  
  /**
   * Tests the {@link User#User(String, String)} constructor and getter methods.
   * Validates that:
   *   User object is created with correct username
   *   Username is retrievable via {@link User#getUsername()}
   *   Password is stored and accessible via {@link User#getPassword()}
   *   Password is not null after creation
   * 
   * @see User#User(String, String)
   * @see User#getUsername()
   * @see User#getPassword()
   */
  @Test
  void testUser(){
    User user = new User("testuser", "testpass");
    assertEquals("testuser", user.getUsername());
    assertNotNull(user.getPassword());
  }
}
