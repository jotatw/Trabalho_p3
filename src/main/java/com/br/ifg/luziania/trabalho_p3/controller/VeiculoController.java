package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.Veiculo;
import javafx.scene.control.*;
import javafx.fxml.FXML;

public class VeiculoController {
    @FXML private TextField campoPlaca;
    @FXML private TextField campoModelo;
    @FXML private TextField campoMarca;
    @FXML private TextField campoCategoria;
    @FXML private TextField campoValor;
    @FXML private CheckBox ckbDisponivel;

    @FXML private TableView <Veiculo> tableVeiculo;
    @FXML private TableColumn<Veiculo, String> colunaPlaca;
    @FXML private TableColumn<Veiculo, String> colunaModelo;
    @FXML private TableColumn<Veiculo, String> colunaMarca;
    @FXML private TableColumn<Veiculo, String> colunaCategoria;
    @FXML private TableColumn<Veiculo, String> colunaValor;

    @FXML
    private void salvar() {
        System.out.println("salvando veiculo ...");
    }

    @FXML
    private void limpar() {
        campoPlaca.clear();
        campoModelo.clear();
        campoMarca.clear();
        campoCategoria.clear();
        campoValor.clear();
        ckbDisponivel.setSelected(false);
    }
}
