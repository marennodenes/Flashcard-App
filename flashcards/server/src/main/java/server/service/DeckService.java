package server.service;

import java.util.List;

import org.springframework.stereotype.Service;

import app.FlashcardDeck;
import itp.storage.FlashcardPersistent;
/**
 * Service class for managing flashcard decks.
 * 
 * This service provides operations for retrieving, creating, and managing
 * flashcard decks through the FlashcardPersistent storage layer.
 * 
 * @author chrsom
 * @author isamw
 */
@Service
public class DeckService {
  private final FlashcardPersistent flashcardPersistent;

  public DeckService() {
      this.flashcardPersistent = new FlashcardPersistent();
  }

  public DeckService(FlashcardPersistent flashcardPersistent) {
      this.flashcardPersistent = flashcardPersistent;
  }

  public List<FlashcardDeck> getAllDecks() {
      return null;
  }

  public FlashcardDeck getDeck(String deckname) {
      return null;
  }

  public boolean createDeck(FlashcardDeck deck) {
      return false;
  }
}
