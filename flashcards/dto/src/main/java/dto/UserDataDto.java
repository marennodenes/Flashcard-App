package dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * A Data Transfer Object (DTO) representing user data.
 * This class is used to transfer user-related data, including username, password,
 * and the user's flashcard deck manager, between different layers of the application.
 * 
 * @author marieroe
 * @author isamw
 */
public class UserDataDto {

  /**
   * The username of the user.
   * This field cannot be null or blank.
   */
  @NotBlank
  @JsonProperty("username")
  private String username;

  /**
   * The password of the user.
   * This field cannot be null or blank for registration/login.
   * Can be null when returning user data (for security).
   */
  @NotBlank
  @JsonProperty("password")
  private String password;

  /**
   * The flashcard deck manager associated with the user.
   * This is a list of flashcard deck managers, initialized as an empty list.
   */
  @Min(1)
  @JsonProperty("deckManager")
  private List<FlashcardDeckManagerDto> deckManager = new ArrayList<>();

  /**
   * Default constructor for UserDataDto.
   * Required for frameworks that use reflection, such as Jackson.
   */
  public UserDataDto() {}

  /**
   * Constructs a new UserDataDto with the specified username and password.
   * Used for login requests.
   *
   * @param username the username of the user; must not be null or blank
   * @param password the password of the user; must not be null or blank
   */
  public UserDataDto(String username, String password) {
    this.username = username;
    this.password = password;
  }

  /**
   * Constructs a new UserDataDto with the specified username, password, and decks.
   *
   * @param username the username of the user; must not be null or blank
   * @param password the password of the user; must not be null or blank
   * @param decks the list of flashcard decks associated with the user
   */
  public UserDataDto(String username, String password, List<FlashcardDeckDto> decks) {
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
   * @param username the new username of the user
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
   * @param password the new password of the user
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Gets the flashcard deck manager associated with the user.
   *
   * @return the list of flashcard deck managers
   */
  public List<FlashcardDeckManagerDto> getDeckManager() {
    return deckManager;
  }

  /**
   * Sets the flashcard deck manager associated with the user.
   *
   * @param deckManager the new list of flashcard deck managers
   */
  public void setDeckManager(List<FlashcardDeckManagerDto> deckManager) {
    this.deckManager = deckManager;
  }
}