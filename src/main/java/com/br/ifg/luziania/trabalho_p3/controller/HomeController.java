package com.br.ifg.luziania.trabalho_p3.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HomeController {
    @FXML
    private void abrirVeiculos() {
        System.out.println("Veiculos");
    }
    @FXML
    private void abrirClientes() {
        System.out.println("Clientes");
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
        System.out.println("Usuario");
    }
    @FXML
    private void sair() {
        System.out.println("Sair");
    }


}
