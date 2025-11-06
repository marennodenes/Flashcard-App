package server.service;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import app.FlashcardDeck;
import app.FlashcardDeckManager;
import itp.storage.FlashcardPersistent;
import shared.ApiConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

/**
 * Unit tests for the {@link DeckService} class.
 * 
 * This test class verifies the functionality of deck management operations including
 * deck retrieval, creation, deletion, and validation. The tests use Mockito to mock
 * the {@link FlashcardPersistent} dependency, isolating the service logic from the
 * persistence layer.
 * 
 * Key testing scenarios covered:
 * - Constructor validation with null parameters
 * - Deck retrieval for existing and non-existing users
 * - Individual deck retrieval by name
 * - Deck creation with validation
 * - Deck deletion operations
 * - Error handling for invalid operations and non-existent entities
 * 
 * All deck operations require a valid username and depend on user existence
 * validation through the persistence layer.
 * 
 * @author chrsom
 * @author isamw
 * @author parts of class is generated with the help of claude.ai
 * 
 * @see DeckService
 * @see FlashcardPersistent
 * @see FlashcardDeckManager
 * @see FlashcardDeck
 * 
 */
@ExtendWith(MockitoExtension.class)
public class DeckServiceTest {
  
  /**
   * Mocked FlashcardPersistent instance used to simulate persistence layer
   * operations without actual file I/O during testing.
   */
  @Mock
  private FlashcardPersistent flashcardPersistent;
  private DeckService deckService;

  /**
   * Sets up the test environment before each test method execution.
   * 
   * This method creates a new DeckService instance with the mocked
   * FlashcardPersistent dependency. This ensures that each test runs with
   * a clean state and isolated from external dependencies.
   */
  @BeforeEach
  void setUp() {
    deckService = new DeckService(flashcardPersistent);
  }

  /**
   * Tests the DeckService constructor parameter validation.
   * 
   * This test verifies that the DeckService constructor properly validates
   * its required dependencies and throws appropriate exceptions when null
   * parameters are provided. This ensures fail-fast behavior and prevents
   * the creation of DeckService instances in an invalid state.
   */
  @Test
  void testConstructor(){
    // test null FlashcardPersistent
    assertThrows(NullPointerException.class, () -> new DeckService(null));
  }


  /**
   * Tests retrieval of all decks for a user with various scenarios.
   * 
   * This test verifies the {@link DeckService#getAllDecks(String)} method
   * behavior in different situations:
   * - Exception thrown when user does not exist in the system
   * - Successful retrieval when user exists and has deck data
   * 
   * The method should validate user existence before attempting to retrieve
   * deck data and throw an {@link IllegalArgumentException} for non-existent users.
   * 
   * @throws IOException if persistence operations fail during test execution
   * 
   */
  @Test
  void testGetAllDecks() throws IOException {
    // test getAllDecks with non-existing user
    String username = "xistingUser";
    org.mockito.Mockito.when(flashcardPersistent.userExists(username)).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> deckService.getAllDecks(username));

    org.mockito.Mockito.when(flashcardPersistent.userExists(username)).thenReturn(true);
    FlashcardDeckManager manager = new FlashcardDeckManager();
    manager.addDeck(new FlashcardDeck("Test"));

    org.mockito.Mockito.when(flashcardPersistent.readDeck(username)).thenReturn(manager);
    
    FlashcardDeckManager result = deckService.getAllDecks(username);

    assertNotNull(result);
    assertEquals(1, result.getDecks().size());
    
