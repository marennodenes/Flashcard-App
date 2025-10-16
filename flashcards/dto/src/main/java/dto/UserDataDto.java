package dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
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
  private final String username;

  /**
   * The password of the user.
   * This field cannot be null or blank for registration/login.
   * Can be null when returning user data (for security).
   */
  @NotBlank
  @JsonProperty("password")
  private final String password;

  /**
   * The flashcard deck manager associated with the user.
   * This is a list of flashcard deck managers, initialized as an empty list.
   */
  @Min(1)
  @JsonProperty("deckManager")
  private final List<FlashcardDeckManagerDto> deckManager;
  


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
    this.deckManager = new ArrayList<>();
  }

  /**
   * Constructs a new UserDataDto with the specified username, password, and decks.
   *
   * @param username the username of the user; must not be null or blank
   * @param password the password of the user; must not be null or blank
   * @param decks the list of flashcard decks associated with the user
   */
  @JsonCreator
  public UserDataDto(@JsonProperty("username") String username, 
                    @JsonProperty("password") String password, 
                    @JsonProperty("deckManager") List<FlashcardDeckManagerDto> decks) {
    this.username = username;
    this.password = password;

    if (decks == null) {
      this.deckManager = new ArrayList<>();
    } else {
      List<FlashcardDeckManagerDto> copied = new ArrayList<>();
      for (FlashcardDeckManagerDto deck : decks) {
        copied.add(new FlashcardDeckManagerDto(deck.getDecks()));
      }
      this.deckManager = copied;
    }
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

  /**
   * Gets the flashcard deck manager associated with the user.
   *
   * @return the list of flashcard deck managers
   */
  public List<FlashcardDeckManagerDto> getDeckManager() {
    List<FlashcardDeckManagerDto> copy = new ArrayList<>();
    for (FlashcardDeckManagerDto manager : deckManager) {
      copy.add(new FlashcardDeckManagerDto(manager.getDecks()));
    }
    return copy;
  }
}