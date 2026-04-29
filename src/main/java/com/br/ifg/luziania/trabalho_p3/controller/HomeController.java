package com.br.ifg.luziania.trabalho_p3.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import java.io.IOException;

public class HomeController {
    @FXML private Button btnClientes;
    @FXML private Button btnVeiculos;
    @FXML private Button btnUsuarios;



    @FXML
    private void abrirVeiculos() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/Veiculo.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnVeiculos.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Locadora - Veiculo");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void abrirClientes() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/Cliente.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnClientes.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Locadora - Cliente");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void abrirLocacao() {
        System.out.println("Nova Locação");
    }
    @FXML
    private void abrirDevolucao() {
        System.out.println("Devoluções");
    }
    @FXML
    private void abrirUsuarios() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/Usuario.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnUsuarios.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Locadora - Usuario");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void sair() {
        System.out.println("Sair");
    }

}
