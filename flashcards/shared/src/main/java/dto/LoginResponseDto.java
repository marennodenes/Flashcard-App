package dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A Data Transfer Object (DTO) representing a login response.
 * This class is used to transfer login results between different layers of the application.
 * It includes success status, a message, and user data if login was successful.
 * 
 * @author marieroe
 * @author isamw
 */
public class LoginResponseDto {
    
    /**
     * Indicates whether the login was successful.
     */
    @JsonProperty("success")
    private boolean success;
    
    /**
     * A message providing additional information about the login result.
     * For example: "Login successful" or "Invalid credentials".
     */
    @JsonProperty("message")
    private final String message;
    
    /**
     * The user data, included if login was successful.
     * Will be null if login failed.
     */
    @JsonProperty("userData")
    private final UserDataDto userData;

    
    /**
     * Constructs a new LoginResponseDto with the specified success status, message, and user data.
     *
     * @param success true if login was successful, false otherwise
     * @param message a message providing additional information about the login result
     * @param userData the user data if login was successful, null otherwise
     */
    @JsonCreator
    public LoginResponseDto(@JsonProperty("success") boolean success, @JsonProperty("message") String message, @JsonProperty("userData") UserDataDto userData) {
        this.success = success;
        this.message = message;
        this.userData = userData;
    }
    
    /**
     * Gets the success status of the login attempt.
     *
     * @return true if login was successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }
    
    
    /**
     * Gets the message providing additional information about the login result.
     *
     * @return the message about the login result
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Gets the user data if login was successful.
     *
     * @return the user data, or null if login failed
     */
    public UserDataDto getUserData() {
        return userData;
    }
}