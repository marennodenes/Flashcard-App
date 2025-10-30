package dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link LoginRequestDto} class.
 * <p>
 * This test class verifies the correct construction and behavior of LoginRequestDto,
 * including username and password handling, and setter/getter methods.
 * @author marennod
 * @author ailinat
 */
public class LoginRequestDtoTest {

    /**
     * Tests the constructor with username and password.
     * Verifies that the fields are set correctly.
     */
    @Test
    void testConstructorWithUsernameAndPassword() {
        LoginRequestDto dto = new LoginRequestDto("alice", "pw");
        assertEquals("alice", dto.getUsername());
        assertEquals("pw", dto.getPassword());
    }

    /**
     * Tests the default constructor and setter methods.
     * Verifies that the fields can be set and retrieved.
     */
    @Test
    void testDefaultConstructorAndSetters() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setUsername("bob");
        dto.setPassword("pass");
        assertEquals("bob", dto.getUsername());
        assertEquals("pass", dto.getPassword());
    }
}
