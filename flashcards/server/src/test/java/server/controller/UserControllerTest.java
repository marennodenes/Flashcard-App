package server.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.User;
import dto.LoginRequestDto;
import server.service.UserService;
import shared.ApiEndpoints;

/**
 * Test class for UserController REST endpoints.
 * Tests user-related HTTP operations including registration, login, logout,
 * profile management, and password validation using MockMvc and mocked services.
 *
 * This test suite validates:
 * - User retrieval by username
 * - User registration with valid and invalid data
 * - User login with correct and incorrect credentials
 * - Password validation
 * - User existence checks
 * - Error handling for various edge cases
 *
 * Uses @WebMvcTest to test only the web layer and @MockBean to mock
 * the UserService dependency for isolated controller testing.
 *
 * @author chrsom 
 * @author isamw
 * @see UserController
 * @see UserService
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @Autowired
  private ObjectMapper objectMapper;

  private User testUser;
  private LoginRequestDto loginRequest;

  /**
   * Sets up test fixtures before each test.
   * Initializes a test user and login request DTO for use in test cases.
   */
  @BeforeEach
  void setUp() {
    testUser = new User("testUser", "TestPassword123!");
    loginRequest = new LoginRequestDto("testUser", "TestPassword123!");
  }

  /**
   * Tests successful retrieval of a user by username.
   * Verifies that the endpoint returns HTTP 200 and correct user data.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  void testGetUser_Success() throws Exception {
    when(userService.getUser("testUser")).thenReturn(testUser);

    mockMvc.perform(get(ApiEndpoints.USERS_V1)
        .param("username", "testUser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("User retrieved successfully"))
        .andExpect(jsonPath("$.data.username").value("testUser"));
  }

  /**
   * Tests user retrieval when the user does not exist.
   * Verifies that the endpoint returns HTTP 200 with success=false
   * and an appropriate error message.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  void testGetUser_UserNotFound() throws Exception {
    when(userService.getUser("nonExistent")).thenThrow(new IllegalArgumentException("User not found"));

    mockMvc.perform(get(ApiEndpoints.USERS_V1)
        .param("username", "nonExistent"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value("Error retrieving user: User not found"));
  }

  /**
   * Tests successful user registration.
   * Verifies that a new user can be created with valid credentials
   * and that the response contains the created user data.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  void testCreateUser_Success() throws Exception {
    when(userService.createUser(anyString(), anyString())).thenReturn(testUser);

    mockMvc.perform(post(ApiEndpoints.USERS_V1 + ApiEndpoints.USER_REGISTER)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("User created successfully"))
        .andExpect(jsonPath("$.data.username").value("testUser"));
  }

  /**
   * Tests user registration with invalid credentials.
   * Verifies that attempting to create a user with invalid data
   * results in an appropriate error response.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  void testCreateUser_InvalidCredentials() throws Exception {
    when(userService.createUser(anyString(), anyString()))
        .thenThrow(new IllegalArgumentException("Invalid credentials"));

    mockMvc.perform(post(ApiEndpoints.USERS_V1 + ApiEndpoints.USER_REGISTER)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value("Error creating user: Invalid credentials"));
  }

  /**
   * Tests user registration when username already exists.
   * Verifies that duplicate username registration is properly rejected.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  void testCreateUser_UserAlreadyExists() throws Exception {
    when(userService.createUser(anyString(), anyString()))
        .thenThrow(new IllegalArgumentException("User already exists"));

    mockMvc.perform(post(ApiEndpoints.USERS_V1 + ApiEndpoints.USER_REGISTER)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value("Error creating user: User already exists"));
  }

  /**
   * Tests successful user login with correct credentials.
   * Verifies that valid credentials result in successful authentication
   * and return the user's data.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  void testLogInUser_Success() throws Exception {
    when(userService.logInUser("testUser", "TestPassword123!")).thenReturn(true);
    when(userService.getUser("testUser")).thenReturn(testUser);

    mockMvc.perform(post(ApiEndpoints.USERS_V1 + ApiEndpoints.USER_LOGIN)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Login successful"))
        .andExpect(jsonPath("$.data.success").value(true))
        .andExpect(jsonPath("$.data.userData.username").value("testUser"));
  }

  /**
   * Tests user login with incorrect password.
   * Verifies that invalid credentials are properly rejected.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  void testLogInUser_WrongPassword() throws Exception {
    when(userService.logInUser("testUser", "WrongPassword"))
        .thenThrow(new IllegalArgumentException("Invalid password"));

    LoginRequestDto wrongRequest = new LoginRequestDto("testUser", "WrongPassword");

    mockMvc.perform(post(ApiEndpoints.USERS_V1 + ApiEndpoints.USER_LOGIN)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(wrongRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value("Error logging in user: Invalid password"));
  }

  /**
   * Tests password validation with a valid password.
   * Verifies that the endpoint correctly validates passwords
   * and returns true for valid passwords.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  void testValidatePassword_Success() throws Exception {
    when(userService.validatePassword("testUser", "TestPassword123!")).thenReturn(true);

    mockMvc.perform(post(ApiEndpoints.USERS_V1 + ApiEndpoints.USER_VALIDATE_PASSWORD)
        .param("username", "testUser")
        .param("password", "TestPassword123!"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Password validation successful"))
        .andExpect(jsonPath("$.data").value(true));
  }

  /**
   * Tests password validation with an invalid password.
   * Verifies that the endpoint correctly identifies invalid passwords
   * and returns false.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  void testValidatePassword_InvalidPassword() throws Exception {
    when(userService.validatePassword("testUser", "weak")).thenReturn(false);

    mockMvc.perform(post(ApiEndpoints.USERS_V1 + ApiEndpoints.USER_VALIDATE_PASSWORD)
        .param("username", "testUser")
        .param("password", "weak"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Password validation successful"))
        .andExpect(jsonPath("$.data").value(false));
  }

  /**
   * Tests password validation when an error occurs during validation.
   * Verifies proper error handling when the service throws an exception.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  void testValidatePassword_Error() throws Exception {
    when(userService.validatePassword(anyString(), anyString()))
        .thenThrow(new RuntimeException("Validation error"));

    mockMvc.perform(post(ApiEndpoints.USERS_V1 + ApiEndpoints.USER_VALIDATE_PASSWORD)
        .param("username", "testUser")
        .param("password", "TestPassword123!"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value("Error validating password: Validation error"));
  }

  /**
   * Tests checking if a user exists.
   * Verifies that the endpoint correctly returns true when the user exists.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  void testFindUser_UserExists() throws Exception {
    when(userService.userExists("testUser")).thenReturn(true);

    mockMvc.perform(get(ApiEndpoints.USERS_V1 + ApiEndpoints.USER_FIND)
        .param("username", "testUser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("User existence check successful"))
        .andExpect(jsonPath("$.data").value(true));
  }

  /**
   * Tests checking if a user exists when they don't.
   * Verifies that the endpoint correctly returns false when the user doesn't exist.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  void testFindUser_UserDoesNotExist() throws Exception {
    when(userService.userExists("nonExistent")).thenReturn(false);

    mockMvc.perform(get(ApiEndpoints.USERS_V1 + ApiEndpoints.USER_FIND)
        .param("username", "nonExistent"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("User existence check successful"))
        .andExpect(jsonPath("$.data").value(false));
  }

  /**
   * Tests user existence check when an error occurs.
   * Verifies proper error handling when the service throws an exception.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  void testFindUser_Error() throws Exception {
    when(userService.userExists(anyString()))
        .thenThrow(new RuntimeException("Database error"));

    mockMvc.perform(get(ApiEndpoints.USERS_V1 + ApiEndpoints.USER_FIND)
        .param("username", "testUser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value("Error checking user existence: Database error"));
  }
}
