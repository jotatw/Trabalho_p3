package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.dao.ClienteDAO;
import com.br.ifg.luziania.trabalho_p3.dao.VeiculoDAO;
import com.br.ifg.luziania.trabalho_p3.model.Cliente;
import com.br.ifg.luziania.trabalho_p3.model.Locacao;
import com.br.ifg.luziania.trabalho_p3.model.Veiculo;
import com.br.ifg.luziania.trabalho_p3.service.LocacaoService;
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

public class LocacaoController {
    private LocacaoService locacaoService = new LocacaoService();
    private ClienteDAO clienteDAO = new ClienteDAO();
    private VeiculoDAO veiculoDAO = new VeiculoDAO();

    @FXML private TextField campoCpf;
    @FXML private TextField campoPlaca;
    @FXML private DatePicker dataRetirada;
    @FXML private DatePicker dataDevolucao;
    @FXML private Label labelCliente;
    @FXML private Label labelVeiculo;
    @FXML private Label labelValor;
    @FXML private Button btnVoltar;

    @FXML
    private void buscarCliente() {
        String cpf = campoCpf.getText();

        if (ValidacaoUtil.cpfValido(cpf)) {
            mostrarAlerta("Informe o cpf!");
            return;
        }
        if (!ValidacaoUtil.cpfValido(cpf)) {
            mostrarAlerta("CPF invalido! Use o formato: 000.000.000-00");
            return;
        }
        try {
            Cliente cliente = clienteDAO.buscarPorCpf(cpf);
            if (cliente != null) {
                labelCliente.setText(cliente.getNome());
            } else {
                labelCliente.setText("Cliente não encontrado!");
            }
        } catch (SQLException e) {
            mostrarAlerta("Erro ao buscar cliente: " + e.getMessage());
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
            mostrarAlerta("Placa invalido! Utilize o formato Mercosul: ABC1D23");
        }
        try {
            Veiculo veiculo = veiculoDAO.buscarPorPlaca(placa);
            if (veiculo != null) {
                labelVeiculo.setText(veiculo.getModelo() + " - R$" + veiculo.getValorLocacao() + "/dia");
            } else {
                labelVeiculo.setText("Veiculo não encontrado!");
            }
        } catch (SQLException e) {
            mostrarAlerta("Erro ao buscar veiculo: " + e.getMessage());
        }
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
            Locacao locacao = locacaoService.realizarLocacao(cpf, placa, retirada, devolucao, Sessao.getUsuarioLogado());
            mostrarSucesso("Locacao realizada! Valor: R$ " + String.format("%.2f", locacao.getValorTotal()));
        } catch (IllegalArgumentException e) {
            mostrarAlerta(e.getMessage());
        } catch (SQLException e){
            mostrarAlerta("Erro no banco: " + e.getMessage());
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
