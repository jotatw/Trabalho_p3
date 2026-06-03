package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.Usuario;
import com.br.ifg.luziania.trabalho_p3.service.UsuarioService;
import com.br.ifg.luziania.trabalho_p3.util.ValidacaoUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

public class UsuarioController extends BaseController {
    private final UsuarioService usuarioService = new UsuarioService();
    private Usuario usuarioSelecionado;
    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();
    private FilteredList<Usuario> listaFiltrada;

    @FXML private TextField campoNome, campoEmail, campoBusca;
    @FXML private PasswordField campoSenha;
    @FXML private ComboBox<String> cmbPerfil;
    @FXML private Button btnVoltar;
    @FXML private TableView<Usuario> tabelaUsuario;
    @FXML private TableColumn<Usuario, String> tabelaNome, tabelaEmail, tabelaPerfil;
    @FXML private TableColumn<Usuario, Boolean> tabelaAtivo;

    @FXML
    private void initialize() {
        cmbPerfil.setItems(FXCollections.observableArrayList("ADMIN", "ATENDENTE"));
        tabelaNome.setCellValueFactory(new PropertyValueFactory<>("nomeCompleto"));
        tabelaEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tabelaPerfil.setCellValueFactory(new PropertyValueFactory<>("perfil"));
        tabelaAtivo.setCellValueFactory(new PropertyValueFactory<>("ativo"));

        carregarTabela();
        configurarBusca();

        tabelaUsuario.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null) {
                usuarioSelecionado = selecionado;
                preencherFormulario(selecionado);
            }
        });
    }

    @FXML
    private void salvar() {
        if (validarCampos(true)) {
            try {
                Usuario u = new Usuario();
                u.setNomeCompleto(campoNome.getText().trim());
                u.setEmail(campoEmail.getText().trim().toLowerCase());
                u.setSenha(campoSenha.getText().trim());
                u.setPerfil(cmbPerfil.getValue());
                u.setAtivo(true);

                usuarioService.salvar(u);
                mostrarSucesso("Usuário criado!");
                limpar();
                carregarTabela();
            } catch (SQLException e) { mostrarErro("Erro ao salvar usuário."); }
        }
    }

    @FXML
    private void atualizar() {
        if (usuarioSelecionado == null) { mostrarAlerta("Selecione um usuário!"); return; }
        if (validarCampos(false)) {
            try {
                usuarioSelecionado.setNomeCompleto(campoNome.getText().trim());
                usuarioSelecionado.setEmail(campoEmail.getText().trim().toLowerCase());
                usuarioSelecionado.setPerfil(cmbPerfil.getValue());
                if (!campoSenha.getText().isEmpty()) usuarioSelecionado.setSenha(campoSenha.getText());

                usuarioService.atualizar(usuarioSelecionado);
                mostrarSucesso("Usuário atualizado!");
                limpar();
                carregarTabela();
            } catch (SQLException e) { mostrarErro("Erro ao atualizar."); }
        }
    }

    @FXML
    private void inativar() {
        if (usuarioSelecionado == null) { mostrarAlerta("Selecione um usuário!"); return; }
        if (confirmarAcao("Inativar", "Deseja desativar o acesso de " + usuarioSelecionado.getNomeCompleto() + "?")) {
            try {
                usuarioService.inativar(usuarioSelecionado);
                mostrarSucesso("Usuário inativado!");
                limpar();
                carregarTabela();
            } catch (SQLException e) { mostrarErro("Erro ao inativar."); }
        }
    }

    @FXML
    private void limpar() {
        campoNome.clear(); campoEmail.clear(); campoSenha.clear(); campoBusca.clear();
        cmbPerfil.setValue(null);
        usuarioSelecionado = null;
        tabelaUsuario.getSelectionModel().clearSelection();
    }

    @FXML private void voltarAction() { voltar(btnVoltar); }

    private boolean validarCampos(boolean novaSenha) {
        if (ValidacaoUtil.campoVazio(campoNome.getText())) { mostrarAlerta("Nome obrigatório!"); return false; }
        if (!ValidacaoUtil.emailValido(campoEmail.getText())) { mostrarAlerta("E-mail inválido!"); return false; }
        if (novaSenha && !ValidacaoUtil.senhaValida(campoSenha.getText())) { mostrarAlerta("Senha deve ter 6+ caracteres!"); return false; }
        if (cmbPerfil.getValue() == null) { mostrarAlerta("Selecione o perfil!"); return false; }
        return true;
    }

    private void preencherFormulario(Usuario u) {
        campoNome.setText(u.getNomeCompleto());
        campoEmail.setText(u.getEmail());
        cmbPerfil.setValue(u.getPerfil());
        campoSenha.clear();
    }

    private void carregarTabela() {
        try { listaUsuarios.setAll(usuarioService.listarTodos()); } catch (SQLException e) { mostrarErro("Erro ao carregar usuários."); }
    }

    private void configurarBusca() {
        listaFiltrada = new FilteredList<>(listaUsuarios, p -> true);
        tabelaUsuario.setItems(listaFiltrada);
        campoBusca.textProperty().addListener((obs, antigo, novo) -> {
            listaFiltrada.setPredicate(u -> {
                if (novo == null || novo.isEmpty()) return true;
                String f = novo.toLowerCase().trim();

                return u.getNomeCompleto().toLowerCase().contains(f)
                        || u.getEmail().toLowerCase().contains(f)
                        || u.getPerfil().toLowerCase().contains(f)
                        || String.valueOf(u.isAtivo()).contains(f);
            });
        });
    }
}
