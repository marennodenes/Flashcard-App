package server.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.User;
import itp.storage.FlashcardPersistent;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shared.ApiConstants;

/**
 * Unit tests for the {@link UserService} class.
 * 
 * <p>This test class verifies the functionality of user-related operations including
 * user retrieval, creation, authentication, and error handling. The tests use Mockito
 * to mock the {@link FlashcardPersistent} dependency and isolate the service logic
 * from the persistence layer.
 * 
 * <p>Key testing scenarios covered:
 * - Successful user retrieval and creation
 * - User authentication with valid and invalid credentials
 * - Error handling for non-existent users
 * - Exception handling for persistence layer failures
 *
 * @author chrsom
 * @author isamw
 * @author parts of class is generated with the help of claude.ai
 * @see UserService
 * @see FlashcardPersistent
 * @see ApiConstants
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  /**
   * Mocked FlashcardPersistent instance used to simulate persistence layer
   * operations without actual file I/O during testing.
   */
  @Mock private FlashcardPersistent persistent;
  private UserService userService;

  /**
   * Sets up the test environment before each test method execution.
   * 
   * <p>This method creates a UserService instance and injects the mocked
   * FlashcardPersistent dependency using reflection. This approach allows
   * testing without modifying the production constructor to accept dependencies.
   *
   * @throws RuntimeException if reflection fails to inject the mock dependency
   */
  @BeforeEach
  public void setUp() {
    userService = new UserService() {
      {
        // overstyrer konstruktøren slik at vi kan injisere mock
        try {
          var field = UserService.class.getDeclaredField("persistent");
          field.setAccessible(true);
          field.set(this, persistent);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };
  }


  /**
   * Tests the getUser method of UserService.
   * 
   * <p>Verifies that:
   * - A valid existing user can be retrieved successfully
   * - The returned user has the correct username
   * - An IllegalArgumentException is thrown when attempting to get a non-existing user
   * - An IllegalArgumentException is thrown when attempting to get a user with an empty username
   * - The exception messages match the expected API constants
   */
  @Test
  public void testGetUser() {
    String username = "user";
    User expected = new User(username, "Passw0rd!");
    when(persistent.userExists(username)).thenReturn(true);
    when(persistent.readUserData(username)).thenReturn(expected);
    
    User result = userService.getUser(username);
    assertNotNull(result);
    assertEquals(username, result.getUsername());
    
    when(persistent.userExists("ghost")).thenReturn(false);
    
    var ex = assertThrows(IllegalArgumentException.class,
        () -> userService.getUser("ghost"));
    assertEquals(ApiConstants.USER_NOT_FOUND, ex.getMessage());
        
    var ex2 = assertThrows(IllegalArgumentException.class,
        () -> userService.getUser(""));
    assertEquals(ApiConstants.USER_NOT_FOUND, ex2.getMessage());
  }

  /**
   * Tests the createUser method of UserService.
   * 
   * <p>Verifies that:
   * - A new user can be created successfully with valid username and password
   * - An IllegalArgumentException is thrown when 
   * attempting to create a user with an existing username
   * - An IllegalArgumentException is thrown when attempting to create a user with invalid password
   * - An IllegalArgumentException is thrown when 
   * attempting to create a user with empty username or password
   * - The exception messages match the expected API constants
   *
   * @throws IOException if an I/O error occurs during user creation
   */
  @Test
  public void testCreateUser() throws IOException {
    String username = "newUser";
    String password = "Valid1@Pass";

    // Mock LoginValidator via konstruktør (bruker ekte validator med mocked persistent)
    when(persistent.userExists(username)).thenReturn(false);

    User result = userService.createUser(username, password);

    assertNotNull(result);
    assertEquals(username, result.getUsername());
    verify(persistent).writeUserData(any(User.class));

    var ex = assertThrows(IllegalArgumentException.class,
        () -> userService.createUser("", ""));
    assertEquals(ApiConstants.INVALID_REQUEST, ex.getMessage());

    String username2 = "existingUser";
    String password2 = "Valid1@Pass";
    
    when(persistent.userExists(username2)).thenReturn(true);
    
    var ex2 = assertThrows(IllegalArgumentException.class,
        () -> userService.createUser(username2, password2));
    assertEquals(ApiConstants.USER_ALREADY_EXISTS, ex2.getMessage());

    String username3 = "user";
    String password3 = "abc"; // invalid
    
    when(persistent.userExists(username3)).thenReturn(false);
    
    var ex3 = assertThrows(IllegalArgumentException.class,
        () -> userService.createUser(username3, password3));
    assertEquals(ApiConstants.INVALID_PASSWORD, ex3.getMessage());
  }

  /**
   * Tests the userExists method of UserService.
   * 
   * <p>Verifies that:
   * - The method returns true for an existing user
   * - An IllegalArgumentException is thrown when checking for an empty username
   * - The exception message matches the expected API constant
   */
  @Test
  public void testUserExists() {
    when(persistent.userExists("test")).thenReturn(true);
    assertTrue(userService.userExists("test"));
        
    // empty username
    var ex = assertThrows(IllegalArgumentException.class,
        () -> userService.userExists(""));
    assertEquals(ApiConstants.INVALID_REQUEST, ex.getMessage());
  }

  /**
   * Tests the logInUser method of UserService.
   * 
   * <p>Verifies that:
   * - A user can log in successfully with valid credentials
   * - The method returns false for empty username or password
   * - The method returns false for non-existing users
   * - The method returns false for invalid passwords
   */
  @Test
  public void testLogInUser() {
    //success
    String username = "user";
    String password = "Valid1@Pass";
    when(persistent.userExists(username)).thenReturn(true);

    assertTrue(userService.logInUser(username, password));

    // empty fields
    assertFalse(userService.logInUser("", "password"));
    assertFalse(userService.logInUser("user", ""));

    // user does not exist
    when(persistent.userExists("ghost")).thenReturn(false);
    assertFalse(userService.logInUser("ghost", "Password1!"));

    // invalid password
    when(persistent.userExists("user")).thenReturn(true);
    assertFalse(userService.logInUser("user", "abc"));
  }

  
  /**
   * Tests the validatePassword method of UserService.
   * 
   * <p>Verifies that:
   * - The method returns true for valid username and password
   * - The method returns false for empty username or password
   */
  @Test
  public void testValidatePassword() {
    // success
    assertTrue(userService.validatePassword("user", "Valid1@Pass"));

    // empty fields
    assertFalse(userService.validatePassword("", "Valid1@Pass"));
    assertFalse(userService.validatePassword("user", ""));
  }


  /**
   * Tests the isValidPassword method of UserService.
   * 
   * <p>Verifies that:
   * - The method returns false for null password
   * - The method returns false for passwords that are too short
   * - The method returns false for passwords missing uppercase letters
   * - The method returns false for passwords missing lowercase letters
   * - The method returns false for passwords missing digits
   * - The method returns false for passwords missing special characters
   * - The method returns true for valid passwords
   */
  @Test
  public void testIsValidPassword() {
    assertFalse(userService.isValidPassword(null));           // null
    assertFalse(userService.isValidPassword("aA1!"));         // too short
    assertFalse(userService.isValidPassword("password1!"));   // no uppercase
    assertFalse(userService.isValidPassword("PASSWORD1!"));   // no lowercase
    assertFalse(userService.isValidPassword("Password!"));    // no digit
    assertFalse(userService.isValidPassword("Password1"));    // no special char
    assertTrue(userService.isValidPassword("Password1!"));    // valid
  }

  /**
   * Tests the validatePasswordDetailed method of UserService.
   * 
   * <p>Verifies that:
   * - The method returns correct error messages for invalid passwords
   * - The method returns null for valid passwords
   * - All password validation rules are properly tested
   */
  @Test
  public void testValidatePasswordDetailed() {
    // null password
    assertEquals(ApiConstants.INVALID_PASSWORD, userService.validatePasswordDetailed(null));
        
    // too short
    assertEquals(ApiConstants.PASSWORD_TOO_SHORT, userService.validatePasswordDetailed("aA1!"));
        
    // no uppercase
    assertEquals(ApiConstants.PASSWORD_MISSING_UPPERCASE, 
        userService.validatePasswordDetailed("password1!"));
        
    // no lowercase
    assertEquals(ApiConstants.PASSWORD_MISSING_LOWERCASE, 
        userService.validatePasswordDetailed("PASSWORD1!"));
        
    // no digit
    assertEquals(ApiConstants.PASSWORD_MISSING_DIGIT, 
        userService.validatePasswordDetailed("Password!"));
        
    // no special character
    assertEquals(ApiConstants.PASSWORD_MISSING_SPECIAL, 
        userService.validatePasswordDetailed("Password1"));
        
    // valid password
    assertNull(userService.validatePasswordDetailed("Password1!"));
  }

  /**
   * Tests the createUserWithValidation method of UserService.
   * 
   * <p>Verifies that:
   * - A new user can be created successfully with valid credentials
   * - Proper exceptions are thrown for various validation failures
   * - IOException from persistence layer is properly handled
   *
   * @throws IOException if an I/O error occurs during user creation
   * @see "docs/release_3/ai_tools.md"
   */
  @Test
  public void testCreateUserWithValidation() throws IOException {
    String username = "newUser";
    String password = "Valid1@Pass";

    // Success case
    when(persistent.userExists(username)).thenReturn(false);
        
    User result = userService.createUserWithValidation(username, password);
        
    assertNotNull(result);
    assertEquals(username, result.getUsername());
    verify(persistent).writeUserData(any(User.class));

    // Empty username or password
    var ex1 = assertThrows(IllegalArgumentException.class,
        () -> userService.createUserWithValidation("", password));
    assertEquals(ApiConstants.INVALID_REQUEST, ex1.getMessage());
        
    var ex2 = assertThrows(IllegalArgumentException.class,
        () -> userService.createUserWithValidation(username, ""));
    assertEquals(ApiConstants.INVALID_REQUEST, ex2.getMessage());

    // User already exists
    when(persistent.userExists("existingUser")).thenReturn(true);
    var ex3 = assertThrows(IllegalArgumentException.class,
        () -> userService.createUserWithValidation("existingUser", password));
    assertEquals(ApiConstants.USER_ALREADY_EXISTS, ex3.getMessage());

    // Invalid password - too short
    when(persistent.userExists("testUser")).thenReturn(false);
    var ex4 = assertThrows(IllegalArgumentException.class,
        () -> userService.createUserWithValidation("testUser", "aB1!"));
    assertEquals(ApiConstants.PASSWORD_TOO_SHORT, ex4.getMessage());

    // Invalid password - missing uppercase
    var ex5 = assertThrows(IllegalArgumentException.class,
        () -> userService.createUserWithValidation("testUser2", "password1!"));
    assertEquals(ApiConstants.PASSWORD_MISSING_UPPERCASE, ex5.getMessage());

    // IOException handling
    when(persistent.userExists("ioErrorUser")).thenReturn(false);
    doThrow(new IOException("Disk full")).when(persistent).writeUserData(any(User.class));
        
    var ex6 = assertThrows(IllegalArgumentException.class,
        () -> userService.createUserWithValidation("ioErrorUser", "Valid1@Pass"));
    assertTrue(ex6.getMessage().startsWith("Failed to save user data:"));
  }
}
