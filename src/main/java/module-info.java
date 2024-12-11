module com.example.buddy_system {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.buddy_system to javafx.fxml;
    exports com.example.buddy_system;
}