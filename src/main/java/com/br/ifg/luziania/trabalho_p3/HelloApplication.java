package com.br.ifg.luziania.trabalho_p3;

import com.br.ifg.luziania.trabalho_p3.util.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            Connection conn = DBConnection.getConexao();
            System.out.println("Banco de dados conectado: "  + !conn.isClosed());
            conn.close();
        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/fxml/Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Locadora");
        stage.setScene(scene);
        stage.show();
    }
}
