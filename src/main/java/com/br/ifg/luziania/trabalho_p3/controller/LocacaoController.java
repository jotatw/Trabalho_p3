package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.Cliente;
import com.br.ifg.luziania.trabalho_p3.model.Locacao;
import com.br.ifg.luziania.trabalho_p3.model.Veiculo;
import com.br.ifg.luziania.trabalho_p3.service.ClienteService;
import com.br.ifg.luziania.trabalho_p3.service.LocacaoService;
import com.br.ifg.luziania.trabalho_p3.service.VeiculoService;
import com.br.ifg.luziania.trabalho_p3.util.MascaraUtil;
import com.br.ifg.luziania.trabalho_p3.util.NavegacaoUtil;
import com.br.ifg.luziania.trabalho_p3.util.Sessao;
import com.br.ifg.luziania.trabalho_p3.util.ValidacaoUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class LocacaoController {
    private final LocacaoService locacaoService = new LocacaoService();
    private final ClienteService clienteService = new ClienteService();
    private final VeiculoService veiculoService = new VeiculoService();
    private final ObservableList<Veiculo> veiculosDisponiveis = FXCollections.observableArrayList();

    @FXML private TextField campoCpf;
    @FXML private TextField campoPlaca;
    @FXML private DatePicker dataRetirada;
    @FXML private DatePicker dataDevolucao;
    @FXML private Label labelCliente;
    @FXML private Label labelVeiculo;
    @FXML private Label labelValor;
    @FXML private Button btnVoltar;
    @FXML private ComboBox<Veiculo> comboVeiculosDisponiveis;

    @FXML
    public void initialize() {
        MascaraUtil.cpf(campoCpf);
        MascaraUtil.placa(campoPlaca);

        configurarComboVeiculos();
        carregarVeiculosDisponiveis();

        dataRetirada.valueProperty().addListener((observable, oldValue, newValue) -> atualizarValorTotal());
        dataDevolucao.valueProperty().addListener((observable, oldValue, newValue) -> atualizarValorTotal());
        campoPlaca.textProperty().addListener((observable, oldValue, newValue) -> atualizarValorTotal());
        comboVeiculosDisponiveis.valueProperty().addListener((observable, antigo, veiculo) -> {
            if (veiculo != null) {
                campoPlaca.setText(veiculo.getPlaca());
                labelVeiculo.setText(
                        veiculo.getModelo() +
                                " - " +
                                veiculo.getCategoria() +
                                " - R$ " +
                                String.format("%.2f", veiculo.getValorLocacao()) +
                                "/dia"
                );
                atualizarValorTotal();
            }
        });
    }
    @FXML
    private void buscarCliente() {
        String cpf = campoCpf.getText();

        if (!ValidacaoUtil.cpfValido(cpf)) {
            mostrarAlerta("CPF inválido! Use o formato: 000.000.000-00");
            return;
        }
        try {
            Cliente cliente = clienteService.buscarPorCpf(cpf);
            if (cliente != null) {
                labelCliente.setText(cliente.getNome());
            } else {
                labelCliente.setText("Cliente não encontrado!");
            }
        } catch (SQLException e) {
            mostrarAlerta("Erro ao buscar cliente. Tente novamente.");
        }
    }
    @FXML
    private void buscarVeiculo() {
        String placa = campoPlaca.getText();

        if (ValidacaoUtil.campoVazio(placa)) {
            mostrarAlerta("Informe a placa ou selecione um veículo disponível!");
            return;
        }
        if (!ValidacaoUtil.placaValido(placa.toUpperCase())) {
            mostrarAlerta("Placa inválida! Use o formato: ABC1D23 ou ABC1234");
            return;
        }
        try {
            Veiculo veiculo = veiculoService.buscarPorPlaca(placa);
            if (veiculo == null) {
                labelVeiculo.setText("Veículo não encontrado!");
                labelValor.setText("R$ 0,00");
                return;
            }
            if (!veiculo.isDisponivel()) {
                labelVeiculo.setText("Veículo indisponível!");
                labelValor.setText("R$ 0,00");
                mostrarAlerta("Este veículo não está disponível para locação.");
                return;
            }
            labelVeiculo.setText(
                    veiculo.getModelo()
                            + " - "
                            + veiculo.getCategoria()
                            + " - R$ "
                            + String.format("%.2f", veiculo.getValorLocacao())
                            + "/dia"
            );
            atualizarValorTotal();
        } catch (SQLException e) {
            mostrarAlerta("Erro ao buscar veículo. Tente novamente.");
        }

    }
    @FXML
    private void confirmarLocacao() {
        String cpf = campoCpf.getText().trim();
        String placa = campoPlaca.getText().trim().toUpperCase();
        LocalDate retirada = dataRetirada.getValue();
        LocalDate devolucao = dataDevolucao.getValue();

        //validação basica
        if (cpf.isEmpty() || placa.isEmpty() || retirada == null || devolucao == null) {
            mostrarAlerta("Preencha todos os campos.");
            return;
        }
        if (!ValidacaoUtil.cpfValido(cpf)) {
            mostrarAlerta("CPF inválido! Use o formato: 000.000.000-00");
            return;
        }
        if (!ValidacaoUtil.placaValido(placa)) {
            mostrarAlerta("Placa inválida! Use o formato: ABC1D23 ou ABC1234");
            return;
        }
        try {
            //pega o usuario logado e depois conecta a sessão
            Locacao locacao = locacaoService.realizarLocacao(cpf, placa, retirada, devolucao, Sessao.getUsuarioLogado());
            mostrarSucesso("Locacao realizada! Valor: R$ " + String.format("%.2f", locacao.getValorTotal()));

            limparCampo();
            carregarVeiculosDisponiveis();
        } catch (IllegalArgumentException e) {
            mostrarAlerta(e.getMessage());
        } catch (SQLException e){
            mostrarAlerta("Não foi possível realizar a locação. Tente novamente.");
        }
    }
    @FXML
    private void voltar() {
        try {
            NavegacaoUtil.trocarTela(btnVoltar, "/fxml/Home.fxml", "Locadora - Home");
        } catch (IOException e) {
            mostrarAlerta("Erro ao voltar para a tela inicial.");
        }
    }
    private void atualizarValorTotal() {
        String placa = campoPlaca.getText().trim().toUpperCase();
        LocalDate retirada = dataRetirada.getValue();
        LocalDate devolucao = dataDevolucao.getValue();

        if (placa.isBlank() || retirada == null || devolucao == null) {
            labelValor.setText("R$ 0,00");
            return;
        }

        long dias = ChronoUnit.DAYS.between(retirada, devolucao);

        if (dias <= 0) {
            labelValor.setText("Data inválida!");
            return;
        }

        try {
            Veiculo veiculo = veiculoService.buscarPorPlaca(placa);

            if (veiculo == null) {
                labelValor.setText("Veículo não encontrado!");
                return;
            }

            double valorTotal = dias * veiculo.getValorLocacao();
            labelValor.setText("R$ " + String.format("%.2f", valorTotal));

        } catch (SQLException e) {
            mostrarAlerta("Erro ao calcular valor.");
        }
    }
    private void configurarComboVeiculos() {
        comboVeiculosDisponiveis.setItems(veiculosDisponiveis);
        comboVeiculosDisponiveis.setConverter(new StringConverter<>() {
            @Override
            public String toString(Veiculo veiculo) {
                if (veiculo == null) {
                    return "";
                }
                return veiculo.getPlaca()
                        + " - " + veiculo.getModelo()
                        + " - " + veiculo.getCategoria()
                        + " - R$ " + String.format("%.2f", veiculo.getValorLocacao()) + "/dia";
            }
            @Override
            public Veiculo fromString(String string) {
                return null;
            }
        });
    }
    private void carregarVeiculosDisponiveis() {
        try {
            List<Veiculo> lista = veiculoService.listarDisponiveis();
            veiculosDisponiveis.setAll(lista);
        } catch (SQLException e) {
            mostrarAlerta("Erro ao carregar veículos disponíveis.");
        }
    }
    private void limparCampo() {
        campoCpf.clear();
        campoPlaca.clear();
        dataRetirada.setValue(null);
        dataDevolucao.setValue(null);
        labelCliente.setText("-");
        labelVeiculo.setText("-");
        labelValor.setText("R$ 0,00");

        comboVeiculosDisponiveis.getSelectionModel().clearSelection();
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
