module gr2516.fxui {
  requires gr2516.core;
  requires gr2516.storage;
  requires javafx.controls;
  requires javafx.fxml;
  
  opens ui to javafx.graphics, javafx.fxml;
}