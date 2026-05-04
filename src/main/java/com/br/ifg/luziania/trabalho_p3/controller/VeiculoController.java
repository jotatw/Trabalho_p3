package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.dao.VeiculoDAO;
import com.br.ifg.luziania.trabalho_p3.model.Veiculo;
import com.br.ifg.luziania.trabalho_p3.util.ValidacaoUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class VeiculoController {
    @FXML private TextField campoPlaca;
    @FXML private TextField campoModelo;
    @FXML private TextField campoMarca;
    @FXML private TextField campoCategoria;
    @FXML private TextField campoValor;
    @FXML private CheckBox ckbDisponivel;
    @FXML private Button btnVoltar;

    @FXML private TableView <Veiculo> tabelaVeiculo;
    @FXML private TableColumn<Veiculo, String> colunaPlaca;
    @FXML private TableColumn<Veiculo, String> colunaModelo;
    @FXML private TableColumn<Veiculo, String> colunaMarca;
    @FXML private TableColumn<Veiculo, String> colunaCategoria;
    @FXML private TableColumn<Veiculo, String> colunaValor;

    @FXML
    private void salvar() {
        String  placa = campoPlaca.getText();
        String modelo = campoModelo.getText();
        String marca = campoMarca.getText();
        String categoria = campoCategoria.getText();
        String valor = campoValor.getText();

        //validações
        if(!ValidacaoUtil.placaValido(placa)) {
            mostraAlerta("Placa invalida! use o modelo Mercoosul: ABC1D23");
            return;
        }
        if(ValidacaoUtil.campoVazio(modelo)) {
            mostraAlerta("Modelo e obrigatorio!");
            return;
        }
        if(ValidacaoUtil.campoVazio(marca)) {
            mostraAlerta("Marca e obrigatoria!");
            return;
        }
        if(ValidacaoUtil.campoVazio(categoria)) {
            mostraAlerta("Categoria e obrigatoria!");
            return;
        }
        if(ValidacaoUtil.campoVazio(valor)) {
            mostraAlerta("Informe o valor da diaria");
            return;
        }
        try {
            double v = Double.parseDouble(valor);
            if (!ValidacaoUtil.valorPositivo(v)) {
                mostraAlerta("O valor de diaria deve ser maior que zero");
                return;
            }
        } catch (NumberFormatException e) {
            mostraAlerta("Valor invalido! use apenas numeros. EX: 89.90");
            return;
        }

        //salva no banco de dados
        try {
            Veiculo veiculo = new Veiculo();
            veiculo.setPlaca(placa);
            veiculo.setModelo(modelo);
            veiculo.setMarca(marca);
            veiculo.setCategoria(categoria);
            veiculo.setValorLocacao(Double.parseDouble(valor));

            VeiculoDAO dao = new VeiculoDAO();
            dao.salvar(veiculo);
            mostraSucesso("Veiculo cadastrado com sucesso");
            limpar();
            carregarTabela();
        } catch (SQLException e) {
            mostraAlerta("Erro ao cadastrar veiculo" + e.getMessage());
        }
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
    @FXML
    public void initialize() {
        colunaPlaca.setCellValueFactory(new PropertyValueFactory<>("placa"));
        colunaModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colunaMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colunaCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colunaValor.setCellValueFactory(new PropertyValueFactory<>("valorLocacao"));
        carregarTabela();
    }
    private void carregarTabela() {
        try {
            VeiculoDAO dao = new VeiculoDAO();
            List<Veiculo> lista = dao.listarTodos();
            tabelaVeiculo.setItems(FXCollections.observableArrayList(lista));
        } catch (SQLException e) {
            mostraAlerta("Erro ao carregar veiculos: " + e.getMessage());
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
