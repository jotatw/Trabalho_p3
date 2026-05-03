package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.dao.ClienteDAO;
import com.br.ifg.luziania.trabalho_p3.model.Cliente;
import com.br.ifg.luziania.trabalho_p3.util.ValidacaoUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class ClienteController {
    @FXML private TextField campoNome;
    @FXML private TextField campoCpf;
    @FXML private TextField campoTelefone;
    @FXML private TextField campoEmail;
    @FXML private TextField campoCnh;
    @FXML private Button btnVoltar;

    @FXML private TableView <Cliente> tabelaClientes;
    @FXML private TableColumn <Cliente, String> colunaNome;
    @FXML private TableColumn <Cliente, String> colunaCpf;
    @FXML private TableColumn <Cliente, String> colunaTelefone;
    @FXML private TableColumn <Cliente, String> colunaEmail;
    @FXML private TableColumn <Cliente, String> colunaCnh;

    @FXML
    private void salvar() {
        String nome = campoNome.getText();
        String cpf = campoCpf.getText();
        String cnh = campoCnh.getText();
        String telefone = campoTelefone.getText();
        String email = campoEmail.getText();

        //validações
        if (ValidacaoUtil.campoVazio(nome)) {
            mostraAlerta("Nome e obrigatorio!");
            return;
        }
        if (ValidacaoUtil.cpfValido(cpf)) {
            mostraAlerta(("CPF invalido!  use o formato: 000.000.000-00"));
            return;
        }
        if (ValidacaoUtil.cnhValido(cnh)) {
            mostraAlerta("CNH invalida! informe 11 digitos numericos");
            return;
        }
        if (!email.isEmpty() && !ValidacaoUtil.emailValido(email)) {
            mostraAlerta("Email invalido!");
            return;
        }

        //salva no banco de dados
        try {
            Cliente cliente = new Cliente();
            cliente.setNome(nome);
            cliente.setCpf(cpf);
            cliente.setCnh(cnh);
            cliente.setTelefone(telefone);
            cliente.setEmail(email);

            ClienteDAO dao = new ClienteDAO();
            dao.salvar(cliente);
            mostraSucesso("Cliente salvo com sucesso!");
            limpar();
        } catch (SQLException e) {
            mostraAlerta("Erro ao salvar: " + e.getMessage());
        }
    }

    @FXML
    private void limpar() {
        campoNome.clear();
        campoCpf.clear();
        campoTelefone.clear();
        campoEmail.clear();
        campoCnh.clear();
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
    private void mostraAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    private void mostraSucesso(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
