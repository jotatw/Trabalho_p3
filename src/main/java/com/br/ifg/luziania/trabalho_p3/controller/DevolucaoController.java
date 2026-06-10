package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.Locacao;
import com.br.ifg.luziania.trabalho_p3.service.DevolucaoService;
import com.br.ifg.luziania.trabalho_p3.util.MascaraUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Controller responsável pela tela de devolução.
// Controla busca da locação ativa, resumo da devolução, cálculo de multa e confirmação.
public class DevolucaoController extends BaseController {

    private final DevolucaoService devolucaoService = new DevolucaoService();

    private Locacao locacaoAtual;

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

    // Inicializa a tela de devolução.
    // Configura máscara, ComboBox de placas e carregamento das locações ativas.
    @FXML
    public void initialize() {
        MascaraUtil.placa(campoPlaca);

        comboPlacasAtivas.setItems(placasAtivas);
        carregarPlacasAtivas();
        configurarSelecaoPlaca();
    }

    // Quando uma placa é escolhida no ComboBox,
    // o campo de placa é preenchido e a locação ativa é buscada.
    private void configurarSelecaoPlaca() {
        comboPlacasAtivas.valueProperty().addListener((obs, antigo, placa) -> {
            if (placa != null) {
                campoPlaca.setText(placa);
                buscarVeiculo();
            }
        });
    }

    // Busca a locação ativa relacionada à placa informada.
    @FXML
    private void buscarVeiculo() {
        String placa = campoPlaca.getText().trim().toUpperCase();

        if (placa.isEmpty()) {
            mostrarAlerta("Informe a placa!");
            return;
        }

        try {
            locacaoAtual = devolucaoService.buscarLocacaoAtivaPorPlaca(placa);

            if (locacaoAtual == null) {
                mostrarAlerta("Nenhuma locação ativa encontrada.");
                return;
            }

            preencherResumo();

        } catch (SQLException e) {
            mostrarErro("Erro ao buscar locação.");
        }
    }

    // Preenche os dados da locação encontrada no resumo da tela.
    // Também calcula uma prévia de atraso, multa e valor final.
    private void preencherResumo() {
        labelCliente.setText(locacaoAtual.getCliente().getNome());
        labelRetirada.setText(locacaoAtual.getDataRetirada().toString());
        labelDevolucao.setText(locacaoAtual.getDataDevolucaoPrevista().toString());
        labelValor.setText(String.format("R$ %.2f", locacaoAtual.getValorTotal()));

        long diasAtraso = ChronoUnit.DAYS.between(
                locacaoAtual.getDataDevolucaoPrevista(),
                LocalDate.now()
        );

        if (diasAtraso > 0) {
            long totalDias = ChronoUnit.DAYS.between(
                    locacaoAtual.getDataRetirada(),
                    locacaoAtual.getDataDevolucaoPrevista()
            );

            // Evita divisão por zero caso exista algum registro com período inválido.
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

    // Confirma a devolução da locação atual.
    // A regra de negócio final fica no DevolucaoService.
    @FXML
    private void confirmarDevolucao() {
        if (locacaoAtual == null) {
            mostrarAlerta("Busque a locação primeiro!");
            return;
        }

        try {
            Locacao locacao = devolucaoService.registrarDevolucao(
                    locacaoAtual.getVeiculo().getPlaca()
            );

            mostrarSucesso(
                    "Devolução concluída!\nTotal: R$ " +
                            String.format("%.2f", locacao.getValorTotal() + locacao.getMultas())
            );

            limpar();
            carregarPlacasAtivas();

        } catch (IllegalArgumentException e) {
            mostrarAlerta(e.getMessage());
        } catch (SQLException e) {
            mostrarErro("Erro ao processar devolução no banco de dados.");
        }
    }

    // Retorna para a tela Home.
    @FXML
    private void voltarAction() {
        voltar(btnVoltar);
    }

    // Carrega as placas que possuem locação ativa.
    private void carregarPlacasAtivas() {
        try {
            placasAtivas.setAll(devolucaoService.listarPlacasComLocacaoAtiva());
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar placas com locação ativa.");
        }
    }

    // Limpa os campos e remove a locação atualmente selecionada.
    private void limpar() {
        campoPlaca.clear();
        comboPlacasAtivas.getSelectionModel().clearSelection();

        labelCliente.setText("-");
        labelRetirada.setText("-");
        labelDevolucao.setText("-");
        labelValor.setText("R$ 0,00");
        labelAtraso.setText("-");
        labelMulta.setText("-");
        labelValorFinal.setText("R$ 0,00");

        locacaoAtual = null;
    }
}