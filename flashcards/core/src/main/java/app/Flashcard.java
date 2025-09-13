package app;

/**
 * A flashcard with a question and answer.
 */
public class Flashcard {
  private String question;
  private String answer;

  /**
   * Creates a new flashcard.
   * 
   * @param question the question text
   * @param answer the answer text
   */
  public Flashcard(String question, String answer) {
    this.question = question;
    this.answer = answer;
  }
  
  /**
   * Gets the question.
   * 
   * @return the question text
   */
  public String getQuestion() {
    return question;
  }
  
  /**
   * Gets the answer.
   * 
   * @return the answer text
   */
  public String getAnswer() {
    return answer;
  }

  /**
   * Returns string representation of the flashcard.
   * 
   * @return formatted question and answer
   */
  @Override
  public String toString() {
    return "Q: " + question + "\nA: " + answer;
  }
}