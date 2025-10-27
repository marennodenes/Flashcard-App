package shared;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link ApiEndpoints} class.
 * <p>
 * This test class verifies that all endpoint constants and URL-building methods
 * in {@code ApiEndpoints} return the expected values. It covers server config,
 * base paths, endpoint URLs, and the getUserDecksUrl method.
 * @author marennod
 * @author ailinat
 */
public class ApiEndpointsTest {

    /**
     * Tests server configuration constants in {@link ApiEndpoints}.
     * Verifies default host, port, and base URL values.
     */
    @Test
    void testServerConfigConstants() {
        assertEquals("localhost", ApiEndpoints.SERVER_HOST);
        assertEquals("8080", ApiEndpoints.SERVER_PORT);
        assertEquals("http://localhost:8080", ApiEndpoints.SERVER_BASE_URL);
    }

    /**
     * Tests base and user endpoint constants in {@link ApiEndpoints}.
     * Verifies base path and user-related endpoints.
     */
    @Test
    void testBaseAndUserEndpoints() {
        assertEquals("/api", ApiEndpoints.BASE);
        assertEquals("/api/users", ApiEndpoints.USERS);
        assertEquals("/api/v1/users/login", ApiEndpoints.LOGIN);
        assertEquals("/api/v1/users/register", ApiEndpoints.REGISTER);
    }

    /**
     * Tests flashcard endpoint constants in {@link ApiEndpoints}.
     * Verifies flashcard-related endpoint paths.
     */
    @Test
    void testFlashcardEndpoints() {
        assertEquals("/api/v1/flashcards", ApiEndpoints.FLASHCARDS);
        assertEquals("/create", ApiEndpoints.FLASHCARD_CREATE);
        assertEquals("/get", ApiEndpoints.FLASHCARD_GET);
        assertEquals("/get-all", ApiEndpoints.FLASHCARD_GET_ALL);
        assertEquals("/delete", ApiEndpoints.FLASHCARD_DELETE);
    }

    /**
     * Tests deck endpoint constants in {@link ApiEndpoints}.
     * Verifies deck-related endpoint paths.
     */
    @Test
    void testDeckEndpoints() {
        assertEquals("/api/v1/decks", ApiEndpoints.DECKS);
        assertEquals("/create", ApiEndpoints.DECK_CREATE);
        assertEquals("/get", ApiEndpoints.DECK_GET);
        assertEquals("/get-all", ApiEndpoints.DECK_GET_ALL);
        assertEquals("/delete", ApiEndpoints.DECK_DELETE);
    }

    /**
     * Tests user endpoint constants in {@link ApiEndpoints}.
     * Verifies user v1 endpoints and user operation paths.
     */
    @Test
    void testUserOperationEndpoints() {
        assertEquals("/api/v1/users", ApiEndpoints.USERS_V1);
        assertEquals("/register", ApiEndpoints.USER_REGISTER);
        assertEquals("/login", ApiEndpoints.USER_LOGIN);
        assertEquals("/logout", ApiEndpoints.USER_LOGOUT);
        assertEquals("/validate-password", ApiEndpoints.USER_VALIDATE_PASSWORD);
        assertEquals("/profile", ApiEndpoints.USER_PROFILE);
        assertEquals("/find", ApiEndpoints.USER_FIND);
    }

    /**
     * Tests complete API URLs in {@link ApiEndpoints}.
     * Verifies that LOGIN_URL and REGISTER_URL are built correctly.
     */
    @Test
    void testCompleteApiUrls() {
        assertEquals("http://localhost:8080/api/v1/users/login", ApiEndpoints.LOGIN_URL);
        assertEquals("http://localhost:8080/api/v1/users/register", ApiEndpoints.REGISTER_URL);
    }

    /**
     * Tests the getUserDecksUrl method in {@link ApiEndpoints}.
     * Verifies that the returned URL includes the username as a query parameter.
     */
    @Test
    void testGetUserDecksUrl() {
        String username = "testuser";
        String expectedUrl = "http://localhost:8080/api/v1/decks?username=testuser";
        assertEquals(expectedUrl, ApiEndpoints.getUserDecksUrl(username));
    }
}
