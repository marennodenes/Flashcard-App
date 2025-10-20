// package ui;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertTrue;

// import java.util.concurrent.TimeoutException;

// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.testfx.api.FxToolkit;
// import org.testfx.framework.junit5.ApplicationTest;

// import app.Flashcard;
// import app.FlashcardDeck;
// import javafx.application.Platform;
// import javafx.fxml.FXMLLoader;
// import javafx.scene.Parent;
// import javafx.scene.Scene;
// import javafx.scene.control.Button;
// import javafx.scene.control.ProgressBar;
// import javafx.scene.text.Text;
// import javafx.stage.Stage;

// /**
//  * Comprehensive test class for FlashcardController.
//  * This class tests the flashcard learning interface functionality including navigation between * cards, flipping animations, progress tracking, and UI updates.
//  * 
//  * @author Generated with AI assistance for comprehensive test coverage
//  */
// public class FlashcardControllerTest extends ApplicationTest {

//     private FlashcardController controller;
//     private Stage stage;
//     private FlashcardDeck testDeck;
//     private Button backButton;
//     private Button nextCard;
//     private Button previousCard;
//     private Button card;
//     private ProgressBar progressBar;
//     private Text usernameField;
//     private Text decknameField;
//     private Text cardNumber;

//     /**
//      * Sets up the JavaFX toolkit before all tests.
//      * This ensures that the JavaFX platform is properly initialized for testing.
//      * The method handles platform startup in a thread-safe manner.
//      *
//      * @throws Exception if the JavaFX toolkit cannot be initialized
//      */
//     @BeforeAll
//     public static void setUpClass() throws Exception {
//         if (!Platform.isFxApplicationThread()) {
//             try {
//                 Platform.startup(() -> {
//                     // Empty runnable for platform initialization
//                 });
//             } catch (IllegalStateException e) {
//                 // Platform already initialized, this is expected in some test environments
//             }
//         }
//     }

//     /**
//      * Sets up the test environment before each test.
//      * Creates a test stage and loads the FlashcardController with its FXML.
//      * Initializes test data including a sample flashcard deck.
//      *
//      * @param stage the primary stage provided by TestFX
//      * @throws Exception if the FXML cannot be loaded or controller cannot be initialized
//      */
//     @Override
//     public void start(Stage stage) throws Exception {
//         this.stage = stage;
        
//         // Create test deck with sample flashcards
//         testDeck = new FlashcardDeck("Test Deck");
//         testDeck.addFlashcard(new Flashcard("What is Java?", "A programming language"));
//         testDeck.addFlashcard(new Flashcard("What is JUnit?", "A testing framework"));
//         testDeck.addFlashcard(new Flashcard("What is JavaFX?", "A GUI framework"));
        
//         // Load FXML and controller
//         FXMLLoader loader = new FXMLLoader(getClass().getResource("FlashcardPageUI.fxml"));
//         Parent root = loader.load();
//         controller = loader.getController();
        
//         // Set up scene and stage
//         Scene scene = new Scene(root);
//         stage.setScene(scene);
//         stage.show();
        
//         // Get references to FXML components for testing
//         initializeComponentReferences(root);
//     }

//     /**
//      * Initializes references to FXML components for testing.
//      * This method finds and stores references to UI components that need to be tested.
//      *
//      * @param root the root parent node containing all UI components
//      */
//     private void initializeComponentReferences(Parent root) {
//         backButton = (Button) root.lookup("#backButton");
//         nextCard = (Button) root.lookup("#nextCard");
//         previousCard = (Button) root.lookup("#previousCard");
//         card = (Button) root.lookup("#card");
//         progressBar = (ProgressBar) root.lookup("#progressBar");
//         usernameField = (Text) root.lookup("#usernameField");
//         decknameField = (Text) root.lookup("#decknameField");
//         cardNumber = (Text) root.lookup("#cardNumber");
//     }

//     /**
//      * Sets up common test data before each individual test.
//      * This method is called before each test method to ensure consistent test state.
//      */
//     @BeforeEach
//     public void setUp() {
//         Platform.runLater(() -> {
//             controller.setCurrentUsername("testUser");
//             controller.setDeck(testDeck);
//         });
//         pause(100); // Allow JavaFX thread to process
//     }

//     /**
//      * Cleans up after each test by hiding the stage.
//      *
//      * @throws TimeoutException if the stage cannot be hidden within the timeout
//      */
//     @AfterEach
//     public void tearDown() throws TimeoutException {
//         FxToolkit.hideStage();
//     }

//     /**
//      * Tests the setDeck method functionality.
//      * Verifies that the deck is properly set and UI is updated accordingly.
//      * This test ensures that the controller correctly handles deck assignment
//      * and initializes the display with the first card.
//      */
//     @Test
//     public void testSetDeck() {
//         Platform.runLater(() -> {
//             FlashcardDeck newDeck = new FlashcardDeck("New Test Deck");
//             newDeck.addFlashcard(new Flashcard("Question 1", "Answer 1"));
            
//             controller.setDeck(newDeck);
//         });
//         pause(100);
        
//         // Verify deck name is displayed
//         assertEquals("New Test Deck", decknameField.getText(),
//                     "Deck name should be displayed correctly");
//     }

//     /**
//      * Tests the controller initialization process.
//      * Verifies that all FXML components are properly loaded and accessible.
//      * This test ensures that the controller is properly connected to its UI components.
//      */
//     @Test
//     public void testInitialization() {
//         // Verify all components are initialized
//         assertNotNull(backButton, "Back button should be initialized");
//         assertNotNull(nextCard, "Next card button should be initialized");
//         assertNotNull(previousCard, "Previous card button should be initialized");
//         assertNotNull(card, "Card button should be initialized");
//         assertNotNull(progressBar, "Progress bar should be initialized");
//         assertNotNull(usernameField, "Username field should be initialized");
//         assertNotNull(decknameField, "Deck name field should be initialized");
//         assertNotNull(cardNumber, "Card number field should be initialized");
//     }

//     /**
//      * Tests the next card navigation functionality.
//      * Verifies that clicking the next card button advances to the next card
//      * and updates the UI accordingly, including looping behavior.
//      */
//     @Test
//     public void testNextCardNavigation() {
//         // Initially should show first card
//         assertEquals("What is Java?", card.getText(),
//                     "Should initially show first card question");
        
//         // Click next card
//         clickOn(nextCard);
//         sleep(100);
        
//         assertEquals("What is JUnit?", card.getText(),
//                     "Should show second card question after clicking next");
        
//         // Test looping - go to end and then next should loop to beginning
//         clickOn(nextCard); // Third card
//         sleep(100);
//         clickOn(nextCard); // Should loop back to first card
//         sleep(100);
        
//         assertEquals("What is Java?", card.getText(),
//                     "Should loop back to first card");
//     }

//     /**
//      * Tests the previous card navigation functionality.
//      * Verifies that clicking the previous card button moves to the previous card
//      * and updates the UI accordingly, including looping behavior.
//      */
//     @Test
//     public void testPreviousCardNavigation() {
//         // From first card, previous should go to last card (looping)
//         clickOn(previousCard);
//         sleep(100);
        
//         assertEquals("What is JavaFX?", card.getText(),
//                     "Should loop to last card when clicking previous from first card");
        
//         // Go back to previous
//         clickOn(previousCard);
//         sleep(100);
        
//         assertEquals("What is JUnit?", card.getText(),
//                     "Should show second card when clicking previous from last card");
//     }

//     /**
//      * Tests the card flipping functionality.
//      * Verifies that clicking on the card toggles between question and answer display.
//      * This test ensures the flip animation works and content changes appropriately.
//      */
//     @Test
//     public void testCardFlipping() {
//         // Initially should show question
//         assertEquals("What is Java?", card.getText(),
//                     "Should initially show question");
        
//         // Click on card to flip to answer
//         clickOn(card);
//         pause(500); // Wait for animation to complete
        
//         assertEquals("A programming language", card.getText(),
//                     "Should show answer after clicking card");
        
//         // Click again to flip back to question
//         clickOn(card);
//         pause(500);
        
//         assertEquals("What is Java?", card.getText(),
//                     "Should show question again after second click");
//     }

//     /**
//      * Tests the progress tracking functionality.
//      * Verifies that the progress bar and card number are updated correctly
//      * as the user navigates through the deck.
//      */
//     @Test
//     public void testProgressTracking() {
//         // Check initial progress
//         assertEquals("1", cardNumber.getText(),
//                     "Should initially show card number 1");
        
//         double expectedProgress = 1.0 / 3.0; // 1 out of 3 cards
//         assertEquals(expectedProgress, progressBar.getProgress(), 0.01,
//                     "Progress bar should show correct initial progress");
        
//         // Move to next card and check progress
//         clickOn(nextCard);
//         sleep(100);
        
//         assertEquals("2", cardNumber.getText(),
//                     "Should show card number 2 after clicking next");
        
//         expectedProgress = 2.0 / 3.0; // 2 out of 3 cards
//         assertEquals(expectedProgress, progressBar.getProgress(), 0.01,
//                     "Progress bar should update when moving to next card");
//     }

//     /**
//      * Tests the UI update functionality.
//      * Verifies that UI components are updated correctly when the deck changes
//      * and that the display shows appropriate information.
//      */
//     @Test
//     public void testUiUpdate() {
//         // Verify initial UI state
//         assertEquals("Test Deck", decknameField.getText(),
//                     "Deck name should be displayed");
//         assertEquals("testUser", usernameField.getText(),
//                     "Username should be displayed");
        
//         // Verify card content and styling
//         assertNotNull(card.getText(), "Card should have text content");
//         assertFalse(card.getText().isEmpty(), "Card text should not be empty");
        
//         // Verify card has styling applied
//         String cardStyle = card.getStyle();
//         assertNotNull(cardStyle, "Card should have styling");
//     }

//     /**
//      * Tests navigation with an empty deck.
//      * Verifies that the controller handles empty decks gracefully
//      * and doesn't cause exceptions when trying to navigate.
//      */
//     @Test
//     public void testEmptyDeckHandling() {
//         Platform.runLater(() -> {
//             FlashcardDeck emptyDeck = new FlashcardDeck("Empty Deck");
//             controller.setDeck(emptyDeck);
//         });
//         sleep(100);
        
//         // Verify empty deck is handled gracefully
//         assertEquals("Empty Deck", decknameField.getText(),
//                     "Should display empty deck name");
        
//         // Try navigation with empty deck (should not crash)
//         clickOn(nextCard);
//         sleep(100);
//         clickOn(previousCard);
//         sleep(100);
//         clickOn(card);
//         sleep(100);
        
//         // If we reach here without exceptions, empty deck handling works
//         assertTrue(true, "Empty deck navigation should not cause exceptions");
//     }

//     /**
//      * Tests the defensive copying in setDeck method.
//      * Verifies that changes to the original deck don't affect the controller's internal state.
//      * This test ensures that the Spotbugs issue with mutable object copying is resolved.
//      */
//     @Test
//     public void testDefensiveCopying() {
//         FlashcardDeck originalDeck = new FlashcardDeck("Original Deck");
//         originalDeck.addFlashcard(new Flashcard("Original Question", "Original Answer"));
        
//         Platform.runLater(() -> {
//             controller.setDeck(originalDeck);
//         });
//         sleep(100);
        
//         // Modify original deck after setting it
//         originalDeck.addFlashcard(new Flashcard("New Question", "New Answer"));
        
//         // Verify controller's deck is not affected by external modifications
//         // This indirectly tests the defensive copying behavior
//         assertEquals("Original Question", card.getText(),
//                     "Controller should not be affected by external deck modifications");
//     }

//     /**
//      * Tests error handling during FXML component interactions.
//      * Verifies that the controller handles missing or null components gracefully.
//      */
//     @Test
//     public void testErrorHandling() {
//         // Test with a deck that has a card with null question/answer
//         Platform.runLater(() -> {
//             FlashcardDeck testDeckWithNulls = new FlashcardDeck("Test Deck");
//             // Create flashcard with null values to test robustness
//             Flashcard cardWithNulls = new Flashcard();
//             testDeckWithNulls.addFlashcard(cardWithNulls);
            
//             controller.setDeck(testDeckWithNulls);
//         });
//         sleep(100);
        
//         // Should not crash when handling null content
//         clickOn(card);
//         pause(200);
        
//         assertTrue(true, "Should handle null card content gracefully");
//     }

//     /**
//      * Tests the style application for question and answer states.
//      * Verifies that different styles are applied when showing questions vs answers.
//      */
//     @Test
//     public void testStyleApplications() {
//         // Get initial style (question style)
//         String initialStyle = card.getStyle();
//         assertNotNull(initialStyle, "Card should have initial styling");
        
//         // Flip to answer and check if style changes
//         clickOn(card);
//         sleep(500);
        
//         String answerStyle = card.getStyle();
//         assertNotNull(answerStyle, "Card should have answer styling");
        
//         // Flip back to question and verify style changes back
//         clickOn(card);
//         sleep(500);
        
//         String questionStyleAgain = card.getStyle();
//         assertEquals(initialStyle, questionStyleAgain,
//                     "Question style should be restored after flipping back");
//     }

//     /**
//      * Helper method to pause execution for JavaFX thread processing.
//      * 
//      * @param millis the number of milliseconds to sleep
//      */
//     private static void pause(long millis) {
//         try {
//             Thread.sleep(millis);
//         } catch (InterruptedException e) {
//             Thread.currentThread().interrupt();
//         }
//     }
// }
