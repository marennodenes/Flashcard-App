package ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Unit tests for the {@link ApiClient} utility class.
 *
 * <p>This test class verifies all public static methods in {@code ApiClient},
 * including:
 * - HTTP request handling (GET, POST, PUT)
 * - JSON serialization and deserialization
 * - Error handling and edge cases
 * - Coverage of the default constructor for JaCoCo
 *
 * <p>All major branches, error paths, and edge cases are tested to ensure
 * robust and maintainable code.
 *
 * @author marennod
 * @author sofietw
 *
 * @see ApiClient
 * @see "docs/release_3/ai_tools.md"
 */
public class ApiClientTest {

  /**
   * Tests the default constructor of {@link ApiClient} to ensure JaCoCo coverage.
   *
   * <p>This test simply instantiates the class, which is a no-op and only
   * exists to satisfy code coverage tools. The constructor is package-private
   * and does not throw.
   */
  @Test
  public void testApiClientConstructor() {
    assertNotNull(new ApiClient());
  }

  /**
   * Tests conversion of a Java object to JSON and parsing back to an object.
   */
  @Test
  public void testConvertObjectToJsonAndParseResponse() throws JsonProcessingException {
    Map<String, String> map = Map.of("key", "value");
    String json = ApiClient.convertObjectToJson(map);
    assertTrue(json.contains("key"));
    TypeReference<Map<String, String>> typeRef = new TypeReference<>() {};
    Map<String, String> result = ApiClient.parseResponse(json, typeRef);
    assertEquals("value", result.get("key"));

    Object nonSerializable = new Object() {
      private void writeObject(java.io.ObjectOutputStream out)
          throws IOException {
        throw new IOException("fail");
      }
    };
    assertThrows(JsonProcessingException.class,
        () -> ApiClient.convertObjectToJson(nonSerializable));

    assertThrows(JsonProcessingException.class,
        () -> ApiClient.parseResponse("not-json", typeRef));
  }

  /**
   * Verifies that sendRequest throws IllegalArgumentException for null or
   * blank URI/method.
   * 
   * @throws Exception if sending the request fails
   */
  @Test
  public void testSendRequest() throws Exception{
    assertThrows(IllegalArgumentException.class,
        () -> ApiClient.sendRequest(null, "GET", null));
    assertThrows(IllegalArgumentException.class,
        () -> ApiClient.sendRequest(" ", "GET", null));
    assertThrows(IllegalArgumentException.class,
        () -> ApiClient.sendRequest("http://localhost", null, null));
    assertThrows(IllegalArgumentException.class,
        () -> ApiClient.sendRequest("http://localhost", " ", null));
    
    assertThrows(IllegalArgumentException.class,
        () -> ApiClient.sendRequest("http://localhost", "PATCH", null));
    assertThrows(IOException.class,
        () -> ApiClient.sendRequest("http://localhost", "DELETE", null));
    assertThrows(IllegalArgumentException.class,
        () -> ApiClient.sendRequest("http://localhost", "POST", null));
    assertThrows(IllegalArgumentException.class,
        () -> ApiClient.sendRequest("http://localhost", "PUT", " "));
    assertThrows(IOException.class,
        () -> ApiClient.sendRequest("http://localhost", "GET", null));

  }



  /**
   * Tests sendRequest for a valid POST request to an external endpoint.
   */
  @Test
  public void testSendRequestHttpsMethods() throws Exception {
    String json = "{\"foo\":\"bar\"}";

    // POST request
    HttpResponse<String> response = ApiClient.sendRequest(
        "https://postman-echo.com/post", "POST", json);
    assertNotNull(response);
    assertTrue(response.statusCode() >= 200);

    // GET request
    response = ApiClient.sendRequest(
        "https://postman-echo.com/get", "GET", null);
    assertNotNull(response);
    assertTrue(response.statusCode() >= 200);

    // PUT request
    response = ApiClient.sendRequest(
        "https://postman-echo.com/put", "PUT", json);
    assertNotNull(response);
    assertTrue(response.statusCode() >= 200);

    // DELETE request
    response = ApiClient.sendRequest(
        "https://postman-echo.com/delete", "DELETE", null);
    assertNotNull(response);
    assertTrue(response.statusCode() >= 200);
  }


