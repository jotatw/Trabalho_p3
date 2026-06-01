package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.Veiculo;
import com.br.ifg.luziania.trabalho_p3.service.VeiculoService;
import com.br.ifg.luziania.trabalho_p3.util.MascaraUtil;
import com.br.ifg.luziania.trabalho_p3.util.ValidacaoUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

public class VeiculoController extends BaseController {
    private final VeiculoService veiculoService = new VeiculoService();
    private Veiculo veiculoSelecionado;
    private final ObservableList<Veiculo> listaVeiculos = FXCollections.observableArrayList();
    private FilteredList<Veiculo> listaFiltrada;

    @FXML private TextField campoPlaca, campoModelo, campoMarca, campoValor, campoBusca;
    @FXML private ComboBox<String> comboCategoria;
    @FXML private Button btnVoltar;

    @FXML private TableView<Veiculo> tabelaVeiculo;
    @FXML private TableColumn<Veiculo, String> colunaPlaca, colunaModelo, colunaMarca, colunaCategoria;
    @FXML private TableColumn<Veiculo, Double> colunaValor;
    @FXML private TableColumn<Veiculo, Boolean> colunaDisponivel;

    @FXML
    public void initialize() {
        MascaraUtil.placa(campoPlaca);
        comboCategoria.setItems(FXCollections.observableArrayList("Econômico", "Hatch", "Sedan", "SUV", "Picape", "Luxo", "Van"));

        colunaPlaca.setCellValueFactory(new PropertyValueFactory<>("placa"));
        colunaModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colunaMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colunaCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colunaValor.setCellValueFactory(new PropertyValueFactory<>("valorLocacao"));
        colunaDisponivel.setCellValueFactory(new PropertyValueFactory<>("disponivel"));

        carregarTabela();
        configurarBusca();

        tabelaVeiculo.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null) {
                veiculoSelecionado = selecionado;
                preencherFormulario(selecionado);
            }
        });
    }

    @FXML
    private void salvar() {
        if (validarCampos()) {
            try {
                Veiculo v = new Veiculo();
                mapearFormulario(v);
                veiculoService.salvar(v);
                mostrarSucesso("Veículo cadastrado!");
                limpar();
                carregarTabela();
            } catch (SQLException e) {
                mostrarErro("Erro ao cadastrar veículo.");
            }
        }
    }

    @FXML
    private void atualizar() {
        if (veiculoSelecionado == null) { mostrarAlerta("Selecione um veículo!"); return; }
        if (validarCampos()) {
            try {
                mapearFormulario(veiculoSelecionado);
                veiculoService.atualizar(veiculoSelecionado);
                mostrarSucesso("Veículo atualizado!");
                limpar();
                carregarTabela();
            } catch (SQLException e) {
                mostrarErro("Erro ao atualizar.");
            }
        }
    }

    @FXML
    private void limpar() {
        campoPlaca.clear(); campoModelo.clear(); campoMarca.clear(); campoValor.clear(); campoBusca.clear();
        comboCategoria.getSelectionModel().clearSelection();
        veiculoSelecionado = null;
        tabelaVeiculo.getSelectionModel().clearSelection();
    }

    @FXML
    private void voltarAction() { voltar(btnVoltar); }

    private boolean validarCampos() {
        if (!ValidacaoUtil.placaValido(campoPlaca.getText())) { mostrarAlerta("Placa inválida!"); return false; }
        if (ValidacaoUtil.campoVazio(campoModelo.getText())) { mostrarAlerta("Modelo obrigatório!"); return false; }
        if (comboCategoria.getValue() == null) { mostrarAlerta("Selecione a categoria!"); return false; }
        try {
            double v = Double.parseDouble(campoValor.getText().replace(",", "."));
            if (v <= 0) throw new Exception();
        } catch (Exception e) { mostrarAlerta("Valor da diária inválido!"); return false; }
        return true;
    }

    private void mapearFormulario(Veiculo v) {
        v.setPlaca(campoPlaca.getText().toUpperCase());
        v.setModelo(campoModelo.getText());
        v.setMarca(campoMarca.getText());
        v.setCategoria(comboCategoria.getValue());
        v.setValorLocacao(Double.parseDouble(campoValor.getText().replace(",", ".")));
    }

    private void preencherFormulario(Veiculo v) {
        campoPlaca.setText(v.getPlaca());
        campoModelo.setText(v.getModelo());
        campoMarca.setText(v.getMarca());
        comboCategoria.setValue(v.getCategoria());
        campoValor.setText(String.format("%.2f", v.getValorLocacao()));
    }

    private void carregarTabela() {
        try { listaVeiculos.setAll(veiculoService.listarTodos()); } catch (SQLException e) { mostrarErro("Erro ao carregar frota."); }
    }

    private void configurarBusca() {
        listaFiltrada = new FilteredList<>(listaVeiculos, p -> true);
        tabelaVeiculo.setItems(listaFiltrada);
        campoBusca.textProperty().addListener((obs, antigo, novo) -> {
            listaFiltrada.setPredicate(v -> {
                if (novo == null || novo.isEmpty()) return true;
                String f = novo.toLowerCase();
                return v.getPlaca().toLowerCase().contains(f) || v.getModelo().toLowerCase().contains(f);
            });
        });
    }
}
