package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.Cliente;
import com.br.ifg.luziania.trabalho_p3.model.Locacao;
import com.br.ifg.luziania.trabalho_p3.model.Veiculo;
import com.br.ifg.luziania.trabalho_p3.service.ClienteService;
import com.br.ifg.luziania.trabalho_p3.service.LocacaoService;
import com.br.ifg.luziania.trabalho_p3.service.VeiculoService;
import com.br.ifg.luziania.trabalho_p3.util.MascaraUtil;
import com.br.ifg.luziania.trabalho_p3.util.Sessao;
import com.br.ifg.luziania.trabalho_p3.util.ValidacaoUtil;
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

public class LocacaoController {
    private final LocacaoService locacaoService = new LocacaoService();
    private final ClienteService clienteService = new ClienteService();
    private final VeiculoService veiculoService = new VeiculoService();

    @FXML private TextField campoCpf;
    @FXML private TextField campoPlaca;
    @FXML private DatePicker dataRetirada;
    @FXML private DatePicker dataDevolucao;
    @FXML private Label labelCliente;
    @FXML private Label labelVeiculo;
    @FXML private Label labelValor;
    @FXML private Button btnVoltar;

    @FXML
    public void initialize() {
        //aplica validação nos campos de busca
        MascaraUtil.cpf(campoCpf);
        MascaraUtil.placa(campoPlaca);

        dataRetirada.valueProperty().addListener((observable, oldValue, newValue) -> atualizarValorTotal());
        dataDevolucao.valueProperty().addListener((observable, oldValue, newValue) -> atualizarValorTotal());

        campoPlaca.textProperty().addListener((observable, oldValue, newValue) -> atualizarValorTotal());
    }

    @FXML
    private void buscarCliente() {
        String cpf = campoCpf.getText();

        if (!ValidacaoUtil.cpfValido(cpf)) {
            mostrarAlerta("CPF invalido! Use o formato: 000.000.000-00");
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
            mostrarAlerta("Informe a Placa!");
            return;
        }
        if (!ValidacaoUtil.placaValido(placa.toUpperCase())) {
            mostrarAlerta("Placa inválida! Use o formato: ABC1D23 ou ABC1234");
            return;
        }
        try {
            Veiculo veiculo = veiculoService.buscarPorPlaca(placa);
            if (veiculo != null) {
                labelVeiculo.setText(veiculo.getModelo() + " - R$" + veiculo.getValorLocacao() + "/dia");
                atualizarValorTotal();
            } else {
                labelVeiculo.setText("Veiculo não encontrado!");
                labelValor.setText("R$: 0,00");
            }
        } catch (SQLException e) {
            mostrarAlerta("Erro ao buscar veiculo: " + e.getMessage());
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
            mostrarAlerta("Prencha todos os campos");
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
        } catch (IllegalArgumentException e) {
            mostrarAlerta(e.getMessage());
        } catch (SQLException e){
            mostrarAlerta("Não foi possível realizar a locação. Tente novamente.");
        }
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
            mostrarAlerta("Erro ao voltar para a tela inicial.");
        }
    }
    private void atualizarValorTotal() {
        String placa = campoPlaca.getText();
        LocalDate retirada = dataRetirada.getValue();
        LocalDate devolucao = dataDevolucao.getValue();

        if (placa == null || placa.isBlank() || retirada == null || devolucao == null) {
            labelValor.setText("R$: ");
            return;
        }
        long dias = ChronoUnit.DAYS.between(retirada, devolucao);
        if (dias <= 0) {
            labelValor.setText("Data invalida!");
            return;
        }
        try {
            Veiculo veiculo = veiculoService.buscarPorPlaca(placa);
            if (veiculo == null) {
                labelValor.setText("Veiculo não encontrado!");
                return;
            }
            double valorTotal = dias * veiculo.getValorLocacao();
            labelValor.setText("R$: " + String.format("%.2f", valorTotal));
        } catch (SQLException e) {
            mostrarAlerta("Erro ao calcular valor");
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
