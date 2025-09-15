package ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import app.Flashcard;

public class FlashcardManagerTest {
  
  @Test
  void testAddFlashcard() {
    FlashcardManager flashcardManager = new FlashcardManager();
    flashcardManager.addFlashcard("Question", "Answer");
    Flashcard card = flashcardManager.getFlashcards().get(0);
    assertEquals("Question", card.getQuestion());
    assertEquals("Answer", card.getAnswer());
  }

}