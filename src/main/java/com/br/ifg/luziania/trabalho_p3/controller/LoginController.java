package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.Usuario;
import com.br.ifg.luziania.trabalho_p3.service.AuthService;
import com.br.ifg.luziania.trabalho_p3.util.Sessao;
import com.br.ifg.luziania.trabalho_p3.util.ValidacaoUtil;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;

// Controller responsável pela tela de login.
// Valida os campos, autentica o usuário e inicia a sessão do sistema.
public class LoginController extends BaseController {

    private final AuthService authService = new AuthService();

    @FXML private TextField campoEmail;
    @FXML private PasswordField campoSenha;

    // Executado ao clicar no botão "Entrar no sistema".
    // Se as credenciais forem válidas, inicia a sessão e abre a tela Home.
    @FXML
    private void fazerLogin() {
        String email = campoEmail.getText().trim();
        String senha = campoSenha.getText().trim();

        // Impede tentativa de login com campos vazios.
        if (email.isEmpty() || senha.isEmpty()) {
            mostrarAlerta("Preencha e-mail e senha!");
            return;
        }

        // Valida o formato do e-mail antes de consultar o banco.
        if (!ValidacaoUtil.emailValido(email)) {
            mostrarAlerta("Informe um e-mail válido!");
            return;
        }

        try {
            Usuario usuario = authService.login(email, senha);

            // O AuthService retorna null quando e-mail/senha são inválidos
            // ou quando o usuário está inativo.
            if (usuario == null) {
                mostrarAlerta("E-mail ou senha inválidos.");
                return;
            }

            // Armazena o usuário autenticado para uso nas outras telas.
            Sessao.inicia(usuario);

            abrirTela(campoEmail, "/fxml/Home.fxml", "Locadora - Home");

        } catch (SQLException e) {
            mostrarErro("Não foi possível acessar o banco de dados.");
        }
    }
}