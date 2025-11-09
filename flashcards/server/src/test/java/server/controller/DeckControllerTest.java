package server.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.FlashcardDeck;
import app.FlashcardDeckManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.FlashcardDeckDto;
import dto.FlashcardDeckManagerDto;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import server.service.DeckService;
import shared.ApiConstants;
import shared.ApiEndpoints;


/**
 * Test class for DeckController REST endpoints.
 * Tests flashcard deck-related HTTP operations including retrieval,
 * creation, deletion, and updates using MockMvc and mocked services.
 *
 * <p>This test suite validates:
 * - Retrieval of all decks for a user
 * - Retrieval of a specific deck by name
 * - Creation of new decks
 * - Deletion of existing decks
 * - Updating all decks for a user
 * - Error handling for various edge cases (user not found, deck not found, etc.)
 *
 * <p>Uses @WebMvcTest to test only the web layer and @MockBean to mock
 * the DeckService dependency for isolated controller testing.
 *
 * @author chrsom
 * @author isamw
 * @see "docs/release_3/ai_tools.md"
 * @see DeckController
 * @see DeckService
 */
@WebMvcTest(DeckController.class)
public class DeckControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private DeckService deckService;

  @Autowired
  private ObjectMapper objectMapper;

  private FlashcardDeck testDeck;
  private FlashcardDeckManager testDeckManager;
  private FlashcardDeckManagerDto testDeckManagerDto;

  /**
   * Sets up test fixtures before each test.
   * Initializes test decks, deck manager, and DTOs for use in test cases.
   */
  @BeforeEach
  public void setUp() {
    testDeck = new FlashcardDeck("TestDeck");
    testDeckManager = new FlashcardDeckManager();
    testDeckManager.addDeck(testDeck);

    List<FlashcardDeckDto> deckDtos = new ArrayList<>();
    deckDtos.add(new FlashcardDeckDto("TestDeck", new ArrayList<>()));
    testDeckManagerDto = new FlashcardDeckManagerDto(deckDtos);
  }

  /**
   * Tests successful retrieval of all decks for a user.
   * Verifies that the endpoint returns HTTP 200 and a list of all user's decks.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testGetAllDecksSuccess() throws Exception {
    when(deckService.getAllDecks("testUser")).thenReturn(testDeckManager);

    mockMvc.perform(request(HttpMethod.valueOf("REQUEST"), ApiEndpoints.DECKS)
        .param("username", "testUser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value(ApiConstants.DECKS_RETRIEVED))
        .andExpect(jsonPath("$.data.decks").isArray())
        .andExpect(jsonPath("$.data.decks[0].deckName").value("TestDeck"));
  }

  /**
   * Tests deck retrieval when user does not exist.
   * Verifies that the endpoint returns appropriate error response
   * when attempting to retrieve decks for non-existent user.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testGetAllDecksUserNotFound() throws Exception {
    when(deckService.getAllDecks("nonExistent"))
        .thenThrow(new IllegalArgumentException("User not found"));

    mockMvc.perform(request(HttpMethod.valueOf("REQUEST"), ApiEndpoints.DECKS)
        .param("username", "nonExistent"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.FAILED_TO_LOAD_DATA));
  }

  /**
   * Tests successful retrieval of a specific deck by name.
   * Verifies that the endpoint returns HTTP 200 and the requested deck data.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testGetDeckByNameSuccess() throws Exception {
    when(deckService.getDeck("testUser", "TestDeck")).thenReturn(testDeck);

    mockMvc.perform(request(HttpMethod.valueOf("REQUEST"), ApiEndpoints.DECKS + "/TestDeck")
        .param("username", "testUser")
        .param("deckName", "TestDeck"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value(ApiConstants.DECK_RETRIEVED))
        .andExpect(jsonPath("$.data.deckName").value("TestDeck"));
  }

  /**
   * Tests deck retrieval when the specific deck does not exist.
   * Verifies that the endpoint returns appropriate error response
   * when requesting a non-existent deck.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testGetDeckByNameDeckNotFound() throws Exception {
    when(deckService.getDeck("testUser", "NonExistentDeck"))
        .thenThrow(new IllegalArgumentException("Deck not found"));

    mockMvc.perform(request(HttpMethod.valueOf("REQUEST"), ApiEndpoints.DECKS + "/NonExistentDeck")
        .param("username", "testUser")
        .param("deckName", "NonExistentDeck"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.FAILED_TO_LOAD_DATA));
  }

  /**
   * Tests getAllDecks when IOException occurs.
   */
  @Test
  public void testGetAllDecksIoException() throws Exception {
    when(deckService.getAllDecks("testUser"))
        .thenThrow(new RuntimeException("Storage error"));

    mockMvc.perform(request(HttpMethod.valueOf("REQUEST"), ApiEndpoints.DECKS)
        .param("username", "testUser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.FAILED_TO_LOAD_DATA));
  }

  /**
   * Tests getDeckByName when IOException occurs.
   */
  @Test 
  public void testGetDeckByNameIoException() throws Exception {
    when(deckService.getDeck("testUser", "TestDeck"))
        .thenThrow(new RuntimeException("IO Error"));

    mockMvc.perform(request(HttpMethod.valueOf("REQUEST"), ApiEndpoints.DECKS + "/TestDeck")
        .param("username", "testUser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.FAILED_TO_LOAD_DATA));
  }

  /**
   * Tests successful creation of a new deck.
   * Verifies that a new deck can be created and returns HTTP 200
   * with the created deck data.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testCreateDeckSuccess() throws Exception {
    when(deckService.createDeck("testUser", "NewDeck")).thenReturn(new FlashcardDeck("NewDeck"));

    mockMvc.perform(post(ApiEndpoints.DECKS + "/NewDeck")
        .param("username", "testUser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value(ApiConstants.DECK_CREATED))
        .andExpect(jsonPath("$.data.deckName").value("NewDeck"));
  }

  /**
   * Tests deck creation when user does not exist.
   * Verifies that the endpoint returns appropriate error response
   * when attempting to create a deck for non-existent user.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testCreateDeckUserNotFound() throws Exception {
    when(deckService.createDeck("nonExistent", "NewDeck"))
        .thenThrow(new IllegalArgumentException(ApiConstants.USER_NOT_FOUND));

    mockMvc.perform(post(ApiEndpoints.DECKS + "/NewDeck")
        .param("username", "nonExistent"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.USER_NOT_FOUND));
  }

  /**
   * Tests deck creation when deck already exists.
   * Verifies that the endpoint returns appropriate error response
   * when attempting to create a duplicate deck.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testCreateDeckDeckAlreadyExists() throws Exception {
    when(deckService.createDeck("testUser", "TestDeck"))
        .thenThrow(new IllegalArgumentException("Deckname must be unique"));

    mockMvc.perform(post(ApiEndpoints.DECKS + "/TestDeck")
        .param("username", "testUser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.DECK_ALREADY_EXISTS));
  }

  /**
   * Tests deck creation when service throws IOException.
   * Verifies that the endpoint returns appropriate error response
   * when storage operations fail.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testCreateDeckServiceThrowsIoException() throws Exception {
    when(deckService.createDeck("testUser", "NewDeck"))
        .thenThrow(new RuntimeException("Storage unavailable"));

    mockMvc.perform(post(ApiEndpoints.DECKS + "/NewDeck")
        .param("username", "testUser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.DECK_OPERATION_FAILED));
  }

  /**
   * Tests deck creation when deck limit is reached.
   * Verifies that the endpoint returns appropriate error response
   * when attempting to create more than the maximum allowed decks.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testCreateDeckDeckLimitReached() throws Exception {
    when(deckService.createDeck("testUser", "NewDeck"))
        .thenThrow(new IllegalArgumentException("You can only have up to 8 decks"));

    mockMvc.perform(post(ApiEndpoints.DECKS + "/NewDeck")
        .param("username", "testUser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.DECK_LIMIT_REACHED));
  }

  /**
   * Tests deck creation when deck name is empty.
   * Verifies that the endpoint returns appropriate error response
   * when attempting to create a deck with empty name.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testCreateDeckEmptyDeckName() throws Exception {
    when(deckService.createDeck("testUser", "EmptyDeck"))
        .thenThrow(new IllegalArgumentException("Deckname cannot be empty"));

    mockMvc.perform(post(ApiEndpoints.DECKS + "/EmptyDeck")
        .param("username", "testUser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.DECK_NAME_EMPTY));
  }

  /**
   * Tests createDeck with technical exception message containing "User not found".
   * This tests the specific branch in exception mapping logic.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testCreateDeckUserNotFoundInMessage() throws Exception {
    when(deckService.createDeck("testUser", "NewDeck"))
        .thenThrow(new IllegalArgumentException("User not found in system"));

    mockMvc.perform(post(ApiEndpoints.DECKS + "/NewDeck")
        .param("username", "testUser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.USER_NOT_FOUND));
  }

  /**
   * Tests createDeck with technical exception message containing "unique".
   * This tests the specific branch for duplicate deck names.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testCreateDeckUniqueConstraintViolation() throws Exception {
    when(deckService.createDeck("testUser", "DuplicateDeck"))
        .thenThrow(new IllegalArgumentException("Deck name must be unique for user"));

    mockMvc.perform(post(ApiEndpoints.DECKS + "/DuplicateDeck")
        .param("username", "testUser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.DECK_ALREADY_EXISTS));
  }

  /**
   * Tests createDeck with unknown exception message.
   * This tests the fallback branch in exception mapping.
   *
   * @throws Exception if the MockMvc request fails
   * 
   */
  @Test
  public void testCreateDeckUnknownError() throws Exception {
    when(deckService.createDeck("testUser", "NewDeck"))
        .thenThrow(new IllegalArgumentException("Some unexpected error"));

    mockMvc.perform(post(ApiEndpoints.DECKS + "/NewDeck")
        .param("username", "testUser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.DECK_OPERATION_FAILED));
  }

  /**
   * Tests createDeck with null exception message.
   * This tests the null check branch in exception mapping.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testCreateDeckNullExceptionMessage() throws Exception {
    // Create exception with null message
    IllegalArgumentException exceptionWithNullMessage = new IllegalArgumentException((String) null);
    when(deckService.createDeck("testUser", "NewDeck"))
        .thenThrow(exceptionWithNullMessage);

    mockMvc.perform(post(ApiEndpoints.DECKS + "/NewDeck")
        .param("username", "testUser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.DECK_OPERATION_FAILED));
  }

  /**
   * Tests createDeck with RuntimeException (non-IllegalArgumentException).
   * This tests the catch (Exception e) branch.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testCreateDeckRuntimeException() throws Exception {
    when(deckService.createDeck("testUser", "NewDeck"))
        .thenThrow(new RuntimeException("Unexpected runtime error"));

    mockMvc.perform(post(ApiEndpoints.DECKS + "/NewDeck")
        .param("username", "testUser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.DECK_OPERATION_FAILED));
  }

  /**
   * Tests createDeck with IOException.
   * This tests the catch (Exception e) branch for IO errors.
   *
   * @throws Exception if the MockMvc request fails
   * @throws IOException if createDeck throws IOException
   */
  @Test
  public void testCreateDeckIoException() throws Exception, IOException {
    when(deckService.createDeck("testUser", "NewDeck"))
        .thenThrow(new java.io.IOException("File system error"));

    mockMvc.perform(post(ApiEndpoints.DECKS + "/NewDeck")
        .param("username", "testUser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.DECK_OPERATION_FAILED));
  }

  /**
   * Tests successful update of all decks for a user.
   * Verifies that multiple decks can be updated in a single operation
   * and returns HTTP 200 with success message.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testUpdateAllDecksSuccess() throws Exception {
    doNothing()
      .when(deckService).updateAllDecks(anyString(), any(FlashcardDeckManager.class));
    
    String requestBody = objectMapper.writeValueAsString(testDeckManagerDto);
    Objects.requireNonNull(requestBody, "Request body cannot be null");
      
    mockMvc.perform(put(ApiEndpoints.DECKS)
        .param("username", "testUser")
        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value(ApiConstants.DECK_UPDATED));
  }

  /**
   * Tests deck update when user does not exist.
   * Verifies that the endpoint returns appropriate error response
   * when attempting to update decks for non-existent user.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testUpdateAllDecksUserNotFound() throws Exception {
    doThrow(new IllegalArgumentException("User not found"))
      .when(deckService).updateAllDecks(anyString(), any(FlashcardDeckManager.class));

    String requestBody = objectMapper.writeValueAsString(testDeckManagerDto);
    Objects.requireNonNull(requestBody, "Request body cannot be null");

    mockMvc.perform(put(ApiEndpoints.DECKS)
        .param("username", "nonExistent")
        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.DECK_UPDATE_FAILED));
  }

  /**
   * Tests deck update with invalid data.
   * Verifies that the endpoint returns appropriate error response
   * when provided with invalid deck data.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testUpdateAllDecksInvalidData() throws Exception {
    doThrow(new IllegalArgumentException("Invalid deck data"))
      .when(deckService).updateAllDecks(anyString(), any(FlashcardDeckManager.class));

    String requestBody = objectMapper.writeValueAsString(testDeckManagerDto);
    Objects.requireNonNull(requestBody, "Request body cannot be null");

    mockMvc.perform(put(ApiEndpoints.DECKS)
        .param("username", "testUser")
        .contentType(Objects.requireNonNull(MediaType.APPLICATION_JSON))
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.DECK_UPDATE_FAILED));
  }

  /**
   * Tests successful deletion of a deck.
   * Verifies that a deck can be successfully deleted and returns
   * HTTP 200 with success message.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testDeleteDeckSuccess() throws Exception {
    doNothing().when(deckService).deleteDeck("testUser", "TestDeck");

    mockMvc.perform(delete(ApiEndpoints.DECKS + "/TestDeck")
        .param("username", "testUser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value(ApiConstants.DECK_DELETED));
  }

  /**
   * Tests deck deletion when deck does not exist.
   * Verifies that the endpoint returns appropriate error response
   * when attempting to delete a non-existent deck.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testDeleteDeckDeckNotFound() throws Exception {
    doThrow(new IllegalArgumentException("Deck not found"))
        .when(deckService).deleteDeck("testUser", "NonExistentDeck");

    mockMvc.perform(delete(ApiEndpoints.DECKS + "/NonExistentDeck")
        .param("username", "testUser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.DECK_OPERATION_FAILED));
  }

  /**
   * Tests deck deletion when user does not exist.
   * Verifies that the endpoint returns appropriate error response
   * when attempting to delete a deck for non-existent user.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  public void testDeleteDeckUserNotFound() throws Exception {
    doThrow(new IllegalArgumentException("User not found"))
        .when(deckService).deleteDeck("nonExistent", "TestDeck");

    mockMvc.perform(delete(ApiEndpoints.DECKS + "/TestDeck")
        .param("username", "nonExistent"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ApiConstants.DECK_OPERATION_FAILED));
  }
}
