package itp.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import app.Flashcard;
import app.FlashcardDeck;
import app.FlashcardDeckManager;


/**
 * Test class for verifying the persistence functionality of Flashcard decks.
 * This class contains unit tests to ensure that Flashcard decks can be written
 * and managed correctly using the {@link FlashcardPersistent} class.
 */
@Tag("storage")
public class FlashcardPersistentTest {

  /**
   * Instance of {@link FlashcardPersistent} used for testing persistence operations.
   */
  FlashcardPersistent persistent = new FlashcardPersistent();
  

   /**
   * Tests the functionality of writing a {@link FlashcardDeck} to persistent storage.
   * 
   * <p>This test creates two sample decks, adds flashcards to them, and verifies
   * that the decks can be written to persistent storage without errors.</p>
   * 
   * @throws IOException if an I/O error occurs during the write operation
   */
  @Test
  public void testWriteDeck() throws IOException {
    // Setup: write test data first
    FlashcardDeck deck1 = new FlashcardDeck("Deck1");
    deck1.addFlashcard(new Flashcard("Q1", "A1"));
    deck1.addFlashcard(new Flashcard("Q2", "A2"));

    FlashcardDeck deck2 = new FlashcardDeck("Deck2");
    deck2.addFlashcard(new Flashcard("Q3", "A3"));
    deck2.addFlashcard(new Flashcard("Q4", "A4"));

    FlashcardDeckManager manager = new FlashcardDeckManager();
    manager.addDeck(deck1);
    manager.addDeck(deck2);

    // Write to JSON-file
    persistent.writeDeck("test_decks", manager);

    // Read from JSON-file directly to check content
    File dataFile = new File(System.getProperty("user.dir") + "/../storage/data/users", "test_decks" + ".json");
    assertTrue(dataFile.exists());
    String jsonContent = Files.readString(dataFile.toPath());
    // Check that the JSON content contains expected data
    assertTrue(jsonContent.contains("Deck1"));
    assertTrue(jsonContent.contains("Q3"));
    assertTrue(jsonContent.contains("A4"));
    dataFile.delete();
  }


  /**
   * Tests the functionality of reading a {@link FlashcardDeck} from persistent storage.
   * 
   * <p>This test writes a sample deck to persistent storage, reads it back, and verifies
   * that the contents of the deck match the original.</p>
   * 
   * @throws IOException if an I/O error occurs during the read or write operation
   */
  @Test
  public void testReadDeck() throws IOException {
    // Setup: write test data first
    FlashcardDeck deck1 = new FlashcardDeck("Deck1");
    deck1.addFlashcard(new Flashcard("Q1", "A1"));
    deck1.addFlashcard(new Flashcard("Q2", "A2"));

    FlashcardDeck deck2 = new FlashcardDeck("Deck2");
    deck2.addFlashcard(new Flashcard("Q3", "A3"));
    deck2.addFlashcard(new Flashcard("Q4", "A4"));
    
    FlashcardDeckManager manager = new FlashcardDeckManager();
    manager.addDeck(deck1);
    manager.addDeck(deck2);
    persistent.writeDeck("test_read", manager);
    
    // Test: read the data back
    FlashcardDeckManager loadedManager = persistent.readDeck("test_read");
    
    // Check if the content of the two managers are the same
    for (int i = 0; i < loadedManager.getDecks().size(); i++) {
      assertEquals(manager.getDecks().get(i).getDeckName(), loadedManager.getDecks().get(i).getDeckName());
      assertEquals(manager.getDecks().get(i).getDeck().size(), loadedManager.getDecks().get(i).getDeck().size());
      assertEquals(manager.getDecks().get(i).getDeck().get(0).getAnswer(), loadedManager.getDecks().get(i).getDeck().get(0).getAnswer());
    }
}

  /**
   * Tests that dataExists correctly identifies whether a user's data file exists.
   * Creates a test file, verifies it exists, deletes it, and verifies that it no longer exists.
   * 
   * @throws IOException if an error occurs during file operations
   */
  @Test
  public void testDataExists() throws IOException {

    FlashcardDeckManager manager = new FlashcardDeckManager();
    persistent.writeDeck("test_user", manager);

    // Check that the file  exists
    assertTrue(persistent.dataExists("test_user"));

    File dataFile = new File(System.getProperty("user.dir") + "/../storage/data/users", "test_user" + ".json");
    dataFile.delete();

    // Check that the file no longer exists
    assertTrue(!persistent.dataExists("test_user"));
  }
  
  
  /**
   * Tests that writeDeck handles usernames with special characters (@, -, ., ~, _).
   * Verifies that a file can be successfully created and written when the username
   * contains special characters.
   * 
   * @throws IOException if an error occurs during file operations
   */
  @Test
  public void testFilenameWithSpecialCharacters() throws IOException {
    // Create a username with special characters
      String specialUsername = "user_@.-~";
      FlashcardDeckManager manager = new FlashcardDeckManager();

      FlashcardDeck deck1 = new FlashcardDeck("Deck1");
      deck1.addFlashcard(new Flashcard("Q1", "A1"));
      deck1.addFlashcard(new Flashcard("Q2", "A2"));
      manager.addDeck(deck1);

      persistent.writeDeck(specialUsername, manager);

      // Verify that the file was created successfully
      File file = new File(System.getProperty("user.dir") + "/../storage/data/users", specialUsername + ".json");
      assertTrue(file.exists());
      file.delete();
  }


  /**
   * Tests that readDeck returns an empty FlashcardDeckManager when reading a non-existing file.
   * This ensures graceful handling of missing data without throwing exceptions.
   * 
   * @throws IOException if an unexpected error occurs during the read operation
   */
  @Test
  public void testReadNonExistingDeck() throws IOException {
    FlashcardDeckManager manager = persistent.readDeck("non_existing_user");
    // Should return an empty manager
    assertTrue(manager.getDecks().isEmpty());
}

}
