module ui.volleyregistration {
    requires javafx.controls;
    requires javafx.fxml;

    exports ui;
    exports model;
    exports service;
    exports exception;

    opens ui to javafx.fxml;
    exports terminal;
    opens terminal to javafx.fxml;
}