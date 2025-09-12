
# GR2516

[open in Eclipse Che](https://che.stud.ntnu.no/#https://git.ntnu.no/IT1901-dev/javafx-template?new)

A repository with three variants of a javafx projects, with maven setup for Java 17+ and JavaFX 21, and JUnit 5 (Jupiter) and TestFX for testing.

This project features an app for creating and revising flashcards. Each new user will create a profile to which each list of flashcards will be saved.

The core logic is implemented in [Flashcard.java](gr2516/core/src/main/java/app/Flashcard.java), [FlashcardArray.java](gr2516/core/src/main/java/app/FlashcardArray.java), the fxml file [FlashcardMainUI.fxml](gr2516/fxui/src/main/resources/ui/FlashcardMainUI.fxml), [FlashcardApp.java](gr2516/fxui/src/main/ui/FlashcardApp.java) and the app controller [FlashcardController.java](gr2516/fxui/src/main/ui/FlashcardController.java). 

We also have the TestFX-based test in [InsertAppFileName.java](gr2516/fxui/src/main/test/java/ui/...) (see the [README](gr2516/fxui/src/main/test/java/ui/README.md) for more details abotu the test).


## javafx-template

Template for  single-module, single-package javafx project.

## packages-template

Template for  single-module, multi-package javafx project.

## modules-template

Template for  multi-module, multi-package javafx project.

## Trying it out

All projects can be tried out by cd-ing into the corresponding folder and using `mvn`:

- change directory to the desired sample, such as `cd javafx-template`
- if using the multi-module template run `mvn clean install` to allow local dependencies to be available
- compile with `mvn compile`
- test with `mvn test`
- run quality checks with `mvn verify`
- run with `mvn javafx:run`
