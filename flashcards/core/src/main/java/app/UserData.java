package app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Combined data structure that holds both user credentials and flashcard data.
 * This prevents user credentials from being overwritten when flashcards are saved.
 */
public class UserData {
    private String username;
    private String password;
    private FlashcardDeckManager deckManager;

    // Default constructor for Jackson
    public UserData() {
        this.deckManager = new FlashcardDeckManager();
    }

    @JsonCreator
    public UserData(@JsonProperty("username") String username, 
                    @JsonProperty("password") String password,
                    @JsonProperty("deckManager") FlashcardDeckManager deckManager) {
        this.username = username;
        this.password = password;
        this.deckManager = deckManager != null ? deckManager : new FlashcardDeckManager();
    }

    public UserData(String username, String password) {
        this(username, password, new FlashcardDeckManager());
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns a copy of the deck manager to prevent external modification.
     * Creates a defensive copy to avoid exposing internal representation.
     */
    public FlashcardDeckManager getDeckManager() {
        FlashcardDeckManager copy = new FlashcardDeckManager();
        for (FlashcardDeck deck : deckManager.getDecks()) {
            copy.addDeck(deck);
        }
        return copy;
    }

    /**
     * Sets the deck manager using a defensive copy.
     * Creates a copy to avoid storing externally mutable objects.
     */
    public void setDeckManager(FlashcardDeckManager deckManager) {
        if (deckManager == null) {
            this.deckManager = new FlashcardDeckManager();
        } else {
            this.deckManager = new FlashcardDeckManager();
            for (FlashcardDeck deck : deckManager.getDecks()) {
                this.deckManager.addDeck(deck);
            }
        }
    }

    /**
     * Creates a User object from this UserData
     */
    public User toUser() {
        return new User(username, password);
    }
}