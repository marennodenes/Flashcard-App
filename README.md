
# GR2516

[open in Eclipse Che](https://che.stud.ntnu.no/#https://git.ntnu.no/IT1901-2025-groups/gr2516.git)

## Overview

This repository contains an application for creating and revising flashcards. Each user creates a profile, and their flashcard lists are stored under that profile.

The project is built with:

* Java 17+
* JavaFX 21
* Maven
* JUnit 5 (Jupiter) + TestFX for testing

## User stories

The functionalities prioritised for the first release are based on these userstories:

* As a user, I want to create a new deck, so that I can group related flashcards by subject.
* As a user, I want to add a card with a question/term on one side and an answer/definition on the other, so that I can test myself later.

## Project structure

This repository is structured with a modules-template inside the `gr2516` root folder.

The actual Flashcards application is implemented in the `flashcards` module.

### Key files

* core - application logic
  [Flashcard.java](flashcards/core/src/main/java/app/Flashcard.java)
* fxui - user interface
  [FlashcardMainUI.fxml](flashcards/fxui/src/main/resources/ui/FlashcardMainUI.fxml)
  [FlashcardController.java](flashcards/fxui/src/main/ui/FlashcardController.java)
  [FlashcardApp.java](flashcards/fxui/src/main/ui/FlashcardApp.java)
* storage - saving and handling of files
  [FlashcardPersistent.java](flashcards/storage/src/main/java/itp/storage/FlashcardPersistent.java)

There are tests located in files [FlashcardManagerTest.java](flashcards/fxui/src/test/java/ui/FlashcardManagerTest.java) and [FlashcardTest.java](flashcards/core/src/test/java/app/FlashcardTest.java)

### Illustration of completed project

Below you will find some images illustrating how the user interface should when running the application.

![illustration of ui in release 1](images/release_1.png)
![illustration of ui in release 1 with questions and answers](images/release_1_example.png)

## Use of AI

As part of this project, we made use of AI-based tools to improve both efficiency and accuracy when working with complex features. In particular, we relied on Claude Sonnet 4 to explore recommended approaches for implementing our app.

In addition we used Chat GPT for more project setup related problems.

## Getting started

### Build and run

All projects can be tried out by cd-ing into the corresponding module (e.g. flashcards) and using `mvn`:

* change directory to the desired sample, such as `cd flashcards`
* if using the multi-module template run `mvn clean install` to allow local dependencies to be available
* compile with `mvn compile`
* test with `mvn test`
* run quality checks with `mvn verify`
* use `cd fxui` to navigate to the fxui folder and run with `mvn javafx:run` in fxui folder
