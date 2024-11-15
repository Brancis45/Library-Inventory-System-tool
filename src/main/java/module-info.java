module com.myapp.listapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires java.desktop;
    requires java.sql;

    opens com.myapp.listapp to javafx.fxml;
    exports com.myapp.listapp;
}