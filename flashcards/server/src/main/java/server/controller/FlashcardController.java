package server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dto.FlashcardDto;
import server.service.FlashcardService;

/**
 * Controller for managing flashcards.
 * Uses the FlashcardService to handle business logic.
 * @see server.service.FlashcardService
 * @author parts of class is generated with the help of claude.ai
 * @author @ailinat
 * @author @sofietw
 */
public class FlashcardController {

  @Autowired
  private FlashcardService flashcardService;

  public FlashcardController(final FlashcardService flashcardService) {
    this.flashcardService = flashcardService;
  }

  @PostMapping ("/create")
  public ResponseEntity <FlashcardDto> createFlashcard(@RequestParam String question, @RequestParam String answer) {
    try {
      FlashcardDto createdFlashcard = flashcardService.createFlashcard(question, answer);
      return new ResponseEntity<>(createdFlashcard, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping ("/delete")
  public ResponseEntity <Void> deleteFlashcard(@RequestParam String id) {
    try {}
  }

  @GetMapping ("/get")
  public ResponseEntity <FlashcardDto> getFlashcard(@RequestParam String cardNumber) {
    try {
      FlashcardDto flashcardDto = flashcardService.getFlashcard(cardNumber);
      return new ResponseEntity<>(flashcardDto, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping ("/get-all")
  public ResponseEntity <List<FlashcardDto>> getAllFlashcards() {
    try {
      List<FlashcardDto> flashcards = flashcardService.getAllFlashcards();
      return new ResponseEntity<>(flashcards, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
