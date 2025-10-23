module flashcards.shared {
  requires com.fasterxml.jackson.annotation;
  requires jakarta.validation;
  requires flashcards.core;
  
  exports dto;
  exports shared;
}
