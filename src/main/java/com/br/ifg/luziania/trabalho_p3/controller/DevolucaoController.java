package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.Locacao;
import com.br.ifg.luziania.trabalho_p3.service.DevolucaoService;
import com.br.ifg.luziania.trabalho_p3.util.MascaraUtil;
import com.br.ifg.luziania.trabalho_p3.util.NavegacaoUtil;
import com.br.ifg.luziania.trabalho_p3.util.ValidacaoUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

// Controla a tela de devolução de veículos:
// busca locação ativa, calcula multa e confirma devolução.
public class DevolucaoController {
    private final DevolucaoService devolucaoService = new DevolucaoService();
    private Locacao locacaoAtual; //guarda a locação encontrada
    private final ObservableList<String> placasAtivas = FXCollections.observableArrayList();

    @FXML private TextField campoPlaca;
    @FXML private Label labelCliente;
    @FXML private Label labelRetirada;
    @FXML private Label labelDevolucao;
    @FXML private Label labelValor;
    @FXML private Label labelAtraso;
    @FXML private Label labelMulta;
    @FXML private Label labelValorFinal;
    @FXML private Button btnVoltar;
    @FXML private ComboBox<String> comboPlacasAtivas;

    @FXML
    public void initialize() {
        MascaraUtil.placa(campoPlaca);

        comboPlacasAtivas.setItems(placasAtivas);
        carregarPlacasAtivas();

        comboPlacasAtivas.valueProperty().addListener((obs, antigo, placa) -> {
            if (placa != null) {
                campoPlaca.setText(placa);
                buscarVeiculo();
            }
        });
    }

    @FXML
    private void buscarVeiculo() {
        String placa = campoPlaca.getText().trim().toUpperCase();

        if (placa.isEmpty()) {
            mostrarAlerta("Preencha o campo Placa!");
            return;
        }
        if (!ValidacaoUtil.placaValido(placa)) {
            mostrarAlerta("Placa inválida! Use o formato: ABC1D23 ou ABC1234");
            return;
        }
        try {
            locacaoAtual = devolucaoService.buscarLocacaoAtivaPorPlaca(placa);

            if (locacaoAtual == null){
                mostrarAlerta("Nenhuma locação ativa para essa placa.");
                return;
            }
            //prencher os label com os detelhes da locação
            labelCliente.setText(locacaoAtual.getCliente().getNome());
            labelRetirada.setText(locacaoAtual.getDataRetirada().toString());
            labelDevolucao.setText(locacaoAtual.getDataDevolucaoPrevista().toString());
            labelValor.setText("R$ " + String.format("%.2f",locacaoAtual.getValorTotal()));
            //calcula atraso e multas para exibir
            long diasAtraso = ChronoUnit.DAYS.between(locacaoAtual.getDataDevolucaoPrevista(), LocalDate.now());

            if (diasAtraso > 0){
                long diasLocacao = ChronoUnit.DAYS.between(locacaoAtual.getDataRetirada(), locacaoAtual.getDataDevolucaoPrevista());
                if (diasLocacao <= 0) {
                    mostrarAlerta("Período de locação inválido.");
                    return;
                }
                double valorDiaria = locacaoAtual.getValorTotal() / diasLocacao;
                double multa = diasAtraso * valorDiaria * 0.20;

                labelAtraso.setText(diasAtraso + " dias");
                labelMulta.setText("R$ " + String.format("%.2f", multa));
                labelValorFinal.setText("R$ " + String.format("%.2f", locacaoAtual.getValorTotal() + multa));
            } else {
                labelAtraso.setText("Sem atrasos");
                labelMulta.setText("R$ 0,00");
                labelValorFinal.setText("R$ " + String.format("%.2f", locacaoAtual.getValorTotal()) );
            }
        } catch (SQLException e){
            mostrarAlerta("Erro ao buscar locação. Tente novamente. ");
        }
    }
    @FXML
    private void confirmarDevolucao() {
        if (locacaoAtual == null) {
            mostrarAlerta("Busque uma locação ativa primeiro!");
            return;
        }
        String placa = campoPlaca.getText().trim().toUpperCase();
        try {
            Locacao locacao = devolucaoService.registrarDevolucao(placa);
            mostrarSucesso("Devolução confirmada com sucesso!\nMulta: R$ " +
                    String.format("%.2f", locacao.getMultas()) +
                    "\nValor final: R$ " +
                    String.format("%.2f", locacao.getValorTotal() + locacao.getMultas()));
            locacaoAtual = null;
            limparCampos();
            carregarPlacasAtivas();
        } catch (IllegalArgumentException e){
            mostrarAlerta(e.getMessage());
        } catch (SQLException e) {
            mostrarAlerta("Não foi possível confirmar a devolução. Tente novamente.");
        }
    }
    private void limparCampos() {
        campoPlaca.clear();
        comboPlacasAtivas.getSelectionModel().clearSelection();
        labelCliente.setText("-");
        labelRetirada.setText("-");
        labelDevolucao.setText("-");
        labelValor.setText("R$ 0,00");
        labelAtraso.setText("-");
        labelMulta.setText("-");
        labelValorFinal.setText("R$ 0,00");
    }
    @FXML
    private void voltar() {
        try {
            NavegacaoUtil.trocarTela(btnVoltar, "/fxml/Home.fxml", "Locadora - Home");
        } catch (IOException e) {
            mostrarAlerta("Erro ao voltar para a tela inicial.");
        }
    }
    private void carregarPlacasAtivas() {
        try {
            List<String> placas = devolucaoService.listarPlacasComLocacaoAtiva();
            placasAtivas.setAll(placas);
        } catch (SQLException e) {
            mostrarAlerta("Erro ao carregar placas com locação ativa.");
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