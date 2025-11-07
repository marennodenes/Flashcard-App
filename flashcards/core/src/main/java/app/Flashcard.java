package app;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a flashcard with a question and answer.
 * Used for studying and learning purposes in the flashcard application.
 * 
 * @author isamw
 * @author chrsom
 */
public class Flashcard {
  @JsonProperty("number")
  private int number;
  
  @JsonProperty("question")
  private String question;
  
  @JsonProperty("answer")
  private String answer;

  /**
   * Default constructor for JSON deserialization.
   */
  public Flashcard(){
  }

  /**
   * Constructor for a flashcard with question and answer.
   * 
   * @param question the question text
   * @param answer the answer text
   */
  public Flashcard(String question, String answer) {
    this.question = question;
    this.answer = answer;
    this.number = 1;
  }

  /**
   * Constructor for a flashcard with number, question, and answer.
   * 
   * @param number the flashcard number
   * @param question the question text
   * @param answer the answer text
   */
  public Flashcard(int number, String question, String answer){
    this.number = number;
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
   * Sets the question.
   * 
   * @param question the question text
   */
  public void setQuestion(String question) {
    if(question != null && !question.isBlank())
    this.question = question;
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
   * Sets the answer.
   * 
   * @param answer the answer text
   */
  public void setAnswer(String answer) {
    if( answer != null && !answer.isBlank()) this.answer = answer;
  }

  /**
   * Gets the flashcard number.
   * 
   * @return the number of this flashcard
   */
  public int getNumber(){
    return number;
  }

  /**
   * Sets the flashcard number.
   * 
   * @param number the number to assign to this flashcard
   */
  public void setNumber(int number){
    this.number = number;
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