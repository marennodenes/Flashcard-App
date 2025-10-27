package dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Unit tests for the {@link FlashcardDeckDto} class.
 * <p>
 * This test class verifies the correct construction and behavior of FlashcardDeckDto,
 * including deck name, flashcard list, and default constructor.
 * @author marennod
 * @author ailinat
 */
public class FlashcardDeckDtoTest {

    /**
     * Tests the constructor with deck name and flashcard list.
     * Verifies that all fields are set correctly.
     */
    @Test
    void testConstructorWithAllFields() {
        List<FlashcardDto> cards = List.of(
            new FlashcardDto("Q1", "A1", 1),
            new FlashcardDto("Q2", "A2", 2)
        );
        FlashcardDeckDto dto = new FlashcardDeckDto("MyDeck", cards);
        assertEquals("MyDeck", dto.getDeckName());
        assertEquals(2, dto.getDeck().size());
        assertEquals("Q1", dto.getDeck().get(0).getQuestion());
        assertEquals("A2", dto.getDeck().get(1).getAnswer());
    }

    /**
     * Tests the default constructor.
     * Verifies that the deck name is empty and the flashcard list is empty.
     */
    @Test
    void testDefaultConstructor() {
        FlashcardDeckDto dto = new FlashcardDeckDto();
        assertEquals("", dto.getDeckName());
        assertTrue(dto.getDeck().isEmpty());
    }
}
