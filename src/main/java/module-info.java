module com.example.miniproject2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.miniproject2 to javafx.fxml;
    opens com.example.miniproject2.view to javafx.fxml;

    exports com.example.miniproject2;
    exports com.example.miniproject2.controller;
    exports com.example.miniproject2.model;
    exports com.example.miniproject2.view;
}