
# GR2516

[open in Eclipse Che](https://che.stud.ntnu.no/ailinat-stud-ntnu-no/javafx-template/3100/)

## Overview

This repository contains an application for creating and revising flashcards. Each user creates a profile, and their flashcard lists are stored under that profile.

The project is built with:

* Java 17+
* JavaFX 21
* Maven
* JUnit 5 (Jupiter) + TestFX for testing

## Project structure

This repository is structured with a modules-template.

The actual Flashcards application is implemented in the gr2516 module.

### Key files

* core - application logic
  [Flashcard.java](gr2516/core/src/main/java/app/Flashcard.java)
  [FlashcardArray.java](gr2516/core/src/main/java/app/FlashcardArray.java)
* fxui - user interface
  [FlashcardMainUI.fxml](gr2516/fxui/src/main/resources/ui/FlashcardMainUI.fxml)
  [FlashcardController.java](gr2516/fxui/src/main/ui/FlashcardController.java)
  [FlashcardApp.java](gr2516/fxui/src/main/ui/FlashcardApp.java)

There are test located under [fxui/src/test/java/ui](gr2516/fxui/src/main/test/java/ui)
See the [test README](gr2516/fxui/src/main/test/java/ui/README.md) for more details

## Getting started

### Build and run

All projects can be tried out by cd-ing into the corresponding module (e.g. gr2516) and using `mvn`:

* change directory to the desired sample, such as `cd gr2516`
* if using the multi-module template run `mvn clean install` to allow local dependencies to be available
* compile with `mvn compile`
* test with `mvn test`
* run quality checks with `mvn verify`
* run with `mvn javafx:run`
