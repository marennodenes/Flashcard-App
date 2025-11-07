package server.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import shared.ApiConstants;
import shared.ApiEndpoints;
import shared.ApiResponse;

/**
 * Controller for managing decks of flashcards.
 * Handles HTTP requests related to flashcard deck operations.
 * Interacts with DeckService to perform business logic and data manipulation.
 * 
 * @author ailinat
 * @author sofietw
 * 
 * @see server.service.DeckService
 */
@RestController
@RequestMapping(ApiEndpoints.DECKS)
public class DeckController {

  @Autowired
  private final DeckService deckService; // Handles business logic for deck operations
  private final FlashcardDeckMapper mapper;

  /**
   * Constructor for DeckController.
   * 
   * @param deckService the DeckService to use for deck operations
   * 
   */
  public DeckController(final DeckService deckService) {
    this.deckService = Objects.requireNonNull(deckService, "DeckService cannot be null");
    this.mapper = new FlashcardDeckMapper();
  }

  /**
   * Gets all decks for a user.
   * 
   * @param username the username of the user
   * @return an ApiResponse containing FlashcardDeckManagerDto if success or an error message
   * 
   */
  @RequestMapping
  public ApiResponse<FlashcardDeckManagerDto> getAllDecks(@RequestParam String username) {
    try {
      FlashcardDeckManager deckManager = deckService.getAllDecks(username);
      FlashcardDeckManagerDto dto = new FlashcardDeckManagerDto(mapper.toDtoList(deckManager.getDecks()));
      return new ApiResponse<>(true, ApiConstants.DECKS_RETRIEVED, dto);
    } catch (Exception e) {
      // Log technical details for developers
      System.err.println(ApiConstants.DECKS_RETRIEVING_ERROR + " for username: '" + username + "': " + e.getMessage());
      // Return user-friendly message
      return new ApiResponse<>(false, ApiConstants.FAILED_TO_LOAD_DATA, null);
    }
  }

  /**
   * Gets a specific deck by name for a user.
   * 
   * @param username the username of the user
   * @param deckName the name of the deck to retrieve
   * @return an ApiResponse containing FlashcardDeckDto if success or an error message
   * 
   */
  @RequestMapping("/{deckName}")
  public ApiResponse<FlashcardDeckDto> getDeckByName(@RequestParam String username, @PathVariable String deckName) {
    try {
      FlashcardDeck deck = deckService.getDeck(username, deckName);
      FlashcardDeckDto dto = mapper.toDto(deck);
      return new ApiResponse<>(true, ApiConstants.DECK_RETRIEVED, dto);
    } catch (Exception e) {
      // Log technical details for developers
      System.err.println(ApiConstants.DECK_RETRIEVING_ERROR + ": '" + deckName + "' for username: '" + username + "': " + e.getMessage());
      // Return user-friendly message
      return new ApiResponse<>(false, ApiConstants.FAILED_TO_LOAD_DATA, null);
    }
  }

  /**
   * Creates a new deck for a user.
   * 
   * @param username the username of the user
   * @param deckName the name of the deck to create
   * @return an ApiResponse containing FlashcardDeckDto if success or an error message
   * 
   */
  @PostMapping("/{deckName}")
  public ApiResponse<FlashcardDeckDto> createDeck(@RequestParam String username, @PathVariable String deckName) {
    try {
      FlashcardDeck deck = deckService.createDeck(username, deckName);
      FlashcardDeckDto dto = mapper.toDto(deck);
      return new ApiResponse<>(true, ApiConstants.DECK_CREATED, dto);
    } catch (IllegalArgumentException e) {
      // Log technical details for developers
      System.err.println(ApiConstants.DECK_CREATED_ERROR + ": '" + deckName + "' for username: '" + username + "': " + e.getMessage());
      // Map technical exception messages to user-friendly constants so users know what's wrong
      String userMessage;
      String techMsg = e.getMessage();
      if (techMsg != null && techMsg.contains("User not found")) {
        userMessage = ApiConstants.USER_NOT_FOUND;
      } else if (techMsg != null && techMsg.contains("unique")) {
        userMessage = ApiConstants.DECK_ALREADY_EXISTS;
      } else if (techMsg != null && techMsg.contains("cannot be empty")) {
        userMessage = ApiConstants.DECK_NAME_EMPTY;
      } else if (techMsg != null && techMsg.contains("You can only have up to")) {
        userMessage = ApiConstants.DECK_LIMIT_REACHED;
      } else {
        userMessage = ApiConstants.DECK_OPERATION_FAILED;  // Generic fallback
      }
      return new ApiResponse<>(false, userMessage, null);
    } catch (Exception e) {
      // Log technical details for developers
      System.err.println(ApiConstants.DECK_CREATED_ERROR + ": '" + deckName + "' for username: '" + username + "': " + e.getMessage());
      // Return generic user-friendly message for other errors (IOException, etc.)
      return new ApiResponse<>(false, ApiConstants.DECK_OPERATION_FAILED, null);
    }
  }

  /**
   * Updates all decks for a user.
   * 
   * @param username the username of the user
   * @param deckManager the FlashcardDeckManager containing updated decks
   * @return an ApiResponse indicating success or failure
   * 
   */
  @PutMapping
  public ApiResponse<Void> updateAllDecks(
      @RequestParam String username,
      @RequestBody FlashcardDeckManager deckManager) {
    try {
      deckService.updateAllDecks(username, deckManager);
      return new ApiResponse<>(true, ApiConstants.DECK_UPDATED, null);
    } catch (Exception e) {
      // Log technical details for developers
      System.err.println(ApiConstants.DECK_UPDATED_ERROR + " for username: '" + username + "': " + e.getMessage());
      // Return user-friendly message
      return new ApiResponse<>(false, ApiConstants.DECK_UPDATE_FAILED, null);
    }
  }

  /**
   * Deletes a deck for a user.
   * 
   * @param username the username of the user
   * @param deckName the name of the deck to delete
   * @return An ApiResponse indicating success or failure
   * 
   */
  @DeleteMapping("/{deckName}")
  public ApiResponse<Void> deleteDeck(@RequestParam String username, @PathVariable String deckName) {
    try {
      deckService.deleteDeck(username, deckName);
      return new ApiResponse<>(true, ApiConstants.DECK_DELETED, null);
    } catch (Exception e) {
      // Log technical details for developers
      System.err.println(ApiConstants.DECK_DELETED_ERROR + ": '" + deckName + "' for username: '" + username + "': " + e.getMessage());
      // Return user-friendly message  
      return new ApiResponse<>(false, ApiConstants.DECK_OPERATION_FAILED, null);
    }
  }
}