  /**
   * Tests performApiRequest for a successful GET request and valid
   * JSON response.
   *
   * @throws Exception if mocking fails
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testPerformApiRequestSuccess() throws Exception {
    String url = "http://localhost";
    String method = "GET";
    String responseJson = "{\"key\":\"value\"}";
    TypeReference<Map<String, String>> typeRef = new TypeReference<>() {};
      
    HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
    when(mockResponse.statusCode()).thenReturn(200);
    when(mockResponse.body()).thenReturn(responseJson);

    try (MockedStatic<ApiClient> apiMock =
        Mockito.mockStatic(ApiClient.class, Mockito.CALLS_REAL_METHODS)) {
      apiMock.when(() -> ApiClient.sendRequest(url, method, null))
          .thenReturn(mockResponse);
      Map<String, String> result =
          ApiClient.performApiRequest(url, method, null, typeRef);
      assertEquals("value", result.get("key"));
    }
  }

  /**
   * Verifies that performApiRequest throws RuntimeException for server
   * error status codes.
   *
   * @throws Exception if mocking fails
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testPerformApiRequestServerError() throws Exception {
    String url = "http://localhost";
    String method = "GET";
  
    HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
    when(mockResponse.statusCode()).thenReturn(500);
    when(mockResponse.body()).thenReturn("error");

    try (MockedStatic<ApiClient> apiMock =
        Mockito.mockStatic(ApiClient.class, Mockito.CALLS_REAL_METHODS)) {
      apiMock.when(() -> ApiClient.sendRequest(url, method, null))
          .thenReturn(mockResponse);
      RuntimeException ex = assertThrows(RuntimeException.class,
          () -> ApiClient.performApiRequest(url, method, null,
              new TypeReference<>() {}));
      assertTrue(ex.getMessage().contains("Server error"));
    }
  }

  /**
   * Verifies that performApiRequest throws RuntimeException when
   * sendRequest throws an exception.
   */
  @Test
  public void testPerformApiRequestThrowsOnSendRequestException() {
    String url = "http://localhost";
    String method = "GET";

    try (MockedStatic<ApiClient> apiMock =
        Mockito.mockStatic(ApiClient.class, Mockito.CALLS_REAL_METHODS)) {
      apiMock.when(() -> ApiClient.sendRequest(url, method, null))
          .thenThrow(new IOException("fail"));
      RuntimeException ex = assertThrows(RuntimeException.class,
          () -> ApiClient.performApiRequest(url, method, null,
              new TypeReference<>() {}));
      assertTrue(ex.getMessage().contains("Request failed"));
    }
  }

  /**
   * Verifies that performApiRequest throws RuntimeException when
   * convertObjectToJson throws an exception.
   */
  @Test
  public void testPerformApiRequestThrowsOnConvertObjectToJsonException() {
    String url = "http://localhost";
    String method = "POST";
    Object badObject = new Object() {
      @Override
      public String toString() {
        throw new RuntimeException("fail");
      }
    };

    try (MockedStatic<ApiClient> apiMock =
        Mockito.mockStatic(ApiClient.class, Mockito.CALLS_REAL_METHODS)) {
      apiMock.when(() -> ApiClient.convertObjectToJson(badObject))
          .thenThrow(new RuntimeException("fail"));
      RuntimeException ex = assertThrows(RuntimeException.class,
          () -> ApiClient.performApiRequest(url, method, badObject,
              new TypeReference<>() {}));
      assertTrue(ex.getMessage().contains("Request failed"));
    }
  }

  /**
   * Verifies that performApiRequest throws RuntimeException when
   * parseResponse throws an exception.
   *
   * @throws Exception if mocking fails
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testPerformApiRequestThrowsOnParseResponseException() throws Exception {
    String url = "http://localhost";
    String method = "GET";
    TypeReference<Map<String, String>> typeRef = new TypeReference<>() {};
      
    HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
    when(mockResponse.statusCode()).thenReturn(200);
    when(mockResponse.body()).thenReturn("not-json");

    try (MockedStatic<ApiClient> apiMock =
        Mockito.mockStatic(ApiClient.class, Mockito.CALLS_REAL_METHODS)) {
      apiMock.when(() -> ApiClient.sendRequest(url, method, null))
          .thenReturn(mockResponse);
      apiMock.when(() -> ApiClient.parseResponse("not-json", typeRef))
          .thenThrow(new JsonProcessingException("fail") {});
      RuntimeException ex = assertThrows(RuntimeException.class,
          () -> ApiClient.performApiRequest(url, method, null, typeRef));
      assertTrue(ex.getMessage().contains("Request failed"));
    }
  }

  /**
   * Verifies that performApiRequest returns null if responseType is null
   * on 2xx status code.
   *
   * @throws Exception if mocking fails
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testPerformApiRequestReturnsNullIfResponseTypeIsNull() throws Exception {
    String url = "http://localhost";
    String method = "GET";
      
    HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
    when(mockResponse.statusCode()).thenReturn(200);
    when(mockResponse.body()).thenReturn("{\"key\":\"value\"}");

    try (MockedStatic<ApiClient> apiMock =
        Mockito.mockStatic(ApiClient.class, Mockito.CALLS_REAL_METHODS)) {
      apiMock.when(() -> ApiClient.sendRequest(url, method, null))
          .thenReturn(mockResponse);
      assertNull(ApiClient.performApiRequest(url, method, null, null));
    }
  }

  /**
   * Verifies that performApiRequest returns null if response body is null
   * on 2xx status code.
   *
   * @throws Exception if mocking fails
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testPerformApiRequestReturnsNullIfResponseBodyIsNull() throws Exception {
    String url = "http://localhost";
    String method = "GET";
    TypeReference<Map<String, String>> typeRef = new TypeReference<>() {};
      
    HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
    when(mockResponse.statusCode()).thenReturn(200);
    when(mockResponse.body()).thenReturn(null);

    try (MockedStatic<ApiClient> apiMock =
        Mockito.mockStatic(ApiClient.class, Mockito.CALLS_REAL_METHODS)) {
      apiMock.when(() -> ApiClient.sendRequest(url, method, null))
          .thenReturn(mockResponse);
      assertNull(ApiClient.performApiRequest(url, method, null, typeRef));
    }
  }

  /**
   * Verifies that performApiRequest returns null if response body is
   * empty/whitespace on 2xx status code or body is null.
   *
   * @throws Exception if mocking fails
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testPerformApiRequestReturnsNull() throws Exception {
    String url = "http://localhost";
    String method = "GET";
    TypeReference<Map<String, String>> typeRef = new TypeReference<>() {};
      
    HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
    when(mockResponse.statusCode()).thenReturn(200);
    when(mockResponse.body()).thenReturn("   ");

    try (MockedStatic<ApiClient> apiMock =
        Mockito.mockStatic(ApiClient.class, Mockito.CALLS_REAL_METHODS)) {
      apiMock.when(() -> ApiClient.sendRequest(url, method, null))
          .thenReturn(mockResponse);
      assertNull(ApiClient.performApiRequest(url, method, null, typeRef));
    }

    HttpResponse<String> mockResponse2 = (HttpResponse<String>) mock(HttpResponse.class);
    when(mockResponse2.statusCode()).thenReturn(404);
    when(mockResponse2.body()).thenReturn(null);

    try (MockedStatic<ApiClient> apiMock =
        Mockito.mockStatic(ApiClient.class, Mockito.CALLS_REAL_METHODS)) {
      apiMock.when(() -> ApiClient.sendRequest(url, method, null))
          .thenReturn(mockResponse2);
      RuntimeException ex = assertThrows(RuntimeException.class,
          () -> ApiClient.performApiRequest(url, method, null, typeRef));
      assertTrue(ex.getMessage().contains("Server error"));
    }

    HttpResponse<String> mockResponse3 = (HttpResponse<String>) mock(HttpResponse.class);
    when(mockResponse3.statusCode()).thenReturn(200);
    when(mockResponse3.body()).thenReturn("{\"key\":\"value\"}");

    try (MockedStatic<ApiClient> apiMock =
        Mockito.mockStatic(ApiClient.class, Mockito.CALLS_REAL_METHODS)) {
      apiMock.when(() -> ApiClient.sendRequest(url, method, null))
          .thenReturn(mockResponse3);
      apiMock.when(() -> ApiClient.parseResponse("{\"key\":\"value\"}",
          typeRef)).thenReturn(null);
      assertNull(ApiClient.performApiRequest(url, method, null, typeRef));
    }
  }
}
