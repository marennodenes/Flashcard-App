package dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;


/**
 * Unit tests for the {@link FlashcardDeckManagerDto} class.
 * This test class verifies the correct construction and behavior of FlashcardDeckManagerDto,
 * including deck list handling and defensive copying.
 *
 * @author marennod
 * @author ailinat
 */
public class FlashcardDeckManagerDtoTest {

  /**
   * Tests the default constructor.
   * Verifies that the deck list is initialized as empty.
   */
  @Test
  public void testDefaultConstructor() {
    FlashcardDeckManagerDto manager = new FlashcardDeckManagerDto();
    assertTrue(manager.getDecks().isEmpty());
  }

  /**
   * Tests the constructor with a list of decks.
   */
  @Test
  public void testConstructorWithDecks() {
    List<FlashcardDeckDto> decks = new ArrayList<>();
    decks.add(new FlashcardDeckDto("Math", List.of()));
    decks.add(new FlashcardDeckDto("Science", List.of()));
    
    FlashcardDeckManagerDto manager = new FlashcardDeckManagerDto(decks);
    assertEquals(2, manager.getDecks().size());
    assertEquals("Math", manager.getDecks().get(0).getDeckName());
    assertEquals("Science", manager.getDecks().get(1).getDeckName());
  }

  /**
   * Tests that getDecks returns a defensive copy.
   * Verifies that the returned list is not the same instance as the original.
   */
  @Test
  public void testGetDecksReturnsCopy() {
    List<FlashcardDeckDto> decks = new ArrayList<>();
    decks.add(new FlashcardDeckDto("History", List.of()));
    FlashcardDeckManagerDto manager = new FlashcardDeckManagerDto(decks);
    List<FlashcardDeckDto> copy = manager.getDecks();
    assertNotSame(decks, copy);
    assertEquals(decks.size(), copy.size());
  }
}
