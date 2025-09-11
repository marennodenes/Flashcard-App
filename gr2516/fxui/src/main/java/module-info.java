module gr2516.fxui {
  requires gr2516.core;
  requires javafx.controls;
  requires javafx.fxml;
  
  opens ui to javafx.graphics, javafx.fxml;
}