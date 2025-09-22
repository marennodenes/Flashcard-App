module flashcards.core {
  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.core;
  exports app;
  opens app to com.fasterxml.jackson.databind;
}