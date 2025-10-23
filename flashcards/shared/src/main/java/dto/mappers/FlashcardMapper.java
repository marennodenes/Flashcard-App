package dto.mappers;


import dto.FlashcardDto;
import app.Flashcard;

/**
 * Mapper class for converting between Flashcard and FlashcardDto.
 * @author @ailinat
 * @author @sofietw
 */
public class FlashcardMapper {

  /**
   * Converts a Flashcard to a FlashcardDto.
   * @param flashcard the Flashcard to convert
   * @return the corresponding FlashcardDto
   */
  public FlashcardDto toDto(Flashcard flashcard) {
    if (flashcard == null) {
      throw new IllegalArgumentException("Flashcard cannot be null");
    }

    return new FlashcardDto(flashcard.getQuestion(), flashcard.getAnswer(), flashcard.getNumber());
  }

  /**
   * Converts a FlashcardDto to a Flashcard.
   * @param flashcardDto the FlashcardDto to convert
   * @return the corresponding Flashcard
   */
  public Flashcard fromDto(FlashcardDto flashcardDto) {
    if (flashcardDto == null) {
      throw new IllegalArgumentException("FlashcardDto cannot be null");
    }

    return new Flashcard(flashcardDto.getNumber(), flashcardDto.getQuestion(), flashcardDto.getAnswer());
  }

}
