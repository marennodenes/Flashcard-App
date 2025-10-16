package server.service;

import org.springframework.stereotype.Service;

import app.User;
/**
 * Service class for managing user operations in the flashcard application.
 * This service provides functionality for user authentication, user management,
 * and user-related business logic.
 * 
 * @author chrsom
 * @author isamw
 */
@Service
public class UserService {
  
  public UserService() {
  }

  public User getUser(String unsername) {
    return null;
  }

  public User createUser(String username, String password){
    return null;
  }

  public User updateUser(String username, User user) {
    return null;
  }

  public void deleteUser(String username) {
  }

  public boolean userExists(String username) {
    return false;
  }

  public boolean loginuser(String username, String password) {
    return false;
  }

  public void logoutUser(String username) {
  }

  public boolean validatePassword(String username, String password) {
    return false;
  }
}
