package app;


import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;



public class FlashcardDeckTest {

  private static Flashcard flashcard;
  private static FlashcardDeck flashcardDeck;
  
  @BeforeAll
  static void setUp(){
    flashcard = new Flashcard("question", "answer");
    flashcardDeck = new FlashcardDeck();
  }

  
  @Test
  void testAddFlashcard() {
    flashcardDeck.addFlashcard(flashcard);

    Flashcard card = flashcardDeck.getDeck().get(0);
    assertEquals("question", card.getQuestion());
    assertEquals("answer", card.getAnswer());
  }

  @Test
  void testGetDeckName(){
    flashcardDeck.setDeckName("test");
    assertEquals("test", flashcardDeck.getDeckName());
  }

  @Test
  void testIsQuestion(){
    assertFalse(flashcardDeck.isQuestion("wrong"));
  }

  @Test
  void testRemoveFlashcardByIndex(){
    flashcardDeck.addFlashcard(flashcard);
    Flashcard flashcard2 = new Flashcard("question2", "answer2");
    flashcardDeck.addFlashcard(flashcard2);
    assertFalse(flashcardDeck.removeFlashcardByIndex(-1));
    assertTrue(flashcardDeck.removeFlashcardByIndex(1));
    assertEquals(flashcard, flashcardDeck.getDeck().get(0));
  }

  @Test
  void testSetDeck(){
    List<Flashcard> deck = new ArrayList<>();
    Flashcard flashcard2 = new Flashcard("question2", "answer2");
    deck.add(flashcard2);
    flashcardDeck.setDeck(deck);
    assertEquals(1, flashcardDeck.getDeck().size());
  }
}