package itp.storage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import java.io.FileNotFoundException;


import app.Flashcard;

public class FlashcardPersistent {

  private List<Flashcard> deck;

  public FlashcardPersistent() {
    this.deck = new ArrayList<>();
  }

  /**
   * Writes all flashcards in deck to a CSV file using pipe (|) as delimiter.
   * Creates a new file or overwrites existing file with current deck.
   * Each flashcard is written as one line with format: question|answer
   * 
   * @throws IOException if an error occurs while writing to the file
   */
  public void writeToFile() {
    String filePath = getFilePath();

    try (FileWriter writer = new FileWriter(filePath, StandardCharsets.UTF_8)) {
        for (Flashcard flashcard : deck) {
            writer.write(flashcard.getQuestion() + " | " + flashcard.getAnswer() + "\n");
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
  }


  /**
   * Reads flashcards from a CSV file and populates the deck.
   * Expects each line to contain a flashcard in format: question|answer
   * Splits lines using pipe (|) as delimiter and creates Flashcard objects.
   * 
   * @throws IOException if an error occurs while reading from the file
   * @throws FileNotFoundException if the deck.csv file does not exist
   */
  public void readFromFile() {
    try (BufferedReader reader = new BufferedReader(new FileReader(getFilePath(), StandardCharsets.UTF_8))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\|");
            if (parts.length == 2) {
                Flashcard flashcard = new Flashcard(parts[0].trim(), parts[1].trim());
                deck.add(flashcard);
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
  }

  /**
   * Adds a flashcard to the deck.
   * 
   * @param flashcard the flashcard to add
   */
  public void addFlashcard(Flashcard flashcard) {
    deck.add(flashcard);
  }

  /**
   * Gets all flashcards in the deck.
   * 
   * @return list of all flashcards
   */
  public List<Flashcard> getDeck() {
    return new ArrayList<>(deck);
  }

  /**
   * Clears all flashcards from the deck.
   */
  public void clearDeck() {
    deck.clear();
  }

  private String getFilePath() {
    return "deck.csv";
  }

}
  