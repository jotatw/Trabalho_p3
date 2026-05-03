package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.dao.ClienteDAO;
import com.br.ifg.luziania.trabalho_p3.dao.LocacaoDAO;
import com.br.ifg.luziania.trabalho_p3.model.Locacao;
import com.br.ifg.luziania.trabalho_p3.service.DevolucaoService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DevolucaoController {
    private DevolucaoService devolucaoService = new DevolucaoService();
    private LocacaoDAO locacaoDAO = new LocacaoDAO();
    private Locacao locacaoAtual; //guarda a locação encontrada

    @FXML private TextField campoPlaca;
    @FXML private Label labelCliente;
    @FXML private Label labelRetirada;
    @FXML private Label labelDevolucao;
    @FXML private Label labelValor;
    @FXML private Label labelAtraso;
    @FXML private Label labelMulta;
    @FXML private Label labelValorFinal;
    @FXML private Button btnVoltar;

    @FXML
    private void buscarVeiculo() {
        String placa = campoPlaca.getText();

        if (placa.isEmpty()) {
            mostrarAlerta("Preencha o campo Placa!");
            return;
        }
        try {
            locacaoAtual = locacaoDAO.buscarLocacaoAtivaPorPlaca(placa);

            if (locacaoAtual == null){
                mostrarAlerta("Nenhuma locação ativa para essa placa.");
                return;
            }
            //prencher os label com os detelhes da locação
            labelRetirada.setText(locacaoAtual.getDataRetirada().toString());
            labelDevolucao.setText(locacaoAtual.getDataDevolucaoPrevista().toString());
            labelValor.setText("R$ " + locacaoAtual.getValorTotal());

            //calcula atraso e multas para exibir
            long diasAtraso = ChronoUnit.DAYS.between(locacaoAtual.getDataDevolucaoPrevista(), LocalDate.now());
            if (diasAtraso > 0){
                double valorDiaria = locacaoAtual.getValorTotal() / ChronoUnit.DAYS.between(locacaoAtual.getDataRetirada(), locacaoAtual.getDataDevolucaoPrevista());
                double multa = diasAtraso * valorDiaria * 0.20;

                labelAtraso.setText(diasAtraso + "Dias");
                labelMulta.setText("R$ " + String.format("%.2f", multa));
                labelValorFinal.setText("R$ " + String.format("%.2f", locacaoAtual.getValorTotal() + multa));
            } else {
                labelAtraso.setText("Sem atrasos");
                labelMulta.setText("R$ 0,00");
                labelValorFinal.setText("R$ " + locacaoAtual.getValorTotal());
            }
        } catch (SQLException e){
            mostrarAlerta("Erro ao buscar locação: " + e.getMessage());
        }

    }
    @FXML
    private void confirmarDevolucao() {
        if (locacaoAtual == null) {
            mostrarAlerta("Busque uma locação ativa primeiro!");
            return;
        }
        try {
            Locacao locacao = devolucaoService.registrarDevolucao(campoPlaca.getText());
            mostrarSucesso("Devolução confirmada com sucesso!\nMulta: " +
                    String.format("%.2f", locacao.getMultas()) + "\nValor final: R$" +
                    String.format("%.2f",  locacao.getValorTotal() + locacao.getMultas()));
            locacaoAtual = null;
            limparCampos();
        } catch (IllegalArgumentException | SQLException e){
            mostrarAlerta(e.getMessage());
        }
    }
    private void limparCampos() {
        campoPlaca.clear();
        labelCliente.setText("Label");
        labelRetirada.setText("Label");
        labelDevolucao.setText("Label");
        labelValor.setText("Label");
        labelAtraso.setText("Label");
        labelMulta.setText("Label");
        labelValorFinal.setText("Label");
    }
    @FXML
    private void voltar() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/Home.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnVoltar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Locadora - Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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
