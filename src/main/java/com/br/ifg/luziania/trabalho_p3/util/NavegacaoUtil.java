package com.br.ifg.luziania.trabalho_p3.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Stage;

import java.io.IOException;

// Centraliza a troca de telas da aplicação.
public class NavegacaoUtil {

    private static final double LARGURA_MINIMA = 950;
    private static final double ALTURA_MINIMA = 650;

    private NavegacaoUtil() {
        // Evita instanciar classe utilitária.
    }

    public static void trocarTela(Control componente, String caminhoFxml, String titulo) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavegacaoUtil.class.getResource(caminhoFxml));
        Parent root = loader.load();

        Stage stage = (Stage) componente.getScene().getWindow();

        double larguraAtual = stage.getWidth();
        double alturaAtual = stage.getHeight();

        if (larguraAtual < LARGURA_MINIMA) {
            larguraAtual = LARGURA_MINIMA;
        }

        if (alturaAtual < ALTURA_MINIMA) {
            alturaAtual = ALTURA_MINIMA;
        }

        Scene scene = new Scene(root, larguraAtual, alturaAtual);

        stage.setScene(scene);
        stage.setTitle(titulo);
        stage.setMinWidth(LARGURA_MINIMA);
        stage.setMinHeight(ALTURA_MINIMA);
        stage.setResizable(true);

        // Garante que a navegação não force tela cheia.
        stage.setFullScreen(false);

        stage.show();
    }
}