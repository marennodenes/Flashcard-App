package dto.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import app.Flashcard;
import dto.FlashcardDto;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link FlashcardMapper} class.
 * This test class verifies the correct mapping between Flashcard and FlashcardDto.
 *
 * @author parts of this code is generated with claude.ai
 * @author marennod
 * @author ailinat
 */
public class FlashcardMapperTest {

  /**
   * Tests toDto maps all fields correctly.
   */
  @Test
  public void testToDto() {
    Flashcard card = new Flashcard(5, "What is AI?", "Artificial Intelligence");
    FlashcardDto dto = new FlashcardMapper().toDto(card);
    assertEquals("What is AI?", dto.getQuestion());
    assertEquals("Artificial Intelligence", dto.getAnswer());
    assertEquals(5, dto.getNumber());
  }

  /**
   * Tests fromDto maps all fields correctly.
   */
  @Test
  public void testFromDto() {
    FlashcardDto dto = new FlashcardDto("Q1", "A1", 2);
    Flashcard card = new FlashcardMapper().fromDto(dto);
    assertEquals("Q1", card.getQuestion());
    assertEquals("A1", card.getAnswer());
    assertEquals(2, card.getNumber());
  }

  /**
   * Tests toDtoList maps a list of Flashcard to a list of FlashcardDto.
   */
  @Test
  public void testToDtoList() {
    List<Flashcard> cards = List.of(
        new Flashcard(1, "Q1", "A1"),
        new Flashcard(2, "Q2", "A2")
    );
    
    List<FlashcardDto> dtos = new FlashcardMapper().toDtoList(cards);
    assertEquals(2, dtos.size());
    assertEquals("Q1", dtos.get(0).getQuestion());
    assertEquals("A2", dtos.get(1).getAnswer());
  }

  /**
   * Tests fromDtoList maps a list of FlashcardDto to a list of Flashcard.
   */
  @Test
  public void testFromDtoList() {
    List<FlashcardDto> dtos = List.of(
        new FlashcardDto("Q1", "A1", 1),
         new FlashcardDto("Q2", "A2", 2)
    );
    
    List<Flashcard> cards = new FlashcardMapper().fromDtoList(dtos);
    assertEquals(2, cards.size());
    assertEquals("Q1", cards.get(0).getQuestion());
    assertEquals("A2", cards.get(1).getAnswer());
  }

  /**
   * Tests null input handling for toDto.
   */
  @Test
  public void testToDtoNullInput() {
    FlashcardMapper mapper = new FlashcardMapper();
    assertThrows(IllegalArgumentException.class, () -> mapper.toDto(null));
  }

  /**
   * Tests null input handling for fromDto.
   */
  @Test
  public void testFromDtoNullInput() {
    FlashcardMapper mapper = new FlashcardMapper();
    assertThrows(IllegalArgumentException.class, () -> mapper.fromDto(null));
  }

  /**
   * Tests toDtoList with empty list.
   */
  @Test
  public void testToDtoListEmpty() {
    List<Flashcard> empty = Collections.emptyList();
    List<FlashcardDto> dtos = new FlashcardMapper().toDtoList(empty);
    assertNotNull(dtos);
    assertTrue(dtos.isEmpty());
  }

  /**
   * Tests fromDtoList with empty list.
   */
  @Test
  public void testFromDtoListEmpty() {
    List<FlashcardDto> empty = Collections.emptyList();
    List<Flashcard> cards = new FlashcardMapper().fromDtoList(empty);
    assertNotNull(cards);
    assertTrue(cards.isEmpty());
  }
}
