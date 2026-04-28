package com.br.ifg.luziania.trabalho_p3.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField campoemail;
    @FXML
    private PasswordField campoSenha;
    @FXML
    private void fazerLogin() {
        String email = campoemail.getText();
        String senha = campoSenha.getText();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) campoemail.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Locadora - Home");
        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
