module com.dnd.creator {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.dnd.creator.view to javafx.fxml;
    exports com.dnd.creator;
    exports com.dnd.creator.model;
    exports com.dnd.creator.presenter;
    exports com.dnd.creator.view;
}
