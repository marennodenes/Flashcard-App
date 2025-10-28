package shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link ApiResponse} class.
 * <p>
 * This test class verifies the behavior of the generic ApiResponse wrapper,
 * including success status, message, and data payload handling.
 * @author marennod
 * @author ailinat
 */
public class ApiResponseTest {

    /**
     * Tests the default constructor and initial values of ApiResponse.
     */
    @Test
    void testDefaultConstructor() {
        ApiResponse<String> response = new ApiResponse<>();
        assertFalse(response.isSuccess(), "Default success should be false");
        assertNull(response.getMessage(), "Default message should be null");
        assertNull(response.getData(), "Default data should be null");
    }

    /**
     * Tests the parameterized constructor and getters of ApiResponse.
     */
    @Test
    void testParameterizedConstructor() {
        ApiResponse<Integer> response = new ApiResponse<>(true, "OK", 42);
        assertTrue(response.isSuccess());
        assertEquals("OK", response.getMessage());
        assertEquals(42, response.getData());
    }

    /**
     * Tests the setters and getters for all fields in ApiResponse.
     */
    @Test
    void testSettersAndGetters() {
        ApiResponse<Double> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Success");
        response.setData(3.14);
        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertEquals(3.14, response.getData());
    }

    /**
     * Tests ApiResponse with null data payload.
     */
    @Test
    void testNullDataPayload() {
        ApiResponse<Object> response = new ApiResponse<>(true, "No data", null);
        assertTrue(response.isSuccess());
        assertEquals("No data", response.getMessage());
        assertNull(response.getData());
    }
}
