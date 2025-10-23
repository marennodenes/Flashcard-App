module flashcards.shared {
  requires com.fasterxml.jackson.annotation;
  opens dto to com.fasterxml.jackson.databind;
  requires jakarta.validation;
  requires transitive flashcards.core;
  
  exports dto;
  exports dto.mappers;
  exports shared;
}
