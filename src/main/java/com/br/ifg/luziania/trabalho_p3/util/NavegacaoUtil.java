package com.br.ifg.luziania.trabalho_p3.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Stage;

import java.io.IOException;

// Centraliza a troca de telas da aplicação.
public class NavegacaoUtil {

    private static final double LARGURA_INICIAL = 1100;
    private static final double ALTURA_INICIAL = 720;
    private static final double LARGURA_MINIMA = 950;
    private static final double ALTURA_MINIMA = 650;

    private NavegacaoUtil() {
        // Evita instanciar classe utilitária.
    }

    public static void trocarTela(Control componente, String caminhoFxml, String titulo) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavegacaoUtil.class.getResource(caminhoFxml));
        Parent root = loader.load();

        Stage stage = (Stage) componente.getScene().getWindow();

        stage.setTitle(titulo);
        stage.setMinWidth(LARGURA_MINIMA);
        stage.setMinHeight(ALTURA_MINIMA);
        stage.setResizable(true);

        // Se estiver em modo tela cheia real, sai do modo tela cheia.
        if (stage.isFullScreen()) {
            stage.setFullScreen(false);
        }

        if (stage.getScene() == null) {
            stage.setScene(new Scene(root, LARGURA_INICIAL, ALTURA_INICIAL));
        } else {
            stage.getScene().setRoot(root);
        }

        stage.show();
    }
}