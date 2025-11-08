package dto.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import app.Flashcard;
import app.FlashcardDeck;
import dto.FlashcardDeckDto;
import dto.FlashcardDto;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link FlashcardDeckMapper} class.
 * This test class verifies the correct mapping between FlashcardDeck and FlashcardDeckDto.
 *
 * @author parts of this code is generated with claude.ai
 * @author marennod
 * @author ailinat
 */
public class FlashcardDeckMapperTest {

  /**
   * Tests toDto maps all fields and nested flashcards correctly.
   */
  @Test
  public void testToDto() {
    FlashcardDeck deck = new FlashcardDeck("History");
    deck.addFlashcard(new Flashcard(1, "Q1", "A1"));
    deck.addFlashcard(new Flashcard(2, "Q2", "A2"));
        
    FlashcardDeckDto dto = new FlashcardDeckMapper().toDto(deck);
    assertEquals("History", dto.getDeckName());
    assertEquals(2, dto.getDeck().size());
    assertEquals("Q1", dto.getDeck().get(0).getQuestion());
    assertEquals("A2", dto.getDeck().get(1).getAnswer());
  }

  /**
   * Tests fromDto maps all fields and nested flashcards correctly.
   */
  @Test
  public void testFromDto() {
    List<FlashcardDto> cardDtos = List.of(
        new FlashcardDto("Q1", "A1", 1),
        new FlashcardDto("Q2", "A2", 2)
    );
    
    FlashcardDeckDto dto = new FlashcardDeckDto("Science", cardDtos);
    FlashcardDeck deck = new FlashcardDeckMapper().fromDto(dto);
    assertEquals("Science", deck.getDeckName());
    assertEquals(2, deck.getDeck().size());
    assertEquals("Q1", deck.getDeck().get(0).getQuestion());
    assertEquals("A2", deck.getDeck().get(1).getAnswer());
  }

  /**
   * Tests toDtoList maps a list of FlashcardDecks to a list of FlashcardDeckDtos.
   */
  @Test
  public void testToDtoList() {
    FlashcardDeck deck1 = new FlashcardDeck("Deck1");
    deck1.addFlashcard(new Flashcard(1, "Q1", "A1"));
    FlashcardDeck deck2 = new FlashcardDeck("Deck2");
    deck2.addFlashcard(new Flashcard(2, "Q2", "A2"));
    List<FlashcardDeck> decks = List.of(deck1, deck2);
    List<FlashcardDeckDto> dtos = new FlashcardDeckMapper().toDtoList(decks);
    assertEquals(2, dtos.size());
    assertEquals("Deck1", dtos.get(0).getDeckName());
    assertEquals("A2", dtos.get(1).getDeck().get(0).getAnswer());
  }

  /**
   * Tests fromDtoList maps a list of FlashcardDeckDtos to a list of FlashcardDecks.
   */
  @Test
  public void testFromDtoList() {
    FlashcardDeckDto dto1 = new FlashcardDeckDto("DeckA", List.of(new FlashcardDto("Qx", "Ax", 1)));
    FlashcardDeckDto dto2 = new FlashcardDeckDto("DeckB", List.of(new FlashcardDto("Qy", "Ay", 2)));
    List<FlashcardDeckDto> dtos = List.of(dto1, dto2);
    List<FlashcardDeck> decks = new FlashcardDeckMapper().fromDtoList(dtos);
    assertEquals(2, decks.size());
    assertEquals("DeckA", decks.get(0).getDeckName());
    assertEquals("Ay", decks.get(1).getDeck().get(0).getAnswer());
  }

  /**
   * Tests null input handling for toDto.
   */
  @Test
  public void testToDtoNullInput() {
    FlashcardDeckMapper mapper = new FlashcardDeckMapper();
    assertThrows(IllegalArgumentException.class, () -> mapper.toDto(null));
  }

  /**
   * Tests null input handling for fromDto.
   */
  @Test
  public void testFromDtoNullInput() {
    FlashcardDeckMapper mapper = new FlashcardDeckMapper();
    assertThrows(IllegalArgumentException.class, () -> mapper.fromDto(null));
  }

  /**
   * Tests null input handling for toDtoList.
   */
  @Test
  public void testToDtoListNullInput() {
    FlashcardDeckMapper mapper = new FlashcardDeckMapper();
    assertThrows(IllegalArgumentException.class, () -> mapper.toDtoList(null));
  }

  /**
   * Tests null input handling for fromDtoList.
   */
  @Test
  public void testFromDtoListNullInput() {
    FlashcardDeckMapper mapper = new FlashcardDeckMapper();
    assertThrows(IllegalArgumentException.class, () -> mapper.fromDtoList(null));
  }

  /**
   * Tests toDto with empty deck.
   */
  @Test
  public void testToDtoEmptyDeck() {
    FlashcardDeck deck = new FlashcardDeck("Empty");
    FlashcardDeckDto dto = new FlashcardDeckMapper().toDto(deck);
    assertEquals("Empty", dto.getDeckName());
    assertNotNull(dto.getDeck());
    assertTrue(dto.getDeck().isEmpty());
  }

  /**
   * Tests fromDto with empty deck.
   */
  @Test
  public void testFromDtoEmptyDeck() {
    FlashcardDeckDto dto = new FlashcardDeckDto("Empty", Collections.emptyList());
    FlashcardDeck deck = new FlashcardDeckMapper().fromDto(dto);
    assertEquals("Empty", deck.getDeckName());
    assertNotNull(deck.getDeck());
    assertTrue(deck.getDeck().isEmpty());
  }

  /**
   * Tests toDtoList with empty list.
   */
  @Test
  public void testToDtoListEmpty() {
    List<FlashcardDeck> empty = Collections.emptyList();
    List<FlashcardDeckDto> dtos = new FlashcardDeckMapper().toDtoList(empty);
    assertNotNull(dtos);
    assertTrue(dtos.isEmpty());
  }

  /**
   * Tests fromDtoList with empty list.
   */
  @Test
  public void testFromDtoListEmpty() {
    List<FlashcardDeckDto> empty = Collections.emptyList();
    List<FlashcardDeck> decks = new FlashcardDeckMapper().fromDtoList(empty);
    assertNotNull(decks);
    assertTrue(decks.isEmpty());
  }
}