    String username2 = "nonExistingUser";
    org.mockito.Mockito.when(flashcardPersistent.userExists(username2)).thenReturn(false);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> deckService.getAllDecks(username2));

    assertEquals(ApiConstants.USER_NOT_FOUND, exception.getMessage());
  }

  /**
   * Tests retrieval of a specific deck by name for a user.
   * 
   * This test verifies the {@link DeckService#getDeck(String, String)} method
   * functionality including:
   * - User existence validation
   * - Deck retrieval from the user's deck collection
   * - Proper delegation to the deck manager
   * 
   * @throws IOException if persistence operations fail during test execution
   * 
   */
  @Test
  void testGetDeck(){
    // test getDeck with existing deck
    String username = "existingUser";
    String deckname = "TestDeck";

    FlashcardDeckManager manager = new FlashcardDeckManager();
    FlashcardDeck deck = new FlashcardDeck(deckname);
    manager.addDeck(deck);

    try {
      org.mockito.Mockito.when(flashcardPersistent.userExists(username)).thenReturn(true);
      org.mockito.Mockito.when(flashcardPersistent.readDeck(username)).thenReturn(manager);

      FlashcardDeck result = deckService.getDeck(username, deckname);

      assertNotNull(result);
      assertEquals(deckname, result.getDeckName());

      // test getDeck with non-existing deck
      String nonExistingDeckName = "NonExistingDeck";

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> deckService.getDeck(username, nonExistingDeckName));

      assertEquals(ApiConstants.DECK_NOT_FOUND, exception.getMessage());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** 
   * Tests deck creation functionality.
   * 
   * This test verifies the {@link DeckService#createDeck(String, String)} method
   * workflow including:
   * - User existence validation
   * - New deck creation with the specified name
   * - Addition of the deck to the user's collection
   * - Persistence of the updated deck manager
   * 
   * @throws IOException if persistence operations fail during test execution
   * 
   */
  @Test
  void testCreateDeck(){
    String username = "existingUser";
    String deckName = "NewDeck";

    FlashcardDeckManager manager = new FlashcardDeckManager();

    try {
      org.mockito.Mockito.when(flashcardPersistent.userExists(username)).thenReturn(true);
      org.mockito.Mockito.when(flashcardPersistent.readDeck(username)).thenReturn(manager);

      FlashcardDeck result = deckService.createDeck(username, deckName);

      assertNotNull(result);
      assertEquals(deckName, result.getDeckName());
      assertEquals(0, result.getDeck().size());

      org.mockito.Mockito.verify(flashcardPersistent).writeDeck(username, manager);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** 
  * Tests deck deletion functionality.
  * 
  * This test verifies the {@link DeckService#deleteDeck(String, String)} method
  * workflow including:
  * - User existence validation
  * - Deck removal from the user's collection
  * - Persistence of the updated deck manager state
  * 
  * @throws IOException if persistence operations fail during test execution
  *
  */
  @Test
  void testDeleteDeck(){
    String username = "existingUser";
    String deckname = "TestDeck";

    FlashcardDeckManager manager = new FlashcardDeckManager();
    FlashcardDeck deck = new FlashcardDeck(deckname);
    manager.addDeck(deck);

    String username2 = "existingUser";
    String deckname2 = "NonExistingDeck";

    FlashcardDeckManager manager2 = new FlashcardDeckManager();

    try {
      org.mockito.Mockito.when(flashcardPersistent.userExists(username)).thenReturn(true);
      org.mockito.Mockito.when(flashcardPersistent.readDeck(username)).thenReturn(manager);

      assertEquals(1, manager.getDecks().size());

      deckService.deleteDeck(username, deckname);

      assertEquals(0, manager.getDecks().size());

      org.mockito.Mockito.verify(flashcardPersistent).writeDeck(username, manager);

      org.mockito.Mockito.when(flashcardPersistent.userExists(username2)).thenReturn(true);
      org.mockito.Mockito.when(flashcardPersistent.readDeck(username2)).thenReturn(manager2);

      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> deckService.deleteDeck(username2, deckname2));

      assertEquals(ApiConstants.DECK_NOT_FOUND, exception.getMessage());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  /**
   * Tests error handling when attempting operations on non-existent users.
   * 
   * This test verifies that all deck operations properly validate user
   * existence and throw appropriate exceptions when attempting to perform
   * operations for users that don't exist in the system.
   * 
   * @throws IOException if persistence operations fail during test execution
   * 
   */
  @Test
  void testUpdateAllDecks() throws IOException {
    String username = "existingUser";
    FlashcardDeckManager manager = new FlashcardDeckManager();
    manager.addDeck(new FlashcardDeck("Deck1"));

    // userExists = false
    org.mockito.Mockito.when(flashcardPersistent.userExists(username)).thenReturn(false);
    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
        () -> deckService.updateAllDecks(username, manager));
    assertEquals(ApiConstants.USER_NOT_FOUND, ex.getMessage());

    // userExists = true
    org.mockito.Mockito.when(flashcardPersistent.userExists(username)).thenReturn(true);

    deckService.updateAllDecks(username, manager);
    org.mockito.Mockito.verify(flashcardPersistent).writeDeck(eq(username), any(FlashcardDeckManager.class));
  }
}
