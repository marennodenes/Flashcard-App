package server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.RequestParam;
import org.springframework.beans.factory.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import server.service.DeckService;
import shared.ApiEndpoints;
import shared.ApiResponse;
import shared.dto.DeckDto;



/**
 * Controller for managing decks of flashcards.
 * Handles HTTP requests related to deck operations such as creation, retrieval, updating, and deletion.
 * Interacts with DeckService to perform business logic and data manipulation.
 * @see server.service.DeckService
 * @author @ailinat
 * @author @sofietw
 */
 @RestController
public class DeckController {

  @Autowired
  private DeckService deckService;

  public DeckController(final DeckService deckService) {
    this.deckService = deckService;
  }

  @PutMapping ("/create") //TODO: update to static paths from
  public ApiResponse <DeckDto> createDeck(@RequestParam String name, @RequestParam String description) {
    try {
      DeckDto createdDeck = deckService.createDeck(name, description);
      return new ApiResponse<>(createdDeck);
    } catch (Exception e) {
      return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
