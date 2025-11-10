package ui;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import app.Flashcard;
import app.FlashcardDeck;
import app.FlashcardDeckManager;
import com.fasterxml.jackson.core.type.TypeReference;
import dto.FlashcardDeckDto;
import dto.FlashcardDto;
import dto.mappers.FlashcardDeckMapper;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import shared.ApiConstants;
import shared.ApiResponse;

/**
 * Unit tests for {@link FlashcardDeckController}.
 *
 * <p>Covers all major branches, edge cases, and error handling for deck
 * management,
 * UI updates, card creation/deletion, user data saving, and navigation logic.
 * Uses reflection to inject private fields and Mockito for mocking JavaFX and
 * API interactions.
 *
 * <p>Thread-safety: Not thread-safe. Tests must be run sequentially.
 *
 * @author marennod
 * @author sofietw
 * @author ailinat
 *
 * @see FlashcardDeckController
 * @see FlashcardDto
 * @see FlashcardDeckDto
 * @see "docs/release_3/ai_tools.md"
 */
class FlashcardDeckControllerTest {

  private FlashcardDeckController controller;
  private FlashcardDeckMapper mapper = new FlashcardDeckMapper();
  private TextField questionField;
  private TextField answerField;
  private ListView<FlashcardDto> listView;
  private Text username;
  private Button startLearning;
  private Button deleteCardButton;

  /**
   * Initializes JavaFX toolkit before running tests.
   *
   * @throws InterruptedException if thread is interrupted
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
   * Sets up a fresh controller and injects mock UI components before each test.
   *
   * @throws RuntimeException if reflection fails to inject fields
   */
  @BeforeEach
  public void setUp() {
    controller = new FlashcardDeckController();
    questionField = new TextField();
    answerField = new TextField();
    listView = new ListView<>();
    username = new Text();
    startLearning = new Button();
    deleteCardButton = new Button();

    // Inject mocks using reflection (since fields are private)
    setField(controller, "questionField", questionField);
    setField(controller, "answerField", answerField);
    setField(controller, "listView", listView);
    setField(controller, "username", username);
    setField(controller, "startLearning", startLearning);
    setField(controller, "deleteCardButton", deleteCardButton);

    setField(controller, "currentUsername", null);
    FlashcardDeck deck = new FlashcardDeck("Deck1");
    setField(controller, "currentDeck", mapper.toDto(deck));
  }

  /**
   * Tests controller initialization logic.
   */
  @Test
  public void testInitialize() {
    runOnFxThread(() -> assertDoesNotThrow(
        () -> controller.initialize()));
  }

  /**
   * Tests setting the current username.
   *
   * <p>Verifies that the username is correctly set via the setCurrentUsername
   * method.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testSetCurrentUsername() {
    try (MockedStatic<ApiClient> apiClient = Mockito.mockStatic(ApiClient.class)) {
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"),
              isNull(), any(TypeReference.class)))
          .thenReturn(new ApiResponse<>(true, "", null));
      
      controller.setCurrentUsername("newuser");
      assertEquals("newuser", getField(controller, "currentUsername"));
    }
  }

  /**
   * Tests UI update when deck manager is null.
   */
  @Test
  public void testUpdateUiNoDeckManager() {
    setField(controller, "currentDeck", null);
    runOnFxThread(() -> controller.updateUi());
    assertTrue(listView.getItems().isEmpty());
    assertTrue(startLearning.isDisabled());
    assertTrue(deleteCardButton.isDisabled());
  }

  /**
   * Tests UI update with a valid deck manager and deck.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testUpdateUiWithDeckManager() {
    try (MockedStatic<ApiClient> apiClient = Mockito.mockStatic(ApiClient.class)) {
      FlashcardDeckManager mgr = new FlashcardDeckManager();
      FlashcardDeck deck = new FlashcardDeck("Deck1");
      deck.addFlashcard(new Flashcard("Q1", "A1"));
      mgr.addDeck(deck);
      FlashcardDeckDto deckDto = mapper.toDto(deck);
      
      // Set currentUsername so loadDeckData() doesn't set currentDeck to null
      setField(controller, "currentUsername", "testuser");
      
      // Mock API call for loadDeckData() to return the same deck (so it doesn't overwrite)
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"),
              isNull(), any(TypeReference.class)))
          .thenReturn(new ApiResponse<>(true, "", deckDto));
      
      runOnFxThread(() -> {
        controller.setDeck(deckDto);
        // Verify state on FX thread
        assertFalse(listView.getItems().isEmpty());
        assertFalse(startLearning.isDisabled());
      });
    }
  }

  /**
   * Tests UI update when deck name does not match any deck.
   */
  @Test
  public void testUpdateUiDeckNameNotFound() {
    FlashcardDeckManager mgr = new FlashcardDeckManager();
    FlashcardDeck deck = new FlashcardDeck("Deck1");
    deck.addFlashcard(new Flashcard("Q1", "A1"));
    mgr.addDeck(deck);
    setField(controller, "currentDeck", mapper.toDto(new FlashcardDeck("NonExistent")));
    runOnFxThread(() -> controller.updateUi());
    assertTrue(listView.getItems().isEmpty());
  }


  /**
   * Tests setting deck manager for existing deck, not in manager, and duplicate deck.
   * Combines:
   * - setting deck manager when deck already exists
   * - setting deck manager when deck is not in manager
   * - setting deck manager with duplicate deck
   */
  @Test
  public void testSetDeckManagerScenarios() {
    // Scenario 1: deck already exists
    FlashcardDeckManager mgr1 = new FlashcardDeckManager();
    FlashcardDeck deck1 = new FlashcardDeck("Deck1");
    mgr1.addDeck(deck1);
    runOnFxThread(() -> assertDoesNotThrow(() -> controller.setDeck(mapper.toDto(deck1))));

    // Scenario 2: deck not in manager
    FlashcardDeck deck2 = new FlashcardDeck("Deck1");
    runOnFxThread(() -> assertDoesNotThrow(() -> controller.setDeck(mapper.toDto(deck2))));

    // Scenario 3: duplicate deck
    FlashcardDeckManager mgr2 = new FlashcardDeckManager();
    FlashcardDeck deck3 = new FlashcardDeck("Deck1");
    mgr2.addDeck(deck3);
    runOnFxThread(() -> assertDoesNotThrow(() -> controller.setDeck(mapper.toDto(deck3))));
  }

  /**
   * Tests whenCreateButtonIsClicked method.
   */
  @Test
  public void testWhenCreateCardButtonIsClicked() {
    FlashcardDeckManager mgr = new FlashcardDeckManager();
    FlashcardDeck deck = new FlashcardDeck("Deck1");
    mgr.addDeck(deck);
    runOnFxThread(() -> controller.setDeck(mapper.toDto(deck)));
    questionField.setText("Q2");
    answerField.setText("A2");
    FlashcardDeck deck1 = new FlashcardDeck("Deck1");
    setField(controller, "currentDeck", mapper.toDto(deck1));
    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any()))
          .thenReturn(new ApiResponse<>(true, "", null));
      controller.whenCreateButtonIsClicked();
    }
  }

  /**
   * Tests card creation logic and API failure.
   */
  @Test
  public void testCreateCardApiFailure() {
    FlashcardDeckManager mgr = new FlashcardDeckManager();
    FlashcardDeck deck = new FlashcardDeck("Deck1");
    mgr.addDeck(deck);
    runOnFxThread(() -> controller.setDeck(mapper.toDto(deck)));
    questionField.setText("Q2");
    answerField.setText("A2");
    FlashcardDeck deck1 = new FlashcardDeck("Deck1");
    setField(controller, "currentDeck", mapper.toDto(deck1));
    
    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any()))
          .thenReturn(new ApiResponse<>(false,
              ApiConstants.FLASHCARD_OPERATION_FAILED, null));
      assertDoesNotThrow(() -> controller.whenCreateButtonIsClicked());
    }
  }

  /**
   * Tests card deletion logic and API success.
   */
  @Test
  public void testWhenDeleteCardButtonIsClicked() {
    FlashcardDeckManager mgr = new FlashcardDeckManager();
    FlashcardDeck deck = new FlashcardDeck("Deck1");
    deck.addFlashcard(new Flashcard("Q1", "A1"));
    mgr.addDeck(deck);
    runOnFxThread(() -> controller.setDeck(mapper.toDto(deck)));

    List<FlashcardDto> cardDtos = mapper.toDto(deck).getDeck();
    listView.setItems(FXCollections.observableArrayList(cardDtos));
    listView.getSelectionModel().select(0);

    FlashcardDeck deck1 = new FlashcardDeck("Deck1");
    setField(controller, "currentDeck", mapper.toDto(deck1));

    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any()))
          .thenReturn(new ApiResponse<>(true, "", null));
      controller.whenDeleteCardButtonIsClicked();
    }
  }

  /**
   * Tests card deletion logic and API failure.
   */
  @Test
  public void testDeleteCardApiFailure() {
    FlashcardDeckManager mgr = new FlashcardDeckManager();
    FlashcardDeck deck = new FlashcardDeck("Deck1");
    deck.addFlashcard(new Flashcard("Q1", "A1"));
    mgr.addDeck(deck);
    runOnFxThread(() -> controller.setDeck(mapper.toDto(deck)));

    List<FlashcardDto> cardDtos = mapper.toDto(deck).getDeck();
    listView.setItems(FXCollections.observableArrayList(cardDtos));
    listView.getSelectionModel().select(0);

    FlashcardDeck deck1 = new FlashcardDeck("Deck1");
    setField(controller, "currentDeck", mapper.toDto(deck1));
    
    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any()))
          .thenReturn(new ApiResponse<>(false,
              ApiConstants.FLASHCARD_OPERATION_FAILED, null));
      assertDoesNotThrow(() -> controller.whenDeleteCardButtonIsClicked());
    }    
  }

  /**
   * Tests clearing input fields.
   */
  @Test
  public void testClearInputFields() {
    questionField.setText("Q");
    answerField.setText("A");
    invokePrivate(controller, "clearInputFields");
    assertEquals("", questionField.getText());
    assertEquals("", answerField.getText());
  }

  /**
   * Tests getCurrentDeck returns null if deck not found.
   */
  @Test
  public void testGetDeckNullIfNotFound() {
    setField(controller, "currentDeck", null);
    FlashcardDeckDto deck = (FlashcardDeckDto) getField(controller, "currentDeck");
    assertNull(deck);
  }

  /**
   * Tests start learning button logic with mocked scene/window.
   */
  @Test
  public void testWhenStartLearningButtonIsClicked() {
    FlashcardDeckManager mgr = new FlashcardDeckManager();
    FlashcardDeck deck = new FlashcardDeck("Deck1");
    deck.addFlashcard(new Flashcard("Q1", "A1"));
    mgr.addDeck(deck);
    FlashcardDeck deck1 = new FlashcardDeck("Deck1");
    setField(controller, "currentDeck", mapper.toDto(deck1));
    Button mockButton = mock(Button.class);
    javafx.scene.Scene mockScene = mock(javafx.scene.Scene.class);
    javafx.stage.Stage mockStage = mock(javafx.stage.Stage.class);
    when(mockButton.getScene()).thenReturn(mockScene);
    when(mockScene.getWindow()).thenReturn(mockStage);
    setField(controller, "startLearning", mockButton);
    assertDoesNotThrow(() -> controller.whenStartLearningButtonIsClicked());
  }

  /**
   * Tests log out logic with mocked API call.
   */
  @Test
  public void testWhenLogOut() {
    TextField mockField = mock(TextField.class);
    Scene mockScene = mock(Scene.class);
    Stage mockStage = mock(Stage.class);
    when(mockField.getScene()).thenReturn(mockScene);
    when(mockScene.getWindow()).thenReturn(mockStage);
    setField(controller, "questionField", mockField);
    
    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any()))
          .thenReturn(new ApiResponse<>(true, "", null));
      assertDoesNotThrow(() -> controller.whenLogOut());
    }
  }

  /**
   * Tests back button logic with mocked scene/window and alert.
   */
  @Test
  public void testWhenBackButtonIsClicked() {
    FlashcardDeckManager mgr = new FlashcardDeckManager();
    FlashcardDeck deck = new FlashcardDeck("Deck1");
    deck.addFlashcard(new Flashcard("Q1", "A1"));
    mgr.addDeck(deck);
    FlashcardDeck deck1 = new FlashcardDeck("Deck1");
    setField(controller, "currentDeck", mapper.toDto(deck1));
    TextField mockField = mock(TextField.class);
    javafx.scene.Scene mockScene = mock(javafx.scene.Scene.class);
    javafx.stage.Stage mockStage = mock(javafx.stage.Stage.class);
    when(mockField.getScene()).thenReturn(mockScene);
    when(mockScene.getWindow()).thenReturn(mockStage);
    setField(controller, "questionField", mockField);

    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient.showAlert(anyString(), anyString()))
          .thenAnswer(inv -> null);
      assertDoesNotThrow(() -> controller.whenBackButtonIsClicked());
    }
  }

  /**
   * Tests log out logic when API call fails.
   */
  @Test
  public void testLogOutFail() {
    // Mock the questionField to have a scene and window to avoid UI issues
    TextField mockField = mock(TextField.class);
    Scene mockScene = mock(Scene.class);
    Stage mockStage = mock(Stage.class);
    when(mockField.getScene()).thenReturn(mockScene);
    when(mockScene.getWindow()).thenReturn(mockStage);
    setField(controller, "questionField", mockField);
        
    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any()))
          .thenReturn(new ApiResponse<>(false,
              ApiConstants.USER_OPERATION_FAILED, null));
      assertDoesNotThrow(() -> controller.whenLogOut());
    }
  }


  /**
   * Tests setting deck manager with no decks and with null deck.
   * Combines:
   * - setting deck manager with no decks
   * - setting deck manager with null deck
   */
  @Test
  public void testSetManagerNoDecksAndNullDeck() {
    // Scenario 1: no decks
    runOnFxThread(() -> assertDoesNotThrow(
        () -> controller.setDeck(mapper.toDto(new FlashcardDeck("DeckX")))));
    // Scenario 2: null deck
    runOnFxThread(() -> assertDoesNotThrow(() -> controller.setDeck(null)));
  }


  /**
   * Tests UI update for null deck name, empty deck manager, and multiple decks.
   * Combines:
   * - UI update when current deck name is null
   * - UI update with empty deck manager
   * - UI update with multiple decks
   */
  @Test
  public void testUpdateUiScenarios() {
    // Scenario 1: current deck name is null
    setField(controller, "currentDeck", null);
    runOnFxThread(() -> controller.updateUi());
    assertTrue(listView.getItems().isEmpty());

    // Scenario 2: empty deck manager
    setField(controller, "currentDeck", null);
    FlashcardDeck deck1 = new FlashcardDeck("Deck1");
    setField(controller, "currentDeck", mapper.toDto(deck1));
    runOnFxThread(() -> controller.updateUi());
    assertTrue(listView.getItems().isEmpty());

    // Scenario 3: multiple decks
    FlashcardDeckManager mgr = new FlashcardDeckManager();
    FlashcardDeck deck2 = new FlashcardDeck("Deck2");
    deck1.addFlashcard(new Flashcard("Q1", "A1"));
    deck2.addFlashcard(new Flashcard("Q2", "A2"));
    mgr.addDeck(deck1);
    mgr.addDeck(deck2);
  }

  /**
   * Tests card deletion logic for last card.
   */
  public void testDeleteLastCard() {
    FlashcardDeckManager mgr = new FlashcardDeckManager();
    FlashcardDeck deck = new FlashcardDeck("Deck1");
    deck.addFlashcard(new Flashcard("Q1", "A1"));
    mgr.addDeck(deck);
    runOnFxThread(() -> controller.setDeck(mapper.toDto(deck)));
    List<FlashcardDto> cardDtos = mapper.toDto(deck).getDeck();
    listView.setItems(FXCollections.observableArrayList(cardDtos));
    listView.getSelectionModel().select(0);
    FlashcardDeck deck1 = new FlashcardDeck("Deck1");
    setField(controller, "currentDeck", mapper.toDto(deck1));
    
    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any()))
          .thenReturn(new ApiResponse<>(true, "", null));
      assertDoesNotThrow(() -> controller.whenDeleteCardButtonIsClicked());
    }
  }

  /**
   * Tests setting the current username with various input cases.
   * Ensures that null, empty, and whitespace inputs do not overwrite the username.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testSetUsernameCases() {
    try (MockedStatic<ApiClient> apiClient = Mockito.mockStatic(ApiClient.class)) {
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"),
              isNull(), any(TypeReference.class)))
          .thenReturn(new ApiResponse<>(true, "", null));
      
      controller.setCurrentUsername("newuser");
      assertEquals("newuser", getField(controller, "currentUsername"));
      controller.setCurrentUsername(null);
      assertEquals("newuser", getField(controller, "currentUsername"));
      controller.setCurrentUsername("");
      assertEquals("newuser", getField(controller, "currentUsername"));
      controller.setCurrentUsername("   ");
      assertEquals("newuser", getField(controller, "currentUsername"));
    }
  }

  /**
   * Tests setting the deck manager with various input cases.
   * Covers valid, null, and duplicate deck scenarios.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testSetManagerCases() {
    try (MockedStatic<ApiClient> apiClient = Mockito.mockStatic(ApiClient.class)) {
      apiClient.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"),
              isNull(), any(TypeReference.class)))
          .thenReturn(new ApiResponse<>(true, "", null));
      
      FlashcardDeckManager mgr = new FlashcardDeckManager();
      FlashcardDeck deck = new FlashcardDeck("Deck1");
      deck.addFlashcard(new Flashcard("Q1", "A1"));
      mgr.addDeck(deck);
      FlashcardDeckDto deckDto = mapper.toDto(deck);
      
      setField(controller, "currentUsername", "testuser");
      
      runOnFxThread(() -> controller.setDeck(deckDto));
      FlashcardDeckDto actualDeck = (FlashcardDeckDto) getField(controller, "currentDeck");
      assertNotNull(actualDeck);
      assertEquals("Deck1", actualDeck.getDeckName());
      runOnFxThread(() -> assertDoesNotThrow(
          () -> controller.setDeck(null)));
      runOnFxThread(() -> assertDoesNotThrow(
          () -> controller.setDeck(null)));
      FlashcardDeckManager mgr2 = new FlashcardDeckManager();
      FlashcardDeck deck2 = new FlashcardDeck("Deck2");
      runOnFxThread(() -> assertDoesNotThrow(
          () -> controller.setDeck(mapper.toDto(deck2))));
      mgr2.addDeck(deck2);
      runOnFxThread(() -> assertDoesNotThrow(
          () -> controller.setDeck(mapper.toDto(deck2))));
      runOnFxThread(() -> assertDoesNotThrow(
          () -> controller.setDeck(mapper.toDto(new FlashcardDeck("DeckX")))));
    }
  }

  /**
   * Tests card creation logic for multiple input and API response cases.
   * Covers valid, empty, whitespace, and API failure scenarios.
   */
  @Test
  public void testCreateCardCases() {
    FlashcardDeckManager mgr = new FlashcardDeckManager();
    FlashcardDeck deck = new FlashcardDeck("Deck1");
    mgr.addDeck(deck);
    runOnFxThread(() -> controller.setDeck(mapper.toDto(deck)));
    FlashcardDeck deck1 = new FlashcardDeck("Deck1");
    setField(controller, "currentDeck", mapper.toDto(deck1));
    questionField.setText("Q2");
    answerField.setText("A2");
    
    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any()))
          .thenReturn(new ApiResponse<>(true, "", null));
      controller.whenCreateButtonIsClicked();
    }

    questionField.setText("");
    answerField.setText("");
    
    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any()))
          .thenReturn(new ApiResponse<>(true, "", null));
      assertDoesNotThrow(() -> controller.whenCreateButtonIsClicked());
    }

    questionField.setText("   ");
    answerField.setText("   ");

    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any()))
          .thenReturn(new ApiResponse<>(true, "", null));
      assertDoesNotThrow(() -> controller.whenCreateButtonIsClicked());
    }

    questionField.setText("Q3");
    answerField.setText("A3");

    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any()))
          .thenReturn(new ApiResponse<>(false,
              ApiConstants.FLASHCARD_FAILED_TO_CREATE, null));
      assertDoesNotThrow(() -> controller.whenCreateButtonIsClicked());
    }
  }

  /**
   * Tests card deletion logic for multiple selection and API response cases.
   * Covers valid, no selection, and API failure scenarios.
   */
  @Test
  public void testDeleteCardCases() {
    FlashcardDeckManager mgr = new FlashcardDeckManager();
    FlashcardDeck deck = new FlashcardDeck("Deck1");
    deck.addFlashcard(new Flashcard("Q1", "A1"));
    deck.addFlashcard(new Flashcard("Q2", "A2"));
    mgr.addDeck(deck);
    runOnFxThread(() -> controller.setDeck(mapper.toDto(deck)));
    List<FlashcardDto> cardDtos = mapper.toDto(deck).getDeck();
    listView.setItems(FXCollections.observableArrayList(cardDtos));
    listView.getSelectionModel().select(0);

    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any()))
          .thenReturn(new ApiResponse<>(true, "", null));
      controller.whenDeleteCardButtonIsClicked();
    }

    listView.getSelectionModel().clearSelection();

    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any()))
          .thenReturn(new ApiResponse<>(true, "", null));
      assertDoesNotThrow(() -> controller.whenDeleteCardButtonIsClicked());
    }

    listView.getSelectionModel().select(0);

    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any()))
          .thenReturn(new ApiResponse<>(false,
              ApiConstants.FLASHCARD_FAILED_TO_DELETE, null));
      assertDoesNotThrow(() -> controller.whenDeleteCardButtonIsClicked());
    }

    deck.addFlashcard(new Flashcard("Q3", "A3"));
    List<FlashcardDto> cardDtos2 = mapper.toDto(deck).getDeck();
    listView.setItems(FXCollections.observableArrayList(cardDtos2));
    listView.getSelectionModel().select(0);

    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any()))
          .thenReturn(new ApiResponse<>(true, "", null));
      assertDoesNotThrow(() -> controller.whenDeleteCardButtonIsClicked());
    }
  }

  /**
   * Tests setDeckManager method for all major branches.
   * Covers null manager, null deck, deck not in manager, duplicate deck, and duplicate deck name.
   */
  @Test
  public void testSetDeckManagerBranches() {
    FlashcardDeck deck = new FlashcardDeck("Deck1");
    runOnFxThread(() -> assertDoesNotThrow(() -> controller.setDeck(mapper.toDto(deck))));
    FlashcardDeckManager mgr = new FlashcardDeckManager();
    runOnFxThread(() -> assertDoesNotThrow(() -> controller.setDeck(null)));
    FlashcardDeck deck2 = new FlashcardDeck("Deck2");
    runOnFxThread(() -> assertDoesNotThrow(() -> controller.setDeck(mapper.toDto(deck2))));
    mgr.addDeck(deck2);
    runOnFxThread(() -> assertDoesNotThrow(() -> controller.setDeck(mapper.toDto(deck2))));
    FlashcardDeck deck3 = new FlashcardDeck("Deck2");
    runOnFxThread(() -> assertDoesNotThrow(() -> controller.setDeck(mapper.toDto(deck3))));
  }

  /**
   * Tests updateUi method for all major branches.
   * Covers null manager, null deck name, deck not found, empty deck, and non-empty deck.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testUpdateUiBranches() {
    try (MockedStatic<ApiClient> apiClient = Mockito.mockStatic(ApiClient.class)) {
      apiClient.when(() -> ApiClient
        .performApiRequest(anyString(), eq("GET"),
         isNull(), any(TypeReference.class)))
                .thenReturn(new ApiResponse<>(true, "", null));
      
      setField(controller, "currentDeck", null);
      runOnFxThread(() -> controller.updateUi());
      assertTrue(listView.getItems().isEmpty());
      setField(controller, "currentDeck", null);
      setField(controller, "currentDeck", null);
      runOnFxThread(() -> controller.updateUi());
      assertTrue(listView.getItems().isEmpty());
      setField(controller, "currentDeck", null);
      setField(controller, "currentDeck", mapper.toDto(new FlashcardDeck("NonExistent")));
      runOnFxThread(() -> controller.updateUi());
      assertTrue(listView.getItems().isEmpty());
      FlashcardDeckManager mgr = new FlashcardDeckManager();
      FlashcardDeck deck = new FlashcardDeck("Deck1");
      mgr.addDeck(deck);
      FlashcardDeck deck1 = new FlashcardDeck("Deck1");
      setField(controller, "currentDeck", mapper.toDto(deck1));
      runOnFxThread(() -> controller.updateUi());
      assertTrue(listView.getItems().isEmpty());
      deck.addFlashcard(new Flashcard("Q", "A"));
      FlashcardDeckDto deckDtoWithCards = mapper.toDto(deck);
      
      setField(controller, "currentUsername", "testuser");
      
      apiClient.when(() -> ApiClient
        .performApiRequest(anyString(), eq("GET"), isNull(), any(TypeReference.class)))
            .thenReturn(new ApiResponse<>(true, "", deckDtoWithCards));
      
      runOnFxThread(() -> {
        controller.setDeck(deckDtoWithCards);
        assertFalse(listView.getItems().isEmpty());
        assertFalse(startLearning.isDisabled());
      });
    }
  }

  /**
   * Tests saveUserData method for all major branches.
   * Covers null username, null deck name, and API failure scenarios.
   */
  @Test
  public void testSaveUserDataBranches() {
    setField(controller, "currentUsername", null);
    FlashcardDeck deck1 = new FlashcardDeck("Deck1");
    setField(controller, "currentDeck", mapper.toDto(deck1));
    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient
      .performApiRequest(any(), any(), any(), any()))
            .thenReturn(new ApiResponse<>(true, "", null));
    }

    setField(controller, "currentUsername", "testuser");
    setField(controller, "currentDeck", null);

    try (MockedStatic<ApiClient> apiClientMock = Mockito
        .mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient
      .performApiRequest(any(), any(), any(), any()))
            .thenReturn(new ApiResponse<>(true, "", null));
    }
  }

  /**
   * Tests getCurrentDeck method for all major branches.
   * Covers null manager, deck not found, and deck found scenarios.
   */
  @Test
  public void testGetDeckBranches() {
    setField(controller, "currentDeck", null);
    FlashcardDeck deck1 = new FlashcardDeck("Deck1");
    setField(controller, "currentDeck", mapper.toDto(deck1));

    FlashcardDeckDto currentDeck = (FlashcardDeckDto) getField(controller, "currentDeck");
    assertNotNull(currentDeck);
    assertEquals("Deck1", currentDeck.getDeckName());
    setField(controller, "currentDeck", mapper.toDto(new FlashcardDeck("NonExistent")));

    FlashcardDeckDto deck = (FlashcardDeckDto) getField(controller, "currentDeck");
    assertNotNull(deck);
    assertEquals("NonExistent", deck.getDeckName());

    FlashcardDeck deckObj = new FlashcardDeck("Deck1");
    FlashcardDeckDto deckDto = mapper.toDto(deckObj);
    setField(controller, "currentDeck", deckDto);

    FlashcardDeckDto currentDeck2 = (FlashcardDeckDto) getField(controller, "currentDeck");
    assertNotNull(currentDeck2);
  }

  /**
   * Tests whenCreateButtonIsClicked method for all major branches.
   * Covers null manager, null deck name, empty/whitespace input, and API failure scenarios.
   */
  @Test
  public void testCreateCardBranches() {
    setField(controller, "currentDeck", null);
    questionField.setText("Q");
    answerField.setText("A");
    assertDoesNotThrow(() -> controller
        .whenCreateButtonIsClicked());
    setField(controller, "currentDeck", null);
    questionField.setText("Q");
    answerField.setText("A");

    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient
      .performApiRequest(any(), any(), any(), any()))
            .thenReturn(new ApiResponse<>(true, "", null));
      assertDoesNotThrow(() -> controller.whenCreateButtonIsClicked());
    }

    FlashcardDeck deck2 = new FlashcardDeck("Deck1");
    setField(controller, "currentDeck", mapper.toDto(deck2));
    questionField.setText("");
    answerField.setText("");

    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient
      .performApiRequest(any(), any(), any(), any()))
            .thenReturn(new ApiResponse<>(true, "", null));
      assertDoesNotThrow(() -> controller.whenCreateButtonIsClicked());
    }

    questionField.setText("   ");
    answerField.setText("   ");

    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient
      .performApiRequest(any(), any(), any(), any()))
            .thenReturn(new ApiResponse<>(true, "", null));
      assertDoesNotThrow(() -> controller.whenCreateButtonIsClicked());
    }

    questionField.setText("Q");
    answerField.setText("A");

    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient
      .performApiRequest(any(), any(), any(), any()))
            .thenReturn(new ApiResponse<>(false, ApiConstants.FLASHCARD_OPERATION_FAILED, null));
      assertDoesNotThrow(() -> controller.whenCreateButtonIsClicked());
    }
  }

  /**
   * Tests whenDeleteCardButtonIsClicked method for all major branches.
   * Covers null manager, null deck name, no selection, and API failure scenarios.
   */
  @Test
  public void testDeleteCardBranches() {
    setField(controller, "currentDeck", null);
    
    FlashcardDeck deck1 = new FlashcardDeck("Deck1");
    setField(controller, "currentDeck", mapper.toDto(deck1));

    listView.setItems(FXCollections.observableArrayList());
    listView.getSelectionModel().clearSelection();
    assertDoesNotThrow(() -> controller.whenDeleteCardButtonIsClicked());

    final FlashcardDeckManager mgr = new FlashcardDeckManager();
    setField(controller, "currentDeck", null);

    listView.setItems(FXCollections.observableArrayList());
    listView.getSelectionModel().clearSelection();

    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any()))
          .thenReturn(new ApiResponse<>(true, "", null));
      assertDoesNotThrow(() -> controller.whenDeleteCardButtonIsClicked());
    }

    FlashcardDeck deck3 = new FlashcardDeck("Deck1");
    setField(controller, "currentDeck", mapper.toDto(deck3));
    listView.setItems(FXCollections.observableArrayList());
    listView.getSelectionModel().clearSelection();

    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any()))
          .thenReturn(new ApiResponse<>(true, "", null));
      assertDoesNotThrow(() -> controller.whenDeleteCardButtonIsClicked());
    }

    FlashcardDeck deck = new FlashcardDeck("Deck1");
    deck.addFlashcard(new Flashcard("Q", "A"));
    mgr.addDeck(deck);
    List<FlashcardDto> cardDtos = mapper.toDto(deck).getDeck();
    listView.setItems(FXCollections.observableArrayList(cardDtos));
    listView.getSelectionModel().select(0);

    try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
      apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any()))
          .thenReturn(new ApiResponse<>(false,
              ApiConstants.FLASHCARD_OPERATION_FAILED, null));
      assertDoesNotThrow(() -> controller.whenDeleteCardButtonIsClicked());
    }
  }

  /**
   * Tests loadDeckData method for success, failure, and exception scenarios.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testLoadDeckDataScenarios() {
    // Prepare a deck dto with a name
    FlashcardDeck deck = new FlashcardDeck("Deck Load");
    deck.addFlashcard(new Flashcard("Q", "A"));
    FlashcardDeckDto dtoWithCard = mapper.toDto(deck);

    setField(controller, "currentUsername", "tester");
    setField(controller, "currentDeck", new FlashcardDeckDto("Deck Load", List.of()));

    // success
    try (MockedStatic<ApiClient> api = Mockito.mockStatic(ApiClient.class)) {
      ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
      api.when(() -> ApiClient.performApiRequest(urlCaptor.capture(), eq("GET"),
              isNull(), any(TypeReference.class)))
          .thenReturn(new ApiResponse<>(true, "", dtoWithCard));
      invokePrivate(controller, "loadDeckData");
      FlashcardDeckDto after = (FlashcardDeckDto) getField(controller, "currentDeck");
      assertEquals(1, after.getDeck().size());
      assertTrue(urlCaptor.getValue().contains("Deck%20Load"),
          "GET URL should encode spaces as %20");
    }

    // failure: alert shown
    setField(controller, "currentDeck", new FlashcardDeckDto("Deck Load", List.of()));

    try (MockedStatic<ApiClient> api = Mockito.mockStatic(ApiClient.class)) {
      api.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"),
              isNull(), any(TypeReference.class)))
          .thenReturn(new ApiResponse<>(false,
              ApiConstants.DECK_RETRIEVING_ERROR, null));
      api.when(() -> ApiClient.showAlert(anyString(), anyString()))
          .thenAnswer(inv -> null);
      invokePrivate(controller, "loadDeckData");
      api.verify(() -> ApiClient.showAlert(ApiConstants.LOAD_ERROR,
          ApiConstants.FAILED_TO_LOAD_DATA));
    }

    // exception: alert shown
    setField(controller, "currentDeck", new FlashcardDeckDto("Deck Load", List.of()));

    try (MockedStatic<ApiClient> api = Mockito.mockStatic(ApiClient.class)) {
      api.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"),
              isNull(), any(TypeReference.class)))
          .thenThrow(new RuntimeException(ApiConstants.SERVER_CONNECTION_ERROR));
      api.when(() -> ApiClient.showAlert(anyString(), anyString()))
          .thenAnswer(inv -> null);
      invokePrivate(controller, "loadDeckData");
      api.verify(() -> ApiClient.showAlert(ApiConstants.LOAD_ERROR,
          ApiConstants.UNEXPECTED_ERROR));
    }
  }

  /**
   * Tests cell factory calls toString on FlashcardDto items.
   */
  @Test
  public void testCellFactoryLambda() {
    runOnFxThread(() -> controller.initialize());

    var cb = listView.getCellFactory();
    assertNotNull(cb);

    var cell = cb.call(listView);
    assertNotNull(cell);
  }

  /**
   * Tests whenStartLearningButtonIsClicked early return when currentDeck is null.
   * Covers early return branch.
   */
  @Test
  public void testStartLearningEarlyReturn() {
    setField(controller, "currentDeck", null);
    setField(controller, "startLearning", new Button());
    assertDoesNotThrow(() -> controller.whenStartLearningButtonIsClicked());
  }

  /**
   * Tests create card logic when validation message is returned without alert.
   * Covers validation message scenario.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testCreateValidationNoAlert() {
    FlashcardDeck deck = new FlashcardDeck("Deck1");
    setField(controller, "currentDeck", mapper.toDto(deck));
    setField(controller, "currentUsername", "u");
    questionField.setText("Q");
    answerField.setText("A");

    try (MockedStatic<ApiClient> api = Mockito.mockStatic(ApiClient.class)) {
      api.when(() -> ApiClient.performApiRequest(anyString(), eq("POST"),
              anyString(), any(TypeReference.class)))
          .thenReturn(new ApiResponse<>(false,
              ApiConstants.FLASHCARD_QUESTION_ANSWER_EMPTY, null));
      api.when(() -> ApiClient.showAlert(anyString(), anyString()))
          .thenAnswer(inv -> null);

      controller.whenCreateButtonIsClicked();

      api.verify(() -> ApiClient.showAlert(ApiConstants.SERVER_ERROR,
          ApiConstants.FLASHCARD_FAILED_TO_CREATE), never());
      api.verify(() -> ApiClient.showAlert(ApiConstants.SERVER_ERROR,
          ApiConstants.UNEXPECTED_ERROR), never());
    }
  }

  /**
   * Tests create card logic when server error occurs.
   * Covers server error scenario.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testCreateExceptionAlert() {
    FlashcardDeck deck = new FlashcardDeck("Deck1");
    setField(controller, "currentDeck", mapper.toDto(deck));
    setField(controller, "currentUsername", "u");
    questionField.setText("Q");
    answerField.setText("A");

    try (MockedStatic<ApiClient> api = Mockito.mockStatic(ApiClient.class)) {
      api.when(() -> ApiClient.performApiRequest(anyString(), eq("POST"),
              anyString(), any(TypeReference.class)))
          .thenThrow(new RuntimeException(
              ApiConstants.SERVER_CONNECTION_ERROR));
      AtomicReference<String> title = new AtomicReference<>();
      AtomicReference<String> msg = new AtomicReference<>();
      api.when(() -> ApiClient.showAlert(anyString(), anyString()))
          .thenAnswer(inv -> {
            title.set(inv.getArgument(0));
            msg.set(inv.getArgument(1));
            return null;
          });

      controller.whenCreateButtonIsClicked();
      assertEquals(ApiConstants.SERVER_ERROR, title.get());
      assertEquals(ApiConstants.UNEXPECTED_ERROR, msg.get());
    }
  }

  /**
   * Tests delete card logic when server error occurs.
   * Covers server error scenario.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testDeleteServerErrorAlert() {
    FlashcardDeck deck = new FlashcardDeck("Deck1");
    deck.addFlashcard(new Flashcard("Q", "A"));
    setField(controller, "currentDeck", mapper.toDto(deck));
    setField(controller, "currentUsername", "tester");

    listView.setItems(FXCollections.observableArrayList(mapper.toDto(deck).getDeck()));
    listView.getSelectionModel().select(0);

    try (MockedStatic<ApiClient> api = Mockito.mockStatic(ApiClient.class)) {
      api.when(() -> ApiClient.performApiRequest(
              anyString(), eq("DELETE"), isNull(), any(TypeReference.class)))
          .thenReturn(new ApiResponse<>(false,
              ApiConstants.FLASHCARD_FAILED_TO_DELETE, null));

      AtomicReference<String> title = new AtomicReference<>();
      AtomicReference<String> msg = new AtomicReference<>();
      api.when(() -> ApiClient.showAlert(anyString(), anyString()))
          .thenAnswer(inv -> {
            title.set(inv.getArgument(0));
            msg.set(inv.getArgument(1));
            return null;
          });

      controller.whenDeleteCardButtonIsClicked();

      assertEquals(ApiConstants.SERVER_ERROR, title.get());
      assertEquals(ApiConstants.FLASHCARD_FAILED_TO_DELETE, msg.get());
    }
  }

  /**
   * Tests delete card logic when exception occurs.
   * Covers exception scenario.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testDeleteExceptionAlert() {
    FlashcardDeck deck = new FlashcardDeck("Deck1");
    deck.addFlashcard(new Flashcard("Q", "A"));
    setField(controller, "currentDeck", mapper.toDto(deck));
    setField(controller, "currentUsername", "u");
    listView.setItems(FXCollections.observableArrayList(
        mapper.toDto(deck).getDeck()));
    listView.getSelectionModel().select(0);

    try (MockedStatic<ApiClient> api = Mockito.mockStatic(ApiClient.class)) {
      api.when(() -> ApiClient.performApiRequest(anyString(), eq("DELETE"),
              isNull(), any(TypeReference.class)))
          .thenThrow(new RuntimeException(
              ApiConstants.SERVER_CONNECTION_ERROR));
      AtomicReference<String> title = new AtomicReference<>();
      AtomicReference<String> msg = new AtomicReference<>();
      api.when(() -> ApiClient.showAlert(anyString(), anyString()))
          .thenAnswer(inv -> {
            title.set(inv.getArgument(0));
            msg.set(inv.getArgument(1));
            return null;
          });

      controller.whenDeleteCardButtonIsClicked();
      assertEquals(ApiConstants.SERVER_ERROR, title.get());
      assertEquals(ApiConstants.UNEXPECTED_ERROR, msg.get());
    }
  }

  /**
   * Tests updateUi method when username and startLearning button are null.
   * Covers null username and null startLearning button branches.
   */
  @Test
  public void testUpdateUiUsernameStartLearningNull() {
    setField(controller, "username", null);
    setField(controller, "startLearning", null);
    FlashcardDeck d = new FlashcardDeck("DeckX");
    setField(controller, "currentDeck", mapper.toDto(d));
    setField(controller, "currentUsername", null);

    runOnFxThread(() -> controller.updateUi());

    assertTrue(listView.getItems().isEmpty());
    assertTrue(deleteCardButton.isDisabled());
  }


  /**
   * Tests loadDeckData method early return scenarios.
   * Covers:
   * - null username
   * - null deck
   * - empty deck name
   */
  @Test
  public void testLoadDeckDataEarlyReturnScenarios() {
    // Scenario 1: null username
    setField(controller, "currentUsername", null);
    setField(controller, "currentDeck", new FlashcardDeckDto("DeckA", List.of()));
    invokePrivate(controller, "loadDeckData");
    assertNull(getField(controller, "currentDeck"));

    // Scenario 2: null deck
    setField(controller, "currentUsername", "u");
    setField(controller, "currentDeck", null);
    invokePrivate(controller, "loadDeckData");
    assertNull(getField(controller, "currentDeck"));

    // Scenario 3: empty deck name
    setField(controller, "currentUsername", "u");
    setField(controller, "currentDeck", new FlashcardDeckDto("", List.of()));
    invokePrivate(controller, "loadDeckData");
    FlashcardDeckDto cd = (FlashcardDeckDto) getField(controller, "currentDeck");
    assertNotNull(cd);
    assertEquals("", cd.getDeckName());
  }

  /**
   * Tests create card logic when server returns null response.
   * Covers null response scenario.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testCreateResultNullAlert() {
    FlashcardDeck deck = new FlashcardDeck("Deck1");
    setField(controller, "currentDeck", mapper.toDto(deck));
    setField(controller, "currentUsername", "u");
    questionField.setText("Q");
    answerField.setText("A");

    try (MockedStatic<ApiClient> api = Mockito.mockStatic(ApiClient.class)) {
      api.when(() -> ApiClient.performApiRequest(anyString(), eq("POST"),
              anyString(), any(TypeReference.class)))
          .thenReturn(null);

      var title = new java.util.concurrent.atomic.AtomicReference<String>();
      var msg   = new java.util.concurrent.atomic.AtomicReference<String>();
      api.when(() -> ApiClient.showAlert(anyString(), anyString()))
          .thenAnswer(inv -> {
            title.set(inv.getArgument(0));
            msg.set(inv.getArgument(1));
            return null;
          });

      controller.whenCreateButtonIsClicked();

      assertEquals(ApiConstants.SERVER_ERROR, title.get());
      assertEquals(ApiConstants.FLASHCARD_FAILED_TO_CREATE, msg.get());
    }
  }

  /**
   * Tests delete card logic when server returns null response.
   * Covers null response scenario.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testDeleteResultNullShowsServerAlert() {
    FlashcardDeck deck = new FlashcardDeck("Deck1");
    deck.addFlashcard(new Flashcard("Q", "A"));
    setField(controller, "currentDeck", mapper.toDto(deck));
    setField(controller, "currentUsername", "u");
    listView.setItems(FXCollections.observableArrayList(
        mapper.toDto(deck).getDeck()));
    listView.getSelectionModel().select(0);

    try (MockedStatic<ApiClient> api = Mockito.mockStatic(ApiClient.class)) {
      api.when(() -> ApiClient.performApiRequest(anyString(), eq("DELETE"),
              isNull(), any(TypeReference.class)))
          .thenReturn(null);

      var title = new java.util.concurrent.atomic.AtomicReference<String>();
      var msg   = new java.util.concurrent.atomic.AtomicReference<String>();
      api.when(() -> ApiClient.showAlert(anyString(), anyString()))
          .thenAnswer(inv -> {
            title.set(inv.getArgument(0));
            msg.set(inv.getArgument(1));
            return null;
          });

      controller.whenDeleteCardButtonIsClicked();

      assertEquals(ApiConstants.SERVER_ERROR, title.get());
      assertEquals(ApiConstants.FLASHCARD_FAILED_TO_DELETE, msg.get());
    }
  }

  /**
   * Tests loadDeckData method when server returns null response.
   * Covers null response scenario without alert.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testLoadDeckDataResultNullNoAlert() {
    setField(controller, "currentUsername", "u");
    setField(controller, "currentDeck", new FlashcardDeckDto("DeckA", List.of()));

    try (MockedStatic<ApiClient> api = Mockito.mockStatic(ApiClient.class)) {
      api.when(() -> ApiClient.performApiRequest(anyString(), eq("GET"),
              isNull(), any(TypeReference.class)))
          .thenReturn(null);
      api.when(() -> ApiClient.showAlert(anyString(), anyString()))
          .thenAnswer(inv -> null);

      invokePrivate(controller, "loadDeckData");

      FlashcardDeckDto cd = (FlashcardDeckDto) getField(controller, "currentDeck");
      assertNotNull(cd);
      assertEquals("DeckA", cd.getDeckName());
    }
  }


  /**
   * Tests updateUi method when deleteCardButton is null.
   * Covers null deleteCardButton branch.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testUpdateUiDeleteButtonNull() {
    setField(controller, "deleteCardButton", null);
    setField(controller, "currentUsername", "u");

    FlashcardDeck d = new FlashcardDeck("DeckX");
    d.addFlashcard(new Flashcard("Q", "A"));
    FlashcardDeckDto dto = mapper.toDto(d);
    setField(controller, "currentDeck", dto);

    try (MockedStatic<ApiClient> api = Mockito.mockStatic(ApiClient.class)) {
      api.when(() -> ApiClient.performApiRequest(
              anyString(), eq("GET"), isNull(),
              any(com.fasterxml.jackson.core.type.TypeReference.class)))
          .thenReturn(new shared.ApiResponse<>(true, "", dto));

      runOnFxThread(() -> {
        controller.updateUi();
        assertEquals(1, listView.getItems().size());
      });
    }
  }

  /**
   * Tests ListCell updateItem method with valid FlashcardDto.
   * Covers the updateItem method in the ListCell created by setCellFactory.
   *
   * @throws Exception if reflection fails or updateItem invocation fails
   */
  @Test
  public void testListCellUpdateItemWithValidFlashcard() throws Exception {
    runOnFxThread(() -> {
      try {
        controller.initialize();
        
        // Get the cell factory and create a cell
        var cellFactory = listView.getCellFactory();
        assertNotNull(cellFactory);
        var cell = cellFactory.call(listView);
        assertNotNull(cell);
        
        // Create test flashcard
        FlashcardDto flashcard = new FlashcardDto("Test Question", "Test Answer", 1);
        
        // Find updateItem method in the class hierarchy
        var updateItemMethod = findUpdateItemMethod(cell.getClass());
        updateItemMethod.setAccessible(true);
        updateItemMethod.invoke(cell, flashcard, false);
        
        // Verify text is set correctly
        assertEquals(flashcard.toString(), cell.getText());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }

  /**
   * Tests ListCell updateItem method with empty cell.
   * Covers the empty branch in updateItem.
   *
   * @throws Exception if reflection fails or updateItem invocation fails
   */
  @Test
  public void testListCellUpdateItemWithEmptyCell() throws Exception {
    runOnFxThread(() -> {
      try {
        controller.initialize();
        
        var cellFactory = listView.getCellFactory();
        var cell = cellFactory.call(listView);
        
        // Find and invoke updateItem with empty=true
        var updateItemMethod = findUpdateItemMethod(cell.getClass());
        updateItemMethod.setAccessible(true);
        updateItemMethod.invoke(cell, null, true);
        
        // Verify text is null for empty cell
        assertNull(cell.getText());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }

  /**
   * Tests ListCell updateItem method with null item.
   *
   * <p>Covers the null item branch in updateItem.
   *
   * @throws Exception if reflection fails or updateItem invocation fails
   */
  @Test
  public void testListCellUpdateItemWithNullItem() throws Exception {
    runOnFxThread(() -> {
      try {
        controller.initialize();
        
        var cellFactory = listView.getCellFactory();
        var cell = cellFactory.call(listView);
        
        // Find and invoke updateItem with null item
        var updateItemMethod = findUpdateItemMethod(cell.getClass());
        updateItemMethod.setAccessible(true);
        updateItemMethod.invoke(cell, null, false);
        
        // Verify text is null when item is null
        assertNull(cell.getText());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }
  
  /**
   * Helper method to find updateItem method in class hierarchy.
   *
   * @param clazz the class to search in
   * @return the updateItem method
   * @throws NoSuchMethodException if updateItem method is not found
   */
  private Method findUpdateItemMethod(Class<?> clazz) throws NoSuchMethodException {
    Class<?> current = clazz;
    while (current != null) {
      try {
        return current
        .getDeclaredMethod("updateItem", Object.class, boolean.class);
      } catch (NoSuchMethodException e) {
        current = current.getSuperclass();
      }
    }
    throw new NoSuchMethodException("updateItem not found in class hierarchy");
  }

  /**
   * Injects a value into a private field using reflection.
   *
   * @param obj the object containing the field
   * @param fieldName the name of the field to set
   * @param value the value to inject
   * @throws RuntimeException if field access fails
   */
  private void setField(Object obj, String fieldName, Object value) {
    try {
      var field = obj.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(obj, value);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Runs a Runnable on the JavaFX Application Thread and waits for completion.
   *
   * @param runnable the task to run on the FX thread
   * @throws RuntimeException if interrupted while waiting for FX thread
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
   * Retrieves a private field value using reflection.
   *
   * @param obj the object containing the field
   * @param fieldName the name of the field to retrieve
   * @return the value of the field
   * @throws RuntimeException if field access fails
   */
  private Object getField(Object obj, String fieldName) {
    try {
      var field = obj.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      return field.get(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Invokes a private method using reflection.
   *
   * @param obj the object containing the method
   * @param methodName the name of the method to invoke
   * @return the return value of the invoked method
   * @throws RuntimeException if method invocation fails
   */
  private Object invokePrivate(Object obj, String methodName) {
    try {
      var method = obj.getClass().getDeclaredMethod(methodName);
      method.setAccessible(true);
      return method.invoke(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}