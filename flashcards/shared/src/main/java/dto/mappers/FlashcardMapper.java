package dto.mappers;

import java.util.List;

import app.Flashcard;
import dto.FlashcardDto;

/**
 * Mapper class for converting between Flashcard and FlashcardDto.
 * 
 * @author ailinat
 * @author sofietw
 * 
 */
public class FlashcardMapper {

  /**
   * Converts a Flashcard to a FlashcardDto.
   * 
   * @param flashcard the Flashcard to convert
   * @return the corresponding FlashcardDto
   * 
   */
  public FlashcardDto toDto(Flashcard flashcard) {
    if (flashcard == null) {
      throw new IllegalArgumentException("Flashcard cannot be null");
    }

    return new FlashcardDto(flashcard.getQuestion(), flashcard.getAnswer(), flashcard.getNumber());
  }

  /**
   * Converts a FlashcardDto to a Flashcard.
   * 
   * @param flashcardDto the FlashcardDto to convert
   * @return the corresponding Flashcard
   * 
   */
  public Flashcard fromDto(FlashcardDto flashcardDto) {
    if (flashcardDto == null) {
      throw new IllegalArgumentException("FlashcardDto cannot be null");
    }

    return new Flashcard(flashcardDto.getNumber(), flashcardDto.getQuestion(), flashcardDto.getAnswer());
  }

  /**
   * Converts a list of Flashcards to a list of FlashcardDtos.
   * 
   * @param flashcards the list of Flashcards to convert
   * @return the corresponding list of FlashcardDtos
   * 
   */
  public List<FlashcardDto> toDtoList(List<Flashcard> flashcards) {
    List<FlashcardDto> dtoList = new java.util.ArrayList<>();
    for (Flashcard flashcard : flashcards) {
      dtoList.add(toDto(flashcard));
    }
    return dtoList;
  }

  /**
   * Converts a list of FlashcardDtos to a list of Flashcards.
   * 
   * @param flashcardDtos the list of FlashcardDtos to convert
   * @return the corresponding list of Flashcards
   * 
   */
  public List<Flashcard> fromDtoList(List<FlashcardDto> flashcardDtos) {
    List<Flashcard> flashcardList = new java.util.ArrayList<>();
    for (FlashcardDto dto : flashcardDtos) {
      flashcardList.add(fromDto(dto));
    }
    return flashcardList;
  }

}
