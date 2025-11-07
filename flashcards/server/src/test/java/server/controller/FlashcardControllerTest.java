package server.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import app.Flashcard;
import server.service.FlashcardService;
import shared.ApiEndpoints;
import shared.ApiConstants;


/**
 * Test class for FlashcardController REST endpoints.
 * Tests flashcard-related HTTP operations including creation, retrieval,
 * and deletion of flashcards using MockMvc and mocked services.
 *
 * This test suite validates:
 * - Creation of new flashcards in a deck
 * - Retrieval of individual flashcards by position
 * - Retrieval of all flashcards in a deck
 * - Deletion of flashcards by position
 * - Error handling for various edge cases (user not found, deck not found, flashcard not found, etc.)
 *
 * Uses @WebMvcTest to test only the web layer and @MockBean to mock
 * the FlashcardService dependency for isolated controller testing.
 *
 * @author chrsom
 * @author isamw
 * @author parts of class is generated with the help of claude.ai@
 * 
 * @see FlashcardController
 * @see FlashcardService
 * 
 */
@WebMvcTest(FlashcardController.class)
public class FlashcardControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private FlashcardService flashcardService;

  private Flashcard testFlashcard;
  private List<Flashcard> testFlashcardList;

  /**
   * Sets up test fixtures before each test.
   * Initializes test flashcards and flashcard lists for use in test cases.
   */
  @BeforeEach
  public void setUp() {
    testFlashcard = new Flashcard("What is Java?", "A programming language");
    testFlashcardList = new ArrayList<>();
    testFlashcardList.add(testFlashcard);
    testFlashcardList.add(new Flashcard("What is Spring?", "A framework"));
  }

  /**
   * Tests successful retrieval of a specific flashcard by position.
   * Verifies that the endpoint returns HTTP 200 and the requested flashcard data.
   *
   * @throws Exception if the MockMvc request fails
   * 
   */
  @Test
  public void testGetFlashcard_Success() throws Exception {
    when(flashcardService.getFlashcard("testUser", "TestDeck", 1))
      .thenReturn(testFlashcard);

    mockMvc.perform(get(ApiEndpoints.FLASHCARDS + ApiEndpoints.FLASHCARD_GET)
      .param("username", "testUser")
      .param("deckname", "TestDeck")
      .param("number", "1"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(true))
      .andExpect(jsonPath("$.message").value(ApiConstants.FLASHCARD_RETRIEVED))
      .andExpect(jsonPath("$.data.question").value("What is Java?"))
      .andExpect(jsonPath("$.data.answer").value("A programming language"));
  }

  /**
   * Tests flashcard retrieval when flashcard does not exist at the specified position.
   * Verifies that the endpoint returns appropriate error response
   * when requesting a non-existent flashcard.
   *
   * @throws Exception if the MockMvc request fails
   * 
   */
  @Test
  public void testGetFlashcard_FlashcardNotFound() throws Exception {
    when(flashcardService.getFlashcard("testUser", "TestDeck", 99))
      .thenThrow(new IllegalArgumentException("Flashcard not found"));

    mockMvc.perform(get(ApiEndpoints.FLASHCARDS + ApiEndpoints.FLASHCARD_GET)
        .param("username", "testUser")
        .param("deckname", "TestDeck")
        .param("number", "99"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.FLASHCARD_OPERATION_FAILED));
  }

  /**
   * Tests flashcard retrieval when deck does not exist.
   * Verifies that the endpoint returns appropriate error response
   * when attempting to retrieve a flashcard from non-existent deck.
   *
   * @throws Exception if the MockMvc request fails
   * 
   */
  @Test
  public void testGetFlashcard_DeckNotFound() throws Exception {
    when(flashcardService.getFlashcard("testUser", "NonExistentDeck", 1))
      .thenThrow(new IllegalArgumentException("Deck not found"));

    mockMvc.perform(get(ApiEndpoints.FLASHCARDS + ApiEndpoints.FLASHCARD_GET)
        .param("username", "testUser")
        .param("deckname", "NonExistentDeck")
        .param("number", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.FLASHCARD_OPERATION_FAILED));
  }

  /**
   * Tests successful retrieval of all flashcards in a deck.
   * Verifies that the endpoint returns HTTP 200 and a list of all flashcards.
   *
   * @throws Exception if the MockMvc request fails
   * 
   */
  @Test
  public void testGetAllFlashcards_Success() throws Exception {
    when(flashcardService.getAllFlashcards("testUser", "TestDeck"))
      .thenReturn(testFlashcardList);

    mockMvc.perform(get(ApiEndpoints.FLASHCARDS + ApiEndpoints.FLASHCARD_GET_ALL)
      .param("username", "testUser")
      .param("deckname", "TestDeck"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(true))
      .andExpect(jsonPath("$.message").value(ApiConstants.FLASHCARDS_RETRIEVED))
      .andExpect(jsonPath("$.data").isArray())
      .andExpect(jsonPath("$.data[0].question").value("What is Java?"))
      .andExpect(jsonPath("$.data[1].question").value("What is Spring?"));
  }

  /**
   * Tests retrieval of all flashcards when deck does not exist.
   * Verifies that the endpoint returns appropriate error response
   * when attempting to retrieve flashcards from non-existent deck.
   *
   * @throws Exception if the MockMvc request fails
   * 
   */
  @Test
  public void testGetAllFlashcards_DeckNotFound() throws Exception {
    when(flashcardService.getAllFlashcards("testUser", "NonExistentDeck"))
      .thenThrow(new IllegalArgumentException("Deck not found"));

    mockMvc.perform(get(ApiEndpoints.FLASHCARDS + ApiEndpoints.FLASHCARD_GET_ALL)
      .param("username", "testUser")
      .param("deckname", "NonExistentDeck"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(jsonPath("$.message").value(ApiConstants.FLASHCARD_OPERATION_FAILED));
  }

  /**
   * Tests retrieval of all flashcards when user does not exist.
   * Verifies that the endpoint returns appropriate error response
   * when attempting to retrieve flashcards for non-existent user.
   *
   * @throws Exception if the MockMvc request fails
   * 
   */
  @Test
  public void testGetAllFlashcards_UserNotFound() throws Exception {
    when(flashcardService.getAllFlashcards("nonExistent", "TestDeck"))
      .thenThrow(new IllegalArgumentException("User not found"));

    mockMvc.perform(get(ApiEndpoints.FLASHCARDS + ApiEndpoints.FLASHCARD_GET_ALL)
        .param("username", "nonExistent")
        .param("deckname", "TestDeck"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.FLASHCARD_OPERATION_FAILED));
  }

  /**
   * Tests retrieval of all flashcards from an empty deck.
   * Verifies that the endpoint returns HTTP 200 with an empty array
   * when the deck contains no flashcards.
   *
   * @throws Exception if the MockMvc request fails
   * 
   */
  @Test
  public void testGetAllFlashcards_EmptyDeck() throws Exception {
    when(flashcardService.getAllFlashcards("testUser", "EmptyDeck"))
      .thenReturn(new ArrayList<>());

    mockMvc.perform(get(ApiEndpoints.FLASHCARDS + ApiEndpoints.FLASHCARD_GET_ALL)
      .param("username", "testUser")
      .param("deckname", "EmptyDeck"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(true))
      .andExpect(jsonPath("$.message").value(ApiConstants.FLASHCARDS_RETRIEVED))
      .andExpect(jsonPath("$.data").isArray())
      .andExpect(jsonPath("$.data").isEmpty());
  }

  /**
   * Tests successful creation of a flashcard.
   * Verifies that a new flashcard can be created with question and answer,
   * and returns HTTP 200 with the created flashcard data.
   *
   * @throws Exception if the MockMvc request fails
   * 
   */
  @Test
  public void testCreateFlashcard_Success() throws Exception {
    when(flashcardService.createFlashcard(anyString(), anyString(), anyString(), anyString()))
      .thenReturn(testFlashcard);
    when(flashcardService.getAllFlashcards(anyString(), anyString()))
      .thenReturn(testFlashcardList);
    when(flashcardService.getFlashcard(anyString(), anyString(), anyInt()))
      .thenReturn(testFlashcard);

    mockMvc.perform(post(ApiEndpoints.FLASHCARDS + ApiEndpoints.FLASHCARD_CREATE)
      .param("username", "testUser")
      .param("deckname", "TestDeck")
      .param("question", "What is Java?")
      .param("answer", "A programming language"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(true))
      .andExpect(jsonPath("$.message").value(ApiConstants.FLASHCARD_CREATED))
      .andExpect(jsonPath("$.data.question").value("What is Java?"))
      .andExpect(jsonPath("$.data.answer").value("A programming language"));
  }

  /**
   * Tests flashcard creation when user does not exist.
   * Verifies that the endpoint returns appropriate error response
   * when attempting to create a flashcard for non-existent user.
   *
   * @throws Exception if the MockMvc request fails
   * 
   */
  @Test
  public void testCreateFlashcard_UserNotFound() throws Exception {
    when(flashcardService.createFlashcard(anyString(), anyString(), anyString(), anyString()))
      .thenThrow(new IllegalArgumentException("User not found"));

    mockMvc.perform(post(ApiEndpoints.FLASHCARDS + ApiEndpoints.FLASHCARD_CREATE)
      .param("username", "nonExistent")
      .param("deckname", "TestDeck")
      .param("question", "What is Java?")
      .param("answer", "A programming language"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(jsonPath("$.message").value(ApiConstants.FLASHCARD_OPERATION_FAILED));
  }

  /**
   * Tests flashcard creation when deck does not exist.
   * Verifies that the endpoint returns appropriate error response
   * when attempting to create a flashcard in non-existent deck.
   *
   * @throws Exception if the MockMvc request fails
   * 
   */
  @Test
  public void testCreateFlashcard_DeckNotFound() throws Exception {
    when(flashcardService.createFlashcard(anyString(), anyString(), anyString(), anyString()))
      .thenThrow(new IllegalArgumentException("Deck not found"));

    mockMvc.perform(post(ApiEndpoints.FLASHCARDS + ApiEndpoints.FLASHCARD_CREATE)
      .param("username", "testUser")
      .param("deckname", "NonExistentDeck")
      .param("question", "What is Java?")
      .param("answer", "A programming language"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(jsonPath("$.message").value(ApiConstants.FLASHCARD_OPERATION_FAILED));
  }

  /**
   * Tests flashcard creation with empty question or answer.
   * Verifies that the endpoint returns appropriate error response
   * when provided with invalid flashcard data.
   *
   * @throws Exception if the MockMvc request fails
   * 
   */
  @Test
  public void testCreateFlashcard_InvalidData() throws Exception {
    when(flashcardService.createFlashcard(anyString(), anyString(), anyString(), anyString()))
      .thenThrow(new IllegalArgumentException("Invalid flashcard data"));

    mockMvc.perform(post(ApiEndpoints.FLASHCARDS + ApiEndpoints.FLASHCARD_CREATE)
      .param("username", "testUser")
      .param("deckname", "TestDeck")
      .param("question", "")
      .param("answer", ""))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(jsonPath("$.message").value(ApiConstants.FLASHCARD_OPERATION_FAILED));
  }

  /**
   * Tests successful deletion of a flashcard by position.
   * Verifies that a flashcard can be successfully deleted and returns
   * HTTP 200 with success message.
   *
   * @throws Exception if the MockMvc request fails
   * 
   */
  @Test
  public void testDeleteFlashcard_Success() throws Exception {
    doNothing().when(flashcardService).deleteFlashcard("testUser", "TestDeck", 1);

    mockMvc.perform(delete(ApiEndpoints.FLASHCARDS + ApiEndpoints.FLASHCARD_DELETE)
      .param("username", "testUser")
      .param("deckname", "TestDeck")
      .param("number", "1"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(true))
      .andExpect(jsonPath("$.message").value(ApiConstants.FLASHCARD_DELETED));
  }

  /**
   * Tests flashcard deletion when flashcard does not exist at the specified position.
   * Verifies that the endpoint returns appropriate error response
   * when attempting to delete a non-existent flashcard.
   *
   * @throws Exception if the MockMvc request fails
   * 
   */
  @Test
  public void testDeleteFlashcard_FlashcardNotFound() throws Exception {
    doThrow(new IllegalArgumentException("Flashcard not found"))
      .when(flashcardService).deleteFlashcard("testUser", "TestDeck", 99);

    mockMvc.perform(delete(ApiEndpoints.FLASHCARDS + ApiEndpoints.FLASHCARD_DELETE)
        .param("username", "testUser")
        .param("deckname", "TestDeck")
        .param("number", "99"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(jsonPath("$.message").value(ApiConstants.FLASHCARD_OPERATION_FAILED));
  }

  /**
   * Tests flashcard deletion when deck does not exist.
   * Verifies that the endpoint returns appropriate error response
   * when attempting to delete a flashcard from non-existent deck.
   *
   * @throws Exception if the MockMvc request fails
   * 
   */
  @Test
  public void testDeleteFlashcard_DeckNotFound() throws Exception {
    doThrow(new IllegalArgumentException("Deck not found"))
      .when(flashcardService).deleteFlashcard("testUser", "NonExistentDeck", 1);

    mockMvc.perform(delete(ApiEndpoints.FLASHCARDS + ApiEndpoints.FLASHCARD_DELETE)
        .param("username", "testUser")
        .param("deckname", "NonExistentDeck")
        .param("number", "1"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(jsonPath("$.message").value(ApiConstants.FLASHCARD_OPERATION_FAILED));
  }

  /**
   * Tests flashcard deletion when user does not exist.
   * Verifies that the endpoint returns appropriate error response
   * when attempting to delete a flashcard for non-existent user.
   *
   * @throws Exception if the MockMvc request fails
   * 
   */
  @Test
  public void testDeleteFlashcard_UserNotFound() throws Exception {
    doThrow(new IllegalArgumentException("User not found"))
      .when(flashcardService).deleteFlashcard("nonExistent", "TestDeck", 1);

    mockMvc.perform(delete(ApiEndpoints.FLASHCARDS + ApiEndpoints.FLASHCARD_DELETE)
        .param("username", "nonExistent")
        .param("deckname", "TestDeck")
        .param("number", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.FLASHCARD_OPERATION_FAILED));
  }

  /**
   * Tests flashcard deletion with invalid position (negative number).
   * Verifies that the endpoint returns appropriate error response
   * when provided with an invalid position.
   *
   * @throws Exception if the MockMvc request fails
   * 
   */
  @Test
  public void testDeleteFlashcard_InvalidPosition() throws Exception {
    doThrow(new IllegalArgumentException("Invalid position"))
      .when(flashcardService).deleteFlashcard("testUser", "TestDeck", -1);

    mockMvc.perform(delete(ApiEndpoints.FLASHCARDS + ApiEndpoints.FLASHCARD_DELETE)
        .param("username", "testUser")
        .param("deckname", "TestDeck")
        .param("number", "-1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.FLASHCARD_OPERATION_FAILED));
  }
}
