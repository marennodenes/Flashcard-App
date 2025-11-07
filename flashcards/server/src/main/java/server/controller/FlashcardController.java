package server.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.Flashcard;
import dto.FlashcardDto;
import dto.mappers.FlashcardMapper;
import server.service.FlashcardService;
import shared.ApiConstants;
import shared.ApiEndpoints;
import shared.ApiResponse;

/**
 * Controller for managing flashcards.
 * Uses the FlashcardService to handle business logic.
 * 
 * @author parts of class is generated with the help of claude.ai
 * @author ailinat
 * @author sofietw
 * @author marennod
 * 
 * @see server.service.FlashcardService
 * 
 */

@RestController
@RequestMapping(ApiEndpoints.FLASHCARDS) // Maps to "/api/v1/flashcards"
public class FlashcardController {

  @Autowired
  private final FlashcardService flashcardService; // Handles business logic for flashcard operations
  private final FlashcardMapper mapper;

  /**
   * Constructor for FlashcardController.
   * 
   * @param flashcardService the flashcard service to use for business logic
   * 
   */
  public FlashcardController(final FlashcardService flashcardService) {
    this.flashcardService = Objects.requireNonNull(flashcardService, "FlashcardService cannot be null");
    this.mapper = new FlashcardMapper();
  }

  /**
   * Retrieves a specific flashcard by its position in the deck.
   * 
   * @param username the username of the user who owns the deck
   * @param deckname the name of the deck containing the flashcard
   * @param number the position/index of the flashcard to retrieve
   * @return ApiResponse containing the FlashcardDto on success,
   *         or error message on failure
   * 
   */
  @GetMapping(ApiEndpoints.FLASHCARD_GET)
  public ApiResponse<FlashcardDto> getFlashcard(
      @RequestParam String username,
      @RequestParam String deckname,
      @RequestParam int number) {
    try {
      // Get flashcard from specified position in deck
      Flashcard flashcard = flashcardService.getFlashcard(username, deckname, number);
      
      // Convert to DTO for response
      FlashcardDto flashcardDto = mapper.toDto(flashcard);
      return new ApiResponse<>(true, ApiConstants.FLASHCARD_RETRIEVED, flashcardDto);
    } catch (Exception e) {
      // Log technical details for developers
      System.err.println(ApiConstants.FLASHCARD_RETRIEVED_FAILED + " for username: '" + username + "', deck: '" + deckname + "', number: " + number + " - " + e.getMessage());
      // Return user-friendly message
      return new ApiResponse<>(false, ApiConstants.FLASHCARD_OPERATION_FAILED, null);
    }
  }

  /**
   * Retrieves all flashcards from a specific deck.
   * 
   * @param username the username of the user who owns the deck
   * @param deckname the name of the deck to retrieve flashcards from
   * @return ApiResponse containing a list of all FlashcardDto objects on success,
   *         or error message on failure
   * 
   */
  @GetMapping(ApiEndpoints.FLASHCARD_GET_ALL)
  public ApiResponse<List<FlashcardDto>> getAllFlashcards(
      @RequestParam String username,
      @RequestParam String deckname) {
    try {
      // Get all flashcards from the specified deck
      List<Flashcard> flashcards = flashcardService.getAllFlashcards(username, deckname);
      List<FlashcardDto> flashcardDtos = mapper.toDtoList(flashcards);
      return new ApiResponse<>(true, ApiConstants.FLASHCARDS_RETRIEVED, flashcardDtos);
    } catch (Exception e) {
      // Log technical details for developers
      System.err.println(ApiConstants.FLASHCARDS_RETRIEVED_FAILED + " for username: '" + username + "', deck: '" + deckname + "' - " + e.getMessage());
      // Return user-friendly message
      return new ApiResponse<>(false, ApiConstants.FLASHCARD_OPERATION_FAILED, null);
    }
  }

  /**
   * Creates a new flashcard with the provided question and answer.
   * 
   * @param username the username of the user who owns the deck
   * @param deckname the name of the deck to add the flashcard to
   * @param question the question text for the flashcard
   * @param answer the answer text for the flashcard
   * @return ApiResponse containing the created FlashcardDto on success,
   *         or error message on failure
   * 
   */
  @PostMapping(ApiEndpoints.FLASHCARD_CREATE)
  public ApiResponse<FlashcardDto> createFlashcard(
      @RequestParam String username,
      @RequestParam String deckname,
      @RequestParam String question, 
      @RequestParam String answer) {
    try {
      // Create flashcard in the specified deck
      flashcardService.createFlashcard(username, deckname, answer, question);
      
      // Get updated deck to find position of newly created flashcard
      List<Flashcard> allFlashcards = flashcardService.getAllFlashcards(username, deckname);
      int position = allFlashcards.size(); // New card is at the end (1-indexed)
      
      // Return the created flashcard as Dto
      FlashcardDto flashcardDto = mapper.toDto(flashcardService.getFlashcard(username, deckname, position));
      return new ApiResponse<>(true, ApiConstants.FLASHCARD_CREATED, flashcardDto);
    } catch (Exception e) {
      // Log technical details for developers
      System.err.println(ApiConstants.FLASHCARD_FAILED + " for username: '" + username + "', deck: '" + deckname + "' - " + e.getMessage());
      // Return user-friendly message
      return new ApiResponse<>(false, ApiConstants.FLASHCARD_OPERATION_FAILED, null);
    }
  }

  /**
   * Deletes a flashcard by its position in the deck.
   * 
   * @param username the username of the user who owns the deck
   * @param deckname the name of the deck containing the flashcard
   * @param number the position/index of the flashcard to delete
   * @return ApiResponse with success message on successful deletion,
   *         or error message on failure
   * 
   */
  @DeleteMapping(ApiEndpoints.FLASHCARD_DELETE)
  public ApiResponse<Void> deleteFlashcard(
      @RequestParam String username,
      @RequestParam String deckname,
      @RequestParam int number) {
    try {
      // Remove flashcard at specified position
      flashcardService.deleteFlashcard(username, deckname, number);
      return new ApiResponse<>(true, ApiConstants.FLASHCARD_DELETED, null);
    } catch (Exception e) {
      // Log technical details for developers
      System.err.println(ApiConstants.FLASHCARD_FAILED_TO_DELETE + " for username: '" + username + "', deck: '" + deckname + "', number: " + number + " - " + e.getMessage());
      // Return user-friendly message
      return new ApiResponse<>(false, ApiConstants.FLASHCARD_OPERATION_FAILED, null);
    }
  }

}
