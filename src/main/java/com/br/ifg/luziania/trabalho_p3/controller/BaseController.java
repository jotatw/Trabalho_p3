package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.util.NavegacaoUtil;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import org.controlsfx.control.Notifications;
import javafx.util.Duration;

import java.io.IOException;

public abstract class BaseController {

    protected void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Atenção");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    protected void mostrarSucesso(String mensagem) {
        Notifications.create()
                .title("Sucesso")
                .text(mensagem)
                .hideAfter(Duration.seconds(3))
                .position(Pos.TOP_RIGHT)
                .showInformation();
    }

    protected void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    protected boolean confirmarAcao(String titulo, String mensagem) {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle(titulo);
        confirmacao.setHeaderText(null);
        confirmacao.setContentText(mensagem);
        return confirmacao.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    protected void abrirTela(Control componente, String caminhoFxml, String titulo) {
        try {
            NavegacaoUtil.trocarTela(componente, caminhoFxml, titulo);
        } catch (IOException e) {
            mostrarErro("Erro ao abrir a tela: " + titulo);
            e.printStackTrace();
        }
    }

    @FXML
    protected void voltar(Control componente) {
        abrirTela(componente, "/fxml/Home.fxml", "Locadora - Home");
    }
}
