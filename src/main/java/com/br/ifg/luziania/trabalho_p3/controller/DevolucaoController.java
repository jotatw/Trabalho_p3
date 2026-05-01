package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.Locacao;
import com.br.ifg.luziania.trabalho_p3.service.DevolucaoService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class DevolucaoController {
    private DevolucaoService devolucaoService = new DevolucaoService();

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
        String placa = campoPlaca.getText();

        if(placa.isEmpty()){
            mostrarAlerta("Informe a placa do veiculo!");
            return;
        }
        try {
            Locacao locacao = devolucaoService.registrarDevolucao(placa);
            mostrarSucesso("Devolução confirmada com sucesso!\nMulta: R$ " + locacao.getMultas() + "\nValor final: R$ " + (locacao.getValorTotal() + locacao.getMultas()));
        } catch (IllegalArgumentException e) {
            mostrarAlerta(e.getMessage());
        } catch (SQLException e) {
            mostrarAlerta("Erro no baco: " + e.getMessage());
        }
    }
    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    public void mostrarSucesso(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
