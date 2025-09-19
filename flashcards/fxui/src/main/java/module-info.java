module flashcards.fxui {
  requires flashcards.core;
  requires flashcards.storage;
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.graphics;
  
  opens ui to javafx.graphics, javafx.fxml;
}