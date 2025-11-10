package dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


/**
 * Unit tests for the {@link LoginRequestDto} class.
 * This test class verifies the correct construction and behavior of LoginRequestDto,
 * including username and password handling, and setter/getter methods.
 *
 * @author marennod
 * @author ailinat
 */
public class LoginRequestDtoTest {

  /**
   * Tests the constructor with username and password.
   * Verifies that the fields are set correctly.
   */
  @Test
  public void testConstructorWithUsernameAndPassword() {
    LoginRequestDto dto = new LoginRequestDto("alice", "pw");
    assertEquals("alice", dto.getUsername());
    assertEquals("pw", dto.getPassword());
  }

  /**
   * Tests the default constructor and setter methods.
   * Verifies that the fields can be set and retrieved.
   */
  @Test
  public void testDefaultConstructorAndSetters() {
    LoginRequestDto dto = new LoginRequestDto();
    dto.setUsername("bob");
    dto.setPassword("pass");
    assertEquals("bob", dto.getUsername());
    assertEquals("pass", dto.getPassword());
  }
}
