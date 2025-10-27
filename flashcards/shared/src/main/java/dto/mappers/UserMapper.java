package dto.mappers;

import java.util.ArrayList;
import java.util.List;

import app.FlashcardDeckManager;
import app.User;
import app.UserData;
import dto.FlashcardDeckManagerDto;
import dto.LoginRequestDto;
import dto.UserDataDto;

/**
 * Mapper for konvertering mellom User/UserData og UserDataDto.
 * Håndterer også mapping av deckManager.
 *
 * @author ailinat
 * @author sofietw
 * @author marennod
 */
public class UserMapper {

  /**
   * Konverterer en User til UserDataDto.
   * @param user User-objekt
   * @return UserDataDto med brukernavn og passord
   */
  public UserDataDto toDto(User user) {
    return new UserDataDto(
      user != null ? user.getUsername() : "",
      user != null ? user.getPassword() : ""
    );
  }

  /**
   * Konverterer en UserData til UserDataDto, inkludert deckManager.
   * @param userData UserData-objekt
   * @return UserDataDto med brukernavn, passord og deckManager
   */
  public UserDataDto toDto(UserData userData) {
    String username = userData.getUser() != null ? userData.getUser().getUsername() : "";
    String password = userData.getUser() != null ? userData.getUser().getPassword() : "";
    List<FlashcardDeckManagerDto> deckManagers = new ArrayList<>();
    if (userData.getDeckManager() != null) {
      List<dto.FlashcardDeckDto> deckDtos = new ArrayList<>();
      for (app.FlashcardDeck deck : userData.getDeckManager().getDecks()) {
        List<dto.FlashcardDto> cardDtos = new ArrayList<>();
        for (app.Flashcard card : deck.getDeck()) {
          cardDtos.add(new dto.FlashcardDto(card.getQuestion(), card.getAnswer(), card.getNumber()));
        }
        deckDtos.add(new dto.FlashcardDeckDto(deck.getDeckName(), cardDtos));
      }
      deckManagers.add(new FlashcardDeckManagerDto(deckDtos));
    }
    return new UserDataDto(username, password, deckManagers);
  }

  /**
   * Konverterer en UserDataDto til UserData, inkludert deckManager.
   * @param dto UserDataDto-objekt
   * @return UserData med User og deckManager
   */
  public UserData fromDto(UserDataDto dto) {
    String username = dto.getUsername() != null ? dto.getUsername() : "";
    String password = dto.getPassword() != null ? dto.getPassword() : "";
    FlashcardDeckManager deckManager = new FlashcardDeckManager();
    if (dto.getDeckManager() != null && !dto.getDeckManager().isEmpty()) {
      for (FlashcardDeckManagerDto managerDto : dto.getDeckManager()) {
        for (dto.FlashcardDeckDto deckDto : managerDto.getDecks()) {
          app.FlashcardDeck deck = new app.FlashcardDeck(deckDto.getDeckName());
          for (dto.FlashcardDto cardDto : deckDto.getDeck()) {
            deck.addFlashcard(new app.Flashcard(cardDto.getQuestion(), cardDto.getAnswer()));
          }
          deckManager.addDeck(deck);
        }
      }
    }
    return new UserData(new User(username, password), deckManager);
  }

  public User fromLoginRequestDto(LoginRequestDto dto) {
    String username = dto.getUsername() != null ? dto.getUsername() : "";
    String password = dto.getPassword() != null ? dto.getPassword() : "";
    return new User(username, password);
  }
}
