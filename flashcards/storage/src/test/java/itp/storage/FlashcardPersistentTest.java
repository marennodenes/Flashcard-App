package itp.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import app.Flashcard;
import app.FlashcardDeck;
import app.FlashcardDeckManager;
import app.PasswordEncoder;
import app.User;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Test class for verifying the persistence functionality of Flashcard decks.
 * This class contains unit tests to ensure that Flashcard decks can be written
 * and managed correctly using the {@link FlashcardPersistent} class.
 *
 * @author chrsom 
 * @author isamw
 * @author sofietw
 *
 * @see FlashcardPersistent
 *
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
   * This method is automatically called after each test method execution.
   * Removes all test user JSON files and any blocking files created during
   * directory failure tests to maintain a clean test environment.
   *
   * @see "docs/release_3/ai_tools.md"
   */
  @AfterEach
  public void cleanup() {
    // Delete all test user files
    String[] testUsers = {"test_decks", "test_read", "test_user", "user_@.-~", "test_exists_user", 
                          "test_read_user", "test_duplicate_user", "test_malformed_json", 
                          "test_dir_exists", "test_existing_dir_user"}; // Added here
    for (String username : testUsers) {
      File userFile = new File(System.getProperty("user.dir") + "/../storage/data/users/"
          + username + ".json");
      if (userFile.exists()) {
        userFile.delete();
      }
    }
    
    // Clean up any blocking files that might have been created during directory failure tests
    File blockingFile = new File(System.getProperty("user.dir")
        + "/../storage/data/blocking_file");
    if (blockingFile.exists()) {
      blockingFile.delete();
    }
  }

  /**
   * Tests the functionality of writing a {@link FlashcardDeck} to persistent storage.
   *
   * <p>This test creates two sample decks with flashcards, adds them to a deck manager,
   * and verifies that the decks can be written to persistent storage without errors.
   * It also validates that the JSON file is created correctly and contains the expected data.
   *
   * @throws IOException if an I/O error occurs during the write operation
   * 
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
    File dataFile = new File(System.getProperty("user.dir")
        + "/../storage/data/users", "test_decks" + ".json");
    assertTrue(dataFile.exists());
    String jsonContent = Files.readString(dataFile.toPath());
    // Check that the JSON content contains expected data
    assertTrue(jsonContent.contains("Deck1"));
    assertTrue(jsonContent.contains("Q3"));
    assertTrue(jsonContent.contains("A4"));
  }


  /**
   * Tests the functionality of reading a {@link FlashcardDeck} from persistent storage.
   * 
   * <p>This test writes sample deck data to persistent storage, reads it back, and verifies
   * that the contents of the loaded deck match the original data exactly.
   * It ensures data integrity during the serialization/deserialization process.
   *
   * @throws IOException if an I/O error occurs during the read or write operation
   * 
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
      assertEquals(manager.getDecks().get(i).getDeckName(), loadedManager.getDecks().get(i)
          .getDeckName());
      assertEquals(manager.getDecks().get(i).getDeck().size(), loadedManager.getDecks().get(i)
          .getDeck().size());
      assertEquals(manager.getDecks().get(i).getDeck().get(0).getAnswer(), loadedManager
          .getDecks().get(i).getDeck().get(0).getAnswer());
    }
  }

  /**
   * Tests that the dataExists method correctly identifies whether a user's data file exists.
   * 
   * <p>This test verifies the dataExists functionality by creating a test file,
   * confirming it exists, deleting it, and then verifying that it no longer exists.
   * This ensures proper file existence checking in the persistence layer.
   *
   * @throws IOException if an error occurs during file operations
   * 
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
    File dataFile = new File(System.getProperty("user.dir") + "/../storage/data/users", "test_user"
        + ".json");
    dataFile.delete();

    // Check that the file no longer exists
    assertTrue(!persistent.dataExists("test_user"));
  }
  
  
  /**
   * Tests that writeDeck handles usernames with special characters correctly.
   * 
   * <p>This test verifies that the persistence layer can handle usernames containing
   * special characters (@, -, ., ~, _) without issues. It ensures that files can be
   * successfully created and written when the username contains these characters,
   * which is important for supporting diverse username formats.
   *
   * @throws IOException if an error occurs during file operations
   * 
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
    File file = new File(System.getProperty("user.dir") + "/../storage/data/users", specialUsername
        + ".json");
    assertTrue(file.exists());
  }


  /**
   * Tests that readDeck returns an empty FlashcardDeckManager when reading a non-existing file.
   * 
   * <p>This test ensures graceful handling of missing data without throwing exceptions.
   * When attempting to read deck data for a user that doesn't exist, the method
   * should return an empty FlashcardDeckManager rather than throwing an error,
   * allowing the application to handle new users properly.
   *
   * @throws IOException if an unexpected error occurs during the read operation
   * 
   */
  @Test
  public void testReadNonExistingDeck() throws IOException {
    FlashcardDeckManager manager = persistent.readDeck("non_existing_user");
    // Should return an empty manager
    assertTrue(manager.getDecks().isEmpty());
  }

  /**
   * Tests the userExists method to verify it correctly identifies existing users.
   * 
   * <p>This test validates the user existence checking functionality by creating a user,
   * verifying that userExists returns true for existing users, and confirming that
   * it returns false for non-existing users. This is essential for authentication
   * and user management operations.
   *
   * @throws IOException if an error occurs during user creation
   * 
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
   * Tests userExists method with corrupted user data to cover all branches.
   * 
   * <p>This test creates files with incomplete user data to test edge cases and ensure
   * the userExists method properly handles corrupted or incomplete data files.
   * It specifically tests scenarios where user data exists but contains null values
   * for critical fields like username or password.
   *
   * <p>Test coverage includes:
   * - User data with null username (should return false)
   * - User data with null password (should return false)
   * - Proper JSON format handling with @JsonUnwrapped annotations
   *
   * @throws IOException if an error occurs during file operations
   * 
   */
  @Test
  public void testUserExistsWithIncompleteData() throws IOException {
    // Test case 1: File exists but username is null
    // Because of @JsonUnwrapped, username and password are at root level, not under "user"
    String testUsername1 = "test_null_username";
    File userFile1 = new File(System.getProperty("user.dir") + "/../storage/data/users/"
        + testUsername1 + ".json");
    userFile1.getParentFile().mkdirs();
    
    // Correct JSON format with @JsonUnwrapped - no nested "user" object
    String jsonWithNullUsername = "{\"username\":null,\"password\":\"hashedPassword\","
        + "\"deckManager\":{\"decks\":[]}}";
    Files.writeString(userFile1.toPath(), jsonWithNullUsername);
    
    // Should return false because username is null
    boolean result1 = persistent.userExists(testUsername1);
    assertTrue(!result1, "Should return false when username is null");
    
    // Test case 2: File exists, username is valid, but password is null  
    String testUsername2 = "test_null_password";
    File userFile2 = new File(System.getProperty("user.dir") + "/../storage/data/users/"
        + testUsername2 + ".json");
    
    // Correct JSON format - password at root level is null
    String jsonWithNullPassword = "{\"username\":\"validUser\",\"password\":null,"
        + "\"deckManager\":{\"decks\":[]}}";
    Files.writeString(userFile2.toPath(), jsonWithNullPassword);
    
    // Should return false because password is null
    boolean result2 = persistent.userExists(testUsername2);
    assertTrue(!result2, "Should return false when password is null");
    
    // Clean up test files
    userFile1.delete();
    userFile2.delete();
  }

  /**
   * Tests the readUserData method to verify it correctly reads user credentials.
   * 
   * <p>This test validates the user data reading functionality by creating a user with
   * specific credentials, reading it back, and verifying that the data matches.
   * It also tests password verification using the PasswordEncoder since passwords
   * are stored in encrypted form. Additionally, it tests the null return behavior
   * for non-existing users.
   *
   * @throws IOException if an error occurs during user operations
   * 
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
   * Tests that readUserDataInternal handles malformed JSON files by throwing RuntimeException.
   * 
   * <p>This test creates a malformed JSON file to trigger the IOException catch block
   * in the readUserDataInternal method, ensuring it throws a RuntimeException when a file
   * exists but cannot be parsed. This is crucial for robustness when dealing with
   * corrupted or manually modified data files.
   * 
   * <p>The test specifically targets the exception handling in readUserDataInternal
   * to achieve 100% code coverage by exercising the catch block that handles
   * JSON parsing errors.
   *
   * @throws IOException if an error occurs during file operations
   * 
   */
  @Test
  public void testReadUserDataInternalWithMalformedJson() throws IOException {
    String testUsername = "test_malformed_json";
    
    // Create a file with malformed JSON content
    File userFile = new File(System.getProperty("user.dir") + "/../storage/data/users/"
        + testUsername + ".json");
    userFile.getParentFile().mkdirs(); // Ensure directory exists
    
    // Write malformed JSON content to the file
    String malformedJson = "{ invalid json content without proper structure }";
    Files.write(userFile.toPath(), malformedJson.getBytes());
    
    // Verify the file exists
    assertTrue(userFile.exists());
    
    // Try to read user data - should throw RuntimeException due to malformed JSON
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      persistent.readUserData(testUsername);
    });
    
    // Verify the exception message contains expected information
    assertTrue(exception.getMessage().contains("Failed to read user data file for user"));
    assertTrue(exception.getMessage().contains(testUsername));
    
    // Clean up the test file
    userFile.delete();
  }

  /**
   * Tests that writeUserDataInternal throws an IOException when directory creation fails.
   * 
   * <p>This comprehensive test covers both branches of the directory creation logic
   * in writeUserDataInternal to achieve 100% code coverage. It tests both the case
   * where the directory already exists (normal operation) and the case where
   * directory creation fails (error condition).
   * 
   * <p>The test manipulates the system property user.dir to create scenarios where
   * directory creation will fail, ensuring that the error handling code path
   * is properly tested and covered.
   * 
   * <p>Test scenarios:
   * 1. Directory already exists - tests the normal path where dataDir.exists() is true
   * 2. Directory creation failure - tests the error path where mkdirs() fails
   *
   * @throws IOException if an error occurs during test setup or execution
   * 
   */
  @Test
  public void testWriteUserDataInternalDirectoryCreationFailure() throws IOException {
    // Save the original system property
    String originalUserDir = System.getProperty("user.dir");
    
    try {
      // First test: directory already exists (dataDir.exists() == true)
      String testUsername1 = "test_dir_exists";
      User testUser1 = new User(testUsername1, "password123");
      
      // Make sure the directory exists
      File dataDir = new File(System.getProperty("user.dir") + "/../storage/data/users");
      dataDir.mkdirs();
      
      // This should work normally since directory already exists
      persistent.writeUserData(testUser1);
      assertTrue(persistent.userExists(testUsername1));
      
      // Second test: Force mkdirs() to fail
      // Point to a path where we cannot create directories (e.g., inside a file)
      String testUsername2 = "test_dir_failure";
      
      // Create a regular file to block directory creation
      File blockingFile = new File(originalUserDir + "/../storage/data/blocking_file");
      blockingFile.getParentFile().mkdirs();
      Files.write(blockingFile.toPath(), "blocking content".getBytes());
      
      User testUser2 = new User(testUsername2, "password123");

      // Set user.dir to point inside this file (impossible location)
      System.setProperty("user.dir", blockingFile.getAbsolutePath() + "/impossible");
      
      IOException exception = assertThrows(IOException.class, () -> {
        persistent.writeUserData(testUser2);
      });
      
      assertTrue(exception.getMessage().contains("Failed to create directory"));
      
      // Clean up
      blockingFile.delete();
      
    } finally {
      // Always restore the original system property
      System.setProperty("user.dir", originalUserDir);
    }
  }

  /**
   * Tests that writeUserData throws an IOException when trying to create a duplicate user.
   * 
   * <p>This test verifies the error handling for duplicate user creation attempts.
   * The persistence layer should prevent overwriting existing user data by throwing
   * an IOException when attempting to create a user that already exists. This is
   * important for data integrity and preventing accidental user data loss.
   *
   * @throws IOException if an error occurs during user operations (expected for duplicate user)
   * 
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
   * Tests writeUserData functionality when the target directory already exists.
   * 
   * <p>This test specifically covers the branch in writeUserDataInternal where
   * the directory already exists, ensuring that the condition dataDir.exists()
   * returns true and the mkdirs() call is skipped. This is part of achieving
   * 100% branch coverage for the directory creation logic.
   *
   * @throws IOException if an error occurs during user creation
   * 
   */
  @Test
  public void testWriteUserDataWithExistingDirectory() throws IOException {
    String username = "test_existing_dir_user";
    
    // Clean up first in case user exists from previous test run
    File userFile = new File(System.getProperty("user.dir") + "/../storage/data/users/"
        + username + ".json");
    if (userFile.exists()) {
      userFile.delete();
    }
    
    // Ensure directory exists
    File dataDir = new File(System.getProperty("user.dir") + "/../storage/data/users");
    dataDir.mkdirs();
    assertTrue(dataDir.exists(), "Directory should exist before test");
    
    // Now create a user - this hits the branch where dataDir.exists() == true
    User testUser = new User(username, "password123");
    persistent.writeUserData(testUser);
    
    // Verify it worked
    assertTrue(persistent.userExists(username));
  }

  /**
   * Tests that writeDeck throws an IOException when trying to write deck data for a non-existing
   * user.
   * 
   * <p>This test verifies the error handling for attempting to write flashcard deck data
   * for a user that doesn't exist in the persistence storage. The method should throw
   * an IOException with an appropriate error message, preventing data corruption and
   * ensuring that deck data is only written for valid, existing users.
   *
   * @throws IOException expected when attempting to write deck for non-existing user
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
  