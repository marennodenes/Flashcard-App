package ui;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javafx.scene.control.Alert;
import javafx.stage.Modality;

/**
 * Utility class for handling HTTP requests and JSON serialization/deserialization.
 *
 * <p>This class provides static methods for:</p>
 * <ul>
 *   <li>Sending HTTP requests (GET, POST, PUT) with JSON payloads</li>
 *   <li>Parsing JSON responses into Java objects</li>
 *   <li>Converting Java objects to JSON strings</li>
 * </ul>
 *
 * <p>All methods are static and the class cannot be instantiated.</p>
 *
 * <p>Some class structure and ideas were inspired by
 * <a href="https://github.com/Oddvar112/ITP-Prosjekt/blob/master/flightradar/fxui/src/main/java/itp/fxui/APIClient.java">
 * APIClient in Oddvar112’s ITP-Prosjekt/flightradar</a>.</p>
 *
 * @author marennod
 * @author marieroe
 */

public final class ApiClient {

    /** The HTTP client instance used for all requests */
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    
    /** The JSON object mapper configured with JavaTimeModule */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ApiClient() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Sends an HTTP request to the specified URI using the given HTTP method and request body.
     * 
     *
     * @param uri The URI to which the request is sent. Must not be {@code null} or blank.
     * @param httpMethod The HTTP method to use ("GET", "POST", or "PUT", case-insensitive). 
     *                   Must not be {@code null} or blank.
     * @param json The JSON string to include as the request body. Required for POST/PUT, 
     *             ignored for GET requests. Must not be {@code null} or blank for POST/PUT.
     * @return The HTTP response containing the server's response body as a string.
     * @throws IOException If an I/O error occurs during request or response handling.
     * @throws InterruptedException If the request operation is interrupted.
     * @throws IllegalArgumentException If the URI or httpMethod is null/blank, if the HTTP method 
     *                                  is unsupported, or if JSON body is null/blank for POST/PUT requests.
     */
    public static HttpResponse<String> sendRequest(final String uri, final String httpMethod, final String json) throws IOException, InterruptedException {
        if (uri == null || uri.isBlank()) {
            throw new IllegalArgumentException("URI cannot be null or blank");
        }
        if (httpMethod == null || httpMethod.isBlank()) {
            throw new IllegalArgumentException("HTTP method cannot be null or blank");
        }
        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder() //requestBuilder
            .uri(URI.create(uri))
            .header("Content-Type", "application/json");

        String upperMethod = httpMethod.toUpperCase();
        if (upperMethod.equals("POST") || upperMethod.equals("PUT")) {
            if (json == null || json.isBlank()) {
                throw new IllegalArgumentException("JSON body cannot be null or blank for POST or PUT requests");
            }
            //either POST or PUT
            if (upperMethod.equals("POST")) {
                httpRequestBuilder.POST(HttpRequest.BodyPublishers.ofString(json));
            } else {
                httpRequestBuilder.PUT(HttpRequest.BodyPublishers.ofString(json));
            }
        } else if (upperMethod.equals("GET")) {
            httpRequestBuilder.GET();
        } else {
            throw new IllegalArgumentException("Unsupported HTTP method: " + httpMethod);
        }

        HttpRequest request = httpRequestBuilder.build();
        return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Deserializes a JSON string into an object of the specified type.
     * 
     * 
     * <p>Example usage:
     * <pre>{@code
     * List<FlashcardDto> flashcards = parseResponse(jsonString, 
     *     new TypeReference<List<FlashcardDto>>() {});
     * }</pre>
     *
     * @param <T> The type of the object to be returned.
     * @param json The JSON string to deserialize. Must be valid JSON.
     * @param typeReference The {@code TypeReference} indicating the type of the target object.
     *                      Use this for generic types like {@code List<FlashcardDto>}.
     * @return The deserialized object of type T.
     * @throws JsonProcessingException If an error occurs during JSON deserialization,
     *                                such as malformed JSON or type mismatch.
     */
    public static <T> T parseResponse(final String json, final TypeReference<T> typeReference) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, typeReference);
    }

    /**
     * Converts a Java object to its JSON string representation.
     * 
     * 
     * <p>Example usage:
     * <pre>{@code
     * FlashcardDto flashcard = new FlashcardDto("Question", "Answer");
     * String json = convertObjectToJson(flashcard);
     * }</pre>
     *
     * @param object The object to convert to JSON. Can be any serializable Java object.
     * @return The JSON string representation of the object.
     * @throws JsonProcessingException If an error occurs during JSON serialization,
     *                                such as circular references or non-serializable fields.
     */
    public static String convertObjectToJson(final Object object) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

    /**
     * Displays an error alert dialog with the specified title and content.
     * The alert is modal and will block interaction with other windows until dismissed.
     *
     * @param title   the title of the alert dialog
     * @param content the content message to display in the alert
     */
    public static void showAlert(final String title, final String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }

    /**
     * Performs an API request and returns a structured ApiResponse.
     * This method wraps the raw HTTP response in an ApiResponse object for better error handling.
     *
     * @param <T> The type of data expected in the response
     * @param url The URL endpoint for the API request
     * @param method The HTTP method to use
     * @param data The data to send with the request
     * @param responseType TypeReference for the expected response data type
     * @return ApiResponse containing success status, message, and data
     */
    public static <T> ApiResponse<T> performApiRequest(final String url, final String method, 
                                                      final Object data, final TypeReference<T> responseType) {
        try {
            // Convert data to JSON if provided
            String json = null;
            if (data != null) {
                json = convertObjectToJson(data);
            }
            
            // Send the HTTP request
            HttpResponse<String> response = sendRequest(url, method, json);
            
            // Check if the response status indicates success (2xx)
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                T responseData = null;
                // Parse response body if we have data and a type reference
                if (responseType != null && response.body() != null && !response.body().trim().isEmpty()) {
                    responseData = parseResponse(response.body(), responseType);
                }
                return new ApiResponse<>(true, "Request successful", responseData);
            } else {
                // Server returned an error status code
                return new ApiResponse<>(false, "Server error: " + response.statusCode(), null);
            }
            
        } catch (JsonProcessingException e) {
            return new ApiResponse<>(false, "Failed to parse server response: " + e.getMessage(), null);
        } catch (IOException e) {
            return new ApiResponse<>(false, "Network error: " + e.getMessage(), null);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Re-interrupt the thread to preserve its interrupted status
            return new ApiResponse<>(false, "Request interrupted: " + e.getMessage(), null);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Request failed: " + e.getMessage(), null);
        }
    }

}