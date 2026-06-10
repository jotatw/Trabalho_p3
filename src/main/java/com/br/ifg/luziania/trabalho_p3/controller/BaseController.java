package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.util.NavegacaoUtil;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.IOException;

// Classe base para os controllers da aplicação.
// Centraliza métodos comuns de alerta, erro, sucesso, confirmação e navegação.
public abstract class BaseController {

    // Exibe uma mensagem de atenção para validações ou ações incompletas.
    protected void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Atenção");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    // Exibe uma notificação visual de sucesso no canto superior direito.
    // Usado após operações como cadastro, atualização, locação e devolução.
    protected void mostrarSucesso(String mensagem) {
        Notifications.create()
                .title("Sucesso")
                .text(mensagem)
                .hideAfter(Duration.seconds(3))
                .position(Pos.TOP_RIGHT)
                .showInformation();
    }

    // Exibe uma mensagem de erro para falhas inesperadas ou problemas com o banco.
    protected void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    // Exibe uma janela de confirmação e retorna true quando o usuário confirma.
    // Usado antes de ações sensíveis, como sair do sistema ou inativar registros.
    protected boolean confirmarAcao(String titulo, String mensagem) {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle(titulo);
        confirmacao.setHeaderText(null);
        confirmacao.setContentText(mensagem);

        return confirmacao.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    // Abre outra tela FXML mantendo a janela principal.
    // A troca de tela é delegada ao NavegacaoUtil.
    protected void abrirTela(Control componente, String caminhoFxml, String titulo) {
        try {
            NavegacaoUtil.trocarTela(componente, caminhoFxml, titulo);
        } catch (IOException e) {
            mostrarErro("Erro ao abrir a tela: " + titulo);
            e.printStackTrace();
        }
    }

    // Retorna para a tela Home.
    // Pode ser reutilizado pelos controllers que possuem botão Voltar.
    @FXML
    protected void voltar(Control componente) {
        abrirTela(componente, "/fxml/Home.fxml", "Locadora - Home");
    }
}