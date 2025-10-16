package server.service;

import java.util.List;

import org.springframework.stereotype.Service;

import app.Flashcard;
/**
 * Service class for managing flashcard operations.
 * 
 * This service provides CRUD operations for flashcards within decks,
 * including retrieval, creation, and deletion of flashcards.
 * 
 * @author chrsom
 * @author isamw
 */
@Service
public class FlashcardService {
  
  public Flashcard getFlashcard(String deckname, int number) {
    return null;
  }

  public List<Flashcard> getAllFlashcards(String deckname) {
    return null;
  }

  public Flashcard createFlashcard(String username, String deckname, String answer, String question) {
    return null;
  }

  public void deleteFlashcard(String deckname, int number) {
  }
}
