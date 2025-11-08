package dto.mappers;

import app.Flashcard;
import app.FlashcardDeck;
import app.FlashcardDeckManager;
import app.User;
import app.UserData;
import dto.FlashcardDeckDto;
import dto.FlashcardDeckManagerDto;
import dto.FlashcardDto;
import dto.LoginRequestDto;
import dto.UserDataDto;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper for converting between User/UserData and UserDataDto or LoginRequestDto.
 * Also handles mapping of deckManager
 *
 * @author parts of this code is generated with the help of Claude Sonnet 4.
 * @author ailinat
 * @author sofietw
 * @author marennod
 */
public class UserMapper {

  /**
   * Converts a User to a UserDataDto.
   *
   * @param user User-object
   * @return UserDataDto with username and password
   */
  public UserDataDto toDto(User user) {
    return new UserDataDto(
      user != null ? user.getUsername() : "",
      user != null ? user.getPassword() : ""
    );
  }

  /**
   * Converts a UserData object to a UserDataDto object.
   *
   * @param userData UserData-object
   * @return UserDataDto with username, password and deck manager
   */
  public UserDataDto toDto(UserData userData) {
    String username = userData.getUser() != null ? userData.getUser().getUsername() : "";
    String password = userData.getUser() != null ? userData.getUser().getPassword() : "";
    List<FlashcardDeckManagerDto> deckManagers = new ArrayList<>();
    
    if (userData.getDeckManager() != null) {
      List<FlashcardDeckDto> deckDtos = new ArrayList<>();

      for (FlashcardDeck deck : userData.getDeckManager().getDecks()) {
        List<FlashcardDto> cardDtos = new ArrayList<>();
        for (Flashcard card : deck.getDeck()) {
          cardDtos.add(new FlashcardDto(card.getQuestion(), card.getAnswer(), card.getNumber()));
        }
        deckDtos.add(new FlashcardDeckDto(deck.getDeckName(), cardDtos));
      }
      deckManagers.add(new FlashcardDeckManagerDto(deckDtos));
    }
    return new UserDataDto(username, password, deckManagers);
  }

  /**
   * Converts a UserDataDto to a UserData object, including a deckManager.
   *
   * @param dto UserDataDto-object
   * @return UserData with User and DeckManager
   */
  public UserData fromDto(UserDataDto dto) {
    String username = dto.getUsername() != null ? dto.getUsername() : "";
    String password = dto.getPassword() != null ? dto.getPassword() : "";
    FlashcardDeckManager deckManager = new FlashcardDeckManager();

    if (dto.getDeckManager() != null && !dto.getDeckManager().isEmpty()) {
      for (FlashcardDeckManagerDto managerDto : dto.getDeckManager()) {
        for (FlashcardDeckDto deckDto : managerDto.getDecks()) {
          FlashcardDeck deck = new FlashcardDeck(deckDto.getDeckName());
          for (FlashcardDto cardDto : deckDto.getDeck()) {
            deck.addFlashcard(new Flashcard(cardDto.getQuestion(), cardDto.getAnswer()));
          }
          deckManager.addDeck(deck);
        }
      }
    }

    return new UserData(new User(username, password), deckManager);
  }

  /**
   * Converts a LoginRequestDto to a User object.
   *
   * @param dto LoginRequestDto object with username and password
   * @return User object after logging in
   */
  public User fromLoginRequestDto(LoginRequestDto dto) {
    String username = dto.getUsername() != null ? dto.getUsername() : "";
    String password = dto.getPassword() != null ? dto.getPassword() : "";

    return new User(username, password);
  }
}
