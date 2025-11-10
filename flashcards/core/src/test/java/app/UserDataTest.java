package app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@link UserData} functionality.
 * Tests user data construction and deck manager initialization scenarios.
 * Validates proper handling of different constructor parameters and defensive copying.
 *
 * @author isamw
 * @author chrsom
 * @see UserData
 */
public class UserDataTest {

  /**
   * Tests the UserData constructor with a null deck manager.
   * Validates that when a null deck manager is provided:
   * - A new empty FlashcardDeckManager is created automatically
   * - The deck manager is properly initialized and accessible
   * - The deck manager starts with zero decks
   */
  @Test
  public void testConstructorWithNullManager() {
    User user = new User("alex", "mypassword");
    UserData userData = new UserData(user, null);

    assertNotNull(userData.getDeckManager());
    assertEquals(0, userData.getDeckManager().getDecks().size());
  }

  /**
   * Tests the UserData constructor with user only.
   * Validates that the single-parameter constructor:
   * - Properly stores the provided user
   * - Creates a new empty FlashcardDeckManager automatically
   * - Initializes the deck manager with zero decks
   */
  @Test
  public void testConstructorUserOnly() {
    User user = new User("sofie", "pass123");
    UserData userData = new UserData(user);

    assertEquals(user, userData.getUser());
    assertNotNull(userData.getDeckManager());
    // This should pass since new FlashcardDeckManager() should be empty
    assertEquals(0, userData.getDeckManager().getDecks().size());
  }

  /**
   * Tests the UserData constructor with both user and deck manager.
   * Validates that when both user and deck manager are provided:
   * - The user is properly stored
   * - A defensive copy of the deck manager is created
   * - All decks from the original manager are copied to the new manager
   * - External modifications to the original manager don't affect the UserData's manager
   */
  @Test
  public void testConstructorUserAndManager() {
    User user = new User("alex", "mypassword");
    FlashcardDeckManager manager = new FlashcardDeckManager();
    FlashcardDeck deck = new FlashcardDeck("Java");
    manager.addDeck(deck);

    UserData userData = new UserData(user, manager);
    assertEquals(1, userData.getDeckManager().getDecks().size());

    manager.addDeck(new FlashcardDeck("Python"));
    assertEquals(1, userData.getDeckManager().getDecks().size());
  } 

  /**
   * Tests the setDeckManager method of UserData.
   * Validates proper handling of:
   * - Null deck manager parameter (creates empty manager)
   * - Empty deck manager setting
   * - Defensive copying behavior (external changes don't affect internal state)
   * - Populated deck manager with correct deck count
   */
  @Test
  public void testSetDeckManager() {
    User user = new User("sofie", "pass123");
    UserData userData = new UserData(user);

    userData.setDeckManager(null);
    assertNotNull(userData.getDeckManager());
    assertEquals(0, userData.getDeckManager().getDecks().size());

    FlashcardDeckManager manager = new FlashcardDeckManager();
    UserData userData2 = new UserData(user);
    userData2.setDeckManager(manager);
    assertEquals(0, userData2.getDeckManager().getDecks().size());
      
    manager.addDeck(new FlashcardDeck("History"));
    assertEquals(0, userData2.getDeckManager().getDecks().size());
    userData2.setDeckManager(manager);
    assertEquals(1, userData2.getDeckManager().getDecks().size());
  }

  /**
   * Tests the getDeckManager method of UserData.
   * Validates that the getDeckManager method returns a defensive copy that:
   * - Prevents external modification of internal state
   * - Allows modifications to the returned copy without affecting the original
   * - Maintains data integrity and encapsulation
   * - Protects against inadvertent mutations from external code
   * This test ensures that adding decks to the returned copy does not modify
   * the internal deck manager of the UserData instance.
   */
  @Test
  public void testGetDeckManagerDefensiveCopy() {
    User user = new User("alex", "mypassword");
    FlashcardDeckManager manager = new FlashcardDeckManager();
    FlashcardDeck deck = new FlashcardDeck("Java");
    manager.addDeck(deck);

    UserData userData = new UserData(user, manager);
    FlashcardDeckManager copy = userData.getDeckManager();

    copy.addDeck(new FlashcardDeck("Python"));

    assertEquals(1, userData.getDeckManager().getDecks().size());
  }

  /**
   * Tests the getUser method of UserData.
   * Validates that the getUser method:
   * - Returns the correct user object that was provided during construction
   * - Preserves the username correctly
   * - Maintains the password data integrity
   * - Provides proper access to user credentials
   */
  @Test
  public void testGetUser() {
    User user = new User("alex", "mypassword");
    UserData userData = new UserData(user);
    assertEquals("alex", userData.getUser().getUsername());
    assertNotNull(userData.getUser().getPassword());
  }
}
