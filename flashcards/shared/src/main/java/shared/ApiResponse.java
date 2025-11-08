package shared;

/**
 * Generic API response wrapper class that encapsulates the result of API operations.
 * This class provides a standardized format for API responses including success status,
 * message, and optional data payload.
 *
 * @author isamw
 * @author marieroe
 * @param <T> the type of data contained in the response
 */
public class ApiResponse<T> {
  private boolean success;
  private String message;
  private T data;

  /**
   * Default constructor for ApiResponse.
   * Creates an ApiResponse with default values.
   */
  public ApiResponse() {}

  /**
   * Constructs an ApiResponse with the specified parameters.
   *
   * @param success indicates whether the API operation was successful
   * @param message descriptive message about the operation result
   * @param data the data payload of the response, can be null
   */
  public ApiResponse(boolean success, String message, T data) {
    this.success = success;
    this.message = message;
    this.data = data;
  }

  /**
   * Returns whether the API operation was successful.
   *
   * @return true if the operation was successful, false otherwise
   */
  public boolean isSuccess() {
    return success;
  }

  /**
   * Sets the success status of the API response.
   *
   * @param success true if the operation was successful, false otherwise
   */
  public void setSuccess(boolean success) {
    this.success = success;
  }

  /**
   * Gets the message associated with the API response.
   *
   * @return the descriptive message about the operation result
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets the message for the API response.
   *
   * @param message descriptive message about the operation result
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Returns the data payload of the API response.
   *
   * @return the data contained in the response, may be null
   */
  public T getData() {
    return data;
  }

  /**
   * Sets the data payload for the API response.
   *
   * @param data the data to be contained in the response
   */
  public void setData(T data) {
    this.data = data;
  }
}


