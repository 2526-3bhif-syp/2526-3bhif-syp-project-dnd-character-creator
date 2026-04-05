module com.dnd.creator {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.dnd.creator.view to javafx.fxml;
    opens com.dnd.creator.model to com.google.gson;
    exports com.dnd.creator;
    exports com.dnd.creator.model;
    exports com.dnd.creator.presenter;
    exports com.dnd.creator.view;
}
