package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.Locacao;
import com.br.ifg.luziania.trabalho_p3.service.DevolucaoService;
import com.br.ifg.luziania.trabalho_p3.util.MascaraUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DevolucaoController extends BaseController {
    private final DevolucaoService devolucaoService = new DevolucaoService();
    private Locacao locacaoAtual;
    private final ObservableList<String> placasAtivas = FXCollections.observableArrayList();

    @FXML private TextField campoPlaca;
    @FXML private Label labelCliente, labelRetirada, labelDevolucao, labelValor, labelAtraso, labelMulta, labelValorFinal;
    @FXML private Button btnVoltar;
    @FXML private ComboBox<String> comboPlacasAtivas;

    @FXML
    public void initialize() {
        MascaraUtil.placa(campoPlaca);
        comboPlacasAtivas.setItems(placasAtivas);
        carregarPlacasAtivas();

        comboPlacasAtivas.valueProperty().addListener((o, a, p) -> {
            if (p != null) {
                campoPlaca.setText(p);
                buscarVeiculo();
            }
        });
    }

    @FXML
    private void buscarVeiculo() {
        String p = campoPlaca.getText().trim().toUpperCase();
        if (p.isEmpty()) { mostrarAlerta("Informe a Placa!"); return; }

        try {
            locacaoAtual = devolucaoService.buscarLocacaoAtivaPorPlaca(p);
            if (locacaoAtual == null) { mostrarAlerta("Nenhuma locação ativa encontrada."); return; }

            preencherResumo();
        } catch (SQLException e) { mostrarErro("Erro ao buscar locação."); }
    }

    private void preencherResumo() {
        labelCliente.setText(locacaoAtual.getCliente().getNome());
        labelRetirada.setText(locacaoAtual.getDataRetirada().toString());
        labelDevolucao.setText(locacaoAtual.getDataDevolucaoPrevista().toString());
        labelValor.setText(String.format("R$ %.2f", locacaoAtual.getValorTotal()));

        long diasAtraso = ChronoUnit.DAYS.between(locacaoAtual.getDataDevolucaoPrevista(), LocalDate.now());
        if (diasAtraso > 0) {
            long totalDias = ChronoUnit.DAYS.between(locacaoAtual.getDataRetirada(), locacaoAtual.getDataDevolucaoPrevista());
            double diaria = locacaoAtual.getValorTotal() / (totalDias > 0 ? totalDias : 1);
            double multa = diasAtraso * diaria * 0.20;

            labelAtraso.setText(diasAtraso + " dias");
            labelMulta.setText(String.format("R$ %.2f", multa));
            labelValorFinal.setText(String.format("R$ %.2f", locacaoAtual.getValorTotal() + multa));
        } else {
            labelAtraso.setText("Em dia");
            labelMulta.setText("R$ 0,00");
            labelValorFinal.setText(String.format("R$ %.2f", locacaoAtual.getValorTotal()));
        }
    }

    @FXML
    private void confirmarDevolucao() {
        if (locacaoAtual == null) { mostrarAlerta("Busque a locação primeiro!"); return; }
        try {
            Locacao loc = devolucaoService.registrarDevolucao(locacaoAtual.getVeiculo().getPlaca());
            mostrarSucesso("Devolução concluída!\nTotal: R$ " + String.format("%.2f", loc.getValorTotal() + loc.getMultas()));
            limpar();
            carregarPlacasAtivas();
        } catch (IllegalArgumentException e) {
        mostrarAlerta(e.getMessage());
    } catch (SQLException e) {
        mostrarErro("Erro ao processar devolução no banco de dados.");
    }
    }

    @FXML private void voltarAction() { voltar(btnVoltar); }

    private void carregarPlacasAtivas() {
        try {
            placasAtivas.setAll(devolucaoService.listarPlacasComLocacaoAtiva());
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar placas com locação ativa.");
        }
    }

    private void limpar() {
        campoPlaca.clear();
        comboPlacasAtivas.getSelectionModel().clearSelection();
        labelCliente.setText("-"); labelRetirada.setText("-"); labelDevolucao.setText("-");
        labelValor.setText("R$ 0,00"); labelAtraso.setText("-"); labelMulta.setText("-"); labelValorFinal.setText("R$ 0,00");
        locacaoAtual = null;
    }
}
