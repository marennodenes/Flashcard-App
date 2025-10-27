package dto.mappers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import app.*;
import dto.*;

/**
 * Unit tests for the {@link UserMapper} class.
 * <p>
 * This test class verifies the correct mapping between User/UserData and UserDataDto/LoginRequestDto.
 * @author marennod
 * @author ailinat
 */
public class UserMapperTest {

    /**
     * Tests toDto(User) maps username and password correctly.
     */
    @Test
    void testToDtoFromUser() {
        User user = new User("alice", "pw");
        UserDataDto dto = new UserMapper().toDto(user);
        assertEquals("alice", dto.getUsername());
        assertTrue(PasswordEncoder.matches("pw", dto.getPassword()));
    }

    /**
     * Tests toDto(UserData) maps all fields and nested decks/cards.
     */
    @Test
    void testToDtoFromUserData() {
        User user = new User("bob", "secret");
        Flashcard card = new Flashcard("Q", "A");
        card.setNumber(1);
        FlashcardDeck deck = new FlashcardDeck("Deck1");
        deck.addFlashcard(card);
        FlashcardDeckManager manager = new FlashcardDeckManager();
        manager.addDeck(deck);
        UserData userData = new UserData(user, manager);
        UserDataDto dto = new UserMapper().toDto(userData);
        assertEquals("bob", dto.getUsername());
        assertTrue(PasswordEncoder.matches("secret", dto.getPassword()));
        assertEquals(1, dto.getDeckManager().size());
        assertEquals("Deck1", dto.getDeckManager().get(0).getDecks().get(0).getDeckName());
        assertEquals("Q", dto.getDeckManager().get(0).getDecks().get(0).getDeck().get(0).getQuestion());
    }

    /**
     * Tests fromDto(UserDataDto) maps all fields and nested decks/cards.
     */
    @Test
    void testFromDto() {
        FlashcardDto cardDto = new FlashcardDto("Q2", "A2", 1);
        FlashcardDeckDto deckDto = new FlashcardDeckDto("Deck2", List.of(cardDto));
        FlashcardDeckManagerDto managerDto = new FlashcardDeckManagerDto(List.of(deckDto));
        UserDataDto dto = new UserDataDto("eve", "pw", List.of(managerDto));
        UserData userData = new UserMapper().fromDto(dto);
        assertEquals("eve", userData.getUser().getUsername());
        assertTrue(PasswordEncoder.matches("pw", userData.getUser().getPassword()));
        assertEquals(1, userData.getDeckManager().getDecks().size());
        assertEquals("Deck2", userData.getDeckManager().getDecks().get(0).getDeckName());
        assertEquals("Q2", userData.getDeckManager().getDecks().get(0).getDeck().get(0).getQuestion());
    }

    /**
     * Tests fromLoginRequestDto maps username and password.
     */
    @Test
    void testFromLoginRequestDto() {
        LoginRequestDto dto = new LoginRequestDto("sofia", "pw123");
        User user = new UserMapper().fromLoginRequestDto(dto);
        assertEquals("sofia", user.getUsername());
        assertTrue(PasswordEncoder.matches("pw123", user.getPassword()));
    }

    /**
     * Tests fromLoginRequestDto with null username and password.
     */
    @Test
    void testFromLoginRequestDtoNullFields() {
        LoginRequestDto dto = new LoginRequestDto(null, null);
        User user = new UserMapper().fromLoginRequestDto(dto);
        assertEquals("", user.getUsername());
        assertTrue(PasswordEncoder.matches("", user.getPassword()));
    }

    /**
     * Tests fromLoginRequestDto with null username only.
     */
    @Test
    void testFromLoginRequestDtoNullUsername() {
        LoginRequestDto dto = new LoginRequestDto(null, "pw123");
        User user = new UserMapper().fromLoginRequestDto(dto);
        assertEquals("", user.getUsername());
        assertTrue(PasswordEncoder.matches("pw123", user.getPassword()));
    }

    /**
     * Tests fromLoginRequestDto with null password only.
     */
    @Test
    void testFromLoginRequestDtoNullPassword() {
        LoginRequestDto dto = new LoginRequestDto("sofia", null);
        User user = new UserMapper().fromLoginRequestDto(dto);
        assertEquals("sofia", user.getUsername());
        assertTrue(PasswordEncoder.matches("", user.getPassword()));
    }

    /**
     * Tests toDto(User) with null input returns UserDataDto with empty fields.
     */
    @Test
    void testToDtoUserNullInput() {
        UserDataDto dto = new UserMapper().toDto((User) null);
        assertNotNull(dto);
        assertEquals("", dto.getUsername());
        assertEquals("", dto.getPassword());
    }

    /**
     * Tests toDto(UserData) with null input throws NullPointerException.
     */
    @Test
    void testToDtoUserDataNullInput() {
        UserMapper mapper = new UserMapper();
        assertThrows(NullPointerException.class, () -> mapper.toDto((UserData) null));
    }

    /**
     * Tests fromDto(UserDataDto) with null input throws NullPointerException.
     */
    @Test
    void testFromDtoNullInput() {
        UserMapper mapper = new UserMapper();
        assertThrows(NullPointerException.class, () -> mapper.fromDto(null));
    }

    /**
     * Tests fromLoginRequestDto with null input throws NullPointerException.
     */
    @Test
    void testFromLoginRequestDtoNullInput() {
        UserMapper mapper = new UserMapper();
        assertThrows(NullPointerException.class, () -> mapper.fromLoginRequestDto(null));
    }
}
