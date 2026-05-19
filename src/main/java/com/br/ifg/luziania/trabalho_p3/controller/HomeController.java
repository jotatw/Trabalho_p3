package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.Usuario;
import com.br.ifg.luziania.trabalho_p3.util.LogUtil;
import com.br.ifg.luziania.trabalho_p3.util.NavegacaoUtil;
import com.br.ifg.luziania.trabalho_p3.util.Sessao;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;

// Controla a tela inicial e a navegação para as principais funções do sistema.
public class HomeController {

    @FXML private Button btnClientes;
    @FXML private Button btnVeiculos;
    @FXML private Button btnUsuarios;
    @FXML private Button btnLocacao;
    @FXML private Button btnDevolucao;
    @FXML private Button btnSair;
    @FXML private Button btnAtalhoLocacao;
    @FXML private Button btnAtalhoDevolucao;
    @FXML private Button btnAtalhoCliente;
    @FXML private Button btnAtalhoVeiculo;

    @FXML private Label labelBoasVindas;
    @FXML private Label labelPerfil;

    @FXML
    public void initialize() {
        carregarUsuarioLogado();
        aplicarPermissoes();
    }

    private void carregarUsuarioLogado() {
        Usuario usuario = Sessao.getUsuarioLogado();

        if (usuario == null) {
            labelBoasVindas.setText("Bem-vindo ao sistema");
            labelPerfil.setText("Perfil: -");
            return;
        }

        labelBoasVindas.setText("Bem-vindo, " + usuario.getNomeCompleto());
        labelPerfil.setText("Perfil: " + usuario.getPerfil());
    }

    private void aplicarPermissoes() {
        Usuario usuario = Sessao.getUsuarioLogado();

        if (usuario == null) {
            btnUsuarios.setDisable(true);
            btnUsuarios.setVisible(false);
            btnUsuarios.setManaged(false);
            return;
        }

        boolean admin = "ADMIN".equalsIgnoreCase(usuario.getPerfil());

        if (!admin) {
            btnUsuarios.setDisable(true);
            btnUsuarios.setVisible(false);
            btnUsuarios.setManaged(false);
        }
    }

    @FXML
    private void abrirVeiculos() {
        abrirTela(btnVeiculos, "/fxml/Veiculo.fxml", "Locadora - Veículos");
    }

    @FXML
    private void abrirClientes() {
        abrirTela(btnClientes, "/fxml/Cliente.fxml", "Locadora - Clientes");
    }

    @FXML
    private void abrirLocacao() {
        abrirTela(btnLocacao, "/fxml/Locacao.fxml", "Locadora - Locação");
    }

    @FXML
    private void abrirDevolucao() {
        abrirTela(btnDevolucao, "/fxml/Devolucao.fxml", "Locadora - Devolução");
    }

    @FXML
    private void abrirUsuarios() {
        Usuario usuario = Sessao.getUsuarioLogado();

        if (usuario == null || !"ADMIN".equalsIgnoreCase(usuario.getPerfil())) {
            mostrarAlerta("Acesso permitido apenas para usuários ADMIN.");
            return;
        }

        abrirTela(btnUsuarios, "/fxml/Usuario.fxml", "Locadora - Usuários");
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