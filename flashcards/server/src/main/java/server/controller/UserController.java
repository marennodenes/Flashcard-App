package server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.User;
import dto.LoginRequestDto;
import dto.LoginResponseDto;
import dto.UserDataDto;
import dto.mappers.UserMapper;
import server.service.UserService;
import shared.ApiConstants;
import shared.ApiResponse;
import shared.ApiEndpoints;

/**
 * UserController handles user-related HTTP requests such as registration, login, logout,
 * profile management, and password validation.
 * It interacts with the UserService to perform business logic and data operations.
 * @see server.service.UserService
 * @author parts of class is generated with the help of claude.ai
 * @author @ailinat
 * @author @sofietw
 */
@RestController
@RequestMapping (ApiEndpoints.USERS_V1)
public class UserController {
  
  @Autowired
  private UserService userService; // Handles business logic for user operations
  private UserMapper mapper;


  /**
   * Constructor for UserController.
   * @param userService
   */
  public UserController (final UserService userService) {
    this.userService = userService;
    if (this.mapper == null) this.mapper = new UserMapper();
  }

  @GetMapping 
  public ApiResponse<UserDataDto> getUser (@RequestParam String username) {
    try {
      User user = userService.getUser(username);
      UserDataDto dto = mapper.toDto(user);
      return new ApiResponse<>(true, "User retrieved successfully", dto);
    } catch (Exception e) {
      return new ApiResponse<>(false, "Error retrieving user: " + e.getMessage(), null);
    }
  }

  /**
   * Register a new user.
   * Uses the userService to create a new user with detailed validation
   * @param request
   * @return
   */
  @PostMapping (ApiEndpoints.USER_REGISTER)
  public ApiResponse <UserDataDto> createUser(@RequestBody LoginRequestDto request) {
    try {
      User user = userService.createUserWithValidation(request.getUsername(), request.getPassword());
      UserDataDto dto = mapper.toDto(user);
      return new ApiResponse<>(true, ApiConstants.USER_CREATED, dto);
    } catch (IllegalArgumentException e) {
      return new ApiResponse<>(false, e.getMessage(), null);
    }
  }

  /**
   * Login a user.
   * Uses the userService to login a user
   * @param loginRequest
   * @return
   */
  @PostMapping (ApiEndpoints.USER_LOGIN)
  public ApiResponse <LoginResponseDto> logInUser(@RequestBody LoginRequestDto request) { 
    try {
      Boolean login = userService.logInUser(request.getUsername(), request.getPassword());
      
      if (login) {
        // Login successful - get user data and return success response
        User user = userService.getUser(request.getUsername());
        UserDataDto userDto = mapper.toDto(user);
        
        LoginResponseDto responseDto = new LoginResponseDto(login, ApiConstants.LOGIN_SUCCESS + ": " + request.getUsername(), userDto);
        return new ApiResponse<>(true, ApiConstants.LOGIN_SUCCESS, responseDto);
      } else {
        // Login failed - check if user exists to provide specific error message
        boolean userExists = userService.userExists(request.getUsername());
        String errorMessage = userExists ? ApiConstants.INVALID_PASSWORD : ApiConstants.USER_NOT_FOUND;
        
        LoginResponseDto responseDto = new LoginResponseDto(login, errorMessage, null);
        return new ApiResponse<>(true, "Login response", responseDto);
      }
    } catch (Exception e) {
      return new ApiResponse<>(false, "Error logging in user: " + e.getMessage(), null);
    }
  }

  /**
   * Validate password for a user.
   * Uses the userService to validate a user's password
   * @param request
   * @return
   */
  @PostMapping (ApiEndpoints.USER_VALIDATE_PASSWORD)
  public ApiResponse <Boolean> validatePassword(@RequestParam String username, @RequestParam String password) {
    try {
      Boolean isValid = userService.validatePassword(username, password);
      return new ApiResponse<>(true, "Password validation successful", isValid);
    } catch (Exception e) {
      return new ApiResponse<>(false, "Error validating password: " + e.getMessage(), null);
    }
  }


  /**
   * Delete a user.
   * Uses the userService to delete a user
   * @return
   */
  @GetMapping(ApiEndpoints.USER_FIND)
  public ApiResponse <Boolean> findUser(@RequestParam String username) {
    try {
      Boolean exists = userService.userExists(username);
      return new ApiResponse<>(true, "User existence check successful", exists);
    } catch (Exception e) {
      return new ApiResponse<>(false, "Error checking user existence: " + e.getMessage(), null);
    }
  }
}