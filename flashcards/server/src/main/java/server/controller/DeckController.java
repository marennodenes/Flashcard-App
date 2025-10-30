package server.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.FlashcardDeck;
import app.FlashcardDeckManager;
import dto.FlashcardDeckDto;
import dto.FlashcardDeckManagerDto;
import dto.mappers.FlashcardDeckMapper;
import server.service.DeckService;
import shared.ApiEndpoints;
import shared.ApiResponse;



/**
 * Controller for managing decks of flashcards.
 * Handles HTTP requests related to flashcard deck operations.
 * Interacts with DeckService to perform business logic and data manipulation.
 * @see server.service.DeckService
 * @author ailinat
 * @author sofietw
 */
@RestController
@RequestMapping(ApiEndpoints.DECKS)
public class DeckController {

  @Autowired
  private final DeckService deckService; // Handles business logic for deck operations
  private final FlashcardDeckMapper mapper;

  public DeckController(final DeckService deckService) {
    this.deckService = Objects.requireNonNull(deckService, "DeckService cannot be null");
    this.mapper = new FlashcardDeckMapper();
  }

  /**
   * Gets all decks for a user.
   * @param username
   * @return an ApiResponse containing FlashcardDeckManagerDto if success or an error message
   */
  @RequestMapping
  public ApiResponse<FlashcardDeckManagerDto> getAllDecks(@RequestParam String username) {
    try {
      FlashcardDeckManager deckManager = deckService.getAllDecks(username);
      FlashcardDeckManagerDto dto = new FlashcardDeckManagerDto(mapper.toDtoList(deckManager.getDecks()));
      return new ApiResponse<>(true, "Decks retrieved successfully", dto);
    } catch (Exception e) {
      return new ApiResponse<>(false, "Error retrieving decks: " + e.getMessage(), null);
    }
  }

  /**
   * Gets a specific deck by name for a user.
   * @param username
   * @param deckName
   * @return an ApiResponse containing FlashcardDeckDto if success or an error message
   */
  @RequestMapping ("/{deckName}")
  public ApiResponse<FlashcardDeckDto> getDeckByName(@RequestParam String username, @RequestParam String deckName) {
    try {
      FlashcardDeck deck = deckService.getDeck(username, deckName);
      FlashcardDeckDto dto = mapper.toDto(deck);
      return new ApiResponse<>(true, "Deck retrieved successfully", dto);
    } catch (Exception e) {
      return new ApiResponse<>(false, "Error retrieving deck: " + e.getMessage(), null);
    }
  }

  /**
   * Creates a new deck for a user.
   * @param username
   * @param deckName
   * @return an ApiResponse containing FlashcardDeckDto if success or an error message
   */
  @PostMapping ("/{deckName}")
  public ApiResponse<FlashcardDeckDto> createDeck(@RequestParam String username, @RequestParam String deckName) {
    try {
      FlashcardDeck deck = deckService.createDeck(username, deckName);
      FlashcardDeckDto dto = mapper.toDto(deck);
      return new ApiResponse<>(true, "Deck created successfully", dto);
    } catch (Exception e) {
      return new ApiResponse<>(false, "Error creating deck: " + e.getMessage(), null);
    }
  }

  /**
   * Deletes a deck for a user.
   * @param username
   * @param deckName
   * @return An ApiResponse indicating success or failure
   */
  @DeleteMapping ("/{deckName}")
  public ApiResponse<Void> deleteDeck(@RequestParam String username, @RequestParam String deckName) {
    try {
      deckService.deleteDeck(username, deckName);
      return new ApiResponse<>(true, "Deck deleted successfully", null);
    } catch (Exception e) {
      return new ApiResponse<>(false, "Error deleting deck: " + e.getMessage(), null);
    }
  }

  @PutMapping
    public ApiResponse<Void> updateAllDecks(
        @RequestParam String username,
        @RequestBody FlashcardDeckManager deckManager) {
      try {
        deckService.updateAllDecks(username, deckManager);
        return new ApiResponse<>(true, "Decks updated successfully", null);
      } catch (Exception e) {
        return new ApiResponse<>(false, "Error updating decks: " + e.getMessage(), null);
      }
    }
}
