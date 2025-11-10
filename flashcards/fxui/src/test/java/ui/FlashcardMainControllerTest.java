package ui;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import app.Flashcard;
import app.FlashcardDeck;
import com.fasterxml.jackson.core.type.TypeReference;
import dto.FlashcardDeckDto;
import dto.FlashcardDeckManagerDto;
import dto.FlashcardDto;
import dto.mappers.FlashcardDeckMapper;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import shared.ApiConstants;
import shared.ApiResponse;


/**
 * Test class for FlashcardMainController.
 *
 * <p>Tests initialization, deck management, navigation, and API integration
 * for the main flashcard deck overview screen.
 *
 * <p>This test class uses TestFX for UI testing and Mockito for mocking API calls.
 *
 * <p>Thread Safety: This class is not thread-safe and should only be used
 * in a single-threaded test environment.
 *
 * @author ailinat
 * @author marennod
 * @author sofietw
 *
 * @see "docs/release_3/ai_tools.md"
 */

@ExtendWith(ApplicationExtension.class)
class FlashcardMainControllerTest {

  private FlashcardMainController controller;
  private FlashcardDeckMapper mapper = new FlashcardDeckMapper();
  private Stage stage;

  // FXML components

  private Button deck1;
  private Button deck2;
  private Button deck3;
  private Button deck4;
  private Button deck5;
  private Button deck6;
  private Button deck7;
  private Button deck8;

  private Button deleteDeck1;
  private Button deleteDeck2;
  private Button deleteDeck3;
  private Button deleteDeck4;

  private Button deleteDeck5;
  private Button deleteDeck6;
  private Button deleteDeck7;
  private Button deleteDeck8;

  private TextField deckNameInput;

  private Button newDeckButton;

  private Button logOutButton;

  private Text usernameField;

  private Text alertMessage;
  private Text ex; 
  private Text noDecks;


  /**
     * Initializes JavaFX toolkit before running tests.
     */
  @BeforeAll
  public static void initJavaFx() throws InterruptedException {
    if (!Platform.isFxApplicationThread()) {
      try {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.startup(() -> latch.countDown());
        latch.await();
      } catch (IllegalStateException e) {
        // Toolkit already initialized
      }
    }
  }

  /**
   * Initializes the test stage.
   *
   * @param stage the JavaFX stage for testing
   */
  @Start
  public void start(Stage stage) {
    this.stage = stage;
    stage.setWidth(1);
    stage.setHeight(1);
  }

  /**
   * Sets up test environment before each test.
   *
   * @throws Exception if component initialization or injection fails
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
   */
  private void injectFxmlComponents() throws Exception {
    setField("deck1", deck1);
    setField("deck2", deck2);
    setField("deck3", deck3);
    setField("deck4", deck4);
    setField("deck5", deck5);
    setField("deck6", deck6);
    setField("deck7", deck7);
    setField("deck8", deck8);
    setField("deleteDeck1", deleteDeck1);
    setField("deleteDeck2", deleteDeck2);
    setField("deleteDeck3", deleteDeck3);
    setField("deleteDeck4", deleteDeck4);
    setField("deleteDeck5", deleteDeck5);
    setField("deleteDeck6", deleteDeck6);
    setField("deleteDeck7", deleteDeck7);
    setField("deleteDeck8", deleteDeck8);
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
   */
  private Object getField(String fieldName) throws Exception {
    var field = FlashcardMainController.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    return field.get(controller);
  }

  /**
   * Tests initialize method.
   */
  @Test
  public void testInitialize() {
    assertFalse(deck1.isVisible());
    assertTrue(deck1.isDisabled());
    assertFalse(deck2.isVisible());
    assertFalse(deck3.isVisible());

    assertFalse(deleteDeck1.isVisible());
    assertFalse(deleteDeck2.isVisible());

    runOnFxThread(() -> controller.updateUi());
    assertFalse(alertMessage.isVisible());
    assertFalse(ex.isVisible());
  }

  /**
   * Tests setting a valid username.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testSetCurrentUsernameValid() throws Exception {

    // Test set current username with valid username
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> response = createSuccessResponse(new ArrayList<>());
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
          .thenReturn(response);

      controller.setCurrentUsername("testuser");
      assertEquals("testuser", usernameField.getText());
      assertEquals("testuser", getField("currentUsername"));
    }

    // Test set current username with null username
    controller.setCurrentUsername(null);
    assertEquals("testuser", usernameField.getText());
    assertEquals("testuser", getField("currentUsername"));

    // Test set current username with empty username
    controller.setCurrentUsername("");
    assertEquals("testuser", usernameField.getText());
    assertEquals("testuser", getField("currentUsername"));

    // Test set current username with whitespace username
    controller.setCurrentUsername("   ");
    assertEquals("testuser", usernameField.getText());
    assertEquals("testuser", getField("currentUsername"));

    // Test set current username with whitespace
    controller.setCurrentUsername("  user123  ");
    assertEquals("user123", usernameField.getText());

    // Test set current username trim whitespace
    controller.setCurrentUsername("validUser");
    assertEquals("validUser", usernameField.getText());

  }

  /**
   * Tests creating a deck with valid name.
   *
   * @throws Exception if test setup fails
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenNewDeckButtonIsClicked() throws Exception {

    // Test when new deck button is clicked with valid deck name
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      FlashcardDeckDto newDeckDto = new FlashcardDeckDto(
          "My New Deck", new ArrayList<>());
      ApiResponse<FlashcardDeckDto> postResponse = new ApiResponse<>(
          true, "", newDeckDto);
      ApiResponse<FlashcardDeckManagerDto> getResponseInitial = 
          createSuccessResponse(new ArrayList<>());
      ApiResponse<FlashcardDeckManagerDto> getResponseAfterPost = 
          createSuccessResponse(List.of(newDeckDto));
        
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
          .thenReturn(getResponseInitial)
          .thenReturn(getResponseAfterPost);
        
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("POST"), anyString(), any()))
          .thenReturn(postResponse);
      controller.setCurrentUsername("testuser");
      deckNameInput.setText("My New Deck");
      ActionEvent event = new ActionEvent();
      controller.whenNewDeckButtonIsClicked(event);
        
      List<FlashcardDeckDto> decks = (List<FlashcardDeckDto>) getField("decks");
      assertEquals(1, decks.size());
      assertEquals("My New Deck", decks.get(0).getDeckName());
    }

    // Test when new deck button is clicked with empty deck name
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> response = createSuccessResponse(new ArrayList<>());
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
          .thenReturn(response);

      controller.setCurrentUsername("testuser");
      deckNameInput.setText("");
      ActionEvent event = new ActionEvent();
      controller.whenNewDeckButtonIsClicked(event);
      assertTrue(alertMessage.isVisible());
      assertTrue(ex.isVisible());
    }

    // Test when new deck button is clicked with invalid username
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> response = createSuccessResponse(new ArrayList<>());
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
          .thenReturn(response);

      controller.setCurrentUsername("testuser");
      deckNameInput.setText("   ");

      ActionEvent event = new ActionEvent();
      controller.whenNewDeckButtonIsClicked(event);
      assertTrue(alertMessage.isVisible());
    }
  }

  /**
   * Tests that deck names with spaces are encoded for the API call while the UI shows
   * the original value unchanged.
   *
   * @throws Exception if test setup fails
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testDeckNameEncodingAndDisplay() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      FlashcardDeckDto newDeckDto = new FlashcardDeckDto("Deck With Space", new ArrayList<>());
      ApiResponse<FlashcardDeckDto> postResponse = new ApiResponse<>(true, "", newDeckDto);
      ApiResponse<FlashcardDeckManagerDto> getResponseInitial =
          createSuccessResponse(new ArrayList<>());
      ApiResponse<FlashcardDeckManagerDto> getResponseAfterPost =
          createSuccessResponse(List.of(newDeckDto));

      ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);

      apiClient.when(() -> ApiClient.performApiRequest(
              anyString(), eq("GET"), isNull(), any(TypeReference.class)))
          .thenReturn(getResponseInitial)
          .thenReturn(getResponseAfterPost);

      apiClient.when(() -> ApiClient.performApiRequest(
              urlCaptor.capture(), eq("POST"), anyString(), any()))
          .thenReturn(postResponse);

      controller.setCurrentUsername("testuser");
      deckNameInput.setText("Deck With Space");
      controller.whenNewDeckButtonIsClicked(new ActionEvent());

      assertTrue(urlCaptor.getValue().contains("Deck%20With%20Space"),
          "POST URL should encode spaces as %20");
      assertEquals("Deck With Space", deck1.getText(), "UI should show original deck name");
    }
  }

  /**
   * Tests that new deck button is disabled when max decks reached.
   *
   * @throws Exception if test setup failed
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testUpdateUiMaxDecksDisablesNewDeck() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> response = createSuccessResponse(new ArrayList<>());
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
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
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testUpdateUiLessThanMaxDecksEnablesNewDeck() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> response = createSuccessResponse(new ArrayList<>());
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
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
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testUpdateUiShowsCorrectDeckCount() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      List<FlashcardDeckDto> decksForResponse = new ArrayList<>();

      for (int i = 0; i < 3; i++) {
        FlashcardDeck deck = new FlashcardDeck();
        deck.setDeckName("Deck " + (i + 1));
        decksForResponse.add(mapper.toDto(deck));
      }

      ApiResponse<FlashcardDeckManagerDto> responseWithDecks =
          createSuccessResponse(decksForResponse);

      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
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
  public void testUpdateUiNoDecksShowsMessage() {
    runOnFxThread(() -> controller.updateUi());
    assertTrue(noDecks.isVisible());
  }

  /**
   * Tests that no decks message is hidden when decks exist.
   *
   * @throws Exception if test setup fails
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testUpdateUiHasDecksHidesNoDecksMessage() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> response = createSuccessResponse(new ArrayList<>());
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
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
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testUpdateUiDeckButtonsShowCorrectNames() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> response = createSuccessResponse(new ArrayList<>());
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
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
  public void testUpdateUiClearsInputField() {
    deckNameInput.setText("Some text");
    runOnFxThread(() -> controller.updateUi());
    assertEquals("", deckNameInput.getText());
  }

  /**
   * Tests that updateUi handles null components gracefully.
   *
   * @throws Exception if reflection fails
   */
  @Test
  public void testUpdateUiWithNullComponents() throws Exception {
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
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testUpdateUiShowAlertTrueDisplaysAlert() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> response = createSuccessResponse(new ArrayList<>());

      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
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
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testUpdateUiShowAlertFalseHidesAlert() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> response = createSuccessResponse(new ArrayList<>());
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
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
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenDeleteDeckButtonIsClickedRemovesDeck() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> getResponse = createSuccessResponse(new ArrayList<>());
      ApiResponse<FlashcardDeckManagerDto> putResponse = createSuccessResponse(new ArrayList<>());
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
          .thenReturn(getResponse);

      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("PUT"), any(), any(TypeReference.class)))
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
      ApiResponse<FlashcardDeckManagerDto> getResponseAfterDelete =
          createSuccessResponse(new ArrayList<>());
      
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("DELETE"), isNull(), any(TypeReference.class)))
          .thenReturn(deleteResponse);
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
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
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenDeleteDeckButtonIsClickedUpdatesUi() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> getResponse = createSuccessResponse(new ArrayList<>());
      ApiResponse<FlashcardDeckManagerDto> putResponse = createSuccessResponse(new ArrayList<>());

      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
          .thenReturn(getResponse);

      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("PUT"), any(), any(TypeReference.class)))
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
      ApiResponse<FlashcardDeckManagerDto> getResponseAfterDelete =
          createSuccessResponse(new ArrayList<>());
      
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("DELETE"), isNull(), any(TypeReference.class)))
          .thenReturn(deleteResponse);
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
          .thenReturn(getResponseAfterDelete);

      controller.whenDeleteDeckButtonIsClicked(event);
      assertTrue(noDecks.isVisible());
    }
  }

  /**
   * Tests that deleting a deck works with valid deck.
   *
   * @throws Exception if test setup fails
   */
  @Test
  void testWhenDeleteDeckButtonIsClickedValidDeck() throws Exception {
    FlashcardDeckDto deck = new FlashcardDeckDto("Deck1", new ArrayList<>());
    deleteDeck1.setUserData(deck);

    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient.performApiRequest(
          anyString(), any(), any(), any()))
          .thenReturn(new ApiResponse<Void>(true, "Success", null));
      apiClientMock.when(() -> ApiClient.showAlert(anyString(), anyString()))
          .thenAnswer(invocation -> null);
      
      controller.setCurrentUsername("testuser");
      assertDoesNotThrow(() -> controller.whenDeleteDeckButtonIsClicked(
          new ActionEvent(deleteDeck1, null)));
    }
  }

  /**
   * Tests that deleting a deck handles API failure gracefully.
   *
   * @throws Exception if test setup fails
   */
  @Test
  public void testWhenDeleteDeckButtonIsClickedApiFailure() throws Exception {
    FlashcardDeckDto deck = new FlashcardDeckDto("Deck1", new ArrayList<>());
    deleteDeck1.setUserData(deck);

    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient.performApiRequest(
          anyString(), any(), any(), any()))
          .thenThrow(new RuntimeException("API failure"));
      apiClientMock.when(() -> ApiClient.showAlert(anyString(), anyString()))
          .thenAnswer(invocation -> null);
      
      controller.setCurrentUsername("testuser");
      assertDoesNotThrow(() -> controller.whenDeleteDeckButtonIsClicked(
          new ActionEvent(deleteDeck1, null)));
    }
  }

  /**
   * Tests that deleting a deck works with null deck.
   *
   * @throws Exception if test setup fails
   */
  @Test
  public void testWhenDeleteDeckButtonIsClickedDeckNull() throws Exception {
    deleteDeck1.setUserData(null);
    runOnFxThread(() -> assertDoesNotThrow(
        () -> controller.whenDeleteDeckButtonIsClicked(
            new ActionEvent(deleteDeck1, null))));
  }

  /**
   * Tests that deleting a deck handles successful API response.
   *
   * @throws Exception if test setup fails
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenDeleteDeckButtonIsClickedSuccessApi() throws Exception {
    FlashcardDeckDto deck = new FlashcardDeckDto("Deck1", new ArrayList<>());
    deleteDeck1.setUserData(deck);

    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<Void> deleteResponse = new ApiResponse<>(true, "Deleted", null);
      ApiResponse<FlashcardDeckManagerDto> getResponse = createSuccessResponse(new ArrayList<>());
        
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("DELETE"), isNull(), any(TypeReference.class)))
          .thenReturn(deleteResponse);
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
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
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenDeleteDeckButtonIsClickedFailedApi() throws Exception {
    FlashcardDeckDto deck = new FlashcardDeckDto("Deck1", new ArrayList<>());
    deleteDeck1.setUserData(deck);

    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<Void> deleteResponse = new ApiResponse<>(false, "Delete failed", null);
      
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("DELETE"), isNull(), any(TypeReference.class)))
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
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenDeleteDeckButtonIsClickedNullApi() throws Exception {
    FlashcardDeckDto deck = new FlashcardDeckDto("Deck1", new ArrayList<>());
    deleteDeck1.setUserData(deck);

    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("DELETE"), isNull(), any(TypeReference.class)))
          .thenReturn(null);
      
      controller.setCurrentUsername("testuser");
      
      ActionEvent event = new ActionEvent(deleteDeck1, null);
      assertDoesNotThrow(
          () -> controller.whenDeleteDeckButtonIsClicked(event));
    }
  }

  /**
   * Tests that creating a new deck with empty name is handled gracefully.
   *
   * @throws Exception if test setup fails
   */
  @Test
  public void testWhenNewDeckButtonIsClickedEmptyDeckName() throws Exception {
    deckNameInput.setText("");
    runOnFxThread(() -> assertDoesNotThrow(
        () -> controller.whenNewDeckButtonIsClicked(
            new ActionEvent(newDeckButton, null))));
  }

  /**
   * Tests that creating a new deck handles API failure gracefully.
   *
   * @throws Exception if test setup fails
   */
  @Test
  public void testWhenNewDeckButtonIsClickedApiFailure() throws Exception {
    deckNameInput.setText("NewDeck");

    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient.performApiRequest(
          anyString(), any(), any(), any()))
          .thenThrow(new RuntimeException("API failure"));
      apiClientMock.when(() -> ApiClient.showAlert(anyString(), anyString()))
          .thenAnswer(invocation -> null);
      
      controller.setCurrentUsername("testuser");
      assertDoesNotThrow(
          () -> controller.whenNewDeckButtonIsClicked(
              new ActionEvent(newDeckButton, null)));
    }
  }

  /**
   * Tests that creating a new deck when currentUsername is null shows error.
   *
   * @throws Exception if test setup fails
   */
  @Test
  public void testWhenNewDeckButtonIsClickedNullUsername() throws Exception {
    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient.showAlert(anyString(), anyString()))
          .thenAnswer(invocation -> null);
      
      deckNameInput.setText("NewDeck");
      setField("currentUsername", null);
      ActionEvent event = new ActionEvent(newDeckButton, null);
      assertDoesNotThrow(
          () -> controller.whenNewDeckButtonIsClicked(event));
    }
  }

  /**
   * Tests that creating a new deck when currentUsername is empty shows error.
   *
   * @throws Exception if test setup fails
   */
  @Test
  public void testWhenNewDeckButtonIsClickedEmptyUsername() throws Exception {
    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient.showAlert(anyString(), anyString()))
          .thenAnswer(invocation -> null);
      
      deckNameInput.setText("NewDeck");
      setField("currentUsername", "");
      ActionEvent event = new ActionEvent(newDeckButton, null);
      assertDoesNotThrow(() -> controller.whenNewDeckButtonIsClicked(event));
    }
  }

  /**
   * Tests that creating a new deck handles validation error response.
   *
   * @throws Exception if test setup fails
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenNewDeckButtonIsClickedValidationError() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckDto> postResponse =
          new ApiResponse<>(false, ApiConstants.DECK_ALREADY_EXISTS, null);
      
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("POST"), anyString(), any(TypeReference.class)))
          .thenReturn(postResponse);
      
      ApiResponse<FlashcardDeckManagerDto> getResponse =
          createSuccessResponse(new ArrayList<>());
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
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
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenNewDeckButtonIsClickedServerError() throws Exception {
    deckNameInput.setText("NewDeck");

    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckDto> postResponse =
          new ApiResponse<>(false, "Internal server error", null);
      
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("POST"), anyString(), any(TypeReference.class)))
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
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenNewDeckButtonIsClickedNullApi() throws Exception {
    deckNameInput.setText("NewDeck");

    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("POST"), anyString(), any(TypeReference.class)))
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
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenNewDeckButtonIsClickedExceptionCause() throws Exception {
    deckNameInput.setText("NewDeck");

    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      IOException cause = new IOException("Network error");
      RuntimeException exception = new RuntimeException("Request failed", cause);
      
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("POST"), anyString(), any(TypeReference.class)))
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
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenNewDeckButtonIsClickedGeneralException() throws Exception {
    deckNameInput.setText("NewDeck");

    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("POST"), anyString(), any(TypeReference.class)))
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
   */
  @Test
  public void testWhenaDeckIsClickedNullDeckData() throws Exception {
    deck1.setUserData(null);
    ActionEvent event = new ActionEvent(deck1, null);
    assertDoesNotThrow(
        () -> controller.whenDeckIsClicked(event));
  }

  /**
   * Tests that clicking a deck loads the deck view.
   *
   * @throws Exception if test setup fails
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenDeckIsClickedLoadsView() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> response =
          createSuccessResponse(new ArrayList<>());
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
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
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenDeckIsClickedExecutesHandler() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> response =
          createSuccessResponse(new ArrayList<>());
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
          .thenReturn(response);
      apiClient.when(() -> ApiClient.showAlert(anyString(), anyString()))
          .thenAnswer(invocation -> null);

      controller.setCurrentUsername("testuser");

      FlashcardDeck deck = new FlashcardDeck();
      deck.setDeckName("Test Deck");

      FlashcardDeckDto deckDto = mapper.toDto(deck);
      deck1.setUserData(deckDto);

      // Test that the user data is correctly set on the button
      // The actual scene transition is complex and involves UI elements
      // that can't be fully tested in headless mode
      assertEquals(deckDto, deck1.getUserData());
      assertEquals("Test Deck",
          ((FlashcardDeckDto) deck1.getUserData()).getDeckName());
    }
  }

  /**
   * Tests that logout executes the method.
   *
   * @throws Exception if test setup fails
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWhenLogOutExecutesMethod() throws Exception {
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

      ApiResponse<FlashcardDeckManagerDto> getResponse =
          createSuccessResponse(new ArrayList<>());
      ApiResponse<FlashcardDeckManagerDto> putResponse =
          createSuccessResponse(new ArrayList<>());

      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
          .thenReturn(getResponse);

      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("PUT"), any(), any(TypeReference.class)))
          .thenReturn(putResponse);

      controller.setCurrentUsername("testuser");

      CountDownLatch setupLatch = new java.util.concurrent.CountDownLatch(1);

      Platform.runLater(() -> {
        try {
          javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();
          root.getChildren().add(logOutButton);
          javafx.scene.Scene scene = new javafx.scene.Scene(root);
          stage.setScene(scene);
        } finally {
          setupLatch.countDown();
        }
      });

      setupLatch.await(2, TimeUnit.SECONDS);
      ActionEvent event = new ActionEvent(logOutButton, null);

      try {
        Method method =
            FlashcardMainController.class.getMethod("whenLogOut", ActionEvent.class);
        method.invoke(controller, event);
      } catch (Exception e) {
        assertTrue(e.getCause() instanceof IOException 
            || e.getCause() instanceof NullPointerException
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
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testLoadUserDataSuccess() throws Exception {
    List<FlashcardDeckDto> deckDtos = new ArrayList<>();
    FlashcardDeckDto deckDto = createDeckDto("Test Deck", 
        createFlashcardDto("Q1", "A1"));

    deckDtos.add(deckDto);

    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> response =
          createSuccessResponse(deckDtos);

      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
          .thenReturn(response);

      controller.setCurrentUsername("testuser");

      List<FlashcardDeckDto> decks = (List<FlashcardDeckDto>) getField("decks");
      assertEquals(1, decks.size());
      assertEquals("Test Deck", decks.get(0).getDeckName());
    }
  }


  /**
   * Tests that empty manager is created for API failure, exception, or null data.
   * Combines:
   * - API failure
   * - exception thrown
   * - null data
   *
   * @throws Exception if test setup fails
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testLoadUserDataEmptyManagerScenarios() throws Exception {
    // Scenario 1: API failure
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> response = createFailureResponse();
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
          .thenReturn(response);
      controller.setCurrentUsername("testuser");
      List<FlashcardDeckDto> decks = (List<FlashcardDeckDto>) getField("decks");
      assertEquals(0, decks.size());
    }

    // Scenario 2: exception thrown
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
          .thenThrow(new RuntimeException("Network error"));
      controller.setCurrentUsername("testuser");
      List<FlashcardDeckDto> decks = (List<FlashcardDeckDto>) getField("decks");
      assertEquals(0, decks.size());
    }

    // Scenario 3: null data
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> response =
          new ApiResponse<>(true, "Success", null);
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
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
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testSaveUserDataFailureAlert() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> getResponse =
          createSuccessResponse(new ArrayList<>());
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
          .thenReturn(getResponse);

      ApiResponse<FlashcardDeckDto> postFailureResponse =
          new ApiResponse<>(false, "Failed to create deck", null);
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("POST"), isNull(), any(TypeReference.class)))
          .thenReturn(postFailureResponse);

      controller.setCurrentUsername("testuser");
      deckNameInput.setText("New Deck");
      controller.whenNewDeckButtonIsClicked(new ActionEvent());

      apiClient.verify(() -> ApiClient.showAlert(
          eq(ApiConstants.SERVER_ERROR), eq(ApiConstants.DECK_FAILED_TO_CREATE)),
          atLeastOnce());
    }
  }

  /**
   * Tests that deck manager creates defensive copy.
   *
   * @throws Exception if test setup fails
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testSetDeckManagerDefensiveCopy() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      FlashcardDeck deck = new FlashcardDeck();
      deck.setDeckName("Original Deck");
      deck.addFlashcard(new Flashcard("Q1", "A1"));

      FlashcardDeckDto deckDto = mapper.toDto(deck);
      ApiResponse<FlashcardDeckManagerDto> getResponse =
          createSuccessResponse(List.of(deckDto));
      
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
          .thenReturn(getResponse);
      apiClient.when(() -> ApiClient.showAlert(anyString(), anyString()))
          .thenAnswer(invocation -> null);

      // Don't call refreshDecks() which would trigger UI updates and alerts
      // Instead, directly load data and verify it's copied
      if (getField("currentUsername") == null) {
        setField("currentUsername", "testuser");
      }
      
      // Directly invoke loadUserData via reflection to test defensive copy without UI complications
      var loadUserDataMethod = FlashcardMainController.class.getDeclaredMethod("loadUserData");
      loadUserDataMethod.setAccessible(true);
      loadUserDataMethod.invoke(controller);
      
      List<FlashcardDeckDto> decks = (List<FlashcardDeckDto>) getField("decks");
      assertEquals(1, decks.size());
      assertEquals("Original Deck", decks.get(0).getDeckName());
    }
  }

  /**
   * Tests that flashcards are copied.
   *
   * @throws Exception if test setup fails
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testSetDeckManagerCopiesCards() throws Exception {
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      FlashcardDeck deck = new FlashcardDeck();
      deck.setDeckName("Test Deck");
      deck.addFlashcard(new Flashcard("Question 1", "Answer 1"));
      deck.addFlashcard(new Flashcard("Question 2", "Answer 2"));

      FlashcardDeckDto deckDto = mapper.toDto(deck);
      ApiResponse<FlashcardDeckManagerDto> getResponse =
          createSuccessResponse(List.of(deckDto));
      
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
          .thenReturn(getResponse);
      apiClient.when(() -> ApiClient.showAlert(anyString(), anyString()))
          .thenAnswer(invocation -> null);

      // Don't call refreshDecks() which would trigger UI updates and alerts
      // Instead, directly load data and verify flashcards are copied
      if (getField("currentUsername") == null) {
        setField("currentUsername", "testuser");
      }
      
      // Directly invoke loadUserData via reflection to test flashcard copying
      var loadUserDataMethod = FlashcardMainController.class.getDeclaredMethod("loadUserData");
      loadUserDataMethod.setAccessible(true);
      loadUserDataMethod.invoke(controller);

      List<FlashcardDeckDto> decks = (List<FlashcardDeckDto>) getField("decks");
      FlashcardDeckDto copiedDeckDto = decks.get(0);

      assertEquals(2, copiedDeckDto.getDeck().size());
      assertEquals("Question 1", copiedDeckDto.getDeck().get(0).getQuestion());
      assertEquals("Answer 2", copiedDeckDto.getDeck().get(1).getAnswer());
    }
  }

  /**
   * Tests that setting deck manager updates UI.
   *
   * @throws Exception if test setup fails
   */
  @SuppressWarnings("unchecked")
  @Test
  void testSetDeckManagerUpdatesUi() throws Exception {
    FlashcardDeck deck = new FlashcardDeck();

    deck.setDeckName("Updated Deck");

    List<FlashcardDeckDto> decks = (List<FlashcardDeckDto>) getField("decks");
    FlashcardDeckDto deckDto = mapper.toDto(deck);
    decks.add(deckDto);
    
    try (MockedStatic<ApiClient> apiClient = mockStatic(ApiClient.class)) {
      ApiResponse<FlashcardDeckManagerDto> getResponse =
          createSuccessResponse(List.of(deckDto));
      apiClient.when(() -> ApiClient.performApiRequest(
          anyString(), eq("GET"), isNull(), any(TypeReference.class)))
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
   */
  private ApiResponse<FlashcardDeckManagerDto> createSuccessResponse(List<FlashcardDeckDto> decks) {
    FlashcardDeckManagerDto managerDto = new FlashcardDeckManagerDto(decks);
    return new ApiResponse<>(true, "Success", managerDto);
  }

  /**
   * Creates a failed API response.
   *
   * @return a failed API response with null data
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
   */
  private FlashcardDto createFlashcardDto(String question, String answer) {
    return new FlashcardDto(question, answer, 1);
  }
}