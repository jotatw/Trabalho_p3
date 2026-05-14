package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.Usuario;
import com.br.ifg.luziania.trabalho_p3.service.AuthService;
import com.br.ifg.luziania.trabalho_p3.util.Sessao;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField campoemail;
    @FXML private PasswordField campoSenha;


    private final AuthService authService = new AuthService();

    @FXML
    private void fazerLogin() {
        String email = campoemail.getText().trim();
        String senha = campoSenha.getText().trim();

        //validar os campos vazios
        if(email.isEmpty() || senha.isEmpty()) {
            mostrarAlerta("prencher e-mail e senha!");
            return;
        }

        try {
            Usuario usuario = authService.login(email, senha);
            if(usuario != null) {
                //quando logar ira para o home
                Sessao.inicia(usuario);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Home.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) campoemail.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Locadora - Home");
                stage.show();
            } else {
                mostrarAlerta("E-mail ou senha invalidos");
            }
        } catch (SQLException e){
            mostrarAlerta("Não foi possível acessar o banco de dados. Tente novamente. " );
        } catch (IOException e) {
            mostrarAlerta("Erro ao carregar a tela de login");
        }
    }
    private void mostrarAlerta(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("ATENÇÂO!");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
