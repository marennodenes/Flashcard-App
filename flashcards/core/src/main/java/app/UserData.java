package app;

import app.User;
import app.FlashcardDeckManager; 

public class UserData {
    private User user;          // credentials
    private FlashcardDeckManager deckManager;  // existing flashcard data

    public UserData(User user, FlashcardDeckManager deckManager) {
        this.user = user;
        this.deckManager = deckManager;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public FlashcardDeckManager getDeckManager() {
        return deckManager;
    }

    public void setDeckManager(FlashcardDeckManager deckManager) {
        this.deckManager = deckManager;
    }
}
