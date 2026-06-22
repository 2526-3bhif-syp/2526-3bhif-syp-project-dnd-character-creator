module com.dnd.creator {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.gson;
    requires org.apache.pdfbox;
    // PDFBox logs via commons-logging, which is a named module that nothing on the
    // module path 'requires', so it must be pulled into the graph explicitly or
    // PDFBox fails at runtime with NoClassDefFoundError: org/apache/commons/logging.
    requires org.apache.commons.logging;

    opens com.dnd.creator.view to javafx.fxml;
    opens com.dnd.creator.model to com.google.gson;
    exports com.dnd.creator;
    exports com.dnd.creator.model;
    exports com.dnd.creator.presenter;
    exports com.dnd.creator.view;
    exports com.dnd.creator.export;
}
