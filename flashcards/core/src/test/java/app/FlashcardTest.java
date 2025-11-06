package app;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link Flashcard} functionality.
 * Tests flashcard operations including question/answer management and validation.
 * 
 * @author isamw
 * @see Flashcard
 * 
 */
public class FlashcardTest {

  private static Flashcard flashcard;
  private static Flashcard flashcard2;
  private static Flashcard flashcard3;

  /**
   * Sets up test fixtures before all tests.
   * Initializes sample Flashcard instances with different constructors for testing.
   * 
   * @author Generated with Claude Sonnet 4 via GitHub Copilot
   * 
   */
  @BeforeAll
  static void setUp(){
    flashcard = new Flashcard("Question", "Answer");
    flashcard2 = new Flashcard();
    flashcard3 = new Flashcard(5, "Hi", "Bye");

  }

  /**
   * Tests the getQuestion and setQuestion methods of Flashcard.
   * Verifies that:
   * - Initial question value is correctly retrieved
   * - Question can be successfully updated
   * - Updated question value is properly stored and retrieved
   */
  @Test
  void testQuestion(){
    assertEquals("Question", flashcard.getQuestion());
    flashcard.setQuestion("Hei");
    assertEquals("Hei", flashcard.getQuestion());
    flashcard.setQuestion(null);
    assertEquals("Hei", flashcard.getQuestion());
    flashcard.setQuestion("     ");
    assertEquals("Hei", flashcard.getQuestion());
  }

  /**
   * Tests the getAnswer and setAnswer methods of Flashcard.
   * Verifies that:
   * - Initial answer value is correctly retrieved
   * - Setting empty string does not change the answer (validation)
   * - Setting whitespace-only string does not change the answer (validation)
   * - Setting null value is allowed and properly stored
   */
  @Test
  void testAnswer(){
    assertEquals("Answer", flashcard.getAnswer());
    flashcard.setAnswer("");
    assertEquals("Answer", flashcard.getAnswer());
    flashcard.setAnswer("      ");
    assertEquals("Answer", flashcard.getAnswer());
    flashcard2.setAnswer(null);
    assertEquals(null, flashcard2.getAnswer());
  }

  /**
   * Tests the getNumber and setNumber methods of Flashcard.
   * Verifies that:
   * - Number can be successfully set and retrieved
   * - Constructor with number parameter correctly initializes the number
   * - Number property maintains its value correctly
   */
  @Test
  void testGetNumber(){
    flashcard2.setNumber(3);
    assertEquals(3, flashcard2.getNumber());
    assertEquals(5, flashcard3.getNumber());
  }

  /**
   * Tests the toString method of Flashcard.
   * Verifies that:
   * - The string representation follows the expected format "Q: [question]\nA: [answer]"
   * - Question and answer values are correctly included in the output
   * - String formatting is consistent and properly structured
   */
  @Test
  void testToString(){
    assertEquals("Q: Question\nA: Answer", flashcard.toString());
  }
}