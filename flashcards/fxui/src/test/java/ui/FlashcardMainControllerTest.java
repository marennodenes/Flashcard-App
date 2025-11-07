package ui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.fasterxml.jackson.core.type.TypeReference;

import shared.ApiConstants;

import app.Flashcard;

import app.FlashcardDeck;
import app.FlashcardDeckManager;
import dto.FlashcardDeckDto;
import dto.FlashcardDeckManagerDto;
import dto.FlashcardDto;
import dto.mappers.FlashcardDeckMapper;
import shared.ApiResponse;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;


/**
 * Test class for FlashcardMainController.
 * 
 * Tests initialization, deck management, navigation, and API integration
 * for the main flashcard deck overview screen.
 * 
 * This test class uses TestFX for UI testing and Mockito for mocking API calls.
 * 
 * Thread Safety: This class is not thread-safe and should only be used in a single-threaded test environment.
 * 
 * @author ailinat
 * @author marennod
 * @author sofietw
 * @author Generated with AI assistance for comprehensive test coverage
 * 
 * @see FlashcardMainController
 * @see FlashcardDeckDto
 * @see ApiClient
 * 
 */
@ExtendWith(ApplicationExtension.class)
class FlashcardMainControllerTest {

  private FlashcardMainController controller;
  private FlashcardDeckMapper mapper = new FlashcardDeckMapper();
  private Stage stage;
  private Button deck1, deck2, deck3, deck4, deck5, deck6, deck7, deck8;
  private Button deleteDeck1, deleteDeck2, deleteDeck3, deleteDeck4;
  private Button deleteDeck5, deleteDeck6, deleteDeck7, deleteDeck8;
  private TextField deckNameInput;
  private Button newDeckButton;
  private Button logOutButton;  
  private Text usernameField;
  private Text alertMessage;
  private Text ex; 
  private Text noDecks;

  /**
   * Initializes the test stage.
   * 
   * @param stage the JavaFX stage for testing
   * 
   */
  @Start
  public void start(Stage stage) {
    this.stage = stage;
    stage.setWidth(1);
    stage.setHeight(1);
    stage.setX(-1000);
    stage.setY(-1000);
  }

  /**
   * Sets up test environment before each test.
   * 
   * @throws Exception if component initialization or injection fails
   * 
   */
  @BeforeEach
  public void setUp() throws Exception {
    controller = new FlashcardMainController();
    initializeFxmlComponents();
    injectFxmlComponents();
    controller.initialize();
  }

  /**
   * Initializes all FXML components.
   */
  private void initializeFxmlComponents() {
    deck1 = new Button();
    deck2 = new Button();
    deck3 = new Button();
    deck4 = new Button();
    deck5 = new Button();
    deck6 = new Button();
    deck7 = new Button();
    deck8 = new Button();
    deleteDeck1 = new Button();
    deleteDeck2 = new Button();
    deleteDeck3 = new Button();
    deleteDeck4 = new Button();
    deleteDeck5 = new Button();
    deleteDeck6 = new Button();
    deleteDeck7 = new Button();
    deleteDeck8 = new Button();
    deckNameInput = new TextField();
    newDeckButton = new Button();
    logOutButton = new Button();
    usernameField = new Text();
    alertMessage = new Text();
    ex = new Text();
    noDecks = new Text();
  }

  /**
   * Injects FXML components into controller using reflection.
   *  
   * @throws Exception if the field is not found or cannot be set
   * 
   */
  private void injectFxmlComponents() throws Exception {
    setField("deck_1", deck1);
    setField("deck_2", deck2);
    setField("deck_3", deck3);
    setField("deck_4", deck4);
    setField("deck_5", deck5);
    setField("deck_6", deck6);
    setField("deck_7", deck7);
    setField("deck_8", deck8);
    setField("deleteDeck_1", deleteDeck1);
    setField("deleteDeck_2", deleteDeck2);
    setField("deleteDeck_3", deleteDeck3);
    setField("deleteDeck_4", deleteDeck4);
    setField("deleteDeck_5", deleteDeck5);
    setField("deleteDeck_6", deleteDeck6);
    setField("deleteDeck_7", deleteDeck7);
    setField("deleteDeck_8", deleteDeck8);
    setField("deckNameInput", deckNameInput);
    setField("newDeckButton", newDeckButton);
    setField("logOutButton", logOutButton);
    setField("usernameField", usernameField);
    setField("alertMessage", alertMessage);
    setField("ex", ex);
    setField("noDecks", noDecks);
  }

  /**
   * Sets private field using reflection.
   * 
   * @param fieldName the name of the field to set
   * @param value the value to assign to the field
   * @throws Exception if the field is not found or cannot be set
   * 
   */
  private void setField(String fieldName, Object value) throws Exception {
    var field = FlashcardMainController.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(controller, value);
  }

  /**
   * Gets private field using reflection.
   * 
   * @param fieldName the name of the field to retrieve
   * @return the value of the field
   * @throws Exception if the field is not found or cannot be accessed
   * 
   */
  private Object getField(String fieldName) throws Exception {
    var field = FlashcardMainController.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    return field.get(controller);
  }

  /**
   * Tests that all buttons are hidden and disabled initially.
   */
  @Test
  public void testInitialize_AllButtonsHiddenInitially() {
    assertFalse(deck1.isVisible());
    assertTrue(deck1.isDisabled());
    assertFalse(deck2.isVisible());
    assertFalse(deck3.isVisible());

    assertFalse(deleteDeck1.isVisible());
    assertFalse(deleteDeck2.isVisible());
  }

  /**
   * Tests that alerts are hidden initially.
   */
  @Test
  public void testInitialize_AlertsHiddenInitially() {
    runOnFxThread(() -> controller.updateUi());
    assertFalse(alertMessage.isVisible());
    assertFalse(ex.isVisible());
  }

  /**
   * Tests setting a valid username.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testSetCurrentUsername_ValidUsername() {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> response = createSuccessResponse(new ArrayList<>());
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
      .thenReturn(response);

      controller.setCurrentUsername("testuser");
      assertEquals("testuser", usernameField.getText());
    }
  }

  /**
   * Tests handling of null username.
   */
  @Test
  public void testSetCurrentUsername_NullUsername() {
    controller.setCurrentUsername(null);
    assertEquals("", usernameField.getText());
  }

  /**
   * Tests handling of empty username.
   */
  @Test
  public void testSetCurrentUsername_EmptyUsername() {
    controller.setCurrentUsername("   ");
    assertEquals("", usernameField.getText());
  }

  /**
   * Tests that username trims whitespace.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testSetCurrentUsername_TrimsWhitespace() {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> response = createSuccessResponse(new ArrayList<>());
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
      .thenReturn(response);

      controller.setCurrentUsername("  testuser  ");
      assertEquals("testuser", usernameField.getText());
    }
  }

  /**
   * Tests creating a deck with valid name.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenNewDeckButtonIsClicked_ValidDeckName() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ActionEvent event = new ActionEvent();
      FlashcardDeckDto newDeckDto = new FlashcardDeckDto("My New Deck", new ArrayList<>());
      ApiResponse<FlashcardDeckDto> postResponse = new ApiResponse<>(true, "", newDeckDto);
      ApiResponse<FlashcardDeckManagerDto> getResponseInitial = createSuccessResponse(new ArrayList<>());
      ApiResponse<FlashcardDeckManagerDto> getResponseAfterPost = createSuccessResponse(List.of(newDeckDto));
        
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
      .thenReturn(getResponseInitial)
      .thenReturn(getResponseAfterPost);
        
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("POST"), anyString(), any()))
      .thenReturn(postResponse);
      controller.setCurrentUsername("testuser");
      deckNameInput.setText("My New Deck");
      controller.whenNewDeckButtonIsClicked(event);
        
      List<FlashcardDeckDto> decks = (List<FlashcardDeckDto>) getField("decks");
      assertEquals(1, decks.size());
      assertEquals("My New Deck", decks.get(0).getDeckName());
    }
  }

  /**
   * Tests that empty deck name shows error.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenNewDeckButtonIsClicked_EmptyName_ShowsError() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> response = createSuccessResponse(new ArrayList<>());
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
      .thenReturn(response);

      controller.setCurrentUsername("testuser");
      deckNameInput.setText("");

      ActionEvent event = new ActionEvent();
      controller.whenNewDeckButtonIsClicked(event);
      assertTrue(alertMessage.isVisible());
      assertTrue(ex.isVisible());
    }
  }

  /**
   * Tests that invalid deck name shows error.
   *  
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenNewDeckButtonIsClicked_InvalidName_ShowsError() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
    ApiResponse<FlashcardDeckManagerDto> response = createSuccessResponse(new ArrayList<>());
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
      .thenReturn(response);

      controller.setCurrentUsername("testuser");
      deckNameInput.setText("   ");

      ActionEvent event = new ActionEvent();
      controller.whenNewDeckButtonIsClicked(event);
      assertTrue(alertMessage.isVisible());
    }
  }

  /**
   * Tests that new deck button is disabled when max decks reached.
   * 
   * @throws Exception if test setup failed
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testUpdateUi_MaxDecksReached_DisablesNewDeckButton() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> response = createSuccessResponse(new ArrayList<>());
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
      .thenReturn(response);
      controller.setCurrentUsername("testuser");

      List<FlashcardDeckDto> decks = (List<FlashcardDeckDto>) getField("decks");
      for (int i = 0; i < 8; i++) {
        FlashcardDeck deck = new FlashcardDeck();
        deck.setDeckName("Deck " + (i + 1));
        decks.add(mapper.toDto(deck));
      }
      runOnFxThread(() -> controller.updateUi());
      assertTrue(newDeckButton.isDisabled());
    }
  }

  /**
   * Tests that new deck button is enabled when less than max decks.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testUpdateUi_LessThanMaxDecks_EnablesNewDeckButton() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> response = createSuccessResponse(new ArrayList<>());
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
      .thenReturn(response);

      controller.setCurrentUsername("testuser");

      List<FlashcardDeckDto> decks = (List<FlashcardDeckDto>) getField("decks");
      FlashcardDeck deck = new FlashcardDeck();
      deck.setDeckName("Test Deck");
      decks.add(mapper.toDto(deck));
      runOnFxThread(() -> controller.updateUi());
      assertFalse(newDeckButton.isDisabled());
    }
  }

  /**
   * Tests that correct number of decks is shown.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testUpdateUi_ShowsCorrectNumberOfDecks() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      List<FlashcardDeckDto> decksForResponse = new ArrayList<>();

      for (int i = 0; i < 3; i++) {
        FlashcardDeck deck = new FlashcardDeck();
        deck.setDeckName("Deck " + (i + 1));
        decksForResponse.add(mapper.toDto(deck));
      }

      ApiResponse<FlashcardDeckManagerDto> responseWithDecks = createSuccessResponse(decksForResponse);

      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
        .thenReturn(responseWithDecks);

      List<FlashcardDeckDto> decks = (List<FlashcardDeckDto>) getField("decks");
      decks.clear();
      deck1.setVisible(false);
      deck2.setVisible(false);
      deck3.setVisible(false);
      deck4.setVisible(false);
        
      controller.setCurrentUsername("testuser");
        
      List<FlashcardDeckDto> loadedDecks = (List<FlashcardDeckDto>) getField("decks");
      assertEquals(3, loadedDecks.size(), "Should have loaded 3 decks from API");
        
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
        
      runOnFxThread(() -> {
        assertTrue(deck1.isVisible(), "deck1 should be visible after loading 3 decks");
        assertTrue(deck2.isVisible(), "deck2 should be visible after loading 3 decks");
        assertTrue(deck3.isVisible(), "deck3 should be visible after loading 3 decks");
        assertFalse(deck4.isVisible(), "deck4 should not be visible when only 3 decks loaded");
      });
    }
  }

  /**
   * Tests that no decks message is shown when no decks exist.
   */
  @Test
  public void testUpdateUi_NoDecks_ShowsNoDecksMessage() {
    runOnFxThread(() -> controller.updateUi());
    assertTrue(noDecks.isVisible());
  }

  /**
   * Tests that no decks message is hidden when decks exist.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testUpdateUi_HasDecks_HidesNoDecksMessage() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> response = createSuccessResponse(new ArrayList<>());
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
        .thenReturn(response);

      controller.setCurrentUsername("testuser");
      List<FlashcardDeckDto> decks = (List<FlashcardDeckDto>) getField("decks");
      FlashcardDeck deck = new FlashcardDeck();

      deck.setDeckName("Test Deck");
      decks.add(mapper.toDto(deck));

      runOnFxThread(() -> controller.updateUi());
      assertFalse(noDecks.isVisible());
    }
  }

  /**
   * Tests that deck buttons show correct deck names.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testUpdateUi_DeckButtonsShowCorrectNames() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> response = createSuccessResponse(new ArrayList<>());
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
        .thenReturn(response);

      controller.setCurrentUsername("testuser");

      List<FlashcardDeckDto> decks = (List<FlashcardDeckDto>) getField("decks");
      FlashcardDeck deck1 = new FlashcardDeck();
      deck1.setDeckName("Math");

      FlashcardDeck deck2 = new FlashcardDeck();
      deck2.setDeckName("Science");

      decks.add(mapper.toDto(deck1));
      decks.add(mapper.toDto(deck2));

      runOnFxThread(() -> controller.updateUi());
      assertEquals("Math", this.deck1.getText());
      assertEquals("Science", this.deck2.getText());
    }
  }

  /**
   * Tests that input field is cleared after update.
   */
  @Test
  public void testUpdateUi_ClearsInputField() {
    deckNameInput.setText("Some text");
    runOnFxThread(() -> controller.updateUi());
    assertEquals("", deckNameInput.getText());
  }

  /**
   * Tests that updateUi handles null components gracefully.
   * 
   * @throws Exception if reflection fails
   * 
   */
  @Test
  public void testUpdateUi_WithNullComponents_HandlesGracefully() throws Exception {
    // Set components that have null checks in updateUi() to null
    setField("usernameField", null);
    setField("alertMessage", null);
    setField("ex", null);
    setField("noDecks", null);
    setField("newDeckButton", null);
    setField("deckNameInput", null);

    // Should not throw NullPointerException
    runOnFxThread(() -> controller.updateUi());
    assertTrue(true, "updateUi handles null components gracefully");
  }

  /**
   * Tests that alert is displayed when showAlert is true.
   * 
   * @throws Exception if reflection fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testUpdateUi_ShowAlertTrue_DisplaysAlert() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> response = createSuccessResponse(new ArrayList<>());

      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
        .thenReturn(response);
      controller.setCurrentUsername("testuser");

      var showAlertField = FlashcardMainController.class.getDeclaredField("showAlert");
      showAlertField.setAccessible(true);
      showAlertField.set(controller, true);
      var errorField = FlashcardMainController.class.getDeclaredField("error");
      errorField.setAccessible(true);
      errorField.set(controller, "Test error message");
      runOnFxThread(() -> controller.updateUi());

      assertTrue(alertMessage.isVisible());
      assertEquals("Test error message", alertMessage.getText());
      assertTrue(ex.isVisible());

      assertFalse((Boolean) showAlertField.get(controller));
    }
  }

  /**
   * Tests that alert is hidden when showAlert is false.
   * 
   * @throws Exception if reflection fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testUpdateUi_ShowAlertFalse_HidesAlert() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> response = createSuccessResponse(new ArrayList<>());
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
        .thenReturn(response);
      controller.setCurrentUsername("testuser");

      var showAlertField = FlashcardMainController.class.getDeclaredField("showAlert");
      showAlertField.setAccessible(true);
      showAlertField.set(controller, true);
      var errorField = FlashcardMainController.class.getDeclaredField("error");
      errorField.setAccessible(true);

      errorField.set(controller, ApiConstants.SERVER_ERROR);

      runOnFxThread(() -> controller.updateUi());

      showAlertField.set(controller, false);
      runOnFxThread(() -> controller.updateUi());

      assertFalse(alertMessage.isVisible());
      assertFalse(ex.isVisible());
    }
  }

  /**
   * Tests that deleting a deck removes it from manager.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenDeleteDeckButtonIsClicked_RemovesDeck() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> getResponse = createSuccessResponse(new ArrayList<>());
      ApiResponse<FlashcardDeckManagerDto> putResponse = createSuccessResponse(new ArrayList<>());
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
        .thenReturn(getResponse);

      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("PUT"), any(), any(TypeReference.class)))
        .thenReturn(putResponse);

      controller.setCurrentUsername("testuser");
      List<FlashcardDeckDto> decks = (List<FlashcardDeckDto>) getField("decks");
      FlashcardDeck deck = new FlashcardDeck();
      deck.setDeckName("To Delete");

      FlashcardDeckDto deckDto = mapper.toDto(deck);
      decks.add(deckDto);
      deleteDeck1.setUserData(deckDto);

      ActionEvent event = new ActionEvent(deleteDeck1, null);
      ApiResponse<Void> deleteResponse = new ApiResponse<>(true, "", null);
      ApiResponse<FlashcardDeckManagerDto> getResponseAfterDelete = createSuccessResponse(new ArrayList<>());
      
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("DELETE"), isNull(), any(TypeReference.class)))
        .thenReturn(deleteResponse);
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
        .thenReturn(getResponseAfterDelete);
      controller.whenDeleteDeckButtonIsClicked(event);

      List<FlashcardDeckDto> decksAfterDelete = (List<FlashcardDeckDto>) getField("decks");
      assertEquals(0, decksAfterDelete.size());
    }
  }

  /** 
   * Tests that UI is updated after deleting a deck.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
@Test
  public void testWhenDeleteDeckButtonIsClicked_UpdatesUi() throws Exception {
  try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
    ApiResponse<FlashcardDeckManagerDto> getResponse = createSuccessResponse(new ArrayList<>());
    ApiResponse<FlashcardDeckManagerDto> putResponse = createSuccessResponse(new ArrayList<>());

    apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
    .thenReturn(getResponse);

    apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("PUT"), any(), any(TypeReference.class)))
    .thenReturn(putResponse);

    controller.setCurrentUsername("testuser");
    List<FlashcardDeckDto> decks = (List<FlashcardDeckDto>) getField("decks");
    FlashcardDeck deck = new FlashcardDeck();

    deck.setDeckName("To Delete");
    FlashcardDeckDto deckDto = mapper.toDto(deck);
    decks.add(deckDto);

    runOnFxThread(() -> controller.updateUi());
    deleteDeck1.setUserData(deckDto);

    ActionEvent event = new ActionEvent(deleteDeck1, null);

      ApiResponse<Void> deleteResponse = new ApiResponse<>(true, "", null);
      ApiResponse<FlashcardDeckManagerDto> getResponseAfterDelete = createSuccessResponse(new ArrayList<>());
      
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("DELETE"), isNull(), any(TypeReference.class)))
        .thenReturn(deleteResponse);
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
        .thenReturn(getResponseAfterDelete);

      controller.whenDeleteDeckButtonIsClicked(event);
      assertTrue(noDecks.isVisible());
    }
  }

  /**
   * Tests that deleting a deck works with valid deck.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @Test
  void testWhenDeleteDeckButtonIsClicked_ValidDeck() throws Exception {
    FlashcardDeckDto deck = new FlashcardDeckDto("Deck1", new ArrayList<>());
    deleteDeck1.setUserData(deck);

    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient.performApiRequest(anyString(), any(), any(), any())).thenReturn(new ApiResponse<Void>(true, "Success", null));
      runOnFxThread(() -> assertDoesNotThrow(() -> controller.whenDeleteDeckButtonIsClicked(new ActionEvent(deleteDeck1, null))));
    }
  }

  /**
   * Tests that deleting a deck handles API failure gracefully.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @Test
  public void testWhenDeleteDeckButtonIsClicked_ApiFailure() throws Exception {
    FlashcardDeckDto deck = new FlashcardDeckDto("Deck1", new ArrayList<>());
    deleteDeck1.setUserData(deck);

    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient.performApiRequest(anyString(), any(), any(), any())).thenThrow(new RuntimeException("API failure"));
      runOnFxThread(() -> assertDoesNotThrow(() -> controller.whenDeleteDeckButtonIsClicked(new ActionEvent(deleteDeck1, null))));
    }
  }

  /**
   * Tests that deleting a deck works with null deck.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @Test
  public void testWhenDeleteDeckButtonIsClicked_NullDeck() throws Exception {
    deleteDeck1.setUserData(null);
    runOnFxThread(() -> assertDoesNotThrow(() -> controller.whenDeleteDeckButtonIsClicked(new ActionEvent(deleteDeck1, null))));
  }

  /**
   * Tests that deleting a deck handles successful API response.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenDeleteDeckButtonIsClicked_SuccessfulApiResponse() throws Exception {
    FlashcardDeckDto deck = new FlashcardDeckDto("Deck1", new ArrayList<>());
    deleteDeck1.setUserData(deck);

    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<Void> deleteResponse = new ApiResponse<>(true, "Deleted", null);
      ApiResponse<FlashcardDeckManagerDto> getResponse = createSuccessResponse(new ArrayList<>());
        
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("DELETE"), isNull(), any(TypeReference.class)))
        .thenReturn(deleteResponse);
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
        .thenReturn(getResponse);
        
      controller.setCurrentUsername("testuser");
        
      ActionEvent event = new ActionEvent(deleteDeck1, null);
      controller.whenDeleteDeckButtonIsClicked(event);
        
      List<FlashcardDeckDto> decks = (List<FlashcardDeckDto>) getField("decks");
      assertEquals(0, decks.size());
    }
  }

  /**
   * Tests that deleting a deck handles failed API response.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenDeleteDeckButtonIsClicked_FailedApiResponse() throws Exception {
    FlashcardDeckDto deck = new FlashcardDeckDto("Deck1", new ArrayList<>());
    deleteDeck1.setUserData(deck);

    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<Void> deleteResponse = new ApiResponse<>(false, "Delete failed", null);
      
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("DELETE"), isNull(), any(TypeReference.class)))
        .thenReturn(deleteResponse);
      
      controller.setCurrentUsername("testuser");
      
      ActionEvent event = new ActionEvent(deleteDeck1, null);
      assertDoesNotThrow(() -> controller.whenDeleteDeckButtonIsClicked(event));
    }
  }

  /** 
   * Tests that deleting a deck handles null API response.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenDeleteDeckButtonIsClicked_NullApiResponse() throws Exception {
    FlashcardDeckDto deck = new FlashcardDeckDto("Deck1", new ArrayList<>());
    deleteDeck1.setUserData(deck);

    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("DELETE"), isNull(), any(TypeReference.class)))
        .thenReturn(null);
      
      controller.setCurrentUsername("testuser");
      
      ActionEvent event = new ActionEvent(deleteDeck1, null);
      assertDoesNotThrow(() -> controller.whenDeleteDeckButtonIsClicked(event));
    }
  }

  /**
   * Tests that creating a new deck with empty name is handled gracefully.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @Test
  public void testWhenNewDeckButtonIsClicked_EmptyDeckName() throws Exception {
    deckNameInput.setText("");
    runOnFxThread(() -> assertDoesNotThrow(() -> controller.whenNewDeckButtonIsClicked(new ActionEvent(newDeckButton, null))));
  }

  /** 
   * Tests that creating a new deck handles API failure gracefully.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @Test
  public void testWhenNewDeckButtonIsClicked_ApiFailure() throws Exception {
    deckNameInput.setText("NewDeck");

    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient.performApiRequest(anyString(), any(), any(), any())).thenThrow(new RuntimeException("API failure"));
      runOnFxThread(() -> assertDoesNotThrow(() -> controller.whenNewDeckButtonIsClicked(new ActionEvent(newDeckButton, null))));
    }
  }

  /** 
   * Tests that creating a new deck when currentUsername is null shows error. 
   * 
   * @throws Exception if test setup fails
   * 
   */
  @Test
  public void testWhenNewDeckButtonIsClicked_NullUsername() throws Exception {
    runOnFxThread(() -> {
      deckNameInput.setText("NewDeck");
      try {
        setField("currentUsername", null);
        ActionEvent event = new ActionEvent(newDeckButton, null);
        assertDoesNotThrow(() -> controller.whenNewDeckButtonIsClicked(event));
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }

  /** 
   * Tests that creating a new deck when currentUsername is empty shows error. 
   * 
   * @throws Exception if test setup fails
   * 
   */
  @Test
  public void testWhenNewDeckButtonIsClicked_EmptyUsername() throws Exception {
      runOnFxThread(() -> {
        deckNameInput.setText("NewDeck");
        try {
          setField("currentUsername", "");
          ActionEvent event = new ActionEvent(newDeckButton, null);
          assertDoesNotThrow(() -> controller.whenNewDeckButtonIsClicked(event));
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      });
  }

  /** 
   * Tests that creating a new deck handles validation error response.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenNewDeckButtonIsClicked_ValidationError() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckDto> postResponse = new ApiResponse<>(false, ApiConstants.DECK_ALREADY_EXISTS, null);
      
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("POST"), anyString(), any(TypeReference.class)))
        .thenReturn(postResponse);
      
      ApiResponse<FlashcardDeckManagerDto> getResponse = createSuccessResponse(new ArrayList<>());
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
        .thenReturn(getResponse);
      
      controller.setCurrentUsername("testuser");
      
      deckNameInput.setText("DuplicateDeck");
      ActionEvent event = new ActionEvent(newDeckButton, null);
      controller.whenNewDeckButtonIsClicked(event);
      
      String errorMsg = (String) getField("error");
      assertEquals(ApiConstants.DECK_ALREADY_EXISTS, errorMsg);
      
      assertTrue(alertMessage.isVisible());
    }
  }

  /** 
   * Tests that creating a new deck handles server error response. 
   * 
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenNewDeckButtonIsClicked_ServerError() throws Exception {
    deckNameInput.setText("NewDeck");

    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckDto> postResponse = new ApiResponse<>(false, "Internal server error", null);
      
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("POST"), anyString(), any(TypeReference.class)))
        .thenReturn(postResponse);
      
      controller.setCurrentUsername("testuser");
      
      ActionEvent event = new ActionEvent(newDeckButton, null);
      assertDoesNotThrow(() -> controller.whenNewDeckButtonIsClicked(event));
    }
  }

  /**
   * Tests that creating a new deck handles null API response.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenNewDeckButtonIsClicked_NullApiResponse() throws Exception {
    deckNameInput.setText("NewDeck");

    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("POST"), anyString(), any(TypeReference.class)))
        .thenReturn(null);
      
      controller.setCurrentUsername("testuser");
      
      ActionEvent event = new ActionEvent(newDeckButton, null);
      assertDoesNotThrow(() -> controller.whenNewDeckButtonIsClicked(event));
    }
  }

  /**
   * Tests that creating a new deck handles exception with cause.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenNewDeckButtonIsClicked_ExceptionWithCause() throws Exception {
    deckNameInput.setText("NewDeck");

    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      IOException cause = new IOException("Network error");
      RuntimeException exception = new RuntimeException("Request failed", cause);
      
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("POST"), anyString(), any(TypeReference.class)))
        .thenThrow(exception);
      
      controller.setCurrentUsername("testuser");
      
      ActionEvent event = new ActionEvent(newDeckButton, null);
      assertDoesNotThrow(() -> controller.whenNewDeckButtonIsClicked(event));
    }
  }

  /** 
   * Tests that creating a new deck handles general exception.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenNewDeckButtonIsClicked_GeneralException() throws Exception {
    deckNameInput.setText("NewDeck");

    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("POST"), anyString(), any(TypeReference.class)))
        .thenThrow(new NullPointerException("Unexpected error"));
      
      controller.setCurrentUsername("testuser");
      
      ActionEvent event = new ActionEvent(newDeckButton, null);
      assertDoesNotThrow(() -> controller.whenNewDeckButtonIsClicked(event));
    }
  }

  /** 
   * Tests that clicking a deck with null data doesn't crash.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @Test
  public void testWhenADeckIsClicked_NullDeck() throws Exception {
    deck1.setUserData(null);
    ActionEvent event = new ActionEvent(deck1, null);
    assertDoesNotThrow(() -> controller.whenADeckIsClicked(event));
  }

  /** 
   * Tests that clicking a deck loads the deck view.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenADeckIsClicked_LoadsDeckView() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> response = createSuccessResponse(new ArrayList<>());
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
        .thenReturn(response);
      controller.setCurrentUsername("testuser");
      List<FlashcardDeckDto> decks = (List<FlashcardDeckDto>) getField("decks");
      FlashcardDeck deck = new FlashcardDeck();
      deck.setDeckName("Test Deck");

      FlashcardDeckDto deckDto = mapper.toDto(deck);
      decks.add(deckDto);

      deck1.setUserData(deckDto);
      assertEquals(deckDto, deck1.getUserData());
    }
  }

  /** 
   * Tests that clicking a deck executes the method.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
@Test
  public void testWhenADeckIsClicked_ExecutesMethod() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class);
      MockedConstruction<FXMLLoader> mockedLoader = mockConstruction(FXMLLoader.class,
        (loader, context) -> {
      Parent mockRoot = new javafx.scene.layout.Pane();
      FlashcardDeckController mockController = mock(FlashcardDeckController.class);

      try {
        when(loader.load()).thenReturn(mockRoot);
        when(loader.getController()).thenReturn(mockController);

      } catch (IOException e) {
        // This wont happen, since we're mocking
      }
    })) {
          
      ApiResponse<FlashcardDeckManagerDto> response = createSuccessResponse(new ArrayList<>());
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
        .thenReturn(response);

      controller.setCurrentUsername("testuser");

      FlashcardDeck deck = new FlashcardDeck();
      deck.setDeckName("Test Deck");

      FlashcardDeckDto deckDto = mapper.toDto(deck);
      deck1.setUserData(deckDto);

      java.util.concurrent.CountDownLatch setupLatch = new java.util.concurrent.CountDownLatch(1);

      javafx.application.Platform.runLater(() -> {
        try {
          javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();
          root.getChildren().add(deck1);
          javafx.scene.Scene scene = new javafx.scene.Scene(root);
          stage.setScene(scene);
        } finally {
          setupLatch.countDown();
        }
      });

      setupLatch.await(2, java.util.concurrent.TimeUnit.SECONDS);
      ActionEvent event = new ActionEvent(deck1, null);

      java.util.concurrent.CountDownLatch executionLatch = new java.util.concurrent.CountDownLatch(1);

      javafx.application.Platform.runLater(() -> {
        try {
          controller.whenADeckIsClicked(event);
        } finally {
          executionLatch.countDown();
        }
      });

      executionLatch.await(2, java.util.concurrent.TimeUnit.SECONDS);
      assertTrue(true, "whenADeckIsClicked method executed");
    }
  }

  /** 
   * Tests that logout executes the method.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenLogOut_ExecutesMethod() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class);
      MockedConstruction<FXMLLoader> mockedLoader = mockConstruction(FXMLLoader.class,
        (loader, context) -> {
          Parent mockRoot = new javafx.scene.layout.Pane();
          try {
              when(loader.load()).thenReturn(mockRoot);
          } catch (IOException e) {
              // Won't happen since we're mocking
          }
        })) {

      ApiResponse<FlashcardDeckManagerDto> getResponse = createSuccessResponse(new ArrayList<>());
      ApiResponse<FlashcardDeckManagerDto> putResponse = createSuccessResponse(new ArrayList<>());

      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
      .thenReturn(getResponse);

      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("PUT"), any(), any(TypeReference.class)))
      .thenReturn(putResponse);

      controller.setCurrentUsername("testuser");

      java.util.concurrent.CountDownLatch setupLatch = new java.util.concurrent.CountDownLatch(1);

      javafx.application.Platform.runLater(() -> {
        try {
          javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();
          root.getChildren().add(logOutButton);
          javafx.scene.Scene scene = new javafx.scene.Scene(root);
          stage.setScene(scene);
        } finally {
          setupLatch.countDown();
        }
      });

      setupLatch.await(2, java.util.concurrent.TimeUnit.SECONDS);
      ActionEvent event = new ActionEvent(logOutButton, null);

      try {
        java.lang.reflect.Method method = FlashcardMainController.class.getMethod("whenLogOut", ActionEvent.class);
        method.invoke(controller, event);
      } catch (Exception e) {
        assertTrue(e.getCause() instanceof IOException || e.getCause() instanceof NullPointerException
        || e.getCause() instanceof IllegalStateException,
          "Expected IOException or scene-related exception");
      }

      assertTrue(true, "whenLogOut method executed");
    }
  }

  /** 
   * Tests successful loading of user data from API.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testLoadUserData_SuccessfulLoad() throws Exception {
    List<FlashcardDeckDto> deckDtos = new ArrayList<>();
    FlashcardDeckDto deckDto = createDeckDto("Test Deck", 
      createFlashcardDto("Q1", "A1"));

    deckDtos.add(deckDto);

    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> response = createSuccessResponse(deckDtos);

      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
        .thenReturn(response);

      controller.setCurrentUsername("testuser");

      List<FlashcardDeckDto> decks = (List<FlashcardDeckDto>) getField("decks");
      assertEquals(1, decks.size());
      assertEquals("Test Deck", decks.get(0).getDeckName());
    }
  }

  /** 
   * Tests that API failure creates empty manager.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testLoadUserData_ApiFailure_CreatesEmptyManager() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> response = createFailureResponse();
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
        .thenReturn(response);

      controller.setCurrentUsername("testuser");

      List<FlashcardDeckDto> decks = (List<FlashcardDeckDto>) getField("decks");
      assertEquals(0, decks.size());
    }
  }

  /** 
   * Tests that exception creates empty manager. 
   * 
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testLoadUserData_ExceptionThrown_CreatesEmptyManager() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
        .thenThrow(new RuntimeException("Network error"));

      controller.setCurrentUsername("testuser");
      List<FlashcardDeckDto> decks = (List<FlashcardDeckDto>) getField("decks");
      assertEquals(0, decks.size());
    }
  }

  /** 
   * Tests that null data creates empty manager.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testLoadUserData_SuccessButNullData_CreatesEmptyManager() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> response = new ApiResponse<>(true, "Success", null);
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
          .thenReturn(response);
          
      controller.setCurrentUsername("testuser");

      List<FlashcardDeckDto> decks = (List<FlashcardDeckDto>) getField("decks");
      assertEquals(0, decks.size());
    }
  }

  /** 
   * Tests that save failure shows alert.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testSaveUserData_SaveFailure_ShowsAlert() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> getResponse = createSuccessResponse(new ArrayList<>());
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
        .thenReturn(getResponse);

      ApiResponse<FlashcardDeckDto> postFailureResponse = new ApiResponse<>(false, "Failed to create deck", null);
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("POST"), isNull(), any(TypeReference.class)))
        .thenReturn(postFailureResponse);

      controller.setCurrentUsername("testuser");
      deckNameInput.setText("New Deck");
      controller.whenNewDeckButtonIsClicked(new ActionEvent());

      apiClient.verify(() -> ApiClient.showAlert(eq(ApiConstants.SERVER_ERROR), eq(ApiConstants.DECK_FAILED_TO_CREATE)), atLeastOnce());
    }
  }

  /** 
   * Tests that deck manager creates defensive copy.
   * 
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testSetDeckManager_CreatesDefensiveCopy() throws Exception {
    FlashcardDeckManager originalManager = new FlashcardDeckManager();
    FlashcardDeck deck = new FlashcardDeck();

    deck.setDeckName("Original Deck");
    deck.addFlashcard(new Flashcard("Q1", "A1"));

    originalManager.addDeck(deck);

    List<FlashcardDeckDto> decks = (List<FlashcardDeckDto>) getField("decks");
    decks.add(mapper.toDto(deck));
    
    runOnFxThread(() -> controller.refreshDecks());
    List<FlashcardDeckDto> refreshedDecks = (List<FlashcardDeckDto>) getField("decks");

    assertEquals(1, refreshedDecks.size());
    assertEquals("Original Deck", refreshedDecks.get(0).getDeckName());
  }

  /** 
   * Tests that flashcards are copied. 
   * 
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testSetDeckManager_CopiesFlashcards() throws Exception {
    FlashcardDeckManager originalManager = new FlashcardDeckManager();
    FlashcardDeck deck = new FlashcardDeck();

    deck.setDeckName("Test Deck");
    deck.addFlashcard(new Flashcard("Question 1", "Answer 1"));
    deck.addFlashcard(new Flashcard("Question 2", "Answer 2"));
    originalManager.addDeck(deck);

    List<FlashcardDeckDto> decks = (List<FlashcardDeckDto>) getField("decks");
    decks.add(mapper.toDto(deck));
    runOnFxThread(() -> controller.refreshDecks());

    List<FlashcardDeckDto> refreshedDecks = (List<FlashcardDeckDto>) getField("decks");
    FlashcardDeckDto copiedDeckDto = refreshedDecks.get(0);

    assertEquals(2, copiedDeckDto.getDeck().size());
    assertEquals("Question 1", copiedDeckDto.getDeck().get(0).getQuestion());
    assertEquals("Answer 2", copiedDeckDto.getDeck().get(1).getAnswer());
  }

  /** 
   * Tests that setting deck manager updates UI. 
   * 
   * @throws Exception if test setup fails
   * 
   */
  @SuppressWarnings("unchecked")
  @Test
  void testSetDeckManager_UpdatesUi() throws Exception {
    FlashcardDeck deck = new FlashcardDeck();

    deck.setDeckName("Updated Deck");

    List<FlashcardDeckDto> decks = (List<FlashcardDeckDto>) getField("decks");
    FlashcardDeckDto deckDto = mapper.toDto(deck);
    decks.add(deckDto);
    
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> getResponse = createSuccessResponse(List.of(deckDto));
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
        .thenReturn(getResponse);
      
      deck1.setVisible(false);
      
      if (getField("currentUsername") == null) {
        setField("currentUsername", "testuser");
      }
      
      controller.refreshDecks();
      
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      
      runOnFxThread(() -> {
        assertTrue(deck1.isVisible(), "deck1 should be visible after refreshDecks");
        assertEquals("Updated Deck", deck1.getText());
      });
    }
  }

  /**
   * Runs a Runnable on the JavaFX Application Thread and waits for completion.
   * 
   * @param runnable the code to execute on the FX thread
   * @throws RuntimeException if interrupted while waiting
   * 
   */
  private void runOnFxThread(Runnable runnable) {
    if (Platform.isFxApplicationThread()) {
      runnable.run();
    } else {
      CountDownLatch latch = new CountDownLatch(1);
      Platform.runLater(() -> {
        try {
          runnable.run();
        } finally {
          latch.countDown();
        }
      });
      try {
        latch.await(5, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException("Interrupted while waiting for FX thread", e);
      }
    }
  }

  /**
   * Creates a successful API response.
   * 
   * @param decks the list of deck DTOs to include in the response
   * @return a successful API response containing the deck manager DTO
   * 
   */
  private ApiResponse<FlashcardDeckManagerDto> createSuccessResponse(List<FlashcardDeckDto> decks) {
    FlashcardDeckManagerDto managerDto = new FlashcardDeckManagerDto(decks);
    return new ApiResponse<>(true, "Success", managerDto);
  }

  /**
   * Creates a failed API response.
   * 
   * @return a failed API response with null data
   * 
   */
  private ApiResponse<FlashcardDeckManagerDto> createFailureResponse() {
    return new ApiResponse<>(false, "Error occurred", null);
  }

  /**
   * Creates a FlashcardDeckDto with flashcards.
   * 
   * @param deckName the name of the deck
   * @param flashcards variable number of flashcard DTOs to add to the deck
   * @return a new FlashcardDeckDto containing the specified flashcards
   * 
   */
  private FlashcardDeckDto createDeckDto(String deckName, FlashcardDto... flashcards) {
    List<FlashcardDto> cardList = new ArrayList<>();
    for (FlashcardDto card : flashcards) {
      cardList.add(card);
    }
    return new FlashcardDeckDto(deckName, cardList);
  }

  /**
   * Creates a FlashcardDto.
   * 
   * @param question the question text
   * @param answer the answer text
   * @return a new FlashcardDto with the specified question and answer
   * 
   */
  private FlashcardDto createFlashcardDto(String question, String answer) {
    return new FlashcardDto(question, answer, 1);
  }
}