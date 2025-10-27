package shared;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link ApiConstants} class.
 * <p>
 * This test class verifies that all constant values in {@code ApiConstants}
 * match their expected string values. It covers user, flashcard, deck, and general
 * message constants to ensure API responses and error messages remain consistent.
 * @author marennod
 * @author ailinat
 */
public class ApiConstantsTest {

    /**
     * Tests user-related constants in {@link ApiConstants}.
     * Verifies that all user/login/signup messages match their expected values.
     */
    @Test
    void testUserConstants() {
        assertEquals("User not found.", ApiConstants.USER_NOT_FOUND);
        assertEquals("User created successfully.", ApiConstants.USER_CREATED);
        assertEquals("User updated.", ApiConstants.USER_UPDATED);
        assertEquals("User already exists.", ApiConstants.USER_ALREADY_EXISTS);
        assertEquals("Login failed, invalid username or password.", ApiConstants.LOGIN_FAILED);
        assertEquals("Login success.", ApiConstants.LOGIN_SUCCESS);
        assertEquals("Password does not meet requirements.", ApiConstants.PASSWORD_INVALID);
    }

    /**
     * Tests flashcard-related constants in {@link ApiConstants}.
     * Verifies that all flashcard messages match their expected values.
     */
    @Test
    void testFlashcardConstants() {
        assertEquals("Flashcard not found.", ApiConstants.FLASHCARD_NOT_FOUND);
        assertEquals("Flashcard created successfully.", ApiConstants.FLASHCARD_CREATED);
        assertEquals("Flashcard deleted successfully.", ApiConstants.FLASHCARD_DELETED);
    }

    /**
     * Tests deck-related constants in {@link ApiConstants}.
     * Verifies that all deck messages match their expected values.
     */
    @Test
    void testDeckConstants() {
        assertEquals("Deck not found.", ApiConstants.DECK_NOT_FOUND);
        assertEquals("Deck created successfully.", ApiConstants.DECK_CREATED);
        assertEquals("Deck deleted successfully.", ApiConstants.DECK_DELETED);
    }

    /**
     * Tests general constants in {@link ApiConstants}.
     * Verifies that all general error and request messages match their expected values.
     */
    @Test
    void testGeneralConstants() {
        assertEquals("Invalid request.", ApiConstants.INVALID_REQUEST);
        assertEquals("Operation failed.", ApiConstants.OPERATION_FAILED);
    }
}