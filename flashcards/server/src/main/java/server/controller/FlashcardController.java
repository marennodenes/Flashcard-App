package server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.Flashcard;
import dto.FlashcardDto;
import server.service.FlashcardService;
import shared.ApiEndpoints;

/**
 * Controller for managing flashcards.
 * Uses the FlashcardService to handle business logic.
 * @see server.service.FlashcardService
 * @author parts of class is generated with the help of claude.ai
 * @author ailinat
 * @author sofietw
 * @author marennod
 */

@RestController
@RequestMapping (ApiEndpoints.FLASHCARDS) // Maps to "/api/v1/flashcards"
public class FlashcardController {

  @Autowired
  private FlashcardService flashcardService; // Handles business logic for flashcard operations

  /**
   * Constructor for FlashcardController.
   * 
   * @param flashcardService the flashcard service to use for business logic
   */
  public FlashcardController(final FlashcardService flashcardService) {
    this.flashcardService = flashcardService;
  }

  /**
   * Creates a new flashcard with the provided question and answer.
   * 
   * @param username the username of the user who owns the deck
   * @param deckname the name of the deck to add the flashcard to
   * @param question the question text for the flashcard
   * @param answer the answer text for the flashcard
   * @return ResponseEntity containing the created FlashcardDto and HTTP status CREATED on success,
   *         or HTTP status INTERNAL_SERVER_ERROR on failure
   */
  @PostMapping (ApiEndpoints.FLASHCARD_CREATE)
  public ResponseEntity <FlashcardDto> createFlashcard(
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
      
      // Return the created flashcard as DTO
      FlashcardDto flashcardDto = new FlashcardDto(question, answer, position);
      return new ResponseEntity<>(flashcardDto, HttpStatus.CREATED);
    } catch (Exception e) {
      // Return server error if creation fails
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Deletes a flashcard by its position in the deck.
   * 
   * @param username the username of the user who owns the deck
   * @param deckname the name of the deck containing the flashcard
   * @param number the position/index of the flashcard to delete
   * @return ResponseEntity with HTTP status NO_CONTENT on successful deletion,
   *         or HTTP status INTERNAL_SERVER_ERROR on failure
   */
  @DeleteMapping (ApiEndpoints.FLASHCARD_DELETE)
  public ResponseEntity <Void> deleteFlashcard(
      @RequestParam String username,
      @RequestParam String deckname,
      @RequestParam int number) {
    try {
      // Remove flashcard at specified position
      flashcardService.deleteFlashcard(username, deckname, number);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 - successful deletion
    } catch (Exception e) {
      // Return server error if deletion fails
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Retrieves a specific flashcard by its position in the deck.
   * 
   * @param username the username of the user who owns the deck
   * @param deckname the name of the deck containing the flashcard
   * @param number the position/index of the flashcard to retrieve
   * @return ResponseEntity containing the FlashcardDto and HTTP status OK on success,
   *         or HTTP status INTERNAL_SERVER_ERROR on failure
   */
  @GetMapping (ApiEndpoints.FLASHCARD_GET)
  public ResponseEntity <FlashcardDto> getFlashcard(
      @RequestParam String username,
      @RequestParam String deckname,
      @RequestParam int number) {
    try {
      // Get flashcard from specified position in deck
      Flashcard flashcard = flashcardService.getFlashcard(username, deckname, number);
      
      // Convert to DTO for response
      FlashcardDto flashcardDto = new FlashcardDto(flashcard.getQuestion(), flashcard.getAnswer(), number);
      return new ResponseEntity<>(flashcardDto, HttpStatus.OK);
    } catch (Exception e) {
      // Return server error if flashcard not found or other error
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Retrieves all flashcards from a specific deck.
   * 
   * @param username the username of the user who owns the deck
   * @param deckname the name of the deck to retrieve flashcards from
   * @return ResponseEntity containing a list of all FlashcardDto objects and HTTP status OK on success,
   *         or HTTP status INTERNAL_SERVER_ERROR on failure
   */
  @GetMapping (ApiEndpoints.FLASHCARD_GET_ALL)
  public ResponseEntity <List<FlashcardDto>> getAllFlashcards(
      @RequestParam String username,
      @RequestParam String deckname) {
    try {
      // Get all flashcards from the specified deck
      List<Flashcard> flashcards = flashcardService.getAllFlashcards(username, deckname);
      
      // Convert each flashcard to DTO with correct position number
      List<FlashcardDto> flashcardDtos = new java.util.ArrayList<>();
      for (int i = 0; i < flashcards.size(); i++) {
        Flashcard flashcard = flashcards.get(i);
        // Position is 1-indexed (i + 1)
        flashcardDtos.add(new FlashcardDto(flashcard.getQuestion(), flashcard.getAnswer(), i + 1));
      }
      return new ResponseEntity<>(flashcardDtos, HttpStatus.OK);
    } catch (Exception e) {
      // Return server error if deck not found or other error
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
