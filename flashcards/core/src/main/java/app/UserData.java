package app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Combined data structure that holds both user credentials and flashcard data.
 * This prevents user credentials from being overwritten when flashcards are saved.
 */
@SuppressWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2", "EI"})
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
     * Returns the deck manager.
     * Note: Returns direct reference for performance - caller should not modify.
     */
    public FlashcardDeckManager getDeckManager() {
        return deckManager;
    }

    /**
     * Sets the deck manager.
     * Note: Stores direct reference for performance.
     */
    public void setDeckManager(FlashcardDeckManager deckManager) {
        this.deckManager = deckManager;
    }

    /**
     * Creates a User object from this UserData
     */
    public User toUser() {
        return new User(username, password);
    }
}