package ui;

/**
 * Represents an error response containing success status and error message.
 * This class is used to encapsulate error information when operations fail.
 * 
 * @author isamw
 * @author marieroe
 */
public class ErrorResponse {

  /**
   * Indicates whether the operation was successful.
   * Always false for error responses.
   */
  private boolean success;

  /**
   * The error message describing what went wrong.
   */
  private String message;

  /**
   * Default constructor that creates an empty ErrorResponse.
   */
  public ErrorResponse() {}

  /**
   * Constructs an ErrorResponse with the specified message.
   * The success field is automatically set to false.
   * 
   * @param success This parameter is not used as success is always false for errors
   * @param message The error message describing the failure
   */
  public ErrorResponse(String success, String message) {
    this.success = false;
    this.message = message;
  }

  /**
   * Returns the success status of the response.
   * 
   * @return false, as this represents an error response
   */
  public boolean isSuccess() {
    return success;
  }

  /**
   * Sets the success status of the response.
   * 
   * @param success The success status to set
   */
  public void setSuccess(boolean success) {
    this.success = success;
  }

  /**
   * Returns the error message.
   * 
   * @return The error message describing what went wrong
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets the error message.
   * 
   * @param message The error message to set
   */
  public void setMessage(String message) {
    this.message = message;
  }
}


