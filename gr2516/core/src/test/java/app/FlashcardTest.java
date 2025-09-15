package app;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FlashcardTest {

  @Test
  void testQuestion(){
    Flashcard flashcard = new Flashcard("Question", "Answer");
    assertEquals("Question", flashcard.getQuestion());
  }

  @Test
  void testAnswer(){
    Flashcard flashcard = new Flashcard("Question", "Answer");
    assertEquals("Answer", flashcard.getAnswer());
  }
}