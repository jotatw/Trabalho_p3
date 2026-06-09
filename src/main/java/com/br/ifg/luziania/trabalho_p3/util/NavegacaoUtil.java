package com.br.ifg.luziania.trabalho_p3.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Stage;

import java.io.IOException;

// Centraliza a navegação entre as telas da aplicação.
// Os controllers usam essa classe para trocar o conteúdo da janela principal.
public class NavegacaoUtil {

    // Tamanho padrão usado caso uma nova Scene precise ser criada.
    private static final double LARGURA_INICIAL = 1100;
    private static final double ALTURA_INICIAL = 720;

    // Tamanho mínimo da janela para evitar que a interface fique quebrada.
    private static final double LARGURA_MINIMA = 950;
    private static final double ALTURA_MINIMA = 650;

    private NavegacaoUtil() {
        // Evita instanciar classe utilitária.
    }

    // Carrega o arquivo FXML informado e troca a tela atual mantendo o Stage.
    // O componente recebido é usado para localizar a janela onde a tela está aberta.
    public static void trocarTela(Control componente, String caminhoFxml, String titulo) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavegacaoUtil.class.getResource(caminhoFxml));
        Parent root = loader.load();

        Stage stage = (Stage) componente.getScene().getWindow();

        stage.setTitle(titulo);
        stage.setMinWidth(LARGURA_MINIMA);
        stage.setMinHeight(ALTURA_MINIMA);
        stage.setResizable(true);

        // Se o sistema estiver em tela cheia real, volta para o modo janela.
        if (stage.isFullScreen()) {
            stage.setFullScreen(false);
        }

        // Se ainda não existir Scene, cria uma.
        // Caso contrário, apenas troca o root da Scene atual.
        // Isso evita abrir várias janelas durante a navegação.
        if (stage.getScene() == null) {
            stage.setScene(new Scene(root, LARGURA_INICIAL, ALTURA_INICIAL));
        } else {
            stage.getScene().setRoot(root);
        }

        stage.show();
    }
}