package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.Locacao;
import com.br.ifg.luziania.trabalho_p3.service.LocacaoService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;

public class LocacaoController {
    private LocacaoService locacaoService = new LocacaoService();

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
        String cpf = campoCpf.getText();
        String placa = campoPlaca.getText();
        LocalDate retirada = dataRetirada.getValue();
        LocalDate devolucao = dataDevolucao.getValue();

        //validação basica
        if (cpf.isEmpty() || placa.isEmpty() || retirada == null || devolucao == null) {
            mostrarAlerta("Prencha todos os campos");
            return;
        }
        try {
            //pega o usuario logado e depois conecta a sessão
            Locacao locacao = locacaoService.realizarLocacao(cpf, placa, retirada, devolucao, null);
            mostrarSucesso("Locacao realizada! Valor: R$ " + locacao.getValorTotal());
        } catch (IllegalArgumentException e) {
            mostrarAlerta(e.getMessage());
        } catch (SQLException e){
            mostrarAlerta("Erro no banco: " + e.getMessage());
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
