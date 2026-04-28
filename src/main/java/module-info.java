module com.br.ifg.luziania.trabalho_p3 {
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

    opens com.br.ifg.luziania.trabalho_p3 to javafx.fxml;
    opens com.br.ifg.luziania.trabalho_p3.controller to javafx.fxml;
    opens com.br.ifg.luziania.trabalho_p3.model to javafx.fxml;
    exports com.br.ifg.luziania.trabalho_p3;
}