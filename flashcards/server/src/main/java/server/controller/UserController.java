package server.controller;

import app.User;
import dto.LoginRequestDto;
import dto.LoginResponseDto;
import dto.UserDataDto;
import dto.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.service.UserService;
import shared.ApiConstants;
import shared.ApiEndpoints;
import shared.ApiResponse;

/**
 * UserController handles user-related HTTP requests such as registration, login, logout,
 * profile management, and password validation.
 * It interacts with the UserService to perform business logic 
 * and data operations.
 *
 * @author parts of class is generated with the help of claude.ai
 * @author @ailinat
 * @author @sofietw
 * @see server.service.UserService
 */
@RestController
@RequestMapping (ApiEndpoints.USERS_V1)
public class UserController {
  
  @Autowired
  private UserService userService; // Handles business logic for user operations
  private UserMapper mapper;

  /**
   * Constructor for UserController.
   *
   * @param userService the UserService to use for user operations
   */
  public UserController(final UserService userService) {
    this.userService = userService;
    if (this.mapper == null) {
      this.mapper = new UserMapper();
    }
  }

  /**
   * Get user information by username.
   * Uses the userService to retrieve user data
   *
   * @param username the username of the user to retrieve
   * @return {@link ApiResponse} with user data
   */
  @GetMapping 
  public ApiResponse<UserDataDto> getUser(@RequestParam String username) {
    try {
      User user = userService.getUser(username);
      UserDataDto dto = mapper.toDto(user);
      return new ApiResponse<>(true, ApiConstants.USER_RETRIEVED, dto);
    } catch (Exception e) {
      // Log technical details for developers
      System.err.println(ApiConstants.USER_RETRIEVED_ERROR + " for username: '" 
                         + username + "' - " + e.getMessage());
      // Return user-friendly message
      return new ApiResponse<>(false, ApiConstants.USER_OPERATION_FAILED, null);
    }
  }

  /**
   * Delete a user.
   * Uses the userService to delete a user
   *
   * @param username the username of the user to delete
   * @return {@link ApiResponse} indicating if the user was deleted
   */
  @GetMapping(ApiEndpoints.USER_FIND)
  public ApiResponse<Boolean> findUser(@RequestParam String username) {
    try {
      Boolean exists = userService.userExists(username);
      return new ApiResponse<>(true, ApiConstants.USER_EXISTS, exists);
    } catch (Exception e) {
      // Log technical details for developers
      System.err.println(ApiConstants.USER_EXISTS_ERROR + " for username: '" 
                         + username + "' - " + e.getMessage());
      // Return user-friendly message
      return new ApiResponse<>(false, ApiConstants.USER_OPERATION_FAILED, null);
    }
  }

  /**
   * Register a new user.
   * Uses the userService to create a new user with detailed validation
   *
   * @param request the login request containing username and password
   * @return {@link ApiResponse} with created user data
   */
  @PostMapping (ApiEndpoints.USER_REGISTER)
  public ApiResponse<UserDataDto> createUser(@RequestBody LoginRequestDto request) {
    try {
      User user = userService.createUserWithValidation(request.getUsername(), 
          request.getPassword());
      UserDataDto dto = mapper.toDto(user);
      return new ApiResponse<>(true, ApiConstants.USER_CREATED, dto);
    } catch (IllegalArgumentException e) {
      // Log technical details for developers
      System.err.println(ApiConstants.USER_CREATION_ERROR + " for username: '" 
                         + request.getUsername() + "' - " + e.getMessage());
      // Return user-friendly error message directly (service already provides clean messages)
      return new ApiResponse<>(false, e.getMessage(), null);
    }
  }

  /**
   * Login a user.
   * Uses the userService to login a user
   *
   * @param request the login request containing username and password
   * @return {@link ApiResponse} with 
   *            login result and user data
   * @see "docs/release_3/ai_tools.md"
   */
  @PostMapping (ApiEndpoints.USER_LOGIN)
  public ApiResponse<LoginResponseDto> logInUser(@RequestBody LoginRequestDto request) { 
    try {
      Boolean login = userService.logInUser(request.getUsername(), request.getPassword());
      
      if (login) {
        // Login successful - get user data and return success response
        User user = userService.getUser(request.getUsername());
        UserDataDto userDto = mapper.toDto(user);

        LoginResponseDto responseDto = new LoginResponseDto(login, 
            ApiConstants.LOGIN_SUCCESS + " for username: '" 
            + request.getUsername() + "'", userDto);
        return new ApiResponse<>(true, ApiConstants.LOGIN_SUCCESS, responseDto);
      } else {
        // Login failed - check if user exists to provide specific error message
        boolean userExists = userService.userExists(request.getUsername());
        String errorMessage = userExists 
            ? ApiConstants.INVALID_PASSWORD : ApiConstants.USER_NOT_FOUND;

        LoginResponseDto responseDto = new LoginResponseDto(login, errorMessage, null);
        return new ApiResponse<>(true, ApiConstants.LOGIN_RESPONSE, responseDto);
      }
    } catch (Exception e) {
      // Log technical details for developers
      System.err.println(ApiConstants.LOGIN_RESPONSE_ERROR + " for username: '" 
                         + request.getUsername() + "' - " + e.getMessage());
      // Return user-friendly message
      return new ApiResponse<>(false, ApiConstants.LOGIN_OPERATION_FAILED, null);
    }
  }

  /**
   * Validate password for a user.
   * Uses the userService to validate a user's password
   *
   * @param username the username of the user
   * @param password the password to validate
   * @return {@link ApiResponse} indicating if the password is valid
   */
  @PostMapping (ApiEndpoints.USER_VALIDATE_PASSWORD)
  public ApiResponse<Boolean> validatePassword(@RequestParam String username, 
                                               @RequestParam String password) {
    try {
      Boolean isValid = userService.validatePassword(username, password);
      return new ApiResponse<>(true, ApiConstants.PASSWORD_VALIDATION_SUCCESSFUL, isValid);
    } catch (Exception e) {
      // Log technical details for developers
      System.err.println(ApiConstants.PASSWORD_VALIDATION_ERROR + " for username: '" 
                         + username + "' - " + e.getMessage());
      // Return user-friendly message
      return new ApiResponse<>(false, ApiConstants.USER_OPERATION_FAILED, null);
    }
  }
}