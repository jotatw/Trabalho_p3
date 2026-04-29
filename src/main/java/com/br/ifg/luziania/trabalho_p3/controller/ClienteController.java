package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.Cliente;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ClienteController {
    @FXML private TextField campoNome;
    @FXML private TextField campoCpf;
    @FXML private TextField campoTelefone;
    @FXML private TextField campoEmail;
    @FXML private TextField campoCnh;

    @FXML private TableView <Cliente> tabelaClientes;
    @FXML private TableColumn <Cliente, String> colunaNome;
    @FXML private TableColumn <Cliente, String> colunaCpf;
    @FXML private TableColumn <Cliente, String> colunaTelefone;
    @FXML private TableColumn <Cliente, String> colunaEmail;
    @FXML private TableColumn <Cliente, String> colunaCnh;

    @FXML
    private void salvar() {
        System.out.println("salvando novo cliente ...");
    }

    @FXML
    private void limpar() {
        campoNome.clear();
        campoCpf.clear();
        campoTelefone.clear();
        campoEmail.clear();
        campoCnh.clear();
    }
}
