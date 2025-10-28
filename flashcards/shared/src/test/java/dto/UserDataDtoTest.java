package dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Unit tests for the {@link UserDataDto} class.
 * <p>
 * This test class verifies the correct construction and behavior of UserDataDto,
 * including handling of username, password, deck manager, and defensive copying.
 * @author marennod
 * @author ailinat
 */
public class UserDataDtoTest {

    /**
     * Tests the constructor with username and password only.
     * Verifies that the username and password are set and deckManager is empty.
     */
    @Test
    void testConstructorWithUsernameAndPassword() {
        UserDataDto dto = new UserDataDto("alice", "secret");
        assertEquals("alice", dto.getUsername());
        assertEquals("secret", dto.getPassword());
        assertTrue(dto.getDeckManager().isEmpty());
    }

    /**
     * Tests the constructor with username, password, and decks.
     * Verifies that all fields are set and deckManager contains the expected decks.
     */
    @Test
    void testConstructorWithDecks() {
        List<FlashcardDeckManagerDto> decks = new ArrayList<>();
        decks.add(new FlashcardDeckManagerDto(Collections.emptyList()));
        UserDataDto dto = new UserDataDto("bob", "pass", decks);
        assertEquals("bob", dto.getUsername());
        assertEquals("pass", dto.getPassword());
        assertEquals(1, dto.getDeckManager().size());
    }

    /**
     * Tests the constructor with null decks.
     * Verifies that deckManager is initialized as an empty list.
     */
    @Test
    void testNullDecksInConstructor() {
        UserDataDto dto = new UserDataDto("eve", "pw", null);
        assertEquals("eve", dto.getUsername());
        assertEquals("pw", dto.getPassword());
        assertTrue(dto.getDeckManager().isEmpty());
    }

    /**
     * Tests that getDeckManager returns a defensive copy.
     * Verifies that the returned list is not the same instance as the original.
     */
    @Test
    void testDeckManagerReturnsCopy() {
        List<FlashcardDeckManagerDto> decks = new ArrayList<>();
        decks.add(new FlashcardDeckManagerDto(Collections.emptyList()));
        UserDataDto dto = new UserDataDto("dan", "pw", decks);
        List<FlashcardDeckManagerDto> copy = dto.getDeckManager();
        assertNotSame(decks, copy);
        assertEquals(decks.size(), copy.size());
    }
}
