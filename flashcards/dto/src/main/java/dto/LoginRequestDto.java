package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

/**
 * A Data Transfer Object (DTO) representing a login request.
 * This class is used to transfer login credentials between different layers of the application.
 * 
 * @author marieroe
 * @author isamw
 */
public class LoginRequestDto {
    
    /**
     * The username of the user attempting to log in.
     * This field cannot be null or blank.
     */
    @NotBlank
    @JsonProperty("username")
    private String username;
    
    /**
     * The password of the user attempting to log in.
     * This field cannot be null or blank.
     */
    @NotBlank
    @JsonProperty("password")
    private String password;
    
    /**
     * Default constructor for LoginRequestDto.
     * Required for frameworks that use reflection, such as Jackson.
     */
    public LoginRequestDto() {
    }
    
    /**
     * Constructs a new LoginRequestDto with the specified username and password.
     *
     * @param username the username of the user; must not be null or blank
     * @param password the password of the user; must not be null or blank
     */
    public LoginRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    /**
     * Gets the username of the user.
     *
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Sets the username of the user.
     *
     * @param username the new username of the user; must not be null or blank
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Gets the password of the user.
     *
     * @return the password of the user
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Sets the password of the user.
     *
     * @param password the new password of the user; must not be null or blank
     */
    public void setPassword(String password) {
        this.password = password;
    }
}