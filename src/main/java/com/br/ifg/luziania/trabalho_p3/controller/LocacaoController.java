package com.br.ifg.luziania.trabalho_p3.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LocacaoController {
    @FXML private TextField campoCpf;
    @FXML private TextField campoPlaca;
    @FXML private DatePicker dataRetirada;
    @FXML private DatePicker dataDevolucao;
    @FXML private Label labelCliente;
    @FXML private Label labelVeiculo;
    @FXML private Label labelValor;

    @FXML
    private void buscarCliente() {
        System.out.println("Buscando Cliente: " + campoCpf.getText());
    }
    @FXML
    private void buscarVeiculo() {
        System.out.println("Buscando Veiculo: " + campoPlaca.getText());
    }
    @FXML
    private void confirmarLocacao() {
        System.out.println("Confirmando Locacao ...");
    }

}
