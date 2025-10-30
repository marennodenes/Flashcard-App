package ui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import app.Flashcard;
import app.FlashcardDeck;
import app.FlashcardDeckManager;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testfx.framework.junit5.ApplicationExtension;
import shared.ApiResponse;

/**
 * Unit tests for {@link FlashcardDeckController}.
 * <p>
 * Covers all major branches, edge cases, and error handling for deck management,
 * UI updates, card creation/deletion, user data saving, and navigation logic.
 * Uses reflection to inject private fields and Mockito for mocking JavaFX and API interactions.
 * </p>
 * @author marennod
 * @author sofietw
 */
@ExtendWith(ApplicationExtension.class)
class FlashcardDeckControllerTest {

    private FlashcardDeckController controller;
    private TextField questionField;
    private TextField answerField;
    private ListView<Flashcard> listView;
    private Text username;
    private Button startLearning;
    private Button deleteCardButton;

    /**
     * Sets up a fresh controller and injects mock UI components before each test.
     */
    @BeforeEach
    void setUp() {
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
        setField(controller, "currentUsername", "testuser");
        setField(controller, "currentDeckName", "Deck1");
    }

    /**
     * Injects a value into a private field using reflection.
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
     * Retrieves a private field value using reflection.
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

    /**
     * Tests setting the current username.
     */
    @Test
    void testSetCurrentUsername() {
        controller.setCurrentUsername("newuser");
        assertEquals("newuser", getField(controller, "currentUsername"));
    }

    /**
     * Tests UI update when deck manager is null.
     */
    @Test
    void testUpdateUiNoDeckManager() {
        setField(controller, "deckManager", null);
        controller.updateUi();
        assertTrue(listView.getItems().isEmpty());
        assertTrue(startLearning.isDisabled());
        assertTrue(deleteCardButton.isDisabled());
    }

    /**
     * Tests UI update with a valid deck manager and deck.
     */
    @Test
    void testUpdateUiWithDeckManager() {
        FlashcardDeckManager mgr = new FlashcardDeckManager();
        FlashcardDeck deck = new FlashcardDeck("Deck1");
        deck.addFlashcard(new Flashcard("Q1", "A1"));
        mgr.addDeck(deck);
        controller.setDeckManager(mgr, deck);
        controller.updateUi();
        assertFalse(listView.getItems().isEmpty());
        assertFalse(startLearning.isDisabled());
    }

    /**
     * Tests UI update when deck name does not match any deck.
     */
    @Test
    void testUpdateUiWithNonMatchingDeckName() {
        FlashcardDeckManager mgr = new FlashcardDeckManager();
        FlashcardDeck deck = new FlashcardDeck("Deck1");
        deck.addFlashcard(new Flashcard("Q1", "A1"));
        mgr.addDeck(deck);
        setField(controller, "deckManager", mgr);
        setField(controller, "currentDeckName", "NonExistent");
        controller.updateUi();
        assertTrue(listView.getItems().isEmpty());
    }

    /**
     * Tests setting deck manager when deck already exists.
     */
    @Test
    void testSetDeckManagerWithExistingDeck() {
        FlashcardDeckManager mgr = new FlashcardDeckManager();
        FlashcardDeck deck = new FlashcardDeck("Deck1");
        mgr.addDeck(deck);
        assertDoesNotThrow(() -> controller.setDeckManager(mgr, deck));
    }

    /**
     * Tests setting deck manager when deck is not in manager.
     */
    @Test
    void testSetDeckManagerWithDeckNotInManager() {
        FlashcardDeckManager mgr = new FlashcardDeckManager();
        FlashcardDeck deck = new FlashcardDeck("Deck1");
        assertDoesNotThrow(() -> controller.setDeckManager(mgr, deck));
    }

    /**
     * Tests setting deck manager with duplicate deck.
     */
    @Test
    void testSetDeckManagerWithDuplicateDeck() {
        FlashcardDeckManager mgr = new FlashcardDeckManager();
        FlashcardDeck deck = new FlashcardDeck("Deck1");
        mgr.addDeck(deck);
        assertDoesNotThrow(() -> controller.setDeckManager(mgr, deck));
    }

    /**
     * Tests card creation logic and API success.
     */
    @Test
    void testWhenCreateButtonIsClickedAddsCard() {
        FlashcardDeckManager mgr = new FlashcardDeckManager();
        FlashcardDeck deck = new FlashcardDeck("Deck1");
        mgr.addDeck(deck);
        controller.setDeckManager(mgr, deck);
        questionField.setText("Q2");
        answerField.setText("A2");
        setField(controller, "currentDeckName", "Deck1");
        setField(controller, "deckManager", mgr);
        try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
            apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any())).thenReturn(new ApiResponse<>(true, "", null));
            controller.whenCreateButtonIsClicked();
        }
        FlashcardDeckManager actualMgr = (FlashcardDeckManager) getField(controller, "deckManager");
        FlashcardDeck actualDeck = (FlashcardDeck) actualMgr.getDecks().get(0);
        assertEquals(1, actualDeck.getDeck().size());
        assertEquals("Q2", actualDeck.getDeck().get(0).getQuestion());
    }

    /**
     * Tests card creation logic and API failure.
     */
    @Test
    void testWhenCreateButtonIsClickedApiFailure() {
        FlashcardDeckManager mgr = new FlashcardDeckManager();
        FlashcardDeck deck = new FlashcardDeck("Deck1");
        mgr.addDeck(deck);
        controller.setDeckManager(mgr, deck);
        questionField.setText("Q2");
        answerField.setText("A2");
        setField(controller, "currentDeckName", "Deck1");
        setField(controller, "deckManager", mgr);
        try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
            apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any())).thenReturn(new ApiResponse<>(false, "error", null));
            assertDoesNotThrow(() -> controller.whenCreateButtonIsClicked());
        }
        FlashcardDeckManager actualMgr = (FlashcardDeckManager) getField(controller, "deckManager");
        FlashcardDeck actualDeck = (FlashcardDeck) actualMgr.getDecks().get(0);
        assertEquals(1, actualDeck.getDeck().size());
    }

    /**
     * Tests card deletion logic and API success.
     */
    @Test
    void testWhenDeleteCardButtonIsClickedRemovesCard() {
        FlashcardDeckManager mgr = new FlashcardDeckManager();
        FlashcardDeck deck = new FlashcardDeck("Deck1");
        deck.addFlashcard(new Flashcard("Q1", "A1"));
        mgr.addDeck(deck);
        controller.setDeckManager(mgr, deck);
        listView.setItems(FXCollections.observableArrayList(deck.getDeck()));
        listView.getSelectionModel().select(0);
        setField(controller, "currentDeckName", "Deck1");
        setField(controller, "deckManager", mgr);
        try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
            apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any())).thenReturn(new ApiResponse<>(true, "", null));
            controller.whenDeleteCardButtonIsClicked();
        }
        FlashcardDeckManager actualMgr = (FlashcardDeckManager) getField(controller, "deckManager");
        FlashcardDeck actualDeck = (FlashcardDeck) actualMgr.getDecks().get(0);
        assertTrue(actualDeck.getDeck().isEmpty());
    }

    /**
     * Tests card deletion logic and API failure.
     */
    @Test
    void testWhenDeleteCardButtonIsClickedApiFailure() {
        FlashcardDeckManager mgr = new FlashcardDeckManager();
        FlashcardDeck deck = new FlashcardDeck("Deck1");
        deck.addFlashcard(new Flashcard("Q1", "A1"));
        mgr.addDeck(deck);
        controller.setDeckManager(mgr, deck);
        listView.setItems(FXCollections.observableArrayList(deck.getDeck()));
        listView.getSelectionModel().select(0);
        setField(controller, "currentDeckName", "Deck1");
        setField(controller, "deckManager", mgr);
        try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
            apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any())).thenReturn(new ApiResponse<>(false, "error", null));
            assertDoesNotThrow(() -> controller.whenDeleteCardButtonIsClicked());
        }
        FlashcardDeckManager actualMgr = (FlashcardDeckManager) getField(controller, "deckManager");
        FlashcardDeck actualDeck = (FlashcardDeck) actualMgr.getDecks().get(0);
        assertTrue(actualDeck.getDeck().isEmpty());
    }

    /**
     * Tests clearing input fields.
     */
    @Test
    void testClearInputFields() {
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
    void testGetCurrentDeckReturnsNullIfNotFound() {
        FlashcardDeckManager mgr = new FlashcardDeckManager();
        setField(controller, "deckManager", mgr);
        setField(controller, "currentDeckName", "NonExistent");
        Object deck = invokePrivate(controller, "getCurrentDeck");
        assertNull(deck);
    }

    /**
     * Tests start learning button logic with mocked scene/window.
     */
    @Test
    void testWhenStartLearningButtonIsClicked() {
        FlashcardDeckManager mgr = new FlashcardDeckManager();
        FlashcardDeck deck = new FlashcardDeck("Deck1");
        deck.addFlashcard(new Flashcard("Q1", "A1"));
        mgr.addDeck(deck);
        setField(controller, "deckManager", mgr);
        setField(controller, "currentDeckName", "Deck1");
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
    void testWhenLogOut() {
        try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
            apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any())).thenReturn(new ApiResponse<>(true, "", null));
            assertDoesNotThrow(() -> controller.whenLogOut());
        }
    }

    /**
     * Tests back button logic with mocked scene/window and alert.
     */
    @Test
    void testWhenBackButtonIsClicked() {
        FlashcardDeckManager mgr = new FlashcardDeckManager();
        FlashcardDeck deck = new FlashcardDeck("Deck1");
        deck.addFlashcard(new Flashcard("Q1", "A1"));
        mgr.addDeck(deck);
        setField(controller, "deckManager", mgr);
        setField(controller, "currentDeckName", "Deck1");
        TextField mockField = mock(TextField.class);
        javafx.scene.Scene mockScene = mock(javafx.scene.Scene.class);
        javafx.stage.Stage mockStage = mock(javafx.stage.Stage.class);
        when(mockField.getScene()).thenReturn(mockScene);
        when(mockScene.getWindow()).thenReturn(mockStage);
        setField(controller, "questionField", mockField);
        try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
            apiClientMock.when(() -> ApiClient.showAlert(anyString(), anyString())).thenAnswer(inv -> null);
            assertDoesNotThrow(() -> controller.whenBackButtonIsClicked());
        }
    }

    /**
     * Tests controller initialization logic.
     */
    @Test
    void testInitialize() {
        assertDoesNotThrow(() -> controller.initialize());
    }

    /**
     * Tests log out logic when API call fails.
     */
    @Test
    void testWhenLogOutFailure() {
        try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
            apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any()))
                .thenReturn(new ApiResponse<>(false, "error", null));
            assertDoesNotThrow(() -> controller.whenLogOut());
        }
    }

    /**
     * Tests saveUserData logic when API call fails.
     */
    @Test
    void testSaveUserDataFailure() {
        setField(controller, "currentUsername", "testuser");
        setField(controller, "currentDeckName", "Deck1");
        try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
            apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any()))
                .thenReturn(new ApiResponse<>(false, "error", null));
            assertDoesNotThrow(() -> invokePrivate(controller, "saveUserData"));
        }
    }

    /**
     * Tests setting deck manager with no decks.
     */
    @Test
    void testSetDeckManagerWithNoDecks() {
        FlashcardDeckManager mgr = new FlashcardDeckManager();
        assertDoesNotThrow(() -> controller.setDeckManager(mgr, new FlashcardDeck("DeckX")));
    }

    /**
     * Tests setting deck manager with null deck.
     */
    @Test
    void testSetDeckManagerWithNullDeck() {
        FlashcardDeckManager mgr = new FlashcardDeckManager();
        assertThrows(NullPointerException.class, () -> controller.setDeckManager(mgr, null));
    }

    /**
     * Tests UI update when current deck name is null.
     */
    @Test
    void testUpdateUiNoCurrentDeckName() {
        setField(controller, "deckManager", new FlashcardDeckManager());
        setField(controller, "currentDeckName", null);
        controller.updateUi();
        assertTrue(listView.getItems().isEmpty());
    }

    /**
     * Tests UI update with empty deck manager.
     */
    @Test
    void testUpdateUiEmptyDeckManager() {
        setField(controller, "deckManager", new FlashcardDeckManager());
        setField(controller, "currentDeckName", "Deck1");
        controller.updateUi();
        assertTrue(listView.getItems().isEmpty());
    }

    /**
     * Tests UI update with multiple decks.
     */
    @Test
    void testUpdateUiMultipleDecks() {
        FlashcardDeckManager mgr = new FlashcardDeckManager();
        FlashcardDeck deck1 = new FlashcardDeck("Deck1");
        FlashcardDeck deck2 = new FlashcardDeck("Deck2");
        deck1.addFlashcard(new Flashcard("Q1", "A1"));
        deck2.addFlashcard(new Flashcard("Q2", "A2"));
        mgr.addDeck(deck1);
        mgr.addDeck(deck2);
    }

    /**
     * Tests card deletion logic for last card.
     */
    void testWhenDeleteCardButtonIsClickedRemovesLastCard() {
        FlashcardDeckManager mgr = new FlashcardDeckManager();
        FlashcardDeck deck = new FlashcardDeck("Deck1");
        deck.addFlashcard(new Flashcard("Q1", "A1"));
        mgr.addDeck(deck);
        controller.setDeckManager(mgr, deck);
        listView.setItems(FXCollections.observableArrayList(deck.getDeck()));
        listView.getSelectionModel().select(0);
        setField(controller, "currentDeckName", "Deck1");
        setField(controller, "deckManager", mgr);
        try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
            apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any())).thenReturn(new ApiResponse<>(true, "", null));
            assertDoesNotThrow(() -> controller.whenDeleteCardButtonIsClicked());
        }
        FlashcardDeckManager actualMgr = (FlashcardDeckManager) getField(controller, "deckManager");
        FlashcardDeck actualDeck = (FlashcardDeck) actualMgr.getDecks().get(0);
        assertTrue(actualDeck.getDeck().isEmpty());
    }

    /**
     * Tests setting the current username with various input cases.
     * Ensures that null, empty, and whitespace inputs do not overwrite the username.
     */
    @Test
    void testSetCurrentUsernameCases() {
        controller.setCurrentUsername("newuser");
        assertEquals("newuser", getField(controller, "currentUsername"));
        controller.setCurrentUsername(null);
        assertEquals("newuser", getField(controller, "currentUsername"));
        controller.setCurrentUsername("");
        assertEquals("newuser", getField(controller, "currentUsername"));
        controller.setCurrentUsername("   ");
        assertEquals("newuser", getField(controller, "currentUsername"));
    }

    /**
     * Tests setting the deck manager with various input cases.
     * Covers valid, null, and duplicate deck scenarios.
     */
    @Test
    void testSetDeckManagerCases() {
        FlashcardDeckManager mgr = new FlashcardDeckManager();
        FlashcardDeck deck = new FlashcardDeck("Deck1");
        deck.addFlashcard(new Flashcard("Q1", "A1"));
        mgr.addDeck(deck);
        controller.setDeckManager(mgr, deck);
        FlashcardDeckManager actualMgr = (FlashcardDeckManager) getField(controller, "deckManager");
        assertNotNull(actualMgr);
        assertEquals("Deck1", ((FlashcardDeck)actualMgr.getDecks().get(0)).getDeckName());
        assertEquals(1, ((FlashcardDeck)actualMgr.getDecks().get(0)).getDeck().size());
        assertThrows(NullPointerException.class, () -> controller.setDeckManager(null, null));
        assertThrows(NullPointerException.class, () -> controller.setDeckManager(mgr, null));
        FlashcardDeckManager mgr2 = new FlashcardDeckManager();
        FlashcardDeck deck2 = new FlashcardDeck("Deck2");
        assertDoesNotThrow(() -> controller.setDeckManager(mgr2, deck2));
        mgr2.addDeck(deck2);
        assertDoesNotThrow(() -> controller.setDeckManager(mgr2, deck2));
        FlashcardDeckManager mgr3 = new FlashcardDeckManager();
        assertDoesNotThrow(() -> controller.setDeckManager(mgr3, new FlashcardDeck("DeckX")));
    }

    /**
     * Tests card creation logic for multiple input and API response cases.
     * Covers valid, empty, whitespace, and API failure scenarios.
     */
    @Test
    void testWhenCreateButtonIsClickedCases() {
        FlashcardDeckManager mgr = new FlashcardDeckManager();
        FlashcardDeck deck = new FlashcardDeck("Deck1");
        mgr.addDeck(deck);
        controller.setDeckManager(mgr, deck);
        setField(controller, "currentDeckName", "Deck1");
        setField(controller, "deckManager", mgr);
        questionField.setText("Q2");
        answerField.setText("A2");
        try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
            apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any())).thenReturn(new ApiResponse<>(true, "", null));
            controller.whenCreateButtonIsClicked();
        }
        FlashcardDeck actualDeck = (FlashcardDeck) mgr.getDecks().get(0);
        assertEquals(1, actualDeck.getDeck().size());
        assertEquals("Q2", actualDeck.getDeck().get(0).getQuestion());
        questionField.setText("");
        answerField.setText("");
        try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
            apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any())).thenReturn(new ApiResponse<>(true, "", null));
            assertDoesNotThrow(() -> controller.whenCreateButtonIsClicked());
        }
        assertEquals(1, actualDeck.getDeck().size());
        questionField.setText("   ");
        answerField.setText("   ");
        try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
            apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any())).thenReturn(new ApiResponse<>(true, "", null));
            assertDoesNotThrow(() -> controller.whenCreateButtonIsClicked());
        }
        assertEquals(1, actualDeck.getDeck().size());
        questionField.setText("Q3");
        answerField.setText("A3");
        try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
            apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any())).thenReturn(new ApiResponse<>(false, "error", null));
            assertDoesNotThrow(() -> controller.whenCreateButtonIsClicked());
        }
        assertEquals(2, actualDeck.getDeck().size());
    }

    /**
     * Tests card deletion logic for multiple selection and API response cases.
     * Covers valid, no selection, and API failure scenarios.
     */
    @Test
    void testWhenDeleteCardButtonIsClickedCases() {
        FlashcardDeckManager mgr = new FlashcardDeckManager();
        FlashcardDeck deck = new FlashcardDeck("Deck1");
        deck.addFlashcard(new Flashcard("Q1", "A1"));
        deck.addFlashcard(new Flashcard("Q2", "A2"));
        mgr.addDeck(deck);
        controller.setDeckManager(mgr, deck);
        setField(controller, "currentDeckName", "Deck1");
        setField(controller, "deckManager", mgr);
        listView.setItems(FXCollections.observableArrayList(deck.getDeck()));
        listView.getSelectionModel().select(0);
        try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
            apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any())).thenReturn(new ApiResponse<>(true, "", null));
            controller.whenDeleteCardButtonIsClicked();
        }
        assertEquals(1, deck.getDeck().size());
        listView.getSelectionModel().clearSelection();
        try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
            apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any())).thenReturn(new ApiResponse<>(true, "", null));
            assertDoesNotThrow(() -> controller.whenDeleteCardButtonIsClicked());
        }
        assertEquals(1, deck.getDeck().size());
        listView.getSelectionModel().select(0);
        try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
            apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any())).thenReturn(new ApiResponse<>(false, "error", null));
            assertDoesNotThrow(() -> controller.whenDeleteCardButtonIsClicked());
        }
        assertEquals(0, deck.getDeck().size());
        deck.addFlashcard(new Flashcard("Q3", "A3"));
        listView.setItems(FXCollections.observableArrayList(deck.getDeck()));
        listView.getSelectionModel().select(0);
        try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
            apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any())).thenReturn(new ApiResponse<>(true, "", null));
            assertDoesNotThrow(() -> controller.whenDeleteCardButtonIsClicked());
        }
        assertEquals(0, deck.getDeck().size());
    }

    /**
     * Tests setDeckManager method for all major branches.
     * Covers null manager, null deck, deck not in manager, duplicate deck, and duplicate deck name.
     */
    @Test
    void testSetDeckManagerBranches() {
        // deckManager == null
        FlashcardDeck deck = new FlashcardDeck("Deck1");
        assertThrows(NullPointerException.class, () -> controller.setDeckManager(null, deck));
        // deck == null
        FlashcardDeckManager mgr = new FlashcardDeckManager();
        assertThrows(NullPointerException.class, () -> controller.setDeckManager(mgr, null));
        // deck not in manager
        FlashcardDeck deck2 = new FlashcardDeck("Deck2");
        assertDoesNotThrow(() -> controller.setDeckManager(mgr, deck2));
        // deck already in manager
        mgr.addDeck(deck2);
        assertDoesNotThrow(() -> controller.setDeckManager(mgr, deck2));
        // duplicate deck name, different object
        FlashcardDeck deck3 = new FlashcardDeck("Deck2");
        assertDoesNotThrow(() -> controller.setDeckManager(mgr, deck3));
    }

    /**
     * Tests updateUi method for all major branches.
     * Covers null manager, null deck name, deck not found, empty deck, and non-empty deck.
     */
    @Test
    void testUpdateUiBranches() {
        // deckManager == null
        setField(controller, "deckManager", null);
        controller.updateUi();
        assertTrue(listView.getItems().isEmpty());
        // currentDeckName == null
        setField(controller, "deckManager", new FlashcardDeckManager());
        setField(controller, "currentDeckName", null);
        controller.updateUi();
        assertTrue(listView.getItems().isEmpty());
        // deck not found
        setField(controller, "deckManager", new FlashcardDeckManager());
        setField(controller, "currentDeckName", "NonExistent");
        controller.updateUi();
        assertTrue(listView.getItems().isEmpty());
        // deck found, empty
        FlashcardDeckManager mgr = new FlashcardDeckManager();
        FlashcardDeck deck = new FlashcardDeck("Deck1");
        mgr.addDeck(deck);
        setField(controller, "deckManager", mgr);
        setField(controller, "currentDeckName", "Deck1");
        controller.updateUi();
        assertTrue(listView.getItems().isEmpty());
        // deck found, not empty
        deck.addFlashcard(new Flashcard("Q", "A"));
        controller.updateUi();
        assertFalse(listView.getItems().isEmpty());
    }

    /**
     * Tests saveUserData method for all major branches.
     * Covers null username, null deck name, and API failure scenarios.
     */
    @Test
    void testSaveUserDataBranches() {
        // currentUsername == null
        setField(controller, "currentUsername", null);
        setField(controller, "currentDeckName", "Deck1");
        try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
            apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any())).thenReturn(new ApiResponse<>(true, "", null));
            assertDoesNotThrow(() -> invokePrivate(controller, "saveUserData"));
        }
        // currentDeckName == null
        setField(controller, "currentUsername", "testuser");
        setField(controller, "currentDeckName", null);
        try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
            apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any())).thenReturn(new ApiResponse<>(true, "", null));
            assertDoesNotThrow(() -> invokePrivate(controller, "saveUserData"));
        }
        // API returns failure
        setField(controller, "currentUsername", "testuser");
        setField(controller, "currentDeckName", "Deck1");
        try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
            apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any())).thenReturn(new ApiResponse<>(false, "error", null));
            assertDoesNotThrow(() -> invokePrivate(controller, "saveUserData"));
        }
    }

    /**
     * Tests getCurrentDeck method for all major branches.
     * Covers null manager, deck not found, and deck found scenarios.
     */
    @Test
    void testGetCurrentDeckBranches() {
        // deckManager == null
        setField(controller, "deckManager", null);
        setField(controller, "currentDeckName", "Deck1");
        Exception ex = assertThrows(RuntimeException.class, () -> invokePrivate(controller, "getCurrentDeck"));
        assertTrue(ex.getCause() instanceof java.lang.reflect.InvocationTargetException);
        assertTrue(ex.getCause().getCause() instanceof NullPointerException);
        // deck not found
        FlashcardDeckManager mgr = new FlashcardDeckManager();
        setField(controller, "deckManager", mgr);
        setField(controller, "currentDeckName", "NonExistent");
        Object deck = invokePrivate(controller, "getCurrentDeck");
        assertNull(deck);
        // deck found
        FlashcardDeck deckObj = new FlashcardDeck("Deck1");
        mgr.addDeck(deckObj);
        setField(controller, "currentDeckName", "Deck1");
        deck = invokePrivate(controller, "getCurrentDeck");
        assertNotNull(deck);
    }

    /**
     * Tests whenCreateButtonIsClicked method for all major branches.
     * Covers null manager, null deck name, empty/whitespace input, and API failure scenarios.
     */
    @Test
    void testWhenCreateButtonIsClickedBranches() {
        // deckManager == null
        setField(controller, "deckManager", null);
        setField(controller, "currentDeckName", "Deck1");
        questionField.setText("Q");
        answerField.setText("A");
        assertThrows(NullPointerException.class, () -> controller.whenCreateButtonIsClicked());
        // currentDeckName == null
        FlashcardDeckManager mgr = new FlashcardDeckManager();
        setField(controller, "deckManager", mgr);
        setField(controller, "currentDeckName", null);
        questionField.setText("Q");
        answerField.setText("A");
        try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
            apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any())).thenReturn(new ApiResponse<>(true, "", null));
            assertDoesNotThrow(() -> controller.whenCreateButtonIsClicked());
        }
        // question/answer empty
        setField(controller, "currentDeckName", "Deck1");
        questionField.setText("");
        answerField.setText("");
        try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
            apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any())).thenReturn(new ApiResponse<>(true, "", null));
            assertDoesNotThrow(() -> controller.whenCreateButtonIsClicked());
        }
        // question/answer whitespace
        questionField.setText("   ");
        answerField.setText("   ");
        try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
            apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any())).thenReturn(new ApiResponse<>(true, "", null));
            assertDoesNotThrow(() -> controller.whenCreateButtonIsClicked());
        }
        // API returns failure
        questionField.setText("Q");
        answerField.setText("A");
        try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
            apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any())).thenReturn(new ApiResponse<>(false, "error", null));
            assertDoesNotThrow(() -> controller.whenCreateButtonIsClicked());
        }
    }

    /**
     * Tests whenDeleteCardButtonIsClicked method for all major branches.
     * Covers null manager, null deck name, no selection, and API failure scenarios.
     */
    @Test
    void testWhenDeleteCardButtonIsClickedBranches() {
        // deckManager == null
        setField(controller, "deckManager", null);
        setField(controller, "currentDeckName", "Deck1");
        listView.setItems(FXCollections.observableArrayList());
        listView.getSelectionModel().clearSelection();
        assertThrows(NullPointerException.class, () -> controller.whenDeleteCardButtonIsClicked());
        // currentDeckName == null
        FlashcardDeckManager mgr = new FlashcardDeckManager();
        setField(controller, "deckManager", mgr);
        setField(controller, "currentDeckName", null);
        listView.setItems(FXCollections.observableArrayList());
        listView.getSelectionModel().clearSelection();
        try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
            apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any())).thenReturn(new ApiResponse<>(true, "", null));
            assertDoesNotThrow(() -> controller.whenDeleteCardButtonIsClicked());
        }
        // no selection
        setField(controller, "currentDeckName", "Deck1");
        listView.setItems(FXCollections.observableArrayList());
        listView.getSelectionModel().clearSelection();
        try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
            apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any())).thenReturn(new ApiResponse<>(true, "", null));
            assertDoesNotThrow(() -> controller.whenDeleteCardButtonIsClicked());
        }
        // API returns failure
        FlashcardDeck deck = new FlashcardDeck("Deck1");
        deck.addFlashcard(new Flashcard("Q", "A"));
        mgr.addDeck(deck);
        listView.setItems(FXCollections.observableArrayList(deck.getDeck()));
        listView.getSelectionModel().select(0);
        try (MockedStatic<ApiClient> apiClientMock = Mockito.mockStatic(ApiClient.class)) {
            apiClientMock.when(() -> ApiClient.performApiRequest(any(), any(), any(), any())).thenReturn(new ApiResponse<>(false, "error", null));
            assertDoesNotThrow(() -> controller.whenDeleteCardButtonIsClicked());
        }
    }
}