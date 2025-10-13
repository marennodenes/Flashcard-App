package app;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Combined data structure that holds both user credentials and flashcard data.
 * This prevents user credentials from being overwritten when flashcards are saved.
 * @author parts of this class is generated with the help of claude.ai
 * @author @sofietw
 * @author @ailinat
 */
public class UserData {
    private String username;
    private String password;
    private FlashcardDeckManager deckManager;

    /**
     * Default constructor for Jackson
     * Creates an empty UserData with default values.
     * Initializes with empty username, password, and new deck manager.
     */
    public UserData() {
        this.deckManager = new FlashcardDeckManager();
    }

    /**
     * Constructor with all fields.
     * @param username the username of the user
     * @param password the password of the user
     * @param deckManager the flashcard deck manager for the user
     */
    @JsonCreator
    public UserData(@JsonProperty("username") String username, 
                    @JsonProperty("password") String password,
                    @JsonProperty("deckManager") FlashcardDeckManager deckManager) {
        this.username = username;
        this.password = password;
        if (deckManager == null) {
            this.deckManager = new FlashcardDeckManager();
        } else {
            // Create defensive copy to avoid storing externally mutable object
            this.deckManager = new FlashcardDeckManager();
            for (FlashcardDeck deck : deckManager.getDecks()) {
                this.deckManager.addDeck(deck);
            }
        }
    }

    /**
     * Constructor without deckManager, initializes with empty deck manager.
     * @param username the username of the user
     * @param password the password of the user
     */
    public UserData(String username, String password) {
        this(username, password, new FlashcardDeckManager());
    }

    /**
     * Gets the username.   
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /** Sets the username.
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /** Gets the password.
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /** Sets the password.
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns a copy of the deck manager to prevent external modification.
     * Creates a defensive copy to avoid exposing internal representation.
     * @return a copy of the FlashcardDeckManager
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
     * @param deckManager the FlashcardDeckManager to set
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
     * @return a User object with username and password
     */
    public User toUser() {
        return new User(username, password);
    }
}