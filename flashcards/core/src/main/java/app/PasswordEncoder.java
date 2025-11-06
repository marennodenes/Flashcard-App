package app;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Handles password encoding and verification using SHA-256 with salt.
 * Provides secure password storage instead of plain text.
 * 
 * @author parts of this class is generated with the help of claude.ai
 * @author @sofietw
 * @author @ailinat
 */
public class PasswordEncoder {
  
  private static final String ALGORITHM = "SHA-256";
  private static final int SALT_LENGTH = 16;
  
  // Reuse SecureRandom instance to avoid creating new ones each time
  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  /**
   * Encodes a plain text password with a random salt.
   * 
   * @param password the plain text password
   * @return encoded password in format "salt:hash"
   */
  public static String encode(String password) {
    try {
      // Generate random salt using reused SecureRandom instance
      byte[] salt = new byte[SALT_LENGTH];
      SECURE_RANDOM.nextBytes(salt);
      
      // Hash password with salt using explicit UTF-8 encoding
      MessageDigest md = MessageDigest.getInstance(ALGORITHM);
      md.update(salt);
      byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
      
      // Encode salt and hash as base64
      String saltBase64 = Base64.getEncoder().encodeToString(salt);
      String hashBase64 = Base64.getEncoder().encodeToString(hashedPassword);
      
      return saltBase64 + ":" + hashBase64;
      
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 algorithm not available", e);
    }
  }

  /**
   * Verifies if a plain text password matches the encoded password.
   * 
   * @param password the plain text password to check
   * @param encodedPassword the stored encoded password in format "salt:hash"
   * @return true if password matches, false otherwise
   * @throws IllegalArgumentException if encodedPassword format is invalid
   */
  public static boolean matches(String password, String encodedPassword) throws IllegalArgumentException {
    try {
      // Split salt and hash
      String[] parts = encodedPassword.split(":", 2);
      if (parts.length != 2) {
        throw new IllegalArgumentException("Invalid encoded password format");
      }
      
      
      byte[] salt = Base64.getDecoder().decode(parts[0]);
      String storedHash = parts[1];
      
      // Hash the provided password with the stored salt using explicit UTF-8 encoding
      MessageDigest md = MessageDigest.getInstance(ALGORITHM);
      md.update(salt);
      byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
      String hashBase64 = Base64.getEncoder().encodeToString(hashedPassword);
      
      return storedHash.equals(hashBase64);
      
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 algorithm not available", e);
    } catch (IllegalArgumentException e) {
      // Invalid base64 encoding
      return false;
    }
  }
}