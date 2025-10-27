package dto.mappers;

import java.util.List;
import java.util.stream.Collectors;

import app.Flashcard;
import app.FlashcardDeck;
import dto.FlashcardDeckDto;
import dto.FlashcardDto;

/**
 * Mapper class for converting between FlashcardDeck and FlashcardDeckDto.
 * Uses FlashcardMapper for mapping individual Flashcards.
 * @author ailinat
 * @author sofietw
 */
public class FlashcardDeckMapper {
  
  /**
   * Converts a FlashcardDeck to a FlashcardDeckDto.
   * @param deck the FlashcardDeck to convert
   * @return the corresponding FlashcardDeckDto
   */
  public FlashcardDeckDto toDto(FlashcardDeck deck) {
    if (deck == null) {
      throw new IllegalArgumentException("FlashcardDeck cannot be null");
    }

    List<Flashcard> flashcards = deck.getDeck();
    List<FlashcardDto> flashcardDtos = flashcards.stream()
        .map(flashcard -> new FlashcardMapper()
        .toDto(flashcard))
        .collect(Collectors.toList());

    return new FlashcardDeckDto(deck.getDeckName(), flashcardDtos);
  }

  /**
   * Converts a FlashcardDeckDto to a FlashcardDeck.
   * @param dto the FlashcardDeckDto to convert
   * @return the corresponding FlashcardDeck
   */
  public FlashcardDeck fromDto(FlashcardDeckDto dto) {
    if (dto == null) {
      throw new IllegalArgumentException("FlashcardDeckDto cannot be null");
    }

    List<FlashcardDto> flashcardDtos = dto.getDeck();
    List<Flashcard> flashcards = flashcardDtos.stream()
        .map(flashcardDto -> new FlashcardMapper()
        .fromDto(flashcardDto))
        .collect(Collectors.toList());

    FlashcardDeck deck = new FlashcardDeck(dto.getDeckName());
    deck.setDeck(flashcards);
    return deck;
  }

  /**
   * Converts a list of FlashcardDecks to a list of FlashcardDeckDtos.
   * @param decks the list of FlashcardDecks to convert
   * @return the corresponding list of FlashcardDeckDtos
   */
  public List<FlashcardDeckDto> toDtoList(List<FlashcardDeck> decks) {
    if (decks == null) {
      throw new IllegalArgumentException("Decks list cannot be null");
    }

    return decks.stream()
        .map(this::toDto)
        .collect(Collectors.toList());
  }

  /**
   * Converts a list of FlashcardDeckDtos to a list of FlashcardDecks.
   * @param dtoList the list of FlashcardDeckDtos to convert
   * @return the corresponding list of FlashcardDecks
   */
  public List<FlashcardDeck> fromDtoList(List<FlashcardDeckDto> dtoList) {
    if (dtoList == null) {
      throw new IllegalArgumentException("FlashcardDeckDto list cannot be null");
    }

    return dtoList.stream()
        .map(this::fromDto)
        .collect(Collectors.toList());
  }
}
  