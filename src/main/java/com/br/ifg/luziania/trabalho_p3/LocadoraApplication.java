package com.br.ifg.luziania.trabalho_p3;

import com.br.ifg.luziania.trabalho_p3.util.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class LocadoraApplication extends Application {
    private static final double LARGURA_INICIAL = 1100;
    private static final double ALTURA_INICIAL = 720;
    private static final double LARGURA_MINIMA = 950;
    private static final double ALTURA_MINIMA = 650;

    @Override
    public void start(Stage stage) throws IOException {
        testarConexaoBanco();

        FXMLLoader fxmlLoader = new FXMLLoader(LocadoraApplication.class.getResource("/fxml/Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), LARGURA_INICIAL, ALTURA_INICIAL);
        stage.setTitle("Locadora");
        stage.setScene(scene);
        // Tamanho mínimo para evitar telas quebradas.
        stage.setMinWidth(LARGURA_MINIMA);
        stage.setMinHeight(ALTURA_MINIMA);
        // Permite redimensionar manualmente.
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }
    private void testarConexaoBanco() {
        try (Connection conn = DBConnection.getConexao()) {
            System.out.println("Banco de dados conectado: " + !conn.isClosed());
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
