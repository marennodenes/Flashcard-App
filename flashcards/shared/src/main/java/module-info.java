module flashcards.shared {
  requires com.fasterxml.jackson.annotation;
  requires jakarta.validation;
  requires transitive flashcards.core;
  
  exports dto;
  exports dto.mappers;
  exports shared;
}
