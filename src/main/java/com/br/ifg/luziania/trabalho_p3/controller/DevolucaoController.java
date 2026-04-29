package com.br.ifg.luziania.trabalho_p3.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class DevolucaoController {
    @FXML private TextField campoPlaca;
    @FXML private Label labelCliente;
    @FXML private Label labelRetirada;
    @FXML private Label labelDevolucao;
    @FXML private Label labelValor;
    @FXML private Label labelAtraso;
    @FXML private Label labelMulta;
    @FXML private Label labelValorFinal;

    @FXML
    private void buscarVeiculo() {
        labelCliente.setText("Nome: " + campoPlaca.getText());
    }
    @FXML
    private void confirmarDevolucao() {
        System.out.println("Confirmar devolucao ...");
    }

}
