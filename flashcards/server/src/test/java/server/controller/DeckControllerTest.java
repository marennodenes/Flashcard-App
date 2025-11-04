package server.controller;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import app.FlashcardDeck;
import app.FlashcardDeckManager;
import dto.FlashcardDeckDto;
import dto.FlashcardDeckManagerDto;
import server.service.DeckService;
import shared.ApiEndpoints;

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

/**
 * Test class for DeckController REST endpoints.
 * Tests flashcard deck-related HTTP operations including retrieval,
 * creation, deletion, and updates using MockMvc and mocked services.
 *
 * This test suite validates:
 * - Retrieval of all decks for a user
 * - Retrieval of a specific deck by name
 * - Creation of new decks
 * - Deletion of existing decks
 * - Updating all decks for a user
 * - Error handling for various edge cases (user not found, deck not found, etc.)
 *
 * Uses @WebMvcTest to test only the web layer and @MockBean to mock
 * the DeckService dependency for isolated controller testing.
 *
 * @author chrsom
 * @author isamw
 * @author parts of class is generated with the help of claude.ai
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
  void setUp() {
    testDeck = new FlashcardDeck("TestDeck");
    testDeckManager = new FlashcardDeckManager();
    testDeckManager.addDeck(testDeck);

    List<FlashcardDeckDto> deckDtos = new ArrayList<>();
    deckDtos.add(new FlashcardDeckDto("TestDeck", new ArrayList<>()));
    testDeckManagerDto = new FlashcardDeckManagerDto(deckDtos);
  }

  // GET methods tests first
  /**
   * Tests successful retrieval of all decks for a user.
   * Verifies that the endpoint returns HTTP 200 and a list of all user's decks.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  void testGetAllDecks_Success() throws Exception {
    when(deckService.getAllDecks("testUser")).thenReturn(testDeckManager);

    mockMvc.perform(request(HttpMethod.valueOf("REQUEST"), ApiEndpoints.DECKS)
      .param("username", "testUser"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(true))
      .andExpect(jsonPath("$.message").value("Decks retrieved successfully"))
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
  void testGetAllDecks_UserNotFound() throws Exception {
    when(deckService.getAllDecks("nonExistent"))
      .thenThrow(new IllegalArgumentException("User not found"));

    mockMvc.perform(request(HttpMethod.valueOf("REQUEST"), ApiEndpoints.DECKS)
      .param("username", "nonExistent"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(jsonPath("$.message").value("Error retrieving decks: User not found"));
  }

  /**
   * Tests successful retrieval of a specific deck by name.
   * Verifies that the endpoint returns HTTP 200 and the requested deck data.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  void testGetDeckByName_Success() throws Exception {
    when(deckService.getDeck("testUser", "TestDeck")).thenReturn(testDeck);

    mockMvc.perform(request(HttpMethod.valueOf("REQUEST"), ApiEndpoints.DECKS + "/TestDeck")
      .param("username", "testUser")
      .param("deckName", "TestDeck"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(true))
      .andExpect(jsonPath("$.message").value("Deck retrieved successfully"))
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
  void testGetDeckByName_DeckNotFound() throws Exception {
    when(deckService.getDeck("testUser", "NonExistentDeck"))
      .thenThrow(new IllegalArgumentException("Deck not found"));

    mockMvc.perform(request(HttpMethod.valueOf("REQUEST"), ApiEndpoints.DECKS + "/NonExistentDeck")
      .param("username", "testUser")
      .param("deckName", "NonExistentDeck"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(jsonPath("$.message").value("Error retrieving deck: Deck not found"));
  }

  // POST methods tests
  /**
   * Tests successful creation of a new deck.
   * Verifies that a new deck can be created and returns HTTP 200
   * with the created deck data.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  void testCreateDeck_Success() throws Exception {
    when(deckService.createDeck("testUser", "NewDeck")).thenReturn(new FlashcardDeck("NewDeck"));

    mockMvc.perform(post(ApiEndpoints.DECKS + "/NewDeck")
      .param("username", "testUser")
      .param("deckName", "NewDeck"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(true))
      .andExpect(jsonPath("$.message").value("Deck created successfully."))
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
  void testCreateDeck_UserNotFound() throws Exception {
    when(deckService.createDeck("nonExistent", "NewDeck"))
      .thenThrow(new IllegalArgumentException("User not found"));

    mockMvc.perform(post(ApiEndpoints.DECKS + "/NewDeck")
      .param("username", "nonExistent")
      .param("deckName", "NewDeck"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(jsonPath("$.message").value("Error creating deck: User not found"));
  }

  /**
   * Tests deck creation when deck already exists.
   * Verifies that the endpoint returns appropriate error response
   * when attempting to create a duplicate deck.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  void testCreateDeck_DeckAlreadyExists() throws Exception {
    when(deckService.createDeck("testUser", "TestDeck"))
      .thenThrow(new IllegalArgumentException("Deck already exists"));

    mockMvc.perform(post(ApiEndpoints.DECKS + "/TestDeck")
      .param("username", "testUser")
      .param("deckName", "TestDeck"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(jsonPath("$.message").value("Error creating deck: Deck already exists"));
  }

  // PUT methods tests
  /**
   * Tests successful update of all decks for a user.
   * Verifies that multiple decks can be updated in a single operation
   * and returns HTTP 200 with success message.
   *
   * @throws Exception if the MockMvc request fails
   */
  @SuppressWarnings("null")
  @Test
  void testUpdateAllDecks_Success() throws Exception {
    doNothing().when(deckService).updateAllDecks(anyString(), any(FlashcardDeckManager.class));

    mockMvc.perform(put(ApiEndpoints.DECKS)
      .param("username", "testUser")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(testDeckManagerDto)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(true))
      .andExpect(jsonPath("$.message").value("Decks updated successfully"));
  }

  /**
   * Tests deck update when user does not exist.
   * Verifies that the endpoint returns appropriate error response
   * when attempting to update decks for non-existent user.
   *
   * @throws Exception if the MockMvc request fails
   */
  @SuppressWarnings("null")
  @Test
  void testUpdateAllDecks_UserNotFound() throws Exception {
    doThrow(new IllegalArgumentException("User not found"))
      .when(deckService).updateAllDecks(anyString(), any(FlashcardDeckManager.class));

    mockMvc.perform(put(ApiEndpoints.DECKS)
      .param("username", "nonExistent")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(testDeckManagerDto)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(jsonPath("$.message").value("Error updating decks: User not found"));
  }

  /**
   * Tests deck update with invalid data.
   * Verifies that the endpoint returns appropriate error response
   * when provided with invalid deck data.
   *
   * @throws Exception if the MockMvc request fails
   */
  @SuppressWarnings("null")
  @Test
  void testUpdateAllDecks_InvalidData() throws Exception {
    doThrow(new IllegalArgumentException("Invalid deck data"))
      .when(deckService).updateAllDecks(anyString(), any(FlashcardDeckManager.class));

    mockMvc.perform(put(ApiEndpoints.DECKS)
      .param("username", "testUser")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(testDeckManagerDto)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(jsonPath("$.message").value("Error updating decks: Invalid deck data"));
  }

  // DELETE methods tests last
  /**
   * Tests successful deletion of a deck.
   * Verifies that a deck can be successfully deleted and returns
   * HTTP 200 with success message.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  void testDeleteDeck_Success() throws Exception {
    doNothing().when(deckService).deleteDeck("testUser", "TestDeck");

    mockMvc.perform(delete(ApiEndpoints.DECKS + "/TestDeck")
      .param("username", "testUser")
      .param("deckName", "TestDeck"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(true))
      .andExpect(jsonPath("$.message").value("Deck deleted successfully."));
  }

  /**
   * Tests deck deletion when deck does not exist.
   * Verifies that the endpoint returns appropriate error response
   * when attempting to delete a non-existent deck.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  void testDeleteDeck_DeckNotFound() throws Exception {
    doThrow(new IllegalArgumentException("Deck not found"))
      .when(deckService).deleteDeck("testUser", "NonExistentDeck");

    mockMvc.perform(delete(ApiEndpoints.DECKS + "/NonExistentDeck")
      .param("username", "testUser")
      .param("deckName", "NonExistentDeck"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(jsonPath("$.message").value("Error deleting deck: Deck not found"));
  }

  /**
   * Tests deck deletion when user does not exist.
   * Verifies that the endpoint returns appropriate error response
   * when attempting to delete a deck for non-existent user.
   *
   * @throws Exception if the MockMvc request fails
   */
  @Test
  void testDeleteDeck_UserNotFound() throws Exception {
    doThrow(new IllegalArgumentException("User not found"))
      .when(deckService).deleteDeck("nonExistent", "TestDeck");

    mockMvc.perform(delete(ApiEndpoints.DECKS + "/TestDeck")
      .param("username", "nonExistent")
      .param("deckName", "TestDeck"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.success").value(false))
      .andExpect(jsonPath("$.message").value("Error deleting deck: User not found"));
  }
}
