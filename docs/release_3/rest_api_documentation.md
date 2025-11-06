# API Documentation

## Introduction

This documentation provides a comprehensive overview of all API endpoints, including detailed request formats, response structures, and error handling standards. The API serves as the primary communication interface between the JavaFX client and the backend server, enabling secure user authentication, flashcard management, and deck operations.

### Purpose

The REST API allows secure interaction with various application features, including user authentication, deck management, and flashcard operations. Each endpoint is documented with clear specifications on required parameters, request bodies, and response types. Following this documentation will ensure correct and secure usage of the API.

## Base URL

```bash
http://localhost:8080
```

## Response Format

All API responses use a standardized `ApiResponse<T>` wrapper format:

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { ... }
}
```

Where:

- `success` (boolean, required): Indicates whether the operation succeeded
- `message` (string, required): Descriptive message about the operation result
- `data` (object, optional): The actual response payload (type varies by endpoint)

---

## User Management API

The User Management API provides endpoints for user registration, authentication, and account management.

### POST /api/v1/users/register

Register a new user by creating an account with a username and password.

#### POST Register Request

- **Endpoint**: /api/v1/users/register
- **Method**: POST
- **Headers**:
  - Content-Type: application/json
- **Request Body**:
  - `username` (string, required): The desired username
  - `password` (string, required): The desired password

#### POST Register Example Request

```json
{
  "username": "johndoe",
  "password": "SecurePass123!"
}
```

#### POST Register Response

- **200 OK**: Registration was successful, and the user data is returned.

  ```json
  {
    "success": true,
    "message": "User created successfully",
    "data": {
      "username": "johndoe",
      "flashcardDeckManager": {
        "decks": []
      }
    }
  }
  ```

- **400 BAD REQUEST**: The request format was invalid or validation failed.

  - "Username requirements not met:"
    - "Username 'johndoe' is already taken"
    - "Username cannot be empty"

  - "Password requirements not met:"
    - "Password must contain at least 8 characters, including uppercase, lowercase, numbers, and special characters"
    - "Password cannot be empty"

- **500 INTERNAL SERVER ERROR**: An error occurred on the server during registration.

  ```json
  {
    "success": false,
    "message": "Error creating user: [error details]",
    "data": null
  }
  ```

#### POST Register HTTP Status Codes

- **200 OK**: Registration was successful.
- **400 BAD REQUEST**: The request format was invalid or validation failed.
- **500 INTERNAL SERVER ERROR**: An error occurred on the server during registration.

---

### POST /api/v1/users/login

Authenticate a user by verifying their username and password.

#### POST Login Request

- **Endpoint**: /api/v1/users/login
- **Method**: POST
- **Headers**:
  - Content-Type: application/json
- **Request Body**:
  - `username` (string, required): The username of the user
  - `password` (string, required): The password for the user's account

#### POST Login Example Request

```json
{
  "username": "johndoe",
  "password": "SecurePass123!"
}
```

#### POST Login Response

- **200 OK**: The login was successful. User data and login status are returned.

  ```json
  {
    "success": true,
    "message": "Login successful",
    "data": {
      "loginSuccessful": true,
      "message": "Login successful: johndoe",
      "userData": {
        "username": "johndoe",
        "flashcardDeckManager": {
          "decks": [
            {
              "name": "Spanish Vocabulary",
              "flashcards": [...]
            }
          ]
        }
      }
    }
  }
  ```

- **401 UNAUTHORIZED**: The provided credentials are incorrect.

  ```json
  {
    "success": true,
    "message": "Login response",
    "data": {
      "loginSuccessful": false,
      "message": "Invalid password",
      "userData": null
    }
  }
  ```

  or

  ```json
  {
    "success": true,
    "message": "Login response",
    "data": {
      "loginSuccessful": false,
      "message": "User not found",
      "userData": null
    }
  }
  ```

- **500 INTERNAL SERVER ERROR**: An error occurred on the server during login.

  ```json
  {
    "success": false,
    "message": "Error logging in user: [error details]",
    "data": null
  }
  ```

#### POST Login HTTP Status Codes

- **200 OK**: The request was processed successfully (check `loginSuccessful` for actual login status).
- **500 INTERNAL SERVER ERROR**: An error occurred on the server during login.

---

### GET /api/v1/users

Retrieve user data for a specific username.

#### GET User Request

- **Endpoint**: /api/v1/users
- **Method**: GET
- **Headers**: None required
- **Query Parameters**:
  - `username` (string, required): The username to retrieve

#### GET User Example Request

- GET /api/v1/users?username=johndoe

#### GET User Response

- **200 OK**: User data retrieved successfully.

  ```json
  {
    "success": true,
    "message": "User retrieved successfully",
    "data": {
      "username": "johndoe",
      "flashcardDeckManager": {
        "decks": [...]
      }
    }
  }
  ```

- **404 NOT FOUND**: The username provided does not match any user in the system.

  ```json
  {
    "success": false,
    "message": "Error retrieving user: User not found",
    "data": null
  }
  ```

- **500 INTERNAL SERVER ERROR**: An error occurred on the server.

  ```json
  {
    "success": false,
    "message": "Error retrieving user: [error details]",
    "data": null
  }
  ```

#### GET User HTTP Status Codes

- **200 OK**: User data retrieved successfully.
- **404 NOT FOUND**: The username provided does not match any user in the system.
- **500 INTERNAL SERVER ERROR**: An error occurred on the server.

---

### GET /api/v1/users/find

Check if a user with the given username exists in the system.

#### GET Find User Request

- **Endpoint**: /api/v1/users/find
- **Method**: GET
- **Headers**: None required
- **Query Parameters**:
  - `username` (string, required): The username to check

#### GET Find User Example Request

- GET /api/v1/users/find?username=johndoe

#### GET Find User Response

- **200 OK**: User existence check completed successfully.

  ```json
  {
    "success": true,
    "message": "User existence check successful",
    "data": true
  }
  ```

  or

  ```json
  {
    "success": true,
    "message": "User existence check successful",
    "data": false
  }
  ```

- **500 INTERNAL SERVER ERROR**: An error occurred on the server.

  ```json
  {
    "success": false,
    "message": "Error checking user existence: [error details]",
    "data": null
  }
  ```

#### GET Find User HTTP Status Codes

- **200 OK**: User existence check completed successfully.
- **500 INTERNAL SERVER ERROR**: An error occurred on the server.

---

### POST /api/v1/users/validate-password

Validate a password for a specific user.

#### POST Validate Password Request

- **Endpoint**: /api/v1/users/validate-password
- **Method**: POST
- **Headers**: None required
- **Query Parameters**:
  - `username` (string, required): The username
  - `password` (string, required): The password to validate

#### POST Validate Password Example Request

- POST /api/v1/users/validate-password?username=johndoe&password=SecurePass123!

#### POST Validate Password Response

- **200 OK**: Password validation completed successfully.

  ```json
  {
    "success": true,
    "message": "Password validation successful",
    "data": true
  }
  ```

  or

  ```json
  {
    "success": true,
    "message": "Password validation successful",
    "data": false
  }
  ```

- **500 INTERNAL SERVER ERROR**: An error occurred on the server.

  ```json
  {
    "success": false,
    "message": "Error validating password: [error details]",
    "data": null
  }
  ```

#### POST Validate Password HTTP Status Codes

- **200 OK**: Password validation completed successfully.
- **500 INTERNAL SERVER ERROR**: An error occurred on the server.

---

### User Management Error Handling

User Management endpoints return structured error responses to assist with troubleshooting:

- **Error Format**:

  ```json
  {
    "success": false,
    "message": "Descriptive error message",
    "data": null
  }
  ```

- **Common Errors**:
  - **400 BAD REQUEST**: The request format was invalid or validation failed.
  - **401 UNAUTHORIZED**: The provided credentials are incorrect.
  - **404 NOT FOUND**: The username provided does not match any user in the system.
  - **500 INTERNAL SERVER ERROR**: An error occurred on the server.

---

## Deck Management API

The Deck Management API provides endpoints for creating, retrieving, updating, and deleting flashcard decks.

### GET /api/v1/decks

Retrieve all decks for a specific user.

#### GET All Decks Request

- **Endpoint**: /api/v1/decks
- **Method**: GET
- **Headers**: None required
- **Query Parameters**:
  - `username` (string, required): The username of the deck owner

#### GET All Decks Example Request

- GET /api/v1/decks?username=johndoe

#### GET All Decks Response

- **200 OK**: Decks retrieved successfully.

  ```json
  {
    "success": true,
    "message": "Decks retrieved successfully",
    "data": {
      "decks": [
        {
          "name": "Spanish Vocabulary",
          "flashcards": [
            {
              "question": "¿Cómo estás?",
              "answer": "How are you?"
            }
          ]
        },
        {
          "name": "Math Formulas",
          "flashcards": []
        }
      ]
    }
  }
  ```

- **404 NOT FOUND**: The username provided does not match any user in the system.

  ```json
  {
    "success": false,
    "message": "Error retrieving decks: User not found",
    "data": null
  }
  ```

- **500 INTERNAL SERVER ERROR**: An error occurred on the server.

  ```json
  {
    "success": false,
    "message": "Error retrieving decks: [error details]",
    "data": null
  }
  ```

#### GET All Decks HTTP Status Codes

- **200 OK**: Decks retrieved successfully.
- **404 NOT FOUND**: The username provided does not match any user in the system.
- **500 INTERNAL SERVER ERROR**: An error occurred on the server.

---

### GET /api/v1/decks/{deckName}

Retrieve a specific deck by name for a user.

#### GET Specific Deck Request

- **Endpoint**: /api/v1/decks/{deckName}
- **Method**: GET
- **Headers**: None required
- **Path Parameters**:
  - `deckName` (string, required): The name of the deck to retrieve
- **Query Parameters**:
  - `username` (string, required): The username of the deck owner

#### GET Specific Deck Example Request

- GET /api/v1/decks/Spanish%20Vocabulary?username=johndoe

#### GET Specific Deck Response

- **200 OK**: Deck retrieved successfully.

  ```json
  {
    "success": true,
    "message": "Deck retrieved successfully",
    "data": {
      "name": "Spanish Vocabulary",
      "flashcards": [
        {
          "question": "¿Cómo estás?",
          "answer": "How are you?"
        }
      ]
    }
  }
  ```

- **404 NOT FOUND**: The deck or user was not found.

  ```json
  {
    "success": false,
    "message": "Error retrieving deck: Deck not found",
    "data": null
  }
  ```

- **500 INTERNAL SERVER ERROR**: An error occurred on the server.

  ```json
  {
    "success": false,
    "message": "Error retrieving deck: [error details]",
    "data": null
  }
  ```

#### GET Specific Deck HTTP Status Codes

- **200 OK**: Deck retrieved successfully.
- **404 NOT FOUND**: The deck or user was not found.
- **500 INTERNAL SERVER ERROR**: An error occurred on the server.

---

### POST /api/v1/decks/{deckName}

Create a new deck for a user.

#### POST Create Deck Request

- **Endpoint**: /api/v1/decks/{deckName}
- **Method**: POST
- **Headers**: None required
- **Path Parameters**:
  - `deckName` (string, required): The name of the deck to create
- **Query Parameters**:
  - `username` (string, required): The username of the deck owner

#### POST Create Deck Example Request

- POST /api/v1/decks/Spanish%20Vocabulary?username=johndoe

#### POST Create Deck Response

- **200 OK**: Deck created successfully.

  ```json
  {
    "success": true,
    "message": "Deck created successfully",
    "data": {
      "name": "Spanish Vocabulary",
      "flashcards": []
    }
  }
  ```

- **400 BAD REQUEST**: The deck already exists or validation failed.

  ```json
  {
    "success": false,
    "message": "Error creating deck: Deck already exists",
    "data": null
  }
  ```

- **404 NOT FOUND**: The username provided does not match any user in the system.

  ```json
  {
    "success": false,
    "message": "Error creating deck: User not found",
    "data": null
  }
  ```

- **500 INTERNAL SERVER ERROR**: An error occurred on the server.

  ```json
  {
    "success": false,
    "message": "Error creating deck: [error details]",
    "data": null
  }
  ```

#### POST Create Deck HTTP Status Codes

- **200 OK**: Deck created successfully.
- **400 BAD REQUEST**: The deck already exists or validation failed.
- **404 NOT FOUND**: The username provided does not match any user in the system.
- **500 INTERNAL SERVER ERROR**: An error occurred on the server.

---

### DELETE /api/v1/decks/{deckName}

Delete a deck for a user.

#### DELETE Deck Request

- **Endpoint**: /api/v1/decks/{deckName}
- **Method**: DELETE
- **Headers**: None required
- **Path Parameters**:
  - `deckName` (string, required): The name of the deck to delete
- **Query Parameters**:
  - `username` (string, required): The username of the deck owner

#### DELETE Deck Example Request

- DELETE /api/v1/decks/Spanish%20Vocabulary?username=johndoe

#### DELETE Deck Response

- **200 OK**: Deck deleted successfully.

  ```json
  {
    "success": true,
    "message": "Deck deleted successfully",
    "data": null
  }
  ```

- **404 NOT FOUND**: The deck or user was not found.

  ```json
  {
    "success": false,
    "message": "Error deleting deck: Deck not found",
    "data": null
  }
  ```

- **500 INTERNAL SERVER ERROR**: An error occurred on the server.

  ```json
  {
    "success": false,
    "message": "Error deleting deck: [error details]",
    "data": null
  }
  ```

#### DELETE Deck HTTP Status Codes

- **200 OK**: Deck deleted successfully.
- **404 NOT FOUND**: The deck or user was not found.
- **500 INTERNAL SERVER ERROR**: An error occurred on the server.

---

### PUT /api/v1/decks

Update all decks for a user.

#### PUT Update Decks Request

- **Endpoint**: /api/v1/decks
- **Method**: PUT
- **Headers**:
  - Content-Type: application/json
- **Query Parameters**:
  - `username` (string, required): The username of the deck owner
- **Request Body**:
  - `decks` (array, required): A list of deck objects representing the user's updated decks

#### PUT Update Decks Example Request

```json
{
  "decks": [
    {
      "name": "Spanish Vocabulary",
      "flashcards": [
        {
          "question": "¿Cómo estás?",
          "answer": "How are you?"
        }
      ]
    },
    {
      "name": "Math Formulas",
      "flashcards": []
    }
  ]
}
```

#### PUT Update Decks Response

- **200 OK**: Decks updated successfully.

  ```json
  {
    "success": true,
    "message": "Decks updated successfully",
    "data": null
  }
  ```

- **404 NOT FOUND**: The username provided does not match any user in the system.

  ```json
  {
    "success": false,
    "message": "Error updating decks: User not found",
    "data": null
  }
  ```

- **500 INTERNAL SERVER ERROR**: An error occurred on the server.

  ```json
  {
    "success": false,
    "message": "Error updating decks: [error details]",
    "data": null
  }
  ```

#### PUT Update Decks HTTP Status Codes

- **200 OK**: Decks updated successfully.
- **404 NOT FOUND**: The username provided does not match any user in the system.
- **500 INTERNAL SERVER ERROR**: An error occurred on the server.

---

### Deck Management Error Handling

Deck Management endpoints return structured error responses to assist with troubleshooting:

- **Error Format**:

  ```json
  {
    "success": false,
    "message": "Descriptive error message",
    "data": null
  }
  ```

- **Common Errors**:
  - **400 BAD REQUEST**: The request format was invalid or the deck already exists.
  - **404 NOT FOUND**: The deck or user was not found.
  - **500 INTERNAL SERVER ERROR**: An error occurred on the server.

---

## Flashcard Management API

The Flashcard Management API provides endpoints for creating, retrieving, and deleting flashcards within decks.

### POST /api/v1/flashcards/create

Create a new flashcard in a specified deck.

#### POST Create Flashcard Request

- **Endpoint**: /api/v1/flashcards/create
- **Method**: POST
- **Headers**: None required
- **Query Parameters**:
  - `username` (string, required): The username of the deck owner
  - `deckname` (string, required): The name of the deck
  - `question` (string, required): The question text for the flashcard
  - `answer` (string, required): The answer text for the flashcard

#### POST Create Flashcard Example Request

- POST /api/v1/flashcards/create?username=johndoe&deckname=Spanish%20Vocabulary&question=%C2%BFC%C3%B3mo%20est%C3%A1s%3F&answer=How%20are%20you%3F

#### POST Create Flashcard Response

- **200 OK**: Flashcard created successfully.

  ```json
  {
    "success": true,
    "message": "Flashcard created successfully",
    "data": {
      "question": "¿Cómo estás?",
      "answer": "How are you?"
    }
  }
  ```

- **404 NOT FOUND**: The deck or user was not found.

  ```json
  {
    "success": false,
    "message": "Failed to create flashcard: Deck not found",
    "data": null
  }
  ```

- **500 INTERNAL SERVER ERROR**: An error occurred on the server.

  ```json
  {
    "success": false,
    "message": "Failed to create flashcard: [error details]",
    "data": null
  }
  ```

#### POST Create Flashcard HTTP Status Codes

- **200 OK**: Flashcard created successfully.
- **404 NOT FOUND**: The deck or user was not found.
- **500 INTERNAL SERVER ERROR**: An error occurred on the server.

---

### GET /api/v1/flashcards/get

Retrieve a specific flashcard by its position in a deck.

#### GET Flashcard Request

- **Endpoint**: /api/v1/flashcards/get
- **Method**: GET
- **Headers**: None required
- **Query Parameters**:
  - `username` (string, required): The username of the deck owner
  - `deckname` (string, required): The name of the deck
  - `number` (integer, required): The position/index of the flashcard (1-indexed)

#### GET Flashcard Example Request

- GET /api/v1/flashcards/get?username=johndoe&deckname=Spanish%20Vocabulary&number=1

#### GET Flashcard Response

- **200 OK**: Flashcard retrieved successfully.

  ```json
  {
    "success": true,
    "message": "Flashcard retrieved successfully",
    "data": {
      "question": "¿Cómo estás?",
      "answer": "How are you?"
    }
  }
  ```

- **404 NOT FOUND**: The flashcard, deck, or user was not found.

  ```json
  {
    "success": false,
    "message": "Failed to retrieve flashcard: Flashcard not found at position 1",
    "data": null
  }
  ```

- **500 INTERNAL SERVER ERROR**: An error occurred on the server.

  ```json
  {
    "success": false,
    "message": "Failed to retrieve flashcard: [error details]",
    "data": null
  }
  ```

#### GET Flashcard HTTP Status Codes

- **200 OK**: Flashcard retrieved successfully.
- **404 NOT FOUND**: The flashcard, deck, or user was not found.
- **500 INTERNAL SERVER ERROR**: An error occurred on the server.

---

### GET /api/v1/flashcards/get-all

Retrieve all flashcards from a specific deck.

#### GET All Flashcards Request

- **Endpoint**: /api/v1/flashcards/get-all
- **Method**: GET
- **Headers**: None required
- **Query Parameters**:
  - `username` (string, required): The username of the deck owner
  - `deckname` (string, required): The name of the deck

#### GET All Flashcards Example Request

- GET /api/v1/flashcards/get-all?username=johndoe&deckname=Spanish%20Vocabulary

#### GET All Flashcards Response

- **200 OK**: Flashcards retrieved successfully.

  ```json
  {
    "success": true,
    "message": "Flashcards retrieved successfully",
    "data": [
      {
        "question": "¿Cómo estás?",
        "answer": "How are you?"
      },
      {
        "question": "¿Qué tal?",
        "answer": "What's up?"
      }
    ]
  }
  ```

- **404 NOT FOUND**: The deck or user was not found.

  ```json
  {
    "success": false,
    "message": "Failed to retrieve flashcards: Deck not found",
    "data": null
  }
  ```

- **500 INTERNAL SERVER ERROR**: An error occurred on the server.

  ```json
  {
    "success": false,
    "message": "Failed to retrieve flashcards: [error details]",
    "data": null
  }
  ```

#### GET All Flashcards HTTP Status Codes

- **200 OK**: Flashcards retrieved successfully.
- **404 NOT FOUND**: The deck or user was not found.
- **500 INTERNAL SERVER ERROR**: An error occurred on the server.

---

### DELETE /api/v1/flashcards/delete

Delete a flashcard by its position in a deck.

#### DELETE Flashcard Request

- **Endpoint**: /api/v1/flashcards/delete
- **Method**: DELETE
- **Headers**: None required
- **Query Parameters**:
  - `username` (string, required): The username of the deck owner
  - `deckname` (string, required): The name of the deck
  - `number` (integer, required): The position/index of the flashcard to delete (1-indexed)

#### DELETE Flashcard Example Request

- DELETE /api/v1/flashcards/delete?username=johndoe&deckname=Spanish%20Vocabulary&number=1

#### DELETE Flashcard Response

- **200 OK**: Flashcard deleted successfully.

  ```json
  {
    "success": true,
    "message": "Flashcard deleted successfully",
    "data": null
  }
  ```

- **404 NOT FOUND**: The flashcard, deck, or user was not found.

  ```json
  {
    "success": false,
    "message": "Failed to delete flashcard: Flashcard not found at position 1",
    "data": null
  }
  ```

- **500 INTERNAL SERVER ERROR**: An error occurred on the server.

  ```json
  {
    "success": false,
    "message": "Failed to delete flashcard: [error details]",
    "data": null
  }
  ```

#### DELETE Flashcard HTTP Status Codes

- **200 OK**: Flashcard deleted successfully.
- **404 NOT FOUND**: The flashcard, deck, or user was not found.
- **500 INTERNAL SERVER ERROR**: An error occurred on the server.

---

### Flashcard Management Error Handling

Flashcard Management endpoints return structured error responses to assist with troubleshooting:

- **Error Format**:

  ```json
  {
    "success": false,
    "message": "Descriptive error message",
    "data": null
  }
  ```

- **Common Errors**:
  - **404 NOT FOUND**: The flashcard, deck, or user was not found.
  - **500 INTERNAL SERVER ERROR**: An error occurred on the server.

---

## General Error Handling

All API endpoints follow a consistent error handling pattern:

- **Error Response Format**:

  ```json
  {
    "success": false,
    "message": "Descriptive error message explaining what went wrong",
    "data": null
  }
  ```

- **HTTP Status Codes**:
  - **200 OK**: The request was successful
  - **400 BAD REQUEST**: The request format was invalid or validation failed
  - **401 UNAUTHORIZED**: The provided credentials are incorrect
  - **404 NOT FOUND**: The requested resource was not found
  - **500 INTERNAL SERVER ERROR**: An unexpected server error occurred

---

## Security Considerations

The REST API implements several security measures to protect user data and ensure secure communication:

- **Password Hashing**: All passwords are hashed using SHA-256 with unique salt generation before storage
- **Password Requirements**: Passwords must contain at least 8 characters, including uppercase, lowercase, numbers, and special characters
- **Input Validation**: Multi-layer validation prevents injection attacks and ensures data integrity
- **Error Messages**: Error messages are informative without leaking sensitive system information
- **Stateless Design**: The API uses a stateless design for scalability and security
- **Secure HTTP Client**: The client implementation includes proper exception handling and secure communication practices

---

**Note**: This API uses JSON-wrapped responses with the `ApiResponse<T>` format for all endpoints, providing a consistent structure that includes success status, descriptive messages, and typed data payloads. This approach facilitates error handling and data processing in the JavaFX client application.
