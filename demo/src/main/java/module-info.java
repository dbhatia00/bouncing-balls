module bouncingballs {
    requires javafx.controls;
    requires javafx.fxml;

    opens bouncingballs to javafx.fxml;
    exports bouncingballs;
}
