package app;

import app.User;
import java.util.ArrayList;

public class LoginValidator {

    private static ArrayList<User> users = new ArrayList<>();

    public static boolean isUsernameUnique(String username) {
      // Check if the username is unique (not already taken)
      User user = findUserByUsername(username);
      return user == null; // Unique if no existing user found
    }


    public static boolean authenticateUser(String username, String password) {
      // Authenticate user by checking username and password
      User user = findUserByUsername(username);
      if (user == null) {
        return false; // User not found
      }
      return user.getPassword().equals(password); 
    }
  
    public static boolean createUser(String username, String password) {
      // Create a new user with the given username and password
      User newUser = new User(username, password);

      if (!isUsernameUnique(username)) {
        return false; // Username already taken
      }

      users.add(newUser); 
      return true; // User created successfully
    }

    public static User findUserByUsername(String username) {
      // Placeholder for user lookup logic
      for (User user : users) {
        if (user.getUsername().equals(username)) {
          return user;
        }
      }
      
      return null;
    }


}