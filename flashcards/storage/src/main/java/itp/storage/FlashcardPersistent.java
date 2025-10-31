package itp.storage;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import app.User;
import app.UserData;
import app.UserPersistence;
import app.FlashcardDeckManager;

/**
 * Handles saving and loading user data (credentials + flashcards) to/from JSON files.
 * Fixed to prevent user credentials from being overwritten by flashcard data.
 * @author parts of these methods are generated with help from claude.ai
 * @author sofietw
 * @author ailinat
 * @author marennod
 * 
 */
public class FlashcardPersistent implements UserPersistence {

    private final ObjectMapper objectMapper;

    /**
     * Constructs a new FlashcardPersistent instance.
     * Initializes the ObjectMapper for JSON serialization/deserialization.
     */
    public FlashcardPersistent() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Saves flashcard deck manager for a user while preserving credentials.
     * If user data exists, updates only the deck manager and keeps existing credentials.
     * If user data doesn't exist, creates new UserData with the deck manager.
     * 
     * @param username the username to save deck data for
     * @param deckManager the flashcard deck manager to save
     * @throws IOException if file writing fails
     */
    public void writeDeck(String username, FlashcardDeckManager deckManager) throws IOException {
        
        //Read existing user data first
        UserData userData = readUserDataInternal(username);
        
        if (userData == null) {
            throw new IOException("User does not exist: " + username);
        }
        
        // Update only the deck manager, keep credentials
        userData.setDeckManager(deckManager);
        
        // Write back the complete user data
        writeUserDataInternal(userData);
    }

    /**
     * Loads flashcard deck manager for a user.
     * Returns the user's deck manager if user data exists, otherwise returns empty deck manager.
     * 
     * @param username the username to load deck data for
     * @return the user's FlashcardDeckManager or empty one if user doesn't exist
     * @throws IOException if file reading fails
     */
    public FlashcardDeckManager readDeck(String username) throws IOException {
        
        UserData userData = readUserDataInternal(username);
        
        if (userData != null) {
            return userData.getDeckManager();
        } else {
            return new FlashcardDeckManager();
        }
    }

    /**
     * Checks if user data file exists.
     * Verifies if a JSON file exists for the given username.
     * 
     * @param username the username to check data existence for
     * @return true if user data file exists, false otherwise
     */
    public boolean dataExists(String username) {
        File file = getUserFile(username);
        boolean exists = file.exists();
        return exists;
    }

    /**
     * Reads user data and returns as User object.
     * Converts UserData to User object for authentication purposes.
     * 
     * @param username the username to read data for
     * @return User object if found, null otherwise
     */
    @Override
    public User readUserData(String username) {        
        UserData userData = readUserDataInternal(username);
        User result = null;

        if(userData != null) result = userData.getUser();

        return result;
    }

    /**
     * Writes user credentials to file.
     * If user exists, updates credentials while preserving deck data.
     * If user is new, creates fresh user data with encoded password.
     * 
     * @param user the User object containing username and password
     * @throws IOException if file writing fails
     */
    @Override
    public void writeUserData(User user) throws IOException {        
        UserData existingData = readUserDataInternal(user.getUsername());

        if (existingData != null) {
            throw new IOException("User already exists: " + user.getUsername());
        } else {
            // New user, create fresh user data
            UserData userData = new UserData(user);
            writeUserDataInternal(userData);
        }
    }

    /**
     * Checks if a user exists with valid credentials.
     * Verifies that user data exists and contains both username and password.
     * 
     * @param username the username to check existence for
     * @return true if user exists with valid credentials, false otherwise
     */
    @Override
    public boolean userExists(String username) {
        UserData userData = readUserDataInternal(username);

        boolean exists = false;
        if(userData != null && userData.getUser().getUsername() != null && userData.getUser().getPassword() != null) exists=true;
        
        return exists;
    }

    /**
     * Internal method to read complete user data from file.
     * 
     * @param username the username to read data for
     * @return UserData object if found and valid, null otherwise
     */
    private UserData readUserDataInternal(String username) {        
        File file = getUserFile(username);

        if (file.exists()) {            
            try {
                // Try reading as UserData
                UserData userData = objectMapper.readValue(file, UserData.class);
                return userData;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }

    /**
     * Internal method to write complete user data to file.
     * Creates the user data directory if it doesn't exist and writes UserData as JSON.
     *
     * @param userData the UserData object to write
     * @throws IOException if file writing fails
     */
    private void writeUserDataInternal(UserData userData) throws IOException {
        File dataDir = new File(System.getProperty("user.dir") + "/../storage/data/users");
        if (!dataDir.exists() && !dataDir.mkdirs()) {
            throw new IOException("Failed to create directory: " + dataDir.getPath());
        }

        File file = getUserFile(userData.getUser().getUsername());
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, userData);
    }

    /**
     * Gets the file for a specific username.
     * Constructs the file path for the user's JSON data file.
     * 
     * @param username the username to get file for
     * @return File object pointing to the user's data file
     */
    private File getUserFile(String username) {
        String path = System.getProperty("user.dir") + "/../storage/data/users/" + username + ".json";        return new File(path);
    }
}