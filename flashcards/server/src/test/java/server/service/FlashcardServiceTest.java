package server.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import app.Flashcard;
import app.FlashcardDeck;
import app.FlashcardDeckManager;
import itp.storage.FlashcardPersistent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import shared.ApiConstants;

/**
 * Unit tests for the FlashcardService class.
 * 
 * This test class verifies the functionality of flashcard-related operations including
 * retrieval, creation, and deletion of flashcards within decks. The tests use Mockito
 * to mock the FlashcardPersistent and DeckService dependencies, isolating the service logic
 * from the persistence and deck management layers.
 * 
 * Key testing scenarios covered:
 *  Successful flashcard retrieval by index
 *  Retrieval of all flashcards from a deck
 *  Creation of new flashcards within a deck
 *  Deletion of flashcards by index
 *  Error handling for non-existent decks and invalid flashcard indices
 * 
 * @author chrsom
 * @author isamw
 * @author parts of class is generated with the help of claude.ai
 * @see FlashcardService
 * @see FlashcardPersistent
 * @see DeckService
 *
 */
class FlashcardServiceTest {

    @Mock
    private FlashcardPersistent persistent;

    @Mock
    private DeckService deckService;

    private FlashcardService flashcardService;


    /**
     * Sets up the test environment before each test method execution.
     * 
     * This method initializes the mock objects using Mockito and creates a new
     * FlashcardService instance with the mocked dependencies. This ensures that
     * each test runs with a clean state and isolated from external dependencies.
     * 
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        flashcardService = new FlashcardService(persistent, deckService);
    }

    /**
     * Tests successful flashcard retrieval by index from an existing deck.
     * 
     * This test verifies that the service can correctly retrieve a specific flashcard
     * from a deck when provided with valid parameters. It tests the complete workflow
     * including deck existence verification, flashcard index validation, and proper
     * flashcard data retrieval.
     * 
     * The test also includes a negative case to verify proper exception handling
     * when attempting to retrieve flashcards from an empty deck collection.

     * 
     * @throws IOException if persistence operations fail during test execution
     */
    @Test
    void testGetFlashcard() throws IOException {
        Flashcard card1 = new Flashcard("Q1", "A1");
        FlashcardDeck deck = mock(FlashcardDeck.class);
        FlashcardDeckManager manager = mock(FlashcardDeckManager.class);

        when(deck.getDeckName()).thenReturn("deck1");
        when(deck.getDeck()).thenReturn(Arrays.asList(card1));
        when(manager.getDecks()).thenReturn(Arrays.asList(deck));
        when(persistent.readDeck("user")).thenReturn(manager);
        when(deckService.getDeck("user", "deck1")).thenReturn(deck);
        when(deckService.getAllDecks("user")).thenReturn(manager);

        Flashcard result = flashcardService.getFlashcard("user", "deck1", 1);
        assertEquals("Q1", result.getQuestion());
        assertEquals("A1", result.getAnswer());

        FlashcardDeckManager emptyManager = mock(FlashcardDeckManager.class);
        when(emptyManager.getDecks()).thenReturn(Collections.emptyList());
        when(persistent.readDeck("user")).thenReturn(emptyManager);

        var ex = assertThrows(IllegalArgumentException.class, () ->
                flashcardService.getFlashcard("user", "deck1", 1));

        assertEquals(ApiConstants.FLASHCARD_NOT_FOUND, ex.getMessage());

        FlashcardDeckManager manager2 = mock(FlashcardDeckManager.class);
        FlashcardDeck deck2 = mock(FlashcardDeck.class);
        when(deck2.getDeckName()).thenReturn("anotherDeck");
        when(manager2.getDecks()).thenReturn(Arrays.asList(deck2));
        when(persistent.readDeck("user")).thenReturn(manager2);

        var ex2 = assertThrows(IllegalArgumentException.class, () ->
                flashcardService.getFlashcard("user", "missingDeck", 1));

        assertEquals(ApiConstants.DECK_NOT_FOUND, ex2.getMessage());
    }

    /**
     * Tests flashcard retrieval with invalid parameters that should trigger exceptions.
     * 
     * This test verifies that the service correctly handles error scenarios when attempting
     * to retrieve flashcards. It checks for proper exception throwing and messages when:
     * - The specified deck does not exist for the user
     * - The requested flashcard index is invalid (less than 1)
     * - The requested flashcard index exceeds the number of flashcards in the deck
     * 
     * @throws IOException if persistence operations fail during test execution
     */
    @Test
    void testGetFlashcard_Throws() throws IOException {
        FlashcardDeckManager manager = mock(FlashcardDeckManager.class);
        FlashcardDeck deck = mock(FlashcardDeck.class);
        when(deck.getDeckName()).thenReturn("anotherDeck");
        when(manager.getDecks()).thenReturn(Arrays.asList(deck));
        when(persistent.readDeck("user")).thenReturn(manager);

        var ex = assertThrows(IllegalArgumentException.class, () ->
                flashcardService.getFlashcard("user", "missingDeck", 1));

        assertEquals(ApiConstants.DECK_NOT_FOUND, ex.getMessage());

        // invalid index
        FlashcardDeckManager manager2 = mock(FlashcardDeckManager.class);
        FlashcardDeck deck2 = mock(FlashcardDeck.class);
        when(deck2.getDeckName()).thenReturn("deck1");
        when(manager2.getDecks()).thenReturn(Arrays.asList(deck2));
        when(persistent.readDeck("user")).thenReturn(manager2);
    
        var ex2 = assertThrows(IllegalArgumentException.class, () ->
                flashcardService.getFlashcard("user", "deck1", 0));
    
        assertEquals(ApiConstants.FLASHCARD_NOT_FOUND, ex2.getMessage());

        // deck smaller than index
        FlashcardDeck deck3 = mock(FlashcardDeck.class);
        FlashcardDeckManager manager3 = mock(FlashcardDeckManager.class);
        when(deck3.getDeckName()).thenReturn("deck1");
        when(deck3.getDeck()).thenReturn(Collections.singletonList(new Flashcard("Q", "A")));
        when(manager3.getDecks()).thenReturn(Arrays.asList(deck3));
    
        when(persistent.readDeck("user")).thenReturn(manager3);
        when(deckService.getDeck("user", "deck1")).thenReturn(deck3);
    
        var ex3 = assertThrows(IllegalArgumentException.class, () ->
                flashcardService.getFlashcard("user", "deck1", 5));
    
        assertEquals(ApiConstants.FLASHCARD_NOT_FOUND, ex3.getMessage());
    }



    /**
     * Tests retrieval of all flashcards from a specific deck.
     * 
     * This test verifies that the service can successfully retrieve all flashcards
     * from a given deck for a user. It checks that the returned list of flashcards
     * matches the expected size and content.
     * 
     * @throws IOException if persistence operations fail during test execution
     */
    @Test
    void testGetAllFlashcards() throws IOException {
        List<Flashcard> cards = Arrays.asList(
                new Flashcard("Q1", "A1"),
                new Flashcard("Q2", "A2")
        );
        FlashcardDeck deck = mock(FlashcardDeck.class);
        when(deck.getDeck()).thenReturn(cards);
        when(deckService.getDeck("user", "deck1")).thenReturn(deck);

        List<Flashcard> result = flashcardService.getAllFlashcards("user", "deck1");
        assertEquals(2, result.size());
        assertEquals("Q2", result.get(1).getQuestion());
    }


    /**
     * Tests the creation of a new flashcard within a specified deck.
     * 
     * This test verifies that the service can successfully create a new flashcard
     * with the given question and answer, add it to the specified deck, and persist
     * the changes. It checks that the created flashcard has the correct properties
     * and that the appropriate methods on the deck and persistence layers are invoked.
     * 
     * @throws IOException if persistence operations fail during test execution
     */
    @Test
    void testCreateFlashcard() throws IOException {
        FlashcardDeck deck = mock(FlashcardDeck.class);
        FlashcardDeckManager manager = mock(FlashcardDeckManager.class);

        when(deckService.getDeck("user", "deck1")).thenReturn(deck);
        when(deckService.getAllDecks("user")).thenReturn(manager);

        Flashcard newCard = flashcardService.createFlashcard("user", "deck1", "A", "Q");

        assertEquals("Q", newCard.getQuestion());
        verify(deck).addFlashcard(any(Flashcard.class));
        verify(persistent).writeDeck("user", manager);
    }

 
    /**
     * Tests the deletion of a flashcard by index from a specified deck.
     * 
     * This test verifies that the service can successfully delete a flashcard
     * from a deck based on the provided index. It checks that the appropriate
     * methods on the deck and persistence layers are invoked to remove the
     * flashcard and persist the changes.
     * 
     * @throws IOException if persistence operations fail during test execution
     */
    @Test
    void testDeleteFlashcard() throws IOException {
        FlashcardDeck deck = mock(FlashcardDeck.class);
        FlashcardDeckManager manager = mock(FlashcardDeckManager.class);
        when(deckService.getDeck("user", "deck1")).thenReturn(deck);
        when(deckService.getAllDecks("user")).thenReturn(manager);

        flashcardService.deleteFlashcard("user", "deck1", 0);

        verify(deck).removeFlashcardByIndex(0);
        verify(persistent).writeDeck("user", manager);
    }
}
