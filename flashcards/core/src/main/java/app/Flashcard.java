package app;

import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * A flashcard with a question and answer.
 */
public class Flashcard {
  @JsonProperty("number")
  private int number;
  
  @JsonProperty("question")
  private String question;
  
  @JsonProperty("answer")
  private String answer;

  public Flashcard(){
    //for jackson
  }

  /**
   * Creates a new flashcard.
   * 
   * @param question the question text
   * @param answer the answer text
   */
  public Flashcard(String question, String answer) {
    this.question = question;
    this.answer = answer;
    this.number = 1;
  }

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
   * Sets the answer.
   * 
   * @param answer the answer text
   */
  public void setAnswer(String answer) {
    if( answer != null && !answer.isBlank()){
this.answer = answer;
    }
    
  }

  
  /**
   * Gets the answer.
   * 
   * @return the answer text
   */
  public String getAnswer() {
    return answer;
  }

  public int getNumber(){
    return number;
  }

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