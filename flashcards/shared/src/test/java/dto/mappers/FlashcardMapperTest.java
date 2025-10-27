package dto.mappers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import app.Flashcard;
import dto.FlashcardDto;

/**
 * Unit tests for the {@link FlashcardMapper} class.
 * <p>
 * This test class verifies the correct mapping between Flashcard and FlashcardDto.
 * @author marennod
 * @author ailinat
 */
public class FlashcardMapperTest {

    /**
     * Tests toDto maps all fields correctly.
     */
    @Test
    void testToDto() {
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
    void testFromDto() {
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
    void testToDtoList() {
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
    void testFromDtoList() {
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
    void testToDtoNullInput() {
        FlashcardMapper mapper = new FlashcardMapper();
        assertThrows(IllegalArgumentException.class, () -> mapper.toDto(null));
    }

    /**
     * Tests null input handling for fromDto.
     */
    @Test
    void testFromDtoNullInput() {
        FlashcardMapper mapper = new FlashcardMapper();
        assertThrows(IllegalArgumentException.class, () -> mapper.fromDto(null));
    }

    /**
     * Tests toDtoList with empty list.
     */
    @Test
    void testToDtoListEmpty() {
        List<Flashcard> empty = Collections.emptyList();
        List<FlashcardDto> dtos = new FlashcardMapper().toDtoList(empty);
        assertNotNull(dtos);
        assertTrue(dtos.isEmpty());
    }

    /**
     * Tests fromDtoList with empty list.
     */
    @Test
    void testFromDtoListEmpty() {
        List<FlashcardDto> empty = Collections.emptyList();
        List<Flashcard> cards = new FlashcardMapper().fromDtoList(empty);
        assertNotNull(cards);
        assertTrue(cards.isEmpty());
    }
}
