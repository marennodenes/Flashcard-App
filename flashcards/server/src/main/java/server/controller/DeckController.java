package server.controller;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Controller for managing decks of flashcards.
 * Handles HTTP requests related to deck operations such as creation, retrieval, updating, and deletion.
 * Interacts with DeckService to perform business logic and data manipulation.
 * @see server.service.DeckService
 * @author @ailinat
 * @author @sofietw
 */
public class DeckController {

  @Autowired
  private DeckService deckService;

  public DeckController(final DeckService deckService) {
    this.deckService = deckService;
  }

  @PutMapping ("/create")
  public ResponseEntity <DeckDto> createDeck(@RequestParam String name, @RequestParam String description) {
    try {
      DeckDto createdDeck = deckService.createDeck(name, description);
      return new ResponseEntity<>(createdDeck, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
