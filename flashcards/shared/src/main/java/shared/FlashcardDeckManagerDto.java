package shared;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Max;
import java.util.ArrayList;
import java.util.List;

/**
 * A Data Transfer Object (DTO) representing a manager for multiple flashcard decks.
 * This class is used to transfer data between different layers of the application.
 * It includes a list of flashcard decks with a maximum limit of 8 decks.
 * 
 * @author marieroe
 * @author isamw
 */
public class FlashcardDeckManagerDto {

  /**
   * The list of flashcard decks managed by this DTO.
   * The list can contain at most 8 decks.
   */
  @Max(8)
  @JsonProperty("decks")
  private List<FlashcardDeckDto> decks = new ArrayList<>();

  /**
   * Default constructor for FlashcardDeckManagerDto.
   * Required for frameworks that use reflection, such as Jackson.
   */
  public FlashcardDeckManagerDto() {}

  /**
   * Constructs a new FlashcardDeckManagerDto with the specified list of decks.
   *
   * @param decks the list of flashcard decks; must not exceed 8 decks
   */
  public FlashcardDeckManagerDto(List<FlashcardDeckDto> decks) {
    this.decks = decks;
  }

  /**
   * Gets the list of flashcard decks managed by this DTO.
   *
   * @return the list of flashcard decks
   */
  public List<FlashcardDeckDto> getDecks() {
    return decks;
  }

  /**
   * Sets the list of flashcard decks managed by this DTO.
   *
   * @param decks the new list of flashcard decks; must not exceed 8 decks
   */
  public void setDecks(List<FlashcardDeckDto> decks) {
    this.decks = decks;
  }
}