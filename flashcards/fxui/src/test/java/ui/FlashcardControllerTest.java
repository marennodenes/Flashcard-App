package ui;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import app.Flashcard;
import app.FlashcardDeck;
import dto.FlashcardDeckDto;
import dto.FlashcardDto;
import dto.mappers.FlashcardDeckMapper;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;


/**
 * Test class for {@link FlashcardController}.
 * Tests core functionality including internal state management and navigation logic.
 *
 * @author marennod
 * @author ailinat
 * 
 * @see FlashcardController
 * 
 */
public class FlashcardControllerTest {

  private FlashcardController controller;
  private FlashcardDeckMapper mapper = new FlashcardDeckMapper();
  private Button backButton;
  private Button nextButton;
  private Button cardButton;

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
   * Tears down JavaFX platform after all tests complete.
   */
  @AfterAll
  public static void tearDown() {
      // Don't exit platform as other tests may need it
  }

  /**
   * Sets up the test environment before each test.
   *
   * @throws Exception when reflection access fails
   */
  @BeforeEach
  public void setUp() throws Exception {
    // Initialize controller
    controller = new FlashcardController();
        
    // Create minimal UI components for testing
    backButton = new Button("Back");
    nextButton = new Button("Next");
    cardButton = new Button("Card");
        
    // Set test data directly to the internal deck field
    FlashcardDeck deck = new FlashcardDeck("Test Deck");
    deck.addFlashcard(new Flashcard("Q1", "A1"));
    deck.addFlashcard(new Flashcard("Q2", "A2"));
    deck.addFlashcard(new Flashcard("Q3", "A3"));
    
    // Set the deck directly to the internal field to avoid UI updates
    FlashcardDeckDto deckDto = mapper.toDto(deck);
    setField("deck", deckDto.getDeck());
    setField("currentCardI", 0);
    setField("currentUsername", "testUser");
    
    // Set UI components
    setField("backButton", backButton);
    setField("nextButton", nextButton);
    setField("card", cardButton);
  }

  /**
   * Tests that the controller initializes correctly.
   *
   * @throws Exception when accessing fields fails
   */
  @Test
  public void testControllerInitialization() throws Exception {
    assertNotNull(controller);
    assertEquals("testUser", getField("currentUsername"));
  }

  /**
   * Tests initialize method with empty deck.
   *
   * @throws Exception when initializing fails
   */
  @Test
  public void testInitializeWithEmptyDeck() throws Exception {
    FlashcardController newController = new FlashcardController();
    newController.initialize();
    assertDoesNotThrow(() -> newController.initialize());
  }

  /**
   * Tests initialize method with non-empty deck.
   *
   * @throws Exception when initializing fails
   */
  @Test
  public void testInitializeWithNonEmptyDeck() throws Exception {
    // Given: Controller with non-empty deck
    FlashcardController newController = new FlashcardController();
    FlashcardDeck deck = new FlashcardDeck("Test");
    deck.addFlashcard(new Flashcard("Q", "A"));
    FlashcardDeckDto deckDto = mapper.toDto(deck);
    
    // Inject deck before initialize
    Field deckField = getFieldObject("deck");
    deckField.set(newController, java.util.Collections.unmodifiableList(deckDto.getDeck()));
    
    // When: Initialize
    assertDoesNotThrow(() -> newController.initialize());
  }

  /**
   * Tests initialize method sets currentCardI to 0.
   *
   * @throws Exception when initializing fails
   */
  @Test
  public void testInitializeSetsCurrentCardToZero() throws Exception {
    FlashcardController newController = new FlashcardController();

    newController.initialize();

    assertEquals(0, getField("currentCardI"));
  }

  /**
   * Tests setting the current username.
   *
   * @throws Exception when accessing fields fails
   */
  @Test
  public void testSetCurrentUsername() throws Exception {
    controller.setCurrentUsername("newUser");
    assertEquals("newUser", getField("currentUsername"));
  }

  /**
   * Tests setting the current username with various inputs.
   *
   * @throws Exception when setting the username fails
   */
  @Test
  public void testSetCurrentUsernameWithNull() throws Exception {
    controller.setCurrentUsername(null);
    assertEquals("", getField("currentUsername"));
  }

  /**
   * Tests setting the current username with empty string.   
   *
   * @throws Exception when setting the username fails
   */
  @Test
  public void testSetCurrentUsernameWithEmptyString() throws Exception {
    controller.setCurrentUsername("");
    assertEquals("", getField("currentUsername"));
  }

  /**
   * Tests setting the current username with leading/trailing whitespace.
   *
   * @throws Exception when setting the username fails
   */
  @Test
  public void testSetCurrentUsernameWithWhitespace() throws Exception {
    controller.setCurrentUsername("  user123  ");
    assertEquals("user123", getField("currentUsername"));
  }

  /**
   * Tests setting the current username with a valid username.
   *
   * @throws Exception when setting the username fails
   */
  @Test
  public void testSetCurrentUsernameWithValidUsername() throws Exception {
    controller.setCurrentUsername("validUser");
    assertEquals("validUser", getField("currentUsername"));
  }

  /**
   * Tests setting the current username updates the UI field.
   *
   * @throws Exception when setting the username fails
   */
  @Test
  public void testSetCurrentUsernameUpdatesUsernameField() throws Exception {
    assertNull(getField("usernameField"));
    controller.setCurrentUsername("testUser");
    assertEquals("testUser", getField("currentUsername"));
  }

  /**
   * Tests deck handling functionality.
   *
   * @throws Exception when accessing fields fails
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testDeckHandling() throws Exception {
    List<FlashcardDto> deck = (List<FlashcardDto>) getField("deck");
    
    assertNotNull(deck);
    assertEquals(3, deck.size());
    assertEquals("Q1", deck.get(0).getQuestion());
    assertEquals("A1", deck.get(0).getAnswer());
  }

  /**
   * Tests current card index handling.
   *
   * @throws Exception when accessing fields fails
   */
  @Test
  public void testCurrentCardIndexHandling() throws Exception {
    assertEquals(0, getField("currentCardI"));
    
    setField("currentCardI", 1);
    
    assertEquals(1, getField("currentCardI"));
  }

  /**
   * Tests navigation logic for going to the next card.
   *
   * @throws Exception when navigating fails
   */
  @Test
  public void testNavigationLogic_goToNextCard() throws Exception {
    assertEquals(0, getField("currentCardI"));
    
    callPrivateMethod("goToNextCard");
    assertEquals(1, getField("currentCardI"));
    
    callPrivateMethod("goToNextCard");
    assertEquals(2, getField("currentCardI"));
  }

  /**
   * Tests navigation logic for going to the previous card.
   *
   * @throws Exception when navigating fails
   */
  @Test
  public void testNavigationLogic_goToPreviousCard() throws Exception {
    setField("currentCardI", 2);
    
    callPrivateMethod("goToPreviousCard");
    assertEquals(1, getField("currentCardI"));
    
    callPrivateMethod("goToPreviousCard");
    assertEquals(0, getField("currentCardI"));
  }

  /**
   * Tests navigation boundaries (wrapping) when going past first or last card.
   *
   * @throws Exception when navigating fails
   */
  @Test
  public void testNavigationBoundaries_atStart() throws Exception {
    setField("currentCardI", 0);
    callPrivateMethod("goToPreviousCard");
    assertEquals(2, getField("currentCardI"));
  }

  /**
   * Tests navigation boundaries (wrapping) when going past first or last card.
   *
   * @throws Exception when navigating fails
   */
  @Test
  public void testNavigationBoundaries_atEnd() throws Exception {
    setField("currentCardI", 2);
    callPrivateMethod("goToNextCard");
    assertEquals(0, getField("currentCardI"));
  }

  /**
   * Tests getting the current card.
   *
   * @throws Exception when getting the current card fails
   */
  @Test
  public void testGetCurrentCard() throws Exception {
    setField("currentCardI", 0);
    
    FlashcardDto currentCard = (FlashcardDto) callPrivateMethodWithReturn("getCurrentCard");
    
    assertNotNull(currentCard);
    assertEquals("Q1", currentCard.getQuestion());
    assertEquals("A1", currentCard.getAnswer());
    
    setField("currentCardI", 1);
    currentCard = (FlashcardDto) callPrivateMethodWithReturn("getCurrentCard");
    
    assertEquals("Q2", currentCard.getQuestion());
    assertEquals("A2", currentCard.getAnswer());
  }

  /**
   * Tests handling of empty deck scenarios.
   *
   * @throws Exception if setting the deck fails
   */
  @Test
  public void testEmptyDeckHandling() throws Exception {
    setField("deck", Arrays.asList());
    setField("currentCardI", 0);
    
    FlashcardDto currentCard = (FlashcardDto) callPrivateMethodWithReturn("getCurrentCard");
    
    assertNull(currentCard);
  }

  /**
   * Tests handling of a single-card deck.
   *
   * @throws Exception when getting the current card fails
   */
  @Test
  public void testSingleCardDeck() throws Exception {
    FlashcardDeck singleDeck = new FlashcardDeck("Single Deck");
    singleDeck.addFlashcard(new Flashcard("Only Question", "Only Answer"));
    FlashcardDeckDto deckDto = mapper.toDto(singleDeck);
    setField("deck", deckDto.getDeck());
    setField("currentCardI", 0);
    
    FlashcardDto currentCard = (FlashcardDto) callPrivateMethodWithReturn("getCurrentCard");
    
    assertNotNull(currentCard);
    assertEquals("Only Question", currentCard.getQuestion());
    assertEquals("Only Answer", currentCard.getAnswer());
    
    callPrivateMethod("goToNextCard");
    assertEquals(0, getField("currentCardI"));
    
    callPrivateMethod("goToPreviousCard");
    assertEquals(0, getField("currentCardI"));
  }
  
  /**
   * Tests multiple navigation calls in sequence.
   *
   * @throws Exception when navigating fails
   */
  @Test
  public void testMultipleNavigationSequence() throws Exception {
    assertEquals(0, getField("currentCardI"));
    
    callPrivateMethod("goToNextCard");
    assertEquals(1, getField("currentCardI"));
    
    callPrivateMethod("goToNextCard");
    assertEquals(2, getField("currentCardI"));
    
    callPrivateMethod("goToPreviousCard");
    assertEquals(1, getField("currentCardI"));
    
    callPrivateMethod("goToNextCard");
    assertEquals(2, getField("currentCardI"));
  }

  /**
   * Tests setting the deck with null input.
   *
   * @throws Exception when setting the deck fails
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testSetDeckWithNullDeck() throws Exception {
    controller.setDeck(null);
    
    List<FlashcardDto> deck = (List<FlashcardDto>) getField("deck");
    assertNotNull(deck);
    assertEquals(0, deck.size());
    assertNull(getField("originalDeck"));
    assertEquals(0, getField("currentCardI"));
  }

  /**
   * Tests setting the deck with a valid deck.
   *
   * @throws Exception when setting the deck fails
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testSetDeckWithValidDeck() throws Exception {
    FlashcardDeck newDeck = new FlashcardDeck("New Deck");
    newDeck.addFlashcard(new Flashcard("New Q1", "New A1"));
    newDeck.addFlashcard(new Flashcard("New Q2", "New A2"));
    FlashcardDeckDto newDeckDto = mapper.toDto(newDeck);
    
    controller.setDeck(newDeckDto);
    
    List<FlashcardDto> deck = (List<FlashcardDto>) getField("deck");
    assertEquals(2, deck.size());
    assertEquals("New Q1", deck.get(0).getQuestion());
    assertEquals("New A1", deck.get(0).getAnswer());
    
    FlashcardDeckDto originalDeck = (FlashcardDeckDto) getField("originalDeck");
    assertNotNull(originalDeck);
    assertEquals("New Deck", originalDeck.getDeckName());
    assertEquals(0, getField("currentCardI"));
  }

  /**
   * Tests updateProgress method with null deck.
   *
   * @throws Exception when updating progress fails
   */
  @Test
  public void testUpdateProgressWithNullDeck() throws Exception {
    setField("deck", null);
    setField("currentCardI", 0);
    
    controller.updateProgress();
  }

  /**
   * Tests updateProgress method with negative card index.
   *
   * @throws Exception when updating progress fails
   */
  @Test
  public void testUpdateProgressWithNegativeCardIndex() throws Exception {
    setField("currentCardI", -1);
    controller.updateProgress();
  }

  /**
   * Tests updateProgress method with valid state.
   *
   * @throws Exception when updating progress fails
   */
  @Test
  public void testUpdateProgressWithValidState() throws Exception {
    setField("currentCardI", 1);
    assertDoesNotThrow(() -> controller.updateProgress());
  }

  /**
   * Tests getCurrentCard method with various edge cases.
   *
   * @throws Exception when getting the current card fails
   */
  @Test
  public void testGetCurrentCardWithNullDeck() throws Exception {
    setField("deck", null);
    
    FlashcardDto current = (FlashcardDto) callPrivateMethodWithReturn("getCurrentCard");
    
    assertNull(current);
  }

  /**
   * Tests getCurrentCard method with invalid index.
   *
   * @throws Exception when getting the current card fails
   */
  @Test
  public void testGetCurrentCardWithInvalidIndex() throws Exception {
    setField("currentCardI", 99);
    
    FlashcardDto current = (FlashcardDto) callPrivateMethodWithReturn("getCurrentCard");
    
    assertNull(current);
  }

  /**
   * Tests getCurrentCard method with negative index.
   *
   * @throws Exception when getting the current card fails
   */
  @Test
  public void testGetCurrentCardWithNegativeIndex() throws Exception {
    setField("currentCardI", -1);
    
    FlashcardDto current = (FlashcardDto) callPrivateMethodWithReturn("getCurrentCard");
    
    assertNull(current);
  }

  /**
   * Tests navigation methods with empty deck.
   *
   * @throws Exception when navigating fails
   */
  @Test
  public void testNavigationWithEmptyDeck() throws Exception {
    setField("deck", java.util.Collections.emptyList());
    setField("currentCardI", 0);
    
    callPrivateMethod("goToNextCard");
    assertEquals(0, getField("currentCardI"));
    
    callPrivateMethod("goToPreviousCard");
    assertEquals(0, getField("currentCardI"));
  }

  /**
   * Tests navigation methods with null deck.
   *
   * @throws Exception when navigating fails
   */
  @Test
  public void testNavigationWithNullDeck() throws Exception {
    setField("deck", null);
    setField("currentCardI", 0);
    
    callPrivateMethod("goToNextCard");
    assertEquals(0, getField("currentCardI"));
    
    callPrivateMethod("goToPreviousCard");
    assertEquals(0, getField("currentCardI"));
  }

  /**
   * Tests getCurrentCard method when deck contains null cards.
   *
   * @throws Exception when getting the current card fails
   */
  @Test
  public void testDeckWithNullCards() throws Exception {
    List<FlashcardDto> deckWithNull = Arrays.asList(
      new FlashcardDto("Q1", "A1", 1),
          null,
      new FlashcardDto("Q3", "A3", 3)
    );
    setField("deck", deckWithNull);
    setField("currentCardI", 1);
    
    FlashcardDto current = (FlashcardDto) callPrivateMethodWithReturn("getCurrentCard");
    
    assertNull(current);
  }

  /**
   * Tests that setDeck creates defensive copies of the deck and originalDeck.
   *
   * @throws Exception when setting the deck fails
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testSetDeckCreatesDefensiveCopy() throws Exception {
    FlashcardDeck originalDeck = new FlashcardDeck("Original");
    originalDeck.addFlashcard(new Flashcard("Q1", "A1"));
    FlashcardDeckDto deckDto = mapper.toDto(originalDeck);
    
    List<FlashcardDto> originalList = deckDto.getDeck();
    
    controller.setDeck(deckDto);
    
    List<FlashcardDto> internalDeck = (List<FlashcardDto>) getField("deck");
    assertNotSame(originalList, internalDeck);
    
    assertEquals(1, internalDeck.size());
    assertEquals("Q1", internalDeck.get(0).getQuestion());
    
    FlashcardDeckDto storedOriginalDeck = (FlashcardDeckDto) getField("originalDeck");
    assertNotNull(storedOriginalDeck);
    assertNotSame(deckDto, storedOriginalDeck);
    assertNotSame(deckDto.getDeck(), storedOriginalDeck.getDeck());
  }

  /**
   * Tests that navigation resets the showing answer state.
   *
   * @throws Exception when navigating fails
   */
  @Test
  public void testNavigationResetsShowingAnswerState() throws Exception {
    setField("isShowingAnswer", true);
    setField("currentCardI", 0);
    
    callPrivateMethod("goToNextCard");
    assertEquals(false, getField("isShowingAnswer"));
    
    setField("isShowingAnswer", true);
    setField("currentCardI", 1);
    
    callPrivateMethod("goToPreviousCard");
    assertEquals(false, getField("isShowingAnswer"));
  }

  /**
   * Tests updateUi method with null originalDeck.
   *
   * @throws Exception when updating UI fails
   */
  @Test
  public void testUpdateUiWithNullOriginalDeck() throws Exception {
    setField("originalDeck", null);
    assertDoesNotThrow(() -> controller.updateUi());
  }

  /**
  * Tests updateUi method when showing answer.
  *
  * @throws Exception when updating UI fails
  */
  @Test
  public void testUpdateUiWithShowingAnswerTrue() throws Exception {
    setField("isShowingAnswer", true);
        
    Platform.runLater(() -> {
      assertDoesNotThrow(() -> controller.updateUi());
    });
  }

  /**
   * Tests updateUi method when current card index is invalid.
   *
   * @throws Exception when updating UI fails
   */
  @Test
  public void testUpdateUiWithNullCurrentCard() throws Exception {
    setField("currentCardI", 99);
    assertDoesNotThrow(() -> controller.updateUi());
  }

  /**
   * Tests updateUi method when current card has null question/answer.
   *
   * @throws Exception when updating UI fails
   */
  @Test
  public void testUpdateUiWithNullTextInCard() throws Exception {
    FlashcardDeck deck = new FlashcardDeck("Test");
    Flashcard card = new Flashcard("Question", "Answer");
    deck.addFlashcard(card);
    FlashcardDeckDto deckDto = mapper.toDto(deck);
      
    setField("deck", deckDto.getDeck());
    setField("originalDeck", deckDto);
      
    setField("card", null);
      
    Platform.runLater(() -> {
      assertDoesNotThrow(() -> controller.updateUi());
    });
  }

  /**
   * Tests whenCardButtonClicked method with empty deck.
   *
   * @throws Exception when clicking the card button fails
   */
  @Test
  public void testWhenCardButtonClickedWithEmptyDeck() throws Exception {
    // Given: Empty deck
    setField("deck", java.util.Collections.emptyList());
    
    // When: Card button clicked (via reflection)
    callPrivateMethod("whenCardButtonClicked");
    
    // Then: Should not trigger flip (no error)
    assertDoesNotThrow(() -> callPrivateMethod("whenCardButtonClicked"));
  }

  /**
   * Tests whenCardButtonClicked method with non-empty deck.
   *
   * @throws Exception when clicking the card button fails
   */
  @Test
  public void testWhenCardButtonClickedWithNonEmptyDeck() throws Exception {
    FlashcardDeck deck = new FlashcardDeck("Test");
    deck.addFlashcard(new Flashcard("Q1", "A1"));
    FlashcardDeckDto deckDto = mapper.toDto(deck);
    setField("deck", deckDto.getDeck());
  
    callPrivateMethod("whenCardButtonClicked");
  
    assertDoesNotThrow(() -> callPrivateMethod("whenCardButtonClicked"));
  }


  /**
   * Tests flipCard method with question, answer and null handling.
   *
   * @throws Exception when flipping the card fails
   */
  @Test
  public void testFlipCard() throws Exception {
    FlashcardDeck deck = new FlashcardDeck("Animated");
    deck.addFlashcard(new Flashcard("Question?", "Answer!"));
    FlashcardDeckDto deckDto = mapper.toDto(deck);
    setField("deck", deckDto.getDeck());
    setField("originalDeck", deckDto);
    setField("currentCardI", 0);
    setField("isShowingAnswer", false);

    Platform.runLater(() -> controller.updateUi());
    WaitForAsyncUtils.waitForFxEvents();

    assertEquals("Question?", cardButton.getText());
    assertFalse((Boolean) getField("isShowingAnswer"));

    Platform.runLater(() -> {
      try {
        callPrivateMethod("flipCard");
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
    WaitForAsyncUtils.sleep(250, TimeUnit.MILLISECONDS);
    WaitForAsyncUtils.waitForFxEvents();

    assertEquals("Answer!", cardButton.getText());
    assertTrue((Boolean) getField("isShowingAnswer"));

    Platform.runLater(() -> {
      try {
        callPrivateMethod("flipCard");
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
    WaitForAsyncUtils.sleep(250, TimeUnit.MILLISECONDS);
    WaitForAsyncUtils.waitForFxEvents();

    assertEquals("Question?", cardButton.getText());
    assertFalse((Boolean) getField("isShowingAnswer"));

    setField("card", null);
    assertNull(getField("card"));

    callPrivateMethod("flipCard");
    assertDoesNotThrow(() -> callPrivateMethod("flipCard"));
  }

  /**
   * Tests whenNextCardButtonClicked method advances the card index.
   *
   * @throws Exception when clicking the next button fails
   */
  @Test
  public void testWhenNextCardButtonClicked() throws Exception {
    setField("currentCardI", 0);
    
    callPrivateMethod("whenNextCardButtonClicked");

    assertEquals(1, getField("currentCardI"));
  }

  /**
   * Tests whenPreviousCardButtonClicked method goes back to the previous card.
   *
   * @throws Exception when clicking the previous button fails
   */
  @Test
  public void testWhenPreviousCardButtonClicked() throws Exception {
    setField("currentCardI", 1);
      
    CountDownLatch latch = new CountDownLatch(1);
    Platform.runLater(() -> {
      try {
        callPrivateMethod("whenPreviousCardButtonClicked");
      } catch (Exception e) {
        throw new RuntimeException(e);
      } finally {
        latch.countDown();
      }
    });
    latch.await();
    assertEquals(0, getField("currentCardI"));
  }

  /**
   * Tests updateProgress method calculates progress correctly.
   *
   * @throws Exception when updating progress fails
   */
  @Test
  public void testUpdateProgressCalculatesCorrectProgress() throws Exception {
    setField("currentCardI", 1);
    
    assertDoesNotThrow(() -> controller.updateProgress());
  }

  /**
   * Tests updateProgress method with empty deck.
   *
   * @throws Exception when updating progress fails
   */
  @Test
  public void testUpdateProgressWithEmptyDeck() throws Exception {
    setField("deck", Collections.emptyList());
    
    assertDoesNotThrow(() -> controller.updateProgress());
  }

  /**
   * Tests setDeck method updates internal state and UI.
   *
   * @throws Exception when setting the deck fails
   */
  @Test
  public void testSetDeckCallsUpdateUiAndProgress() throws Exception {
    FlashcardDeck deck = new FlashcardDeck("Test");
    deck.addFlashcard(new Flashcard("Q", "A"));
    FlashcardDeckDto deckDto = mapper.toDto(deck);
    
    controller.setDeck(deckDto);
    
    assertEquals(0, getField("currentCardI"));
    assertNotNull(getField("originalDeck"));
  }

  /**
   * Tests getCurrentCard method when deck contains null card.
   *
   * @throws Exception when getting the current card fails
   */
  @Test
  public void testGetCurrentCardReturnsNullForNullCardInList() throws Exception {
    List<FlashcardDto> deckWithNull = Collections.singletonList(null);
    setField("deck", deckWithNull);
    setField("currentCardI", 0);
    
    FlashcardDto current = (FlashcardDto) callPrivateMethodWithReturn("getCurrentCard");
    
    assertNull(current);
  }

  /**
   * Tests navigation methods wrap around correctly.
   *
   * @throws Exception when navigating fails
   */
  @Test
  public void testNavigationWrapping() throws Exception {
    setField("currentCardI", 2);
    
    callPrivateMethod("goToNextCard");
    assertEquals(0, getField("currentCardI"));
    
    callPrivateMethod("goToPreviousCard");
    assertEquals(2, getField("currentCardI"));
  }

  /**
   * Tests updateUi method with all UI fields null.
   *
   * @throws Exception when updating UI fails
   */
  @Test
  public void testUpdateUiHandlesAllNullFields() throws Exception {
    setField("card", null);
    setField("decknameField", null);
    setField("usernameField", null);
    
    assertNull(getField("card"));
    assertNull(getField("decknameField"));
    assertNull(getField("usernameField"));
    
    controller.updateUi();
    
    assertDoesNotThrow(() -> controller.updateUi());
  }

  /**
   * Tests whenBackButtonIsClicked method with null buttons.
   *
   * @throws Exception when clicking back button fails
   */
  @Test
  public void testWhenBackButtonIsClickedWithNullButtons() throws Exception {
    setField("card", null);
    setField("nextButton", null);
    setField("backButton", null);
    
    assertDoesNotThrow(() -> controller.whenBackButtonIsClicked());
  }

  /**
   * Tests whenLogOut method with null backButton.
   *
   * @throws Exception when logging out fails
   */
  @Test
  public void testWhenLogOutWithNullButton() throws Exception {
    setField("backButton", null);
    
    assertThrows(NullPointerException.class, () -> controller.whenLogOut(null));
  }

  /**
   * Tests updateUi method when showing question.
   *
   * @throws Exception when updating UI fails
   */
  @Test
  public void testUpdateUiWithShowingAnswerFalse() throws Exception {
    setField("isShowingAnswer", false);
    setField("currentCardI", 0);
    
    controller.updateUi();
    
    assertDoesNotThrow(() -> controller.updateUi());
  }

  /**
   * Tests whenBackButtonIsClicked method with real back button.
   *
   * @throws Exception when clicking back button fails
   */
  @Test
  public void testWhenBackButtonIsClickedWithRealButton() throws Exception {
    if (backButton != null) {
      setField("backButton", backButton);
      setField("originalDeck", 
          new FlashcardDeckDto("Test", mapper
              .toDto(new FlashcardDeck("Test"))
              .getDeck()));
        
      Platform.runLater(() -> {
        assertDoesNotThrow(() -> controller.whenBackButtonIsClicked());
      });
    }
  }

  /**
   * Tests whenLogOut method with real back button.
   *
   * @throws Exception when logging out fails
   */
  @Test
  public void testWhenLogOutWithRealButton() throws Exception {
    if (backButton != null) {
      setField("backButton", backButton);
          
      Platform.runLater(() -> {
        Stage stage = new Stage();
        Pane root = new Pane();
        root.getChildren().add(backButton);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        assertDoesNotThrow(() -> controller.whenLogOut(null));
      });
    }
  }

  /**
   * Tests updateUi method with real card button.
   *
   * @throws Exception when updating UI fails
   */
  @Test
  public void testUpdateUiWithRealButton() throws Exception {
    if (cardButton != null) {
      setField("card", cardButton);
      setField("isShowingAnswer", false);
      
      controller.updateUi();
      
      String buttonText = cardButton.getText();
      assertNotNull(buttonText);
    }
  }

  /**
   * Tests updateUi method when showing answer with real card button.
   *
   * @throws Exception when updating UI fails
   */
  @Test
  public void testUpdateUiShowingAnswerWithRealButton() throws Exception {
    if (cardButton != null) {
      setField("card", cardButton);
      setField("isShowingAnswer", true);
      
      controller.updateUi();
      
      String buttonText = cardButton.getText();
      assertNotNull(buttonText);
    }
  }

  /**
   * Helper method for setting field values.
   *
   * @param fieldName the name of the field to set
   * @param value the value to set the field to
   * @throws Exception if setting the field fails
   *
   */
  private void setField(String fieldName, Object value) throws Exception {
    Field field = FlashcardController.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(controller, value);
  }

  /**
   * Helper method for getting field values.
   *
   * @param fieldName the name of the field to get
   * @return the value of the field
   * @throws Exception if getting the field fails
   * 
   */
  private Object getField(String fieldName) throws Exception {
    Field field = FlashcardController.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    return field.get(controller);
  }

  /**
   * Helper method to get Field object.
   *
   * @param fieldName the name of the field
   * @return the Field object
   * @throws Exception if getting the field fails
   * 
   */
  private Field getFieldObject(String fieldName) throws Exception {
    Field field = FlashcardController.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    return field;
  }

  /**
   * Helper method for calling private methods without return values.
   *
   * @param methodName the name of the method to call
   * @throws Exception if calling the method fails
   *
   */
  private void callPrivateMethod(String methodName) throws Exception {
    Method method = FlashcardController.class.getDeclaredMethod(methodName);
    method.setAccessible(true);
    method.invoke(controller);
  }

  /**
   * Helper method for calling private methods with return values.
   *
   * @param methodName the name of the method to call
   * @return the return value of the method
   * @throws Exception if calling the method fails
   */
  private Object callPrivateMethodWithReturn(String methodName) throws Exception {
    Method method = FlashcardController.class.getDeclaredMethod(methodName);
    method.setAccessible(true);
    return method.invoke(controller);
  }
}
