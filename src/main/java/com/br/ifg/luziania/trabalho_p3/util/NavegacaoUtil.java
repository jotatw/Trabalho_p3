package com.br.ifg.luziania.trabalho_p3.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Stage;

import java.io.IOException;

// Centraliza a troca de telas da aplicação.
public class NavegacaoUtil {
    private NavegacaoUtil() {}  // Evita instanciar classe utilitária.
    public static void trocarTela(Control componente, String caminhoFxml, String titulo) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavegacaoUtil.class.getResource(caminhoFxml));
        Parent root = loader.load();

        Stage stage = (Stage)componente.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(titulo);
        stage.show();
    }
}
