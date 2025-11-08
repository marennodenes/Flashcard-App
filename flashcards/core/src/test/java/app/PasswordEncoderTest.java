package app;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@link PasswordEncoder} functionality.
 * Tests password encoding and verification operations to ensure secure password handling.
 * Validates proper salt generation, Base64 encoding, and password matching.
 *
 * @author isamw
 * @author chrsom
 * @see PasswordEncoder
 */
public class PasswordEncoderTest {
  /**
   * Tests the password encoding and verification process.
   * Validates password encoding and verification functionality including:
   * - Successful password encoding with salt generation
   * - Correct password verification with encoded password
   * - Rejection of incorrect passwords against encoded password
   * - Rejection of plain text passwords that aren't properly encoded
   * - Proper handling of invalid Base64 encoded passwords
   *
   * <p>The test ensures that:
   * - Encoded passwords can be successfully verified with the original password
   * - Wrong passwords are rejected during verification
   * - Invalid or malformed encoded passwords are handled gracefully
   */
  @Test
  public void testEncodeAndVerify() {
    String password = "mySecurePassword";
    String encodedPassword = PasswordEncoder.encode(password);

    assertTrue(PasswordEncoder.matches(password, encodedPassword));
    assertFalse(PasswordEncoder.matches("password", encodedPassword));
    assertFalse(PasswordEncoder.matches("password", "notEncoded"));

    String invalidBase64 = "invalidSalt:@@@";
    assertFalse(PasswordEncoder.matches(password, invalidBase64));
  }
}
