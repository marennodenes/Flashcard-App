package ui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import java.net.http.HttpResponse;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import org.junit.jupiter.api.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Unit tests for the {@link ApiClient} utility class.
 * <p>
 * This test class verifies all public static methods in {@code ApiClient}, including:
 * <ul>
 *   <li>HTTP request handling (GET, POST, PUT)</li>
 *   <li>JSON serialization and deserialization</li>
 *   <li>Error handling and edge cases</li>
 *   <li>Coverage of the default constructor for JaCoCo</li>
 * </ul>
 * <p>
 * All major branches, error paths, and edge cases are tested to ensure robust and maintainable code.
 * @author marennod
 * @author sofietw
 */
public class FlashcardApiClientTest {

    /**
     * Verifies that sendRequest throws IllegalArgumentException for null or blank URI/method.
     */
    @Test
    void testSendRequestThrowsOnNullOrBlankUriOrMethod() {
        assertThrows(IllegalArgumentException.class, () -> ApiClient.sendRequest(null, "GET", null));
        assertThrows(IllegalArgumentException.class, () -> ApiClient.sendRequest(" ", "GET", null));
        assertThrows(IllegalArgumentException.class, () -> ApiClient.sendRequest("http://localhost", null, null));
        assertThrows(IllegalArgumentException.class, () -> ApiClient.sendRequest("http://localhost", " ", null));
    }

    /**
     * Verifies that sendRequest throws IllegalArgumentException for unsupported HTTP methods.
     */
    @Test
    void testSendRequestThrowsOnUnsupportedMethod() {
        assertThrows(IllegalArgumentException.class, () -> ApiClient.sendRequest("http://localhost", "DELETE", null));
    }

    /**
     * Verifies that sendRequest throws IllegalArgumentException for missing or blank JSON body on POST/PUT.
     */
    @Test
    void testSendRequestThrowsOnMissingJsonForPostPut() {
        assertThrows(IllegalArgumentException.class, () -> ApiClient.sendRequest("http://localhost", "POST", null));
        assertThrows(IllegalArgumentException.class, () -> ApiClient.sendRequest("http://localhost", "PUT", " "));
    }

    /**
     * Tests conversion of a Java object to JSON and parsing back to an object.
     */
    @Test
    void testConvertObjectToJsonAndParseResponse() throws JsonProcessingException {
        Map<String, String> map = Map.of("key", "value");
        String json = ApiClient.convertObjectToJson(map);
        assertTrue(json.contains("key"));
        TypeReference<Map<String, String>> typeRef = new TypeReference<>() {};
        Map<String, String> result = ApiClient.parseResponse(json, typeRef);
        assertEquals("value", result.get("key"));
    }

    /**
     * Verifies that convertObjectToJson throws JsonProcessingException for non-serializable objects.
     */
    @Test
    void testConvertObjectToJsonThrowsOnNonSerializable() {
        Object nonSerializable = new Object() {
            private void writeObject(java.io.ObjectOutputStream out) throws IOException {
                throw new IOException("fail");
            }
        };
        assertThrows(JsonProcessingException.class, () -> ApiClient.convertObjectToJson(nonSerializable));
    }

    /**
     * Verifies that parseResponse throws JsonProcessingException for malformed JSON input.
     */
    @Test
    void testParseResponseThrowsOnMalformedJson() {
        TypeReference<Map<String, String>> typeRef = new TypeReference<>() {};
        assertThrows(JsonProcessingException.class, () -> ApiClient.parseResponse("not-json", typeRef));
    }

    /**
     * Tests performApiRequest for a successful GET request and valid JSON response.
     */
    @Test
    void testPerformApiRequestSuccess() throws Exception {
        String url = "http://localhost";
        String method = "GET";
        String responseJson = "{\"key\":\"value\"}";
        TypeReference<Map<String, String>> typeRef = new TypeReference<>() {};
        @SuppressWarnings("unchecked")
        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(responseJson);
        try (MockedStatic<ApiClient> apiMock = Mockito.mockStatic(ApiClient.class, Mockito.CALLS_REAL_METHODS)) {
            apiMock.when(() -> ApiClient.sendRequest(url, method, null)).thenReturn(mockResponse);
            Map<String, String> result = ApiClient.performApiRequest(url, method, null, typeRef);
            assertEquals("value", result.get("key"));
        }
    }

    /**
     * Verifies that performApiRequest throws RuntimeException for server error status codes.
     */
    @Test
    void testPerformApiRequestServerError() throws Exception {
        String url = "http://localhost";
        String method = "GET";
        @SuppressWarnings("unchecked")
        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockResponse.body()).thenReturn("error");
        try (MockedStatic<ApiClient> apiMock = Mockito.mockStatic(ApiClient.class, Mockito.CALLS_REAL_METHODS)) {
            apiMock.when(() -> ApiClient.sendRequest(url, method, null)).thenReturn(mockResponse);
            RuntimeException ex = assertThrows(RuntimeException.class, () -> ApiClient.performApiRequest(url, method, null, new TypeReference<>() {}));
            assertTrue(ex.getMessage().contains("Server error"));
        }
    }

    /**
     * Verifies that performApiRequest throws RuntimeException when sendRequest throws an exception.
     */
    @Test
    void testPerformApiRequestThrowsOnSendRequestException() {
        String url = "http://localhost";
        String method = "GET";
        try (MockedStatic<ApiClient> apiMock = Mockito.mockStatic(ApiClient.class, Mockito.CALLS_REAL_METHODS)) {
            apiMock.when(() -> ApiClient.sendRequest(url, method, null)).thenThrow(new IOException("fail"));
            RuntimeException ex = assertThrows(RuntimeException.class, () -> ApiClient.performApiRequest(url, method, null, new TypeReference<>() {}));
            assertTrue(ex.getMessage().contains("Request failed"));
        }
    }

    /**
     * Verifies that performApiRequest returns null if responseType is null on 2xx status code.
     */
    @Test
    void testPerformApiRequestReturnsNullIfResponseTypeIsNull() throws Exception {
        String url = "http://localhost";
        String method = "GET";
        @SuppressWarnings("unchecked")
        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"key\":\"value\"}");
        try (MockedStatic<ApiClient> apiMock = Mockito.mockStatic(ApiClient.class, Mockito.CALLS_REAL_METHODS)) {
            apiMock.when(() -> ApiClient.sendRequest(url, method, null)).thenReturn(mockResponse);
            assertNull(ApiClient.performApiRequest(url, method, null, null));
        }
    }

    /**
     * Verifies that performApiRequest returns null if response body is null on 2xx status code.
     */
    @Test
    void testPerformApiRequestReturnsNullIfResponseBodyIsNull() throws Exception {
        String url = "http://localhost";
        String method = "GET";
        TypeReference<Map<String, String>> typeRef = new TypeReference<>() {};
        @SuppressWarnings("unchecked")
        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(null);
        try (MockedStatic<ApiClient> apiMock = Mockito.mockStatic(ApiClient.class, Mockito.CALLS_REAL_METHODS)) {
            apiMock.when(() -> ApiClient.sendRequest(url, method, null)).thenReturn(mockResponse);
            assertNull(ApiClient.performApiRequest(url, method, null, typeRef));
        }
    }

    /**
     * Verifies that performApiRequest returns null if response body is empty/whitespace on 2xx status code.
     */
    @Test
    void testPerformApiRequestReturnsNullIfResponseBodyIsEmpty() throws Exception {
        String url = "http://localhost";
        String method = "GET";
        TypeReference<Map<String, String>> typeRef = new TypeReference<>() {};
        @SuppressWarnings("unchecked")
        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("   ");
        try (MockedStatic<ApiClient> apiMock = Mockito.mockStatic(ApiClient.class, Mockito.CALLS_REAL_METHODS)) {
            apiMock.when(() -> ApiClient.sendRequest(url, method, null)).thenReturn(mockResponse);
            assertNull(ApiClient.performApiRequest(url, method, null, typeRef));
        }
    }

    /**
     * Verifies that performApiRequest returns null if responseType is null and body is empty on 2xx status code.
     */
    @Test
    void testPerformApiRequestReturnsNullIfResponseTypeIsNullOn2xxStatusCode() throws Exception {
        String url = "http://localhost";
        String method = "GET";
        @SuppressWarnings("unchecked")
        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("   ");
        try (MockedStatic<ApiClient> apiMock = Mockito.mockStatic(ApiClient.class, Mockito.CALLS_REAL_METHODS)) {
            apiMock.when(() -> ApiClient.sendRequest(url, method, null)).thenReturn(mockResponse);
            assertNull(ApiClient.performApiRequest(url, method, null, null));
        }
    }

    /**
     * Verifies that performApiRequest returns null if response body is empty on 2xx status code.
     */
    @Test
    void testPerformApiRequestReturnsNullIfResponseBodyIsEmptyOn2xxStatusCode() throws Exception {
        String url = "http://localhost";
        String method = "GET";
        TypeReference<Map<String, String>> typeRef = new TypeReference<>() {};
        @SuppressWarnings("unchecked")
        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("   ");
        try (MockedStatic<ApiClient> apiMock = Mockito.mockStatic(ApiClient.class, Mockito.CALLS_REAL_METHODS)) {
            apiMock.when(() -> ApiClient.sendRequest(url, method, null)).thenReturn(mockResponse);
            assertNull(ApiClient.performApiRequest(url, method, null, typeRef));
        }
    }

    /**
     * Tests sendRequest for a valid GET request to an external endpoint.
     */
    @Test
    void testSendRequestGetMethod() throws Exception {
        HttpResponse<String> response = ApiClient.sendRequest("https://postman-echo.com/get", "GET", null);
        assertNotNull(response);
        assertTrue(response.statusCode() >= 200);
    }

    /**
     * Tests sendRequest for a valid POST request to an external endpoint.
     */
    @Test
    void testSendRequestPostMethod() throws Exception {
        String json = "{\"foo\":\"bar\"}";
        HttpResponse<String> response = ApiClient.sendRequest("https://postman-echo.com/post", "POST", json);
        assertNotNull(response);
        assertTrue(response.statusCode() >= 200);
    }

    /**
     * Tests sendRequest for a valid PUT request to an external endpoint.
     */
    @Test
    void testSendRequestPutMethod() throws Exception {
        String json = "{\"foo\":\"bar\"}";
        HttpResponse<String> response = ApiClient.sendRequest("https://postman-echo.com/put", "PUT", json);
        assertNotNull(response);
        assertTrue(response.statusCode() >= 200);
    }

    /**
     * Verifies that performApiRequest throws RuntimeException when convertObjectToJson throws an exception.
     */
    @Test
    void testPerformApiRequestThrowsOnConvertObjectToJsonException() {
        String url = "http://localhost";
        String method = "POST";
        Object badObject = new Object() {
            @Override
            public String toString() { throw new RuntimeException("fail"); }
        };
        try (MockedStatic<ApiClient> apiMock = Mockito.mockStatic(ApiClient.class, Mockito.CALLS_REAL_METHODS)) {
            apiMock.when(() -> ApiClient.convertObjectToJson(badObject)).thenThrow(new RuntimeException("fail"));
            RuntimeException ex = assertThrows(RuntimeException.class, () -> ApiClient.performApiRequest(url, method, badObject, new TypeReference<>() {}));
            assertTrue(ex.getMessage().contains("Request failed"));
        }
    }

    /**
     * Verifies that performApiRequest throws RuntimeException when parseResponse throws an exception.
     */
    @Test
    void testPerformApiRequestThrowsOnParseResponseException() throws Exception {
        String url = "http://localhost";
        String method = "GET";
        TypeReference<Map<String, String>> typeRef = new TypeReference<>() {};
        @SuppressWarnings("unchecked")
        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("not-json");
        try (MockedStatic<ApiClient> apiMock = Mockito.mockStatic(ApiClient.class, Mockito.CALLS_REAL_METHODS)) {
            apiMock.when(() -> ApiClient.sendRequest(url, method, null)).thenReturn(mockResponse);
            apiMock.when(() -> ApiClient.parseResponse("not-json", typeRef)).thenThrow(new JsonProcessingException("fail") {});
            RuntimeException ex = assertThrows(RuntimeException.class, () -> ApiClient.performApiRequest(url, method, null, typeRef));
            assertTrue(ex.getMessage().contains("Request failed"));
        }
    }

    /**
     * Verifies that performApiRequest returns null for 2xx status code and whitespace body.
     */
    @Test
    void testPerformApiRequestReturnsNullFor2xxStatusCodeAndWhitespaceBody() throws Exception {
        String url = "http://localhost";
        String method = "GET";
        TypeReference<Map<String, String>> typeRef = new TypeReference<>() {};
        @SuppressWarnings("unchecked")
        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("   ");
        try (MockedStatic<ApiClient> apiMock = Mockito.mockStatic(ApiClient.class, Mockito.CALLS_REAL_METHODS)) {
            apiMock.when(() -> ApiClient.sendRequest(url, method, null)).thenReturn(mockResponse);
            assertNull(ApiClient.performApiRequest(url, method, null, typeRef));
        }
    }

    /**
     * Verifies that performApiRequest returns null for 2xx status code and parseResponse returns null.
     */
    @Test
    void testPerformApiRequestReturnsNullFor2xxStatusCodeAndParseResponseReturnsNull() throws Exception {
        String url = "http://localhost";
        String method = "GET";
        TypeReference<Map<String, String>> typeRef = new TypeReference<>() {};
        @SuppressWarnings("unchecked")
        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"key\":\"value\"}");
        try (MockedStatic<ApiClient> apiMock = Mockito.mockStatic(ApiClient.class, Mockito.CALLS_REAL_METHODS)) {
            apiMock.when(() -> ApiClient.sendRequest(url, method, null)).thenReturn(mockResponse);
            apiMock.when(() -> ApiClient.parseResponse("{\"key\":\"value\"}", typeRef)).thenReturn(null);
            assertNull(ApiClient.performApiRequest(url, method, null, typeRef));
        }
    }

    /**
     * Verifies that performApiRequest throws RuntimeException for non-2xx status code and null body.
     */
    @Test
    void testPerformApiRequestReturnsNullForNon2xxStatusCodeAndNullBody() throws Exception {
        String url = "http://localhost";
        String method = "GET";
        TypeReference<Map<String, String>> typeRef = new TypeReference<>() {};
        @SuppressWarnings("unchecked")
        HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(404);
        when(mockResponse.body()).thenReturn(null);
        try (MockedStatic<ApiClient> apiMock = Mockito.mockStatic(ApiClient.class, Mockito.CALLS_REAL_METHODS)) {
            apiMock.when(() -> ApiClient.sendRequest(url, method, null)).thenReturn(mockResponse);
            RuntimeException ex = assertThrows(RuntimeException.class, () -> ApiClient.performApiRequest(url, method, null, typeRef));
            assertTrue(ex.getMessage().contains("Server error"));
        }
    }

    /**
     * Tests the default constructor of {@link ApiClient} to ensure JaCoCo coverage.
     * <p>
     * This test simply instantiates the class, which is a no-op and only exists
     * to satisfy code coverage tools. The constructor is package-private and does not throw.
     */
    @Test
    void testApiClientConstructorCoverage() {
        // Covers the default constructor for JaCoCo
        assertNotNull(new ApiClient());
    }
}
