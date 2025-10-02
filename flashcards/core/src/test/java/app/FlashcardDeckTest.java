package app;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


/**
 * Test class for FlashcardDeck functionality.
 * Tests deck operations including adding flashcards, managing deck names, and validation.
 * 
 * @author Generated with Claude Sonnet 4 via GitHub Copilot
 */
public class FlashcardDeckTest {

  private static Flashcard flashcard;
  private static FlashcardDeck flashcardDeck;

  /**
   * Sets up test fixtures before all tests.
   * Initializes a sample Flashcard and FlashcardDeck for testing.
   * 
   * @author Generated with Claude Sonnet 4 via GitHub Copilot
   */
  @BeforeAll
  static void setUp(){
    flashcard = new Flashcard("question", "answer");
    flashcardDeck = new FlashcardDeck();
  }

  /**
   * Tests the addFlashcard method of FlashcardDeck.
   * Verifies that:
   * - Flashcards can be successfully added to the deck
   * - Added flashcards retain their correct question and answer values
   * 
   * @author Generated with Claude Sonnet 4 via GitHub Copilot
   */
  @Test
  void testAddFlashcard() {
    flashcardDeck.addFlashcard(flashcard);

    Flashcard card = flashcardDeck.getDeck().get(0);
    assertEquals("question", card.getQuestion());
    assertEquals("answer", card.getAnswer());
  }

  /**
   * Tests the setDeckName and getDeckName methods of FlashcardDeck.
   * Verifies that:
   * - Deck name can be successfully set
   * - The retrieved deck name matches the set value
   * - Name persistence works correctly
   * 
   * @author Generated with Claude Sonnet 4 via GitHub Copilot
   */
  @Test
  void testGetDeckName(){
    flashcardDeck.setDeckName("test");
    assertEquals("test", flashcardDeck.getDeckName());
  }
  
  /**
   * Tests the isQuestion method of FlashcardDeck.
   * Verifies that:
   * - The method correctly returns false for non-matching questions
   * - Question validation logic works as expected
   * 
   * @author Generated with Claude Sonnet 4 via GitHub Copilot
   */
  @Test
  void testIsQuestion(){
    assertFalse(flashcardDeck.isQuestion("wrong"));
  }
  
  /**
   * Tests the removeFlashcardByIndex method of FlashcardDeck.
   * Verifies that:
   * - Removing with invalid index (-1) returns false
   * - Removing with valid index returns true
   * - The correct flashcard is removed from the deck
   * - Remaining flashcards maintain their correct positions
   * 
   * @author Generated with Claude Sonnet 4 via GitHub Copilot
   */
  @Test
  void testRemoveFlashcardByIndex(){
    flashcardDeck.addFlashcard(flashcard);
    Flashcard flashcard2 = new Flashcard("question2", "answer2");
    flashcardDeck.addFlashcard(flashcard2);
    assertFalse(flashcardDeck.removeFlashcardByIndex(-1));
    assertTrue(flashcardDeck.removeFlashcardByIndex(1));
    assertEquals(flashcard, flashcardDeck.getDeck().get(0));
  }

  /**
   * Tests the setDeck method of FlashcardDeck.
   * 
   * @author Generated with Claude Sonnet 4 via GitHub Copilot
   */
  @Test
  void testSetDeck(){
    List<Flashcard> deck = new ArrayList<>();
    Flashcard flashcard2 = new Flashcard("question2", "answer2");
    deck.add(flashcard2);
    flashcardDeck.setDeck(deck);
    assertEquals(1, flashcardDeck.getDeck().size());
  }
}