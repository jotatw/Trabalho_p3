package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.Usuario;
import com.br.ifg.luziania.trabalho_p3.service.UsuarioService;
import com.br.ifg.luziania.trabalho_p3.util.ValidacaoUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class UsuarioController {
    private final UsuarioService usuarioService = new UsuarioService();

    @FXML private TextField campoNome;
    @FXML private TextField campoEmail;
    @FXML private PasswordField campoSenha;
    @FXML private ComboBox<String> cmbPerfil;
    @FXML private Button btnVoltar;
    @FXML private Button btnSalvar;

    @FXML private TableView<Usuario> tabelaUsuario;
    @FXML private TableColumn<Usuario, String> tabelaNome;
    @FXML private TableColumn<Usuario, String> tabelaEmail;
    @FXML private TableColumn<Usuario, String> tabelaPerfil;

    @FXML
    private void initialize() {
        cmbPerfil.setItems(FXCollections.observableArrayList("ADMIN", "ATENDENTE"));
        tabelaNome.setCellValueFactory(new PropertyValueFactory<>("nomeCompleto"));
        tabelaEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tabelaPerfil.setCellValueFactory(new PropertyValueFactory<>("perfil"));
        carregarTabela();
    }

    @FXML
    private void salvar(){
        String nome = campoNome.getText().trim();
        String email = campoEmail.getText().trim();
        String senha = campoSenha.getText().trim();
        String perfil = cmbPerfil.getValue();

        //validações
        if (ValidacaoUtil.campoVazio(nome)){
            mostraAlerta("Nome e obrigatorio!");
            return;
        }
        if (!ValidacaoUtil.emailValido(email)){
            mostraAlerta("Email invalido ou obrigatorio!");
            return;
        }
        if (!ValidacaoUtil.senhaValida(senha)){
            mostraAlerta("Senha obrigatória e deve ter pelo menos 6 caracteres!");
            return;
        }
        if(perfil == null){
            mostraAlerta("Selecione o perfil do usuario!");
            return;
        }

        //salva no banco de dados
        try {
            Usuario usuario = new Usuario();
            usuario.setNomeCompleto(nome);
            usuario.setEmail(email);
            usuario.setSenha(senha);
            usuario.setPerfil(perfil);

            usuarioService.salvar(usuario);
            mostraSucesso("Usuario salvo com sucesso!");
            limpar();
            carregarTabela();
        } catch (SQLException e) {
            mostraAlerta("Erro ao salvar usuário. Verifique se o e-mail já está cadastrado.");
        }
    }

    @FXML
    private void limpar(){
        campoNome.clear();
        campoEmail.clear();
        campoSenha.clear();
        cmbPerfil.setValue(null);
    }
    @FXML
    private void voltar() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/Home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnVoltar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Locadora - Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void carregarTabela() {
        try {
            List<Usuario> lista = usuarioService.listarTodos();
            tabelaUsuario.setItems(FXCollections.observableArrayList(lista));
        } catch (SQLException e) {
            mostraAlerta("Erro ao carregar Usuarios: " + e.getMessage());
        }
    }
    private void mostraAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    private void mostraSucesso(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
