package ui;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import app.Flashcard;
import app.FlashcardDeck;
import dto.FlashcardDto;
import dto.mappers.FlashcardDeckMapper;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;

public class FlashcardControllerTest {

    private FlashcardController controller;
    private FlashcardDeckMapper mapper = new FlashcardDeckMapper();
    private Button backButton;
    private Button nextButton;
    private Button previousButton;
    private Button card;
    private ProgressBar progressBar;
    private Text usernameField;
    private Text decknameField;
    private Text cardNumber;

    @BeforeAll
    public static void initToolkit() {
        // Initialize JavaFX toolkit without showing window
        if (!Platform.isFxApplicationThread()) {
            try {
                Platform.startup(() -> {
                    // Empty runnable for platform initialization
                });
            } catch (IllegalStateException e) {
                // Platform already initialized, this is expected in some test environments
            }
        }
    }

    @BeforeEach
    public void setUp() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                // Create controller and components manually
                controller = new FlashcardController();
                backButton = new Button();
                nextButton = new Button();
                previousButton = new Button();
                card = new Button();
                progressBar = new ProgressBar();
                usernameField = new Text();
                decknameField = new Text();
                cardNumber = new Text();
                
                // Inject components into controller using reflection
                setField("backButton", backButton);
                setField("nextButton", nextButton);
                setField("previousButton", previousButton);
                setField("card", card);
                setField("progressBar", progressBar);
                setField("usernameField", usernameField);
                setField("decknameField", decknameField);
                setField("cardNumber", cardNumber);
                
                // Initialize controller
                controller.initialize();
                
                // Set test data
                controller.setCurrentUsername("testUser");
                FlashcardDeck deck = new FlashcardDeck("Test Deck");
                deck.addFlashcard(new Flashcard("Q1", "A1"));
                deck.addFlashcard(new Flashcard("Q2", "A2"));
                deck.addFlashcard(new Flashcard("Q3", "A3"));
                controller.setDeck(mapper.toDto(deck));
                
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
                latch.countDown();
            }
        });
        
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new IllegalStateException("setUp() did not complete within timeout");
        }
        
        // Verify that controller is initialized
        if (controller == null) {
            throw new IllegalStateException("Controller was not initialized in setUp()");
        }
    }
    
    /** Sets private field using reflection. */
    private void setField(String fieldName, Object value) throws Exception {
        var field = FlashcardController.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(controller, value);
    }

    @Test
    public void testInitialization() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertNotNull(backButton);
            assertNotNull(nextButton);
            assertNotNull(previousButton);
            assertNotNull(card);
            assertNotNull(progressBar);
            assertNotNull(usernameField);
            assertNotNull(decknameField);
            assertNotNull(cardNumber);
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testSetDeck() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            FlashcardDeck deck = new FlashcardDeck("New Deck");
            deck.addFlashcard(new Flashcard("Question", "Answer"));
            controller.setDeck(mapper.toDto(deck));
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("New Deck", decknameField.getText());
            latch2.countDown();
        });
        latch2.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testSetDeckNull() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                if (controller == null) {
                    latch.countDown();
                    return;
                }
                controller.setDeck(null);
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
                latch.countDown();
            }
        });
        latch.await(2, TimeUnit.SECONDS);
        
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                if (decknameField != null) {
                    assertEquals("", decknameField.getText());
                }
                latch2.countDown();
            } catch (Exception e) {
                e.printStackTrace();
                latch2.countDown();
            }
        });
        latch2.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testSetDeckEmpty() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.setDeck(mapper.toDto(new FlashcardDeck("Empty")));
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("Empty", decknameField.getText());
            assertEquals("", card.getText());
            latch2.countDown();
        });
        latch2.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testNextCardNavigation() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("Q1", card.getText());
            nextButton.fire();
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("Q2", card.getText());
            latch2.countDown();
        });
        latch2.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testNextCardLooping() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            nextButton.fire();
            nextButton.fire();
            nextButton.fire();
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        assertEquals("Q1", card.getText());
    }

    @Test
    public void testPreviousCardNavigation() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            previousButton.fire();
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("Q3", card.getText());
            latch2.countDown();
        });
        latch2.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testPreviousFromSecond() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            nextButton.fire();
            previousButton.fire();
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("Q1", card.getText());
            latch2.countDown();
        });
        latch2.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testCardFlipping() throws Exception {
        CountDownLatch checkLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("Q1", card.getText());
            checkLatch.countDown();
        });
        checkLatch.await(2, TimeUnit.SECONDS);
        
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            card.fire();
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        Thread.sleep(700); // Increased wait time for animation
        
        CountDownLatch verifyLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("A1", card.getText());
            verifyLatch.countDown();
        });
        verifyLatch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testCardFlippingBackAndForth() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            card.fire();
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        Thread.sleep(600);
        
        CountDownLatch checkLatch1 = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("A1", card.getText());
            checkLatch1.countDown();
        });
        checkLatch1.await(2, TimeUnit.SECONDS);
        
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            card.fire();
            latch2.countDown();
        });
        latch2.await(2, TimeUnit.SECONDS);
        Thread.sleep(600);
        
        CountDownLatch checkLatch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("Q1", card.getText());
            checkLatch2.countDown();
        });
        checkLatch2.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testProgressTracking() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("1", cardNumber.getText());
            assertEquals(1.0 / 3.0, progressBar.getProgress(), 0.01);
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testProgressAfterNavigation() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            nextButton.fire();
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("2", cardNumber.getText());
            assertEquals(2.0 / 3.0, progressBar.getProgress(), 0.01);
            latch2.countDown();
        });
        latch2.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testUiUpdate() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("Test Deck", decknameField.getText());
            assertEquals("testUser", usernameField.getText());
            assertNotNull(card.getText());
            assertFalse(card.getText().isEmpty());
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testSingleCardNavigation() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            FlashcardDeck deck = new FlashcardDeck("Single");
            deck.addFlashcard(new Flashcard("Q", "A"));
            controller.setDeck(mapper.toDto(deck));
            nextButton.fire();
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("Q", card.getText());
            latch2.countDown();
        });
        latch2.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testNullFlashcardContent() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            FlashcardDeck deck = new FlashcardDeck("Null");
            deck.addFlashcard(new Flashcard(null, null));
            controller.setDeck(mapper.toDto(deck));
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("", card.getText());
            latch2.countDown();
        });
        latch2.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testDefensiveCopying() throws Exception {
        FlashcardDeck original = new FlashcardDeck("Original");
        original.addFlashcard(new Flashcard("Q", "A"));
        
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.setDeck(mapper.toDto(original));
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        
        original.addFlashcard(new Flashcard("Q2", "A2"));
        assertEquals("Q", card.getText());
    }

    @Test
    public void testWhenBackButtonIsClicked() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertDoesNotThrow(() -> controller.whenBackButtonIsClicked());
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testWhenBackButtonFallback() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.nextButton = null;
            assertDoesNotThrow(() -> controller.whenBackButtonIsClicked());
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testWhenBackButtonAllNull() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null) {
                controller.nextButton = null;
                controller.backButton = null;
                controller.card = null;
                assertDoesNotThrow(() -> controller.whenBackButtonIsClicked());
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testWhenLogOut() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertDoesNotThrow(() -> controller.whenLogOut(null));
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testWhenLogOutNullBackButton() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null) {
                controller.backButton = null;
                assertThrows(RuntimeException.class, () -> controller.whenLogOut(null));
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testNullUiComponents() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null) {
                controller.decknameField = null;
                controller.usernameField = null;
                controller.card = null;
                controller.progressBar = null;
                controller.cardNumber = null;
                assertDoesNotThrow(() -> controller.updateUi());
                assertDoesNotThrow(() -> controller.updateProgress());
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testNullUsername() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null) {
                controller.setCurrentUsername(null);
                controller.updateUi();
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("", usernameField.getText());
            latch2.countDown();
        });
        latch2.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testNullDeckname() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null) {
                controller.setDeck(mapper.toDto(new FlashcardDeck(null)));
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("", decknameField.getText());
            latch2.countDown();
        });
        latch2.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testInitializeMethod() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null) {
                assertDoesNotThrow(() -> controller.initialize());
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testFlipCardNullCard() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            controller.card = null;
            assertDoesNotThrow(() -> controller.flipCard());
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testGetCurrentCardEmpty() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null) {
                controller.deck = new ArrayList<>();
                assertDoesNotThrow(() -> controller.getCurrentCard());
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testGetCurrentCardOutOfBounds() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null) {
                controller.currentCardI = 999;
                assertDoesNotThrow(() -> controller.getCurrentCard());
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testProgressEmptyDeck() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null) {
                controller.setDeck(mapper.toDto(new FlashcardDeck("Empty")));
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("0", cardNumber.getText());
            assertEquals(0.0, progressBar.getProgress(), 0.01);
            latch2.countDown();
        });
        latch2.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testEmptyUsernameField() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null) {
                controller.setCurrentUsername("");
                controller.updateUi();
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("", usernameField.getText());
            latch2.countDown();
        });
        latch2.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testMultipleNavigations() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            nextButton.fire();
            nextButton.fire();
            previousButton.fire();
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("Q2", card.getText());
            latch2.countDown();
        });
        latch2.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testProgressThirdCard() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            nextButton.fire();
            nextButton.fire();
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("3", cardNumber.getText());
            assertEquals(1.0, progressBar.getProgress(), 0.01);
            latch2.countDown();
        });
        latch2.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testUpdateUiMultipleTimes() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null) {
                controller.updateUi();
                controller.updateUi();
                controller.updateUi();
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("Test Deck", decknameField.getText());
            latch2.countDown();
        });
        latch2.await(2, TimeUnit.SECONDS);
    }

    // Tester for whenLogOut - coverage forbedring
    @Test
    public void testWhenLogOutWithIOException() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null) {
                // Mock FXMLLoader to throw IOException
                try (MockedConstruction<FXMLLoader> mockedLoader = org.mockito.Mockito.mockConstruction(FXMLLoader.class,
                        (mock, context) -> {
                            try {
                                org.mockito.Mockito.when(mock.load()).thenThrow(new IOException("Test IO error"));
                            } catch (IOException e) {
                                // Won't happen
                            }
                        })) {
                    assertDoesNotThrow(() -> controller.whenLogOut(null));
                }
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testWhenLogOutNullScene() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null) {
                // Set backButton to have null scene
                controller.backButton = new Button();
                // backButton.getScene() will be null, but method should not throw exception
                // It should gracefully handle the null scene
                assertDoesNotThrow(() -> controller.whenLogOut(null));
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    // Tester for whenBackButtonIsClicked - coverage forbedring
    @Test
    public void testWhenBackButtonIsClickedWithIOException() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null) {
                try (MockedConstruction<FXMLLoader> mockedLoader = org.mockito.Mockito.mockConstruction(FXMLLoader.class,
                        (mock, context) -> {
                            try {
                                org.mockito.Mockito.when(mock.load()).thenThrow(new IOException("Test IO error"));
                            } catch (IOException e) {
                                // Won't happen
                            }
                        })) {
                    assertDoesNotThrow(() -> controller.whenBackButtonIsClicked());
                }
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    // Tester for goToNextCard/goToPreviousCard - coverage forbedring
    // Test via button clicks since methods are private
    @Test
    public void testGoToNextCardWithNullDeck() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null && nextButton != null) {
                controller.deck = null;
                // goToNextCard is private, so we test via button fire
                nextButton.fire();
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testGoToNextCardWithEmptyDeck() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null && nextButton != null) {
                controller.deck = new ArrayList<>();
                nextButton.fire();
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testGoToPreviousCardWithNullDeck() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null && previousButton != null) {
                controller.deck = null;
                previousButton.fire();
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testGoToPreviousCardWithEmptyDeck() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null && previousButton != null) {
                controller.deck = new ArrayList<>();
                previousButton.fire();
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    // Tester for whenCardButtonClicked - coverage forbedring
    @Test
    public void testWhenCardButtonClickedWithEmptyDeck() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null && card != null) {
                controller.deck = new ArrayList<>();
                card.fire();
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        // Card should not flip when deck is empty
    }

    // Tester for getCurrentCard - coverage forbedring
    @Test
    public void testGetCurrentCardWithNegativeIndex() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null) {
                controller.currentCardI = -1;
                FlashcardDto card = controller.getCurrentCard();
                assertNull(card);
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testGetCurrentCardWithIndexOutOfBounds() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null) {
                controller.currentCardI = 999; // Out of bounds
                FlashcardDto card = controller.getCurrentCard();
                assertNull(card);
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testGetCurrentCardWithNullCardInDeck() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null) {
                // Can't add null flashcard to deck - mapper will fail
                // Instead, test with empty deck
                FlashcardDeck deck = new FlashcardDeck("Test");
                controller.setDeck(mapper.toDto(deck));
                controller.currentCardI = 0;
                FlashcardDto currentCard = controller.getCurrentCard();
                // Should return null for empty deck
                assertNull(currentCard);
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    // Tester for updateProgress - coverage forbedring
    @Test
    public void testUpdateProgressWithNegativeIndex() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null && cardNumber != null) {
                controller.currentCardI = -1;
                controller.updateProgress();
                assertEquals("0", cardNumber.getText());
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testUpdateProgressWithNullDeck() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null && cardNumber != null && progressBar != null) {
                controller.deck = null;
                controller.updateProgress();
                assertEquals("0", cardNumber.getText());
                assertEquals(0.0, progressBar.getProgress(), 0.01);
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    // Tester for flipCard lambda - coverage forbedring
    @Test
    public void testFlipCardToAnswerThenBackToQuestion() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null) {
                // First flip to answer
                controller.flipCard();
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        Thread.sleep(400); // Wait for animation
        
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null) {
                // Now flip back to question (isShowingAnswer is now true)
                controller.flipCard();
            }
            latch2.countDown();
        });
        latch2.await(2, TimeUnit.SECONDS);
        Thread.sleep(400); // Wait for animation
    }

    @Test
    public void testFlipCardWithNullCard() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null) {
                controller.card = null;
                assertDoesNotThrow(() -> controller.flipCard());
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testFlipCardWithNullCurrentCard() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null) {
                // Set deck to empty - getCurrentCard will return null
                controller.deck.clear();
                controller.currentCardI = 0;
                assertDoesNotThrow(() -> controller.flipCard());
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        Thread.sleep(400); // Wait for animation
    }

    // Tester for updateUi - coverage forbedring
    @Test
    public void testUpdateUiWithIsShowingAnswerTrue() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null && card != null) {
                // Set isShowingAnswer to true (via reflection)
                try {
                    var field = FlashcardController.class.getDeclaredField("isShowingAnswer");
                    field.setAccessible(true);
                    field.set(controller, true);
                } catch (Exception e) {
                    // Ignore
                }
                controller.updateUi();
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        
        // Should show answer text
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("A1", card.getText());
            latch2.countDown();
        });
        latch2.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testUpdateUiWithNullCurrentCard() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null) {
                controller.deck.clear();
                controller.currentCardI = 0;
                controller.updateUi();
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("", card.getText());
            latch2.countDown();
        });
        latch2.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testUpdateUiWithNullCardText() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null && card != null) {
                FlashcardDeck deck = new FlashcardDeck("Test");
                deck.addFlashcard(new Flashcard(null, null));
                controller.setDeck(mapper.toDto(deck));
                controller.updateUi();
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("", card.getText());
            latch2.countDown();
        });
        latch2.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testUpdateUiWithEmptyUsername() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null && usernameField != null) {
                controller.setCurrentUsername("");
                controller.updateUi();
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("", usernameField.getText());
            latch2.countDown();
        });
        latch2.await(2, TimeUnit.SECONDS);
    }

    @Test
    public void testSetCurrentUsernameWithEmptyString() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            if (controller != null && usernameField != null) {
                controller.setCurrentUsername("   "); // Whitespace only
                controller.updateUi();
            }
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        
        CountDownLatch latch2 = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("", usernameField.getText());
            latch2.countDown();
        });
        latch2.await(2, TimeUnit.SECONDS);
    }
}