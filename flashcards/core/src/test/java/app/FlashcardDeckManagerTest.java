package app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test class for FlashcardDeckManager functionality.
 * Tests deck management operations including adding and removing decks.
 * 
 * @author Generated with Claude Sonnet 4 via GitHub Copilot
 */
public class FlashcardDeckManagerTest {

  private static FlashcardDeckManager manager;
  private static FlashcardDeck flashcardDeck;
  private static Flashcard flashcard;
  
  /**
   * Sets up test fixtures before all tests.
   * Initializes a FlashcardDeckManager, FlashcardDeck, and sample Flashcard.
   * 
   * @author Generated with Claude Sonnet 4 via GitHub Copilot
   */
  @BeforeAll
  static void setUp(){
    flashcard = new Flashcard("question", "answer");
    flashcardDeck = new FlashcardDeck();
    manager = new FlashcardDeckManager();
  }

  /**
   * Tests the addDeck method of FlashcardDeckManager with various scenarios.
   * Verifies that:
   * - Adding an empty deck (no flashcards) throws IllegalArgumentException
   * - Adding a deck without a name throws IllegalArgumentException  
   * - Adding a valid deck with name and flashcards succeeds
   * - Adding a duplicate deck name throws IllegalArgumentException
   * - Adding a deck with blank/whitespace name throws IllegalArgumentException
   * - Adding a null-named deck throws IllegalArgumentException
   * - Multiple valid decks can be added successfully
   * 
   * @author Generated with Claude Sonnet 4 via GitHub Copilot
   */
  @Test
  void testAddDeck(){
    assertThrows(IllegalArgumentException.class,() -> manager.addDeck(flashcardDeck));

    flashcardDeck.addFlashcard(flashcard);
    assertThrows(IllegalArgumentException.class, () -> manager.addDeck(flashcardDeck));

    flashcardDeck.setDeckName("test");
    manager.addDeck(flashcardDeck);
    assertEquals(1, manager.getDecks().size());

    FlashcardDeck nDeck = new FlashcardDeck("test");
    assertThrows(IllegalArgumentException.class, () ->  manager.addDeck(nDeck));

    FlashcardDeck deck = new FlashcardDeck(" ");
    assertThrows(IllegalArgumentException.class, () ->  manager.addDeck(deck));

    FlashcardDeck deckNull = new FlashcardDeck();
    assertThrows(IllegalArgumentException.class, () -> manager.addDeck(deckNull));

    FlashcardDeck deck1 = new FlashcardDeck();
    deck1.setDeckName("1");
    manager.addDeck(deck1);
    FlashcardDeck deck2 = new FlashcardDeck();
    deck2.setDeckName("2");
    manager.addDeck(deck2);
    FlashcardDeck deck3 = new FlashcardDeck();
    deck3.setDeckName("3");
    manager.addDeck(deck3);
    FlashcardDeck deck4 = new FlashcardDeck();
    deck4.setDeckName("4");
    manager.addDeck(deck4);
    FlashcardDeck deck5 = new FlashcardDeck();
    deck5.setDeckName("5");
    manager.addDeck(deck5);
    FlashcardDeck deck6 = new FlashcardDeck();
    deck6.setDeckName("6");
    manager.addDeck(deck6);
    FlashcardDeck deck7 = new FlashcardDeck();
    deck7.setDeckName("7");    
    manager.addDeck(deck7);
    FlashcardDeck deck8 = new FlashcardDeck();
    deck8.setDeckName("8");    
    assertThrows(IllegalArgumentException.class, () -> manager.addDeck(deck8));

  }

  /**
   * Tests the removeDeck method of FlashcardDeckManager.
   * Verifies that:
   * - Removing an existing deck decreases the deck count by 1
   * - Manager correctly updates its internal collection after removal
   * 
   * @author Generated with Claude Sonnet 4 via GitHub Copilot
   */
  @Test
  void testRemoveDeck(){
    manager.removeDeck(flashcardDeck);
    assertEquals(7, manager.getDecks().size());
  }
}
