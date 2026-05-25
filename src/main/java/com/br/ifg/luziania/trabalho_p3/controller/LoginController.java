package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.Usuario;
import com.br.ifg.luziania.trabalho_p3.service.AuthService;
import com.br.ifg.luziania.trabalho_p3.util.NavegacaoUtil;
import com.br.ifg.luziania.trabalho_p3.util.Sessao;
import com.br.ifg.luziania.trabalho_p3.util.ValidacaoUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

import java.io.IOException;
import java.sql.SQLException;
// Controla a autenticação do usuário no sistema.
public class LoginController {
    private final AuthService authService = new AuthService();

    @FXML private TextField campoEmail;
    @FXML private PasswordField campoSenha;
    // Valida os campos e tenta autenticar o usuário.
    @FXML
    private void fazerLogin() {
        String email = campoEmail.getText().trim();
        String senha = campoSenha.getText().trim();

        //validar os campos vazios
        if(email.isEmpty() || senha.isEmpty()) {
            mostrarAlerta("prencher e-mail e senha!");
            return;
        }
        if (!ValidacaoUtil.emailValido(email)) {
            mostrarAlerta("Informe um e-mail válido!");
            return;
        }
        try {
            Usuario usuario = authService.login(email, senha);
            if(usuario == null) {
                mostrarAlerta("E-mail ou senha inválidos.");
                return;
            }
            Sessao.inicia(usuario);
            NavegacaoUtil.trocarTela(campoEmail, "/fxml/Home.fxml", "Locadora - Home");
        } catch (SQLException e){
            mostrarAlerta("Não foi possível acessar o banco de dados. Tente novamente. " );
        } catch (IOException e) {
            mostrarAlerta("Erro ao abrir a tela inicial.");
        }
    }
    private void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("atenção!");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
