module com.four.othello {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.four.othello to javafx.fxml;
    exports com.four.othello;
}