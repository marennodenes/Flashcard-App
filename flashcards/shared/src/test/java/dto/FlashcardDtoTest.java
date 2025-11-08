package dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


/**
 * Unit tests for the {@link FlashcardDto} class.
 * This test class verifies the correct construction and behavior of FlashcardDto,
 * including question, answer, and number handling.
 *
 * @author parts of this code is generated with claude.ai
 * @author marennod
 * @author ailinat
 */
public class FlashcardDtoTest {

  /**
   * Tests the constructor with question, answer, and number.
   * Verifies that all fields are set correctly.
   */
  @Test
  public void testConstructorWithAllFields() {
    FlashcardDto dto = new FlashcardDto("What is Java?", "A programming language", 1);
    assertEquals("What is Java?", dto.getQuestion());
    assertEquals("A programming language", dto.getAnswer());
    assertEquals(1, dto.getNumber());
  }
}
