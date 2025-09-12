package itp.storage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.Flashcard;

public class FlashcardPersistent {

  private List<Flashcard> flashcards;

  public FlashcardPersistent() {
    this.flashcards = new ArrayList<>();
  }

  /**
   * Writes all flashcards to a CSV file using pipe (|) as delimiter.
   * Creates a new file or overwrites existing file with current flashcard collection.
   * Each flashcard is written as one line with format: question|answer
   * 
   * @throws IOException if an error occurs while writing to the file
   */
  public void writeToFile() {
    try (FileWriter writer = new FileWriter("flashcards.csv")) {
        for (Flashcard flashcard : flashcards) {
            writer.write(flashcard.getQuestion() + " | " + flashcard.getAnswer() + "\n");
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
  }

  /**
   * Reads flashcards from a CSV file and populates the flashcards collection.
   * Expects each line to contain a flashcard in format: question|answer
   * Splits lines using pipe (|) as delimiter and creates Flashcard objects.
   * 
   * @throws IOException if an error occurs while reading from the file
   * @throws FileNotFoundException if the flashcards.csv file does not exist
   */
  public void readFromFile() {
    try (BufferedReader reader = new BufferedReader(new FileReader("flashcards.csv"))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\|");
            if (parts.length == 2) {
                Flashcard flashcard = new Flashcard(parts[0].trim(), parts[1].trim());
                flashcards.add(flashcard);
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
  }

  /**
   * Adds a flashcard to the collection.
   * 
   * @param flashcard the flashcard to add
   */
  public void addFlashcard(Flashcard flashcard) {
    flashcards.add(flashcard);
  }

  /**
   * Gets all flashcards in the collection.
   * 
   * @return list of all flashcards
   */
  public List<Flashcard> getFlashcards() {
    return new ArrayList<>(flashcards);
  }

  /**
   * Clears all flashcards from the collection.
   */
  public void clearFlashcards() {
    flashcards.clear();
  }
}
  