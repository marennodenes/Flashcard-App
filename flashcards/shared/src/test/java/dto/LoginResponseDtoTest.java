package dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link LoginResponseDto} class.
 * This test class verifies the correct construction and behavior of LoginResponseDto,
 * including success status, message, and user data handling.
 *
 * @author parts of this code is generated with claude.ai
 * @author marennod
 * @author ailinat
 */
public class LoginResponseDtoTest {

  /**
   * Tests the constructor and getters for a successful login response with user data.
   */
  @Test
  public void testSuccessfulLoginResponse() {
    UserDataDto userData = new UserDataDto("alice", "pw");
    LoginResponseDto response = new LoginResponseDto(true, "Login successful", userData);
    assertTrue(response.isSuccess());
    assertEquals("Login successful", response.getMessage());
    assertEquals(userData, response.getUserData());
  }

  /**
   * Tests the constructor and getters for a failed login response with no user data.
   */
  @Test
  public void testFailedLoginResponse() {
    LoginResponseDto response = new LoginResponseDto(false, "Invalid credentials", null);
    assertFalse(response.isSuccess());
    assertEquals("Invalid credentials", response.getMessage());
    assertNull(response.getUserData());
  }
}
