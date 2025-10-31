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
        assertEquals("Login failed, invalid\nusername or password.", ApiConstants.LOGIN_FAILED);
        assertEquals("Login success.", ApiConstants.LOGIN_SUCCESS);
        assertEquals("Invalid password", ApiConstants.INVALID_PASSWORD);
        // Test detailed password validation messages
        assertEquals("Password must be at\nleast 8 characters long.", ApiConstants.PASSWORD_TOO_SHORT);
        assertEquals("Password must contain at\nleast one uppercase letter.", ApiConstants.PASSWORD_MISSING_UPPERCASE);
        assertEquals("Password must contain at\nleast one lowercase letter.", ApiConstants.PASSWORD_MISSING_LOWERCASE);
        assertEquals("Password must contain\nat least one digit.", ApiConstants.PASSWORD_MISSING_DIGIT);
        assertEquals("Password must contain at\nleast one special character.", ApiConstants.PASSWORD_MISSING_SPECIAL);
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
    }
}