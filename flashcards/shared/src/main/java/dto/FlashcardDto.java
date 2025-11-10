package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * A Data Transfer Object (DTO) representing a single flashcard.
 * This class is used to transfer data between different layers of the application.
 * It includes the question, answer, and a unique number for the flashcard.
 *
 * @author marieroe
 * @author isamw
 */
public class FlashcardDto {

  /**
   * The question on the flashcard.
   * This field cannot be null or blank.
   */
  @NotBlank
  @JsonProperty("question")
  private String question;

  /**
   * The answer on the flashcard.
   * This field cannot be null or blank.
   */
  @NotBlank
  @JsonProperty("answer")
  private String answer;

  /**
   * The unique number of the flashcard.
   * This field must be at least 1.
   */
  @Min(1)
  @JsonProperty("number")
  private int number;

  /**
   * Default constructor for FlashcardDto.
   * Required for frameworks that use reflection, such as Jackson.
   */
  public FlashcardDto() {
    // No-arg constructor for Jackson
  }

  /**
   * Constructs a new FlashcardDto with the specified question, answer, and number.
   *
   * @param question the question on the flashcard; must not be null or blank
   * @param answer the answer on the flashcard; must not be null or blank
   * @param number the unique number of the flashcard; must be at least 1
   */
  public FlashcardDto(String question, String answer, int number) {
    this.question = question;
    this.answer = answer;
    this.number = number;
  }

  /**
   * Gets the question on the flashcard.
   *
   * @return the question on the flashcard
   */
  public String getQuestion() {
    return question;
  }

  /**
   * Gets the answer on the flashcard.
   *
   * @return the answer on the flashcard
   */
  public String getAnswer() {
    return answer;
  }


  /**
   * Gets the unique number of the flashcard.
   *
   * @return the unique number of the flashcard
   */
  public int getNumber() {
    return number;
  }

  /**
   * Returns a string representation of the flashcard showing question and answer.
   *
   * @return a string in the format "Question \n Answer"
   */
  @Override
  public String toString() {
    return question + "\n" + answer;
  }
}