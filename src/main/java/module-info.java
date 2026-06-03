module com.br.ifg.luziania.trabalho_p3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.postgresql.jdbc;
    requires org.controlsfx.controls;
    requires jbcrypt;

    opens com.br.ifg.luziania.trabalho_p3 to javafx.fxml, javafx.graphics;
    opens com.br.ifg.luziania.trabalho_p3.controller to javafx.fxml;
    opens com.br.ifg.luziania.trabalho_p3.model to javafx.base, javafx.fxml;

    exports com.br.ifg.luziania.trabalho_p3;
}