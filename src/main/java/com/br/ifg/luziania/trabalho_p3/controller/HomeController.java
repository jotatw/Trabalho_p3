package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.util.LogUtil;
import com.br.ifg.luziania.trabalho_p3.util.NavegacaoUtil;
import com.br.ifg.luziania.trabalho_p3.util.Sessao;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import java.io.IOException;
// Controla a tela inicial e a navegação para as principais funções do sistema.
public class HomeController {
    @FXML private Button btnClientes;
    @FXML private Button btnVeiculos;
    @FXML private Button btnUsuarios;
    @FXML private Button btnLocacao;
    @FXML private Button btnDevolucao;
    @FXML private Button btnSair;

    @FXML
    private void abrirVeiculos() {
        abrirTela(btnVeiculos, "/fxml/Veiculo.fxml", "Locadora - Veiculo");
    }
    @FXML
    private void abrirClientes() {
        abrirTela(btnClientes, "/fxml/Cliente.fxml", "Locadora - Cliente");
    }
    @FXML
    private void abrirLocacao() {
        abrirTela(btnLocacao, "/fxml/Locacao.fxml", "Locadora - Locacao");
    }
    @FXML
    private void abrirDevolucao() {
        abrirTela(btnDevolucao, "/fxml/Devolucao.fxml", "Locadora - Devolucao");
    }
    @FXML
    private void abrirUsuarios() {
        abrirTela(btnUsuarios, "/fxml/Usuario.fxml", "Locadora - Usuario");
    }
    // Encerra a sessão atual e retorna para a tela de login.
    @FXML
    private void sair() {
        LogUtil.registrarAcao("LOGOUT");
        Sessao.encerrar();
        abrirTela(btnSair, "/fxml/Login.fxml", "Locadora - Login");
    }
    // Método auxiliar para reduzir repetição na navegação.
    private void abrirTela(Button botaoOrigem, String caminhoFxml, String titulo) {
        try {
            NavegacaoUtil.trocarTela(botaoOrigem, caminhoFxml, titulo);
        } catch (IOException e) {
            mostrarAlerta("Erro ao abrir a tela solicitada.");
        }
    }
    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Atenção");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
