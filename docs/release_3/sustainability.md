# Sustainability and Scalability

This document explains what we would need to change in our Flashcards app if it became popular and had millions of users.

## Current Setup

Right now our app has:

- **JSON files** - Each user has their own `.json` file with all their flashcards
- **JavaFX desktop app** - Users run the app on their computer
- **REST API server** - Handles requests between the app and storage
- **File-based storage** - All data is stored as files on the server

This works fine for a few hundred users, but would break with millions of users.

## Main Problems with Many Users

### 1. Storage Problems

**What we have now:**

- Each user gets their own JSON file (like `marie.json`, `erik.json`)
- All files are stored on one server

**Why this won't work with 1 million users:**

- The server would have 1 million separate files - this becomes very slow
- If multiple users try to save at the same time, files can get corrupted
- Finding a specific user takes too long
- No way to backup or copy data easily

### 2. Login and Security Issues

**Current system:**

- Simple username/password login
- App remembers who you are on your computer

**Problems with many users:**

- Can't handle users logging in from multiple devices
- No protection against too many failed login attempts
- Hard to log out users from all their devices

### 3. Server Performance

**Current setup:**

- Server does one thing at a time
- No way to handle many users at once
- Gets slower as more people use it

### 4. User Interface Limitations

**Current problem:** The app has a fixed window that cannot adapt to different devices or user needs.

**What's wrong with our current JavaFX app:**

- **Fixed tiny window size** - Cannot be resized larger or smaller by users
- **Hard-coded FXML layouts** - All buttons, text fields, and components have fixed pixel positions
- **Desktop-only design** - Only works on computers, completely unusable on phones or tablets
- **Poor user experience** - Users are stuck with whatever window size we chose
- **No accessibility options** - Cannot adjust text size or layout for different needs
- **One-size-fits-all approach** - Doesn't work well on small laptop screens or large monitors

**Real-world impact:**

- Students with small laptop screens struggle to use the app effectively
- App does not work on phones where students might want to study on the go
- Users with vision issues cannot make text larger

## What We Would Need to Change

### 1. Switch from Files to Database

**The biggest change:** Replace our JSON files with a proper database.

**Current JSON approach:**

Each user has their own file (like `julie.json`):

```json
{
  "username": "julie",
  "password": "encrypted_password",
  "deckManager": {
    "decks": [
      {
        "deckName": "Spanish Vocabulary",
        "flashcards": [
          {"number": 1, "question": "Hello", "answer": "Hola"},
          {"number": 2, "question": "Goodbye", "answer": "Adiós"}
        ]
      }
    ]
  }
}
```

**New database approach:**

```sql
-- Users table
CREATE TABLE users (
    id INT PRIMARY KEY,
    username VARCHAR(50),
    password VARCHAR(255)
);

-- Decks table  
CREATE TABLE decks (
    id INT PRIMARY KEY,
    user_id INT,
    deck_name VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Flashcards table
CREATE TABLE flashcards (
    id INT PRIMARY KEY,
    deck_id INT,
    question TEXT,
    answer TEXT,
    FOREIGN KEY (deck_id) REFERENCES decks(id)
);
```

**Why this helps:**

- Much faster to find and save data
- Can handle many users at the same time
- Easy to backup and restore
- Can search through all flashcards quickly

### 2. Better Login System

**Current problem:** Users can only log in from one computer.

**Solution:** Use login tokens that work on any device.

- Users can sign in from phones, tablets, and computers
- Automatic logout after a period of inactivity or on suspicious activity
- Option to remember a device for a limited time, with the ability to revoke sessions from all devices

### 3. Handle More Users at Once

**Current problem:** Server gets slow when many people use it.

**Solutions:**

- Use multiple servers instead of just one
- Cache (temporarily store) popular data for quick access
- Add more servers automatically when busy

### 4. Create Modern, Flexible User Interface

**The solution:** Move away from fixed JavaFX desktop app to modern web and mobile interfaces.

**Short-term improvements:**

- Try to make current FXML layouts more flexible (but this has limits)
- Allow users to resize the window properly

**Long-term solution - Build new frontends:**

- **Web app with React** - Works on any device with a browser (phones, tablets, laptops)
- **Mobile apps** - Native apps for iPhone and Android
- **Keep desktop option** - Could be a web app in a desktop wrapper

**The smart approach - Separate frontend and backend:**

Instead of everything in one JavaFX app, we split it:

- **Frontend (what users see)** - React web app, mobile apps, etc.
- **Backend (data and logic)** - Our existing REST API server handles all the business logic

**Why this separation works:**

- **One backend, many frontends** - Same data and logic works for web, mobile, and desktop
- **Easier development** - Frontend team works on user interface, backend team works on data
- **Better user experience** - Each platform gets an interface designed specifically for it
- **Future-proof** - Can easily add new platforms (smartwatch app, voice interface, etc.)
- **Faster updates** - Can update web app instantly without users downloading anything

**Real benefits for users:**

- Study flashcards on phone
- Use full-screen mode on laptop for better focus
- Switch between devices and pick up where they left off
- App automatically adapts to their screen size and device type

## Conclusion

Right now our flashcards app works well for a small number of users. If it became popular with millions of users, we would need to make larger changes:

**Most important changes:**

- Switch from JSON files to a database
- Make the user interface responsive so it works across device sizes

**Other changes needed:**

- Better login system supporting multiple devices and session management
- Multiple servers to handle more users
- Web and mobile versions of the app
- Bigger development team
- More expensive hosting

**The good news:** We can make these changes gradually without breaking the current app. Start with the database migration and a responsive UI, then add the other improvements as demand grows.

This way, our app could grow from tens of users to millions without major problems.
