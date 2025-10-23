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
    private final String username;
    
    /**
     * The password of the user attempting to log in.
     * This field cannot be null or blank.
     */
    @NotBlank
    @JsonProperty("password")
    private final String password;
    
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
     * Gets the password of the user.
     *
     * @return the password of the user
     */
    public String getPassword() {
        return password;
    }
}