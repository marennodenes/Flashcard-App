package dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * A Data Transfer Object (DTO) representing a deck of flashcards.
 * This class is used to transfer data between different layers of the application.
 * It includes the name of the deck and a list of flashcards in the deck.
 * 
 * @author marieroe
 * @author isamw
 * 
 */
public class FlashcardDeckDto {

  /**
   * The name of the flashcard deck.
   * This field cannot be null or blank.
   */
  @NotBlank
  @JsonProperty("deckName")
  private final String deckName;

  /**
   * The list of flashcards in the deck.
   * The list must contain at least one flashcard.
   */
  @Min(1)
  @JsonProperty("flashcards")
  private final List<FlashcardDto> deck;

  /**
   * Constructs a new, empty FlashcardDeckDto.
   * This constructor is public and takes no arguments, and is used by Jackson for deserialization.
   */
  public FlashcardDeckDto() {
    this.deckName = "";
    this.deck = List.of();
  }

  /**
   * Constructs a new FlashcardDeckDto with the specified deck name and list of flashcards.
   *
   * @param deckName the name of the flashcard deck; must not be null or blank
   * @param deck the list of flashcards in the deck; must contain at least one flashcard
   * 
   */
  public FlashcardDeckDto(String deckName, List<FlashcardDto> deck) {
    this.deckName = deckName;
    this.deck = List.copyOf(deck);
  }

  /**
   * Gets the name of the flashcard deck.
   *
   * @return the name of the flashcard deck
   * 
   */
  public String getDeckName() {
    return deckName;
  }


  /**
   * Gets the list of flashcards in the deck.
   *
   * @return the list of flashcards in the deck
   * 
   */
  public List<FlashcardDto> getDeck() {
    return deck;
  }
}