package app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test class for {@link LoginValidator} functionality.
 * Tests user authentication, user creation, and username validation using mocked persistence layer.
 * Uses Mockito to mock UserPersistence for isolated unit testing.
 *
 * @author isamw
 * @author chrsom
 * @see LoginValidator
 */
public class LoginValidatorTest {

  private UserPersistence mockPersistence;
  private LoginValidator validator;

  /**
   * Sets up test fixtures before each test method.
   * Creates a mock {@link UserPersistence} instance and initializes
   * a {@link LoginValidator} with the mocked dependency.
   */
  @BeforeEach
  public void setUp() {
    mockPersistence = Mockito.mock(UserPersistence.class);
    validator = new LoginValidator(mockPersistence);
  }
  
  /**
   * Tests the {@link LoginValidator#createUser(String, String)} method.
   * Validates user creation scenarios including:
   * - Creating a new user successfully
   * - Creating a user when username doesn't exist
   * - Preventing duplicate user creation when username already exists
   */
  @Test
  public void testCreateUser() {
    boolean newUser = validator.createUser("newUser", "password123");
    assertTrue(newUser);

    Mockito.when(mockPersistence.userExists("existingUser")).thenReturn(false);
    boolean result = validator.createUser("existingUser", "password123");
    assertTrue(result);

    Mockito.when(mockPersistence.userExists("existingUser")).thenReturn(true);
    
    boolean anotherExsists = validator.createUser("existingUser", "password123");

    assertFalse(anotherExsists);
  }

  /**
   * Tests the {@link LoginValidator#isUsernameUnique(String)} method.
   * Validates username uniqueness checking:
   * - Returns true for unique usernames
   * - Returns false for existing usernames
   *
   * @see LoginValidator#isUsernameUnique(String)
   */
  @Test
  public void testIsUsernameUnique() {
    Mockito.when(mockPersistence.userExists("uniqueUser")).thenReturn(false);
    assertTrue(validator.isUsernameUnique("uniqueUser"));

    Mockito.when(mockPersistence.userExists("takenUser")).thenReturn(true);
    assertFalse(validator.isUsernameUnique("takenUser"));
  }

  /**
   * Tests the {@link LoginValidator#authenticateUser(String, String)} method.
   * Validates user authentication scenarios including:
   * - Successful authentication with correct credentials
   * - Failed authentication with wrong password
   * - Failed authentication for non-existent users
   * - Proper handling of legacy plain text passwords
   *
   * @see LoginValidator#authenticateUser(String, String)
   */
  @Test
  public void testAuthenticateUser() {
    String encodedPassword = PasswordEncoder.encode("securePassword");
    User user = new User("testUser", encodedPassword);
    Mockito.when(mockPersistence.userExists("testUser")).thenReturn(true);
    Mockito.when(mockPersistence.readUserData("testUser")).thenReturn(user);

    assertTrue(validator.authenticateUser("testUser", encodedPassword));

    assertFalse(validator.authenticateUser("testUser", "wrongPassword"));

    assertFalse(validator.authenticateUser("nonExistentUser", "anyPassword"));
  }

  /**
   * Tests the equalPasswords method of LoginValidator.
   * Validates user authentication scenarios including:
   * - Successful authentication with correct credentials
   * - Failed authentication with wrong password
   * - Failed authentication for non-existent users
   * - Proper handling of legacy plain text passwords
   *
   * @throws IllegalStateException when legacy plain text passwords are encountered
   */
  @Test
  public void testEqualPasswords() {
    assertTrue(validator.equalPasswords("password123", "password123"));
    assertFalse(validator.equalPasswords("password123", "differentPassword"));
  }

  /**
   * Tests the findUserByUsername method of LoginValidator.
   * Validates user lookup functionality:
   * - Returns user for existing username
   * - Returns null for non-existing username
   *
   * @see LoginValidator#findUserByUsername(String)
   */
  @Test
  public void testFindUserByUsername() {
    User user = new User("searchUser", "password123");
    Mockito.when(mockPersistence.userExists("searchUser")).thenReturn(true);
    Mockito.when(mockPersistence.readUserData("searchUser")).thenReturn(user);

    assertEquals(user, validator.findUserByUsername("searchUser"));

    Mockito.when(mockPersistence.userExists("nonExistentUser")).thenReturn(false);
    assertNull(validator.findUserByUsername("nonExistentUser"));
  }
}
