package ui;

import app.Flashcard;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import itp.storage.FlashcardPersistent;

public class FlashcardController {
  @FXML private TextField questionField;
  @FXML private TextField answerField;
  @FXML private ListView<Flashcard> listView;

  private final FlashcardManager manager = new FlashcardManager();
  private final FlashcardPersistent storage = new FlashcardPersistent(); 


  /**
   * Sets up the UI when loaded.
   * Loads existing flashcards from CSV file
   */
  @FXML public void initialize(){
    updateUI();
    loadFlashcardsFromFile();
  }

  /**
   * Loads flashcards from CSV file into the manager.
   */
  private void loadFlashcardsFromFile() {
    storage.readFromFile();  // Read from CSV file
    for (Flashcard card : storage.getFlashcards()) { 
        manager.addFlashcard(card.getQuestion(), card.getAnswer()); // Add to manager
    }
  }

  private void saveFlashcardToFile(Flashcard card) {
    storage.addFlashcard(card); // Add to storage
    storage.writeToFile();      // Write to CSV file
  }

  /**
   * Updates the flashcard list display.
   */
  public void updateUI(){
    listView.setItems(manager.getFlashcards());
  }

   /**
   * Adds a new flashcard when button is clicked.
   */
  public void whenGenerateButtonClicked(){
    String q = questionField.getText().trim();
    String a = answerField.getText().trim();

    if (!q.isEmpty() && !a.isEmpty()) {
      
      //add to memory
      manager.addFlashcard(q, a);

      //save to CSV file 
      Flashcard newFlashcard = new Flashcard(q, a);
      saveFlashcardToFile(newFlashcard);

      //clear input fields
      clearInputFields();

      //update UI
      updateUI();
    }

  }
  
  /**
   * Clears the input fields.
   */
  private void clearInputFields() {
    questionField.clear();
    answerField.clear();
  } 

}
