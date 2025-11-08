package dto.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import app.Flashcard;
import app.FlashcardDeck;
import app.FlashcardDeckManager;
import app.PasswordEncoder;
import app.User;
import app.UserData;
import dto.FlashcardDeckDto;
import dto.FlashcardDeckManagerDto;
import dto.FlashcardDto;
import dto.LoginRequestDto;
import dto.UserDataDto;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link UserMapper} class.
 * This test class verifies the correct mapping between 
 * User/UserData and UserDataDto/LoginRequestDto.
 *
 * @author marennod
 * @author ailinat
 * 
 * @see "docs/release_3/ai_tools.md"
 */
public class UserMapperTest {

  /**
   * Tests toDto(User) maps username and password correctly.
   */
  @Test
  public void testToDtoFromUser() {
    User user = new User("alice", "pw");
    UserDataDto dto = new UserMapper().toDto(user);
    assertEquals("alice", dto.getUsername());
    assertTrue(PasswordEncoder.matches("pw", dto.getPassword()));
  }

  /**
   * Tests toDto(UserData) maps all fields and nested decks/cards.
   */
  @Test
  public void testToDtoFromUserData() {
    Flashcard card = new Flashcard("Q", "A");
    card.setNumber(1);
    FlashcardDeck deck = new FlashcardDeck("Deck1");
    deck.addFlashcard(card);
    FlashcardDeckManager manager = new FlashcardDeckManager();
    manager.addDeck(deck);
    User user = new User("bob", "secret");
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
  public void testFromDto() {
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
  public void testFromLoginRequestDto() {
    LoginRequestDto dto = new LoginRequestDto("sofia", "pw123");
    User user = new UserMapper().fromLoginRequestDto(dto);
    assertEquals("sofia", user.getUsername());
    assertTrue(PasswordEncoder.matches("pw123", user.getPassword()));
  }

  /**
   * Tests fromLoginRequestDto with null username and password.
   */
  @Test
  public void testFromLoginRequestDtoNullFields() {
    LoginRequestDto dto = new LoginRequestDto(null, null);
    User user = new UserMapper().fromLoginRequestDto(dto);
    assertEquals("", user.getUsername());
    assertTrue(PasswordEncoder.matches("",
               user.getPassword()));
  }

  /**
   * Tests fromLoginRequestDto with null username only.
   */
  @Test
  public void testFromLoginRequestDtoNullUsername() {
    LoginRequestDto dto = new LoginRequestDto(null, "pw123");
    User user = new UserMapper().fromLoginRequestDto(dto);
    assertEquals("", user.getUsername());
    assertTrue(PasswordEncoder.matches("pw123", user.getPassword()));
  }

  /**
   * Tests fromLoginRequestDto with null password only.
   */
  @Test
  public void testFromLoginRequestDtoNullPassword() {
    LoginRequestDto dto = new LoginRequestDto("sofia", null);
    User user = new UserMapper().fromLoginRequestDto(dto);
    assertEquals("sofia", user.getUsername());
    assertTrue(PasswordEncoder.matches("", user.getPassword()));
  }

  /**
   * Tests toDto(User) with null input returns UserDataDto with empty fields.
   */
  @Test
  public void testToDtoUserNullInput() {
    UserDataDto dto = new UserMapper().toDto((User) null);
    assertNotNull(dto);
    assertEquals("", dto.getUsername());
    assertEquals("", dto.getPassword());
  }

  /**
   * Tests toDto(UserData) with null input throws NullPointerException.
   */
  @Test
  public void testToDtoUserDataNullInput() {
    UserMapper mapper = new UserMapper();
    assertThrows(NullPointerException.class, () -> mapper.toDto((UserData) null));
  }

  /**
   * Tests fromDto(UserDataDto) with null input throws NullPointerException.
   */
  @Test
  public void testFromDtoNullInput() {
    UserMapper mapper = new UserMapper();
    assertThrows(NullPointerException.class, () -> mapper.fromDto(null));
  }

  /**
   * Tests fromLoginRequestDto with null input throws NullPointerException.
   */
  @Test
  public void testFromLoginRequestDtoNullInput() {
    UserMapper mapper = new UserMapper();
    assertThrows(NullPointerException.class, () -> mapper.fromLoginRequestDto(null));
  }
}
