module flashcards.storage {
  requires transitive flashcards.core;
  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.core;
  requires java.base;
  
  exports itp.storage;
}