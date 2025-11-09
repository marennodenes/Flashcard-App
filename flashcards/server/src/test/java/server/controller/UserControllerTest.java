package server.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.LoginRequestDto;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import server.service.UserService;
import shared.ApiConstants;
import shared.ApiEndpoints;

/**
 * Test class for UserController REST endpoints.
 * Tests user-related HTTP operations including registration, login, logout,
 * profile management, and password validation using MockMvc and mocked services.
 *
 * <p>This test suite validates:
 * - User retrieval by username
 * - User registration with valid and invalid data
 * - User login with correct and incorrect credentials
 * - Password validation
 * - User existence checks
 * - Error handling for various edge cases
 *
 * <p>Uses @WebMvcTest to test only the web layer and @MockBean to mock
 * the UserService dependency for isolated controller testing.
 *
 * @author chrsom 
 * @author isamw
 * @see "docs/release_3/ai_tools.md"
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
  public void setUp() {
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
  public void testGetUserSuccess() throws Exception {
    when(userService.getUser("testUser")).thenReturn(testUser);

    mockMvc.perform(get(ApiEndpoints.USERS_V1)
        .param("username", "testUser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value(ApiConstants.USER_RETRIEVED))
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
  public void testGetUserUserNotFound() throws Exception {
    when(userService.getUser("nonExistent"))
        .thenThrow(new IllegalArgumentException("User not found"));

    mockMvc.perform(get(ApiEndpoints.USERS_V1)
        .param("username", "nonExistent"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.USER_OPERATION_FAILED));
  }

  /**
   * Tests successful user registration.
   * Verifies that a new user can be created with valid credentials
   * and that the response contains the created user data.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testCreateUserSuccess() throws Exception {
    when(userService.createUserWithValidation(anyString(), anyString())).thenReturn(testUser);

    String requestBody = objectMapper.writeValueAsString(loginRequest);
    Objects.requireNonNull(requestBody, "Request body cannot be null");

    mockMvc.perform(post(ApiEndpoints.USERS_V1 + ApiEndpoints.USER_REGISTER)
        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value(ApiConstants.USER_CREATED))
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
  public void testCreateUserInvalidCredentials() throws Exception {
    when(userService.createUserWithValidation(anyString(), anyString()))
        .thenThrow(new IllegalArgumentException(ApiConstants.PASSWORD_TOO_SHORT));

    String requestBody = objectMapper.writeValueAsString(loginRequest);
    Objects.requireNonNull(requestBody, "Request body cannot be null");

    mockMvc.perform(post(ApiEndpoints.USERS_V1 + ApiEndpoints.USER_REGISTER)
        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.PASSWORD_TOO_SHORT));
  }

  /**
   * Tests user registration when username already exists.
   * Verifies that duplicate username registration is properly rejected.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testCreateUserUserAlreadyExists() throws Exception {
    when(userService.createUserWithValidation(anyString(), anyString()))
        .thenThrow(new IllegalArgumentException(ApiConstants.USER_ALREADY_EXISTS));

    String requestBody = objectMapper.writeValueAsString(loginRequest);
    Objects.requireNonNull(requestBody, "Request body cannot be null");

    mockMvc.perform(post(ApiEndpoints.USERS_V1 + ApiEndpoints.USER_REGISTER)
        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.USER_ALREADY_EXISTS));
  }

  /**
   * Tests successful user login with correct credentials.
   * Verifies that valid credentials result in successful authentication
   * and return the user's data.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testLogInUserSuccess() throws Exception {
    when(userService.logInUser("testUser", "TestPassword123!")).thenReturn(true);
    when(userService.getUser("testUser")).thenReturn(testUser);

    String requestBody = objectMapper.writeValueAsString(loginRequest);
    Objects.requireNonNull(requestBody, "Request body cannot be null");

    mockMvc.perform(post(ApiEndpoints.USERS_V1 + ApiEndpoints.USER_LOGIN)
        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value(ApiConstants.LOGIN_SUCCESS))
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
  public void testLogInUserWrongPassword() throws Exception {
    when(userService.logInUser("testUser", "WrongPassword"))
        .thenThrow(new IllegalArgumentException("Invalid password"));

    LoginRequestDto wrongRequest = new LoginRequestDto("testUser", "WrongPassword");

    String requestBody = objectMapper.writeValueAsString(wrongRequest);
    Objects.requireNonNull(requestBody, "Request body cannot be null");

    mockMvc.perform(post(ApiEndpoints.USERS_V1 + ApiEndpoints.USER_LOGIN)
        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.LOGIN_OPERATION_FAILED));
  }

  /**
   * Tests user login when user does not exist.
   * Verifies that login fails and returns user not found message.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testLogInUserUserNotFound() throws Exception {
    when(userService.logInUser("ghost", "anyPassword")).thenReturn(false);
    when(userService.userExists("ghost")).thenReturn(false);

    LoginRequestDto request = new LoginRequestDto("ghost", "anyPassword");

    String requestBody = objectMapper.writeValueAsString(request);
    Objects.requireNonNull(requestBody, "Request body cannot be null");

    mockMvc.perform(post(ApiEndpoints.USERS_V1 + ApiEndpoints.USER_LOGIN)
        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.success").value(false))
        .andExpect(jsonPath("$.data.message").value(ApiConstants.USER_NOT_FOUND));
  }

  /**
   * Tests user login with empty username.
   * Verifies that login fails and returns error message.
   *
   * @throws Exception if the MockMvc request fails
   * 
   */
  @Test
  public void testLogInUserEmptyUsername() throws Exception {
    when(userService.logInUser("", "TestPassword123!"))
        .thenThrow(new IllegalArgumentException("Username cannot be empty"));

    LoginRequestDto request = new LoginRequestDto("", "TestPassword123!");

    String requestBody = objectMapper.writeValueAsString(request);
    Objects.requireNonNull(requestBody, "Request body cannot be null");

    mockMvc.perform(post(ApiEndpoints.USERS_V1 + ApiEndpoints.USER_LOGIN)
      .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
      .content(requestBody))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.message").value(ApiConstants.LOGIN_OPERATION_FAILED));
  }

  /**
   * Tests user login with empty password.
   * Verifies that login fails and returns error message.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testLogInUserEmptyPassword() throws Exception {
    when(userService.logInUser("testUser", ""))
        .thenThrow(new IllegalArgumentException("Password cannot be empty"));

    LoginRequestDto request = new LoginRequestDto("testUser", "");

    String requestBody = objectMapper.writeValueAsString(request);
    Objects.requireNonNull(requestBody, "Request body cannot be null");

    mockMvc.perform(post(ApiEndpoints.USERS_V1 + ApiEndpoints.USER_LOGIN)
      .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
      .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.LOGIN_OPERATION_FAILED));
  }

  /**
   * Tests user login when an exception occurs in service.
   * Verifies that login fails and returns error message.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testLogInUserException() throws Exception {
    when(userService.logInUser(anyString(), anyString()))
        .thenThrow(new RuntimeException("Unexpected error"));

    LoginRequestDto request = new LoginRequestDto("testUser", "TestPassword123!");

    String requestBody = objectMapper.writeValueAsString(request);
    Objects.requireNonNull(requestBody, "Request body cannot be null");

    mockMvc.perform(post(ApiEndpoints.USERS_V1 + ApiEndpoints.USER_LOGIN)
      .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
      .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.LOGIN_OPERATION_FAILED));
  }

  /**
   * Tests password validation with a valid password.
   * Verifies that the endpoint correctly validates passwords
   * and returns true for valid passwords.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testValidatePasswordSuccess() throws Exception {
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
  public void testValidatePasswordInvalidPassword() throws Exception {
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
  public void testValidatePasswordError() throws Exception {
    when(userService.validatePassword(anyString(), anyString()))
        .thenThrow(new RuntimeException("Validation error"));

    mockMvc.perform(post(ApiEndpoints.USERS_V1 + ApiEndpoints.USER_VALIDATE_PASSWORD)
        .param("username", "testUser")
        .param("password", "TestPassword123!"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.USER_OPERATION_FAILED));
  }

  /**
   * Tests checking if a user exists.
   * Verifies that the endpoint correctly returns true when the user exists.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testFindUserUserExists() throws Exception {
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
  public void testFindUserUserDoesNotExist() throws Exception {
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
  public void testFindUserError() throws Exception {
    when(userService.userExists(anyString()))
        .thenThrow(new RuntimeException("Database error"));

    mockMvc.perform(get(ApiEndpoints.USERS_V1 + ApiEndpoints.USER_FIND)
        .param("username", "testUser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.USER_OPERATION_FAILED));
  }
}
