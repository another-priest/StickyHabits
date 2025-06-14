module org.example.stickyhabits {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires smile.core;
    requires smile.base;
    requires java.prefs;
    //requires smile.data;

    opens org.example.stickyhabits to javafx.fxml;
    exports org.example.stickyhabits;
    exports org.example.stickyhabits.Controllers;
    opens org.example.stickyhabits.Controllers to javafx.fxml;

}