package dto.mappers;


import dto.FlashcardDto;
import app.Flashcard;

public class FlashcardMapper {

  private FlashcardDto flashcardDto; 
  
  public FlashcardMapper(FlashcardDto flashcardDto) {
    this.flashcardDto = flashcardDto;
  }

  public FlashcardDto toDto(Flashcard flashcard) {
    return new FlashcardDto(flashcard.getQuestion(), flashcard.getAnswer(), flashcard.getNumber());
  }

  public Flashcard fromDto(FlashcardDto flashcardDto) {
    return new Flashcard(flashcardDto.getNumber(), flashcardDto.getQuestion(), flashcardDto.getAnswer());
  }

}
