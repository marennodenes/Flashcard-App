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

public final class APIClient {

    /** The HTTP client instance used for all requests */
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    
    /** The JSON object mapper configured with JavaTimeModule */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private APIClient() {
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
     * Sends an API request with specified URL, HTTP method, and data.
     * Converts the data to JSON format if provided, and sends the request using the specified HTTP method.
     * Handles exceptions by displaying alerts and returns null if an error occurs.
     *
     * @param url The URL endpoint for the API request.
     * @param method The HTTP method to use, such as "GET" or "POST".
     * @param data The data to be sent with the request, which will be serialized to JSON if not null.
     * @return The HTTP response if the request succeeds; otherwise, returns null.
     */
    public static HttpResponse<String> performRequest(final String url, final String method, final Object data) {
        String json = null;
        try {
            if (data != null) {
                json = APIClient.convertObjectToJson(data);
            }
            return APIClient.sendRequest(url, method, json);
        } catch (IOException e) {
            showAlert("Network Error", "Error connecting to server. Please try again.");
        } catch (InterruptedException e) {
            showAlert("Request Interrupted", "The request was interrupted. Please try again.");
            Thread.currentThread().interrupt(); // Re-interrupt the thread to preserve its interrupted status
        }
        return null;
    }

}