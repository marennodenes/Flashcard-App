package server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.User;
import app.UserData;
import dto.LoginRequestDto;
import dto.LoginResponseDto;
import dto.UserDataDto;
import dto.mappers.UserMapper;
import server.service.UserService;
import shared.ApiResponse;

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
@RequestMapping  ("/api/v1/users")
public class UserController {
  
  @Autowired
  private UserService userService;


  /**
   * Constructor for UserController.
   * @param userService
   */
  public UserController (final UserService userService) {
    this.userService = userService;
  }

  @GetMapping 
  public ApiResponse<UserDataDto> getUser (@RequestParam String username) {
    try {
      User user = userService.getUser(username);
      UserDataDto userDataDto = new UserMapper().toDto(user);
      return new ApiResponse<>(userDataDto);
    } catch (Exception e) {
      return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
  }

  /**
   * Register a new user.
   * Uses the userService to create a new user
   * @param request
   * @return
   */
  @PostMapping ("/register")
  public ApiResponse <UserDataDto> createUser(@RequestBody UserDataDto userDto) {
    try {
      UserData userData = userDto.fromDto(userDto);
      
      

      User user = userService.createUser(userData.getUsername(), userData.getPassword());
      UserDataDto userDataDto = new UserDataDto(user);
      return new ApiResponse<>(userDataDto);
    } catch (Exception e) {
      return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Login a user.
   * Uses the userService to login a user
   * @param loginRequest
   * @return
   */
  @PostMapping ("/login")
  public ResponseEntity <LoginResponseDto> loginUser(@RequestBody LoginRequestDto loginRequest) { 
    try {
      LoginResponseDto response = userService.loginUser(loginRequest);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
  }

  /**
   * Log out a user.
   * Uses the userService to log out a user
   * @param username
   * @return
   */
  @PostMapping ("/logout")
  public ResponseEntity <Void> logOutUser(@RequestParam String username) {
    try {
      userService.logOutUser(username);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Validate password for a user.
   * Uses the userService to validate a user's password
   * @param request
   * @return
   */
  @PostMapping ("/validate-password")
  public ResponseEntity <Boolean> validatePassword(@RequestBody LoginRequestDto request) {
    try {
      boolean isValid = userService.validatePassword(request);
      return new ResponseEntity<>(isValid, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Get current user details.
   * Uses the userService to get the current user's details
   * @return
   */
  @GetMapping ("/profile") 
  public ResponseEntity <UserDataDto> getCurrentUser() {
    try {
      UserData userDto = userService.getUser();
      return new ResponseEntity<>(userDto, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Update user details.
   * Uses the userService to update a user's details
   * @param userDataDto
   * @return
   */
  @PutMapping ("/profile")
  public ResponseEntity <UserDataDto> updateUser(@RequestBody UserDataDto userDataDto) {
    try {
      UserDataDto updatedUser = userService.updateUser(userDataDto);
      return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Delete a user.
   * Uses the userService to delete a user
   * @return
   */
  @DeleteMapping ("/profile")
  public ResponseEntity <Void> deleteUser() {
    try {
      userService.deleteUser();
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}