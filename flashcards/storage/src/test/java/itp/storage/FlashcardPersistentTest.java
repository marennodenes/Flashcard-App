package itp.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import app.Flashcard;
import app.FlashcardDeck;
import app.FlashcardDeckManager;
import app.PasswordEncoder;
import app.User;


/**
 * Test class for verifying the persistence functionality of Flashcard decks.
 * This class contains unit tests to ensure that Flashcard decks can be written
 * and managed correctly using the {@link FlashcardPersistent} class.
 * 
 * @author chrsom 
 * @author isamw
 * @see FlashcardPersistent
 */
@Tag("storage")
public class FlashcardPersistentTest {

  /**
   * Instance of {@link FlashcardPersistent} used for testing persistence operations.
   */
  FlashcardPersistent persistent = new FlashcardPersistent();

  /**
   * Cleanup method that runs after each test to delete test user files.
   * Ensures tests don't interfere with each other by removing test data.
   */
  @AfterEach
  public void cleanup() {
    // Delete all test user files
    String[] testUsers = {"test_decks", "test_read", "test_user", "user_@.-~", "test_exists_user", "test_read_user", "test_duplicate_user"};
    for (String username : testUsers) {
      File userFile = new File(System.getProperty("user.dir") + "/../storage/data/users/" + username + ".json");
      if (userFile.exists()) {
        userFile.delete();
      }
    }
  }

   /**
   * Tests the functionality of writing a {@link FlashcardDeck} to persistent storage.
   * This test creates two sample decks, adds flashcards to them, and verifies
   * that the decks can be written to persistent storage without errors.
   * 
   * @throws IOException if an I/O error occurs during the write operation
   */
  @Test
  public void testWriteDeck() throws IOException {
    // Setup: Create user first
    User testUser = new User("test_decks", "password123");
    persistent.writeUserData(testUser);

    // Setup: write test data
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
  }


  /**
   * Tests the functionality of reading a {@link FlashcardDeck} from persistent storage.
   * This test writes a sample deck to persistent storage, reads it back, and verifies
   * that the contents of the deck match the original.
   * 
   * @throws IOException if an I/O error occurs during the read or write operation
   */
  @Test
  public void testReadDeck() throws IOException {
    // Setup: Create user first
    User testUser = new User("test_read", "password123");
    persistent.writeUserData(testUser);

    // Setup: write test data
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
    // Setup: Create user first
    User testUser = new User("test_user", "password123");
    persistent.writeUserData(testUser);

    FlashcardDeckManager manager = new FlashcardDeckManager();
    persistent.writeDeck("test_user", manager);

    // Check that the file exists
    assertTrue(persistent.dataExists("test_user"));

    // Manually delete the file to test the negative case
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

      // Setup: Create user first
      User testUser = new User(specialUsername, "password123");
      persistent.writeUserData(testUser);

      FlashcardDeckManager manager = new FlashcardDeckManager();

      FlashcardDeck deck1 = new FlashcardDeck("Deck1");
      deck1.addFlashcard(new Flashcard("Q1", "A1"));
      deck1.addFlashcard(new Flashcard("Q2", "A2"));
      manager.addDeck(deck1);

      persistent.writeDeck(specialUsername, manager);

      // Verify that the file was created successfully
      File file = new File(System.getProperty("user.dir") + "/../storage/data/users", specialUsername + ".json");
      assertTrue(file.exists());
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

  /**
   * Tests the userExists method to verify it correctly identifies existing users.
   * Creates a user, verifies userExists returns true, then checks that
   * it returns false for a non-existing user.
   *
   * @throws IOException if an error occurs during user creation
   */
  @Test
  public void testUserExists() throws IOException {
    // Test that non-existing user returns false
    assertTrue(!persistent.userExists("non_existing_user"));

    // Create a user
    User testUser = new User("test_exists_user", "password123");
    persistent.writeUserData(testUser);

    // Verify userExists returns true for existing user
    assertTrue(persistent.userExists("test_exists_user"));

    // Verify it still returns false for non-existing user
    assertTrue(!persistent.userExists("another_non_existing_user"));
  }

  /**
   * Tests the readUserData method to verify it correctly reads user credentials.
   * Creates a user with specific credentials, reads it back, and verifies
   * the username matches and password can be verified using PasswordEncoder.
   * Also tests that reading non-existing user data returns null.
   *
   * @throws IOException if an error occurs during user operations
   */
  @Test
  public void testReadUserData() throws IOException {
    // Test reading non-existing user returns null
    User nonExistingUser = persistent.readUserData("non_existing_user");
    assertTrue(nonExistingUser == null);

    // Create a user with specific credentials
    String username = "test_read_user";
    String password = "securePassword456";
    User testUser = new User(username, password);
    persistent.writeUserData(testUser);

    // Read the user data back
    User readUser = persistent.readUserData(username);

    // Verify the user was read correctly
    assertTrue(readUser != null);
    assertEquals(username, readUser.getUsername());

    // Verify password using PasswordEncoder since passwords are encrypted
    assertTrue(PasswordEncoder.matches(password, readUser.getPassword()));

    // Verify that wrong password doesn't match
    assertTrue(!PasswordEncoder.matches("wrongPassword", readUser.getPassword()));
  }

  /**
   * Tests that writeUserData throws an IOException when trying to create
   * a user that already exists. Verifies the error handling for duplicate users.
   *
   * @throws IOException if an error occurs during user operations
   */
  @Test
  public void testWriteUserDataDuplicateUser() throws IOException {
    // Create a user
    String username = "test_duplicate_user";
    User testUser = new User(username, "password123");
    persistent.writeUserData(testUser);

    // Try to create the same user again - should throw IOException
    User duplicateUser = new User(username, "differentPassword");
    IOException exception = assertThrows(IOException.class, () -> {
      persistent.writeUserData(duplicateUser);
    });

    // Verify the exception message
    assertTrue(exception.getMessage().contains("User already exists"));
    assertTrue(exception.getMessage().contains(username));
  }

  /**
   * Tests that writeDeck throws an IOException when trying to write deck data
   * for a user that doesn't exist. Verifies the error handling for missing users.
   * 
   */
  @Test
  public void testWriteDeckNonExistingUser() {
    // Try to write deck for a non-existing user - should throw IOException
    String nonExistingUsername = "non_existing_deck_user";
    FlashcardDeckManager manager = new FlashcardDeckManager();
    FlashcardDeck deck = new FlashcardDeck("TestDeck");
    manager.addDeck(deck);

    IOException exception = assertThrows(IOException.class, () -> {
      persistent.writeDeck(nonExistingUsername, manager);
    });

    // Verify the exception message
    assertTrue(exception.getMessage().contains("User does not exist"));
    assertTrue(exception.getMessage().contains(nonExistingUsername));
  }

}

