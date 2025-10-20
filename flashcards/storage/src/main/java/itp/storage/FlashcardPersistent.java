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
 * @author @sofietw
 * @author @ailinat
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
        System.out.println("DEBUG: writeDeck called for user: " + username);
        
        //Read existing user data first
        UserData userData = readUserDataInternal(username);
        
        if (userData == null) {
            System.out.println("DEBUG: No user data found, please create a new user");
            throw new IOException("User does not exist: " + username);
        }
        
        System.out.println("DEBUG: Updating deck for user: " + username);
        // Update only the deck manager, keep credentials
        userData.setDeckManager(deckManager);
        
        // Write back the complete user data
        writeUserDataInternal(userData);
        System.out.println("DEBUG: Successfully wrote deck for user: " + username);
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
        System.out.println("DEBUG: readDeck called for user: " + username);
        
        UserData userData = readUserDataInternal(username);
        
        if (userData != null) {
            System.out.println("DEBUG: Found user data, returning deck manager for: " + username);
            return userData.getDeckManager();
        } else {
            System.out.println("DEBUG: No user data found, returning empty deck manager for: " + username);
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
        System.out.println("DEBUG: dataExists for " + username + ": " + exists);
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
        System.out.println("DEBUG: readUserData called for: " + username);
        
        UserData userData = readUserDataInternal(username);
        User result = null;

        if(userData != null) result = userData.getUser();
        
        if(result != null) System.out.println("DEBUG: readUserData result for " + username + ": " + "found");
        
        else System.out.println("DEBUG: readUserData result for " + username + ": " + "not found");

        
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
        System.out.println("DEBUG: writeUserData called for: " + user.getUsername());
        
        UserData existingData = readUserDataInternal(user.getUsername());

        if (existingData != null) {
            System.out.println("DEBUG: User already exists: " + user.getUsername());
            throw new IOException("User already exists: " + user.getUsername());
        } else {
            System.out.println("DEBUG: New user, creating fresh user data for: " + user.getUsername());
            // New user, create fresh user data
            UserData userData = new UserData(user);
            writeUserDataInternal(userData);
        }
        
        System.out.println("DEBUG: Successfully wrote user data for: " + user.getUsername());
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
        System.out.println("DEBUG: userExists called for: " + username);
        
        UserData userData = readUserDataInternal(username);

        boolean exists = false;
        if(userData != null && userData.getUser().getUsername() != null && userData.getUser().getPassword() != null) exists=true;
        
        System.out.println("DEBUG: userExists result for " + username + ": " + exists);
        return exists;
    }

    /**
     * Internal method to read complete user data from file.
     * 
     * @param username the username to read data for
     * @return UserData object if found and valid, null otherwise
     */
    private UserData readUserDataInternal(String username) {
        System.out.println("DEBUG: readUserDataInternal called for: " + username);
        
        File file = getUserFile(username);

        if (file.exists()) {
            System.out.println("DEBUG: File exists for: " + username);
            
            try {
                // Try reading as UserData
                User user = objectMapper.readValue(file, User.class);

                UserData userData = new UserData(user);
                System.out.println("DEBUG: Successfully read as UserData for: " + username);
                return userData;
            } catch (IOException e) {
                System.out.println("DEBUG: Failed to read UserData for: " + username);
            }
        } else {
            System.out.println("DEBUG: No file exists for: " + username);
        }
        
        return null;
    }

    /**
     * Internal method to write complete user data to file.
     * Creates the user data directory if it doesn't exist and writes UserData as JSON.
     * 
     * @param userData the UserData object to write
     * @throws IOException if directory creation or file writing fails
     */
    private void writeUserDataInternal(UserData userData) throws IOException {
        System.out.println("DEBUG: writeUserDataInternal called for: " + userData.getUser().getUsername());
        
        File dataDir = new File(System.getProperty("user.dir") + "/../storage/data/users");
        if (!dataDir.exists()) {
            boolean created = dataDir.mkdirs();
            if (!created) {
                throw new IOException("Failed to create data directory: " + dataDir.getAbsolutePath());
            }
        }
        
        File file = getUserFile(userData.getUser().getUsername());
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, userData);
        
        System.out.println("DEBUG: Successfully wrote UserData to file: " + file.getAbsolutePath());
    }

    /**
     * Gets the file for a specific username.
     * Constructs the file path for the user's JSON data file.
     * 
     * @param username the username to get file for
     * @return File object pointing to the user's data file
     */
    private File getUserFile(String username) {
        String path = System.getProperty("user.dir") + "/../storage/data/users/" + username + ".json";
        System.out.println("DEBUG: getUserFile for " + username + ": " + path);
        return new File(path);
    }


    public static void main(String[] args) {
        // Simple test
        FlashcardPersistent storage = new FlashcardPersistent();
        try {
            User testUser = new User("sofietw", "passord");
            storage.writeUserData(testUser);
            
            User loadedUser = storage.readUserData("sofietw");
            System.out.println("Loaded User: " + loadedUser.getUsername());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}