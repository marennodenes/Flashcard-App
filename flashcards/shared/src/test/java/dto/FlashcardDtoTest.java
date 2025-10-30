package dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link FlashcardDto} class.
 * <p>
 * This test class verifies the correct construction and behavior of FlashcardDto,
 * including question, answer, and number handling.
 * @author marennod
 * @author ailinat
 */
public class FlashcardDtoTest {

    /**
     * Tests the constructor with question, answer, and number.
     * Verifies that all fields are set correctly.
     */
    @Test
    void testConstructorWithAllFields() {
        FlashcardDto dto = new FlashcardDto("What is Java?", "A programming language", 1);
        assertEquals("What is Java?", dto.getQuestion());
        assertEquals("A programming language", dto.getAnswer());
        assertEquals(1, dto.getNumber());
    }
}
