
# Javafx template

[open in Eclipse Che](https://che.stud.ntnu.no/#https://git.ntnu.no/IT1901-dev/javafx-template?new)

A repository with three variants of a javafx projects, with maven setup for Java 17+ and JavaFX 21, and JUnit 5 (Jupiter) and TestFX for testing.

Use this as a starting point for your project to see examples of configuration. It is recommended to use modules-template for your project, but you can start with a simpler template and move on to multi module multi package project later.

To make the project(s) templates more interesting, it is the start of an [RPN](https://en.wikipedia.org/wiki/Reverse_Polish_notation) calculator. 
The core logic is implemented (in [Calc.java](javafx-template/src/main/java/app/Calc.java)), the fxml file (in [App.fxml](javafx-template/src/main/resources/app/App.fxml) and the controller class (in [AppController.java](javafx-template/src/main/java/app/AppController.java). And last, but not least, there is a TestFX-based test (in [AppTest.java](javafx-template/src/test/java/app/AppTest.java), see the [README](javafx-template/src/test/java/app/README.md) for details about what it tests).

## javafx-template

Template for  single-module, single-package javafx project.

## packages-template

Template for  single-module, multi-package javafx project.

## modules-template

Template for  multi-module, multi-package javafx project.

## Trying it out

All projects can be tried out by cd-ing into the corresponding folder and using `mvn`:

- change directory to the desired sample, such as `cd javafx-template`
- compile with `mvn compile`
- test with `mvn test`
- run quality checks with `mvn verify`
- run with `mvn javafx:run`
