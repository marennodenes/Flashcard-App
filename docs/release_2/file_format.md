# File Format

In this release we have implemented user logic, so that we can have several users logging in to our flashcard app and make their own flascard-sets. When logging out the data (decks and flashcards) will be stored in JSON files in our storage folder.
JSON file format:

```java
{
  "decks" : [ {
    "deckName" : "new deck 1",
    "flashcards" : [ {
      "number" : 1,
      "question" : "question 1",
      "answer" : "answer 2"
    }, {
      "number" : 2,
      "question" : "question 2",
      "answer" : "answer 2"
    } ]
  }, {
    "deckName" : "new deck 2",
    "flashcards" : [ {
      "number" : 1,
      "question" : "question 1",
      "answer" : "answer 1"
    }, {
      "number" : 2,
      "question" : "question 2",
      "answer" : "answer 2"
    } ]
  } ]
}

```

The JSON file name is the users username. In the example above the file is named [defaultUserName.json](../../flashcards/storage/data/users/defaultUserName.json), where defaultUserName is the username.

## Explanation of fields

- **decks**: name of the collection of decks. This is a set name, and is the same for every users JSON file.
- **deckName**: what the user chooses to name the deck they are creating
- **flashcards**: a collection of the flashcards Q/A the user are creating inside the given deck. a flashcard consists of:
  - **number**: Every Q/A that the user is creating gets numbered when added to the system. This is to make our application scalabel for later implementations.
  - **question**: the question user is adding
  - **answer**: the answer to the question

## Reason for choosing JSON as the file format

- **Human readable and editable**: For our team this makes developing smooth and programming more intuitive
- **JSON as plaintext**: Easy for our team to handle when it comes to debugging and without needing extra tools.
- **Compact**: it works well for small datasets where the perfomance is not a critical issue, compared to other databases with higher runtime. This works perfect for our application since the scalability of our projects data is limited.
- **No external dependencies**: no need for database setup and maintnance, and the program can read and write to the JSON file directly. For our team, we consider this as good since the project sice is limited, so there is no need of extra dependencies.
