module flashcards.fxui {
  requires flashcards.core;
  requires flashcards.storage;
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.graphics;
  requires javafx.base;
  
  // HTTP client for REST API communication
  requires java.net.http;
  
  // Jackson for JSON processing
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.datatype.jsr310;
  
  // Exports the UI package to allow access from other modules
  opens ui to javafx.graphics, javafx.fxml;
  requires flashcards.dto;
}