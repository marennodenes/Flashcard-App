package app;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FlashcardTest {

  private static Flashcard flashcard;
  private static Flashcard flashcard2;
  private static Flashcard flashcard3;

  @BeforeAll
  static void setUp(){
    flashcard = new Flashcard("Question", "Answer");
    flashcard2 = new Flashcard();
    flashcard3 = new Flashcard(5, "Hi", "Bye");

  }

  @Test
  void testQuestion(){
    assertEquals("Question", flashcard.getQuestion());
    flashcard.setQuestion("Hei");
    assertEquals("Hei", flashcard.getQuestion());
  }

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

  @Test
  void testGetNumber(){
    flashcard2.setNumber(3);
    assertEquals(3, flashcard2.getNumber());
    assertEquals(5, flashcard3.getNumber());
  }

  @Test
  void testToString(){
    assertEquals("Q: Question\nA: Answer", flashcard.toString());
  }
}