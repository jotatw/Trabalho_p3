package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.Usuario;
import com.br.ifg.luziania.trabalho_p3.service.AuthService;
import com.br.ifg.luziania.trabalho_p3.util.Sessao;
import com.br.ifg.luziania.trabalho_p3.util.ValidacaoUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

import java.sql.SQLException;

public class LoginController extends BaseController {
    private final AuthService authService = new AuthService();

    @FXML private TextField campoEmail;
    @FXML private PasswordField campoSenha;

    @FXML
    private void fazerLogin() {
        String email = campoEmail.getText().trim();
        String senha = campoSenha.getText().trim();

        if(email.isEmpty() || senha.isEmpty()) {
            mostrarAlerta("Preencha e-mail e senha!");
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
            abrirTela(campoEmail, "/fxml/Home.fxml", "Locadora - Home");
        } catch (SQLException e){
            mostrarErro("Não foi possível acessar o banco de dados.");
        }
    }
}
