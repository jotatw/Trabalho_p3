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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LocacaoController extends BaseController {
    private final LocacaoService locacaoService = new LocacaoService();
    private final ClienteService clienteService = new ClienteService();
    private final VeiculoService veiculoService = new VeiculoService();
    private final ObservableList<Veiculo> veiculosDisponiveis = FXCollections.observableArrayList();

    @FXML private TextField campoCpf, campoPlaca;
    @FXML private DatePicker dataRetirada, dataDevolucao;
    @FXML private Label labelCliente, labelVeiculo, labelValor;
    @FXML private Button btnVoltar;
    @FXML private ComboBox<Veiculo> comboVeiculosDisponiveis;

    @FXML
    public void initialize() {
        MascaraUtil.cpf(campoCpf);
        MascaraUtil.placa(campoPlaca);

        configurarComboVeiculos();
        carregarVeiculosDisponiveis();

        dataRetirada.valueProperty().addListener((o, a, n) -> atualizarValorTotal());
        dataDevolucao.valueProperty().addListener((o, a, n) -> atualizarValorTotal());
        campoPlaca.textProperty().addListener((o, a, n) -> atualizarValorTotal());

        comboVeiculosDisponiveis.valueProperty().addListener((o, a, v) -> {
            if (v != null) {
                campoPlaca.setText(v.getPlaca());
                atualizarValorTotal();
            }
        });
    }

    @FXML
    private void buscarCliente() {
        String cpf = campoCpf.getText();
        if (!ValidacaoUtil.cpfValido(cpf)) { mostrarAlerta("CPF inválido!"); return; }
        try {
            Cliente c = clienteService.buscarPorCpf(cpf);
            labelCliente.setText(c != null ? c.getNome() : "Não encontrado");
        } catch (SQLException e) { mostrarErro("Erro ao buscar cliente."); }
    }

    @FXML
    private void buscarVeiculo() {
        String placa = campoPlaca.getText().toUpperCase();
        if (placa.isEmpty()) { mostrarAlerta("Informe a placa!"); return; }
        try {
            Veiculo v = veiculoService.buscarPorPlaca(placa);
            if (v == null) { mostrarAlerta("Não encontrado!"); return; }
            if (!v.isDisponivel()) { mostrarAlerta("Indisponível!"); return; }
            atualizarValorTotal();
        } catch (SQLException e) { mostrarErro("Erro ao buscar veículo."); }
    }

    @FXML
    private void confirmarLocacao() {
        String cpf = campoCpf.getText().trim();
        String placa = campoPlaca.getText().trim().toUpperCase();
        LocalDate r = dataRetirada.getValue();
        LocalDate d = dataDevolucao.getValue();

        if (cpf.isEmpty() || placa.isEmpty() || r == null || d == null) { mostrarAlerta("Preencha tudo!"); return; }

        try {
            Locacao loc = locacaoService.realizarLocacao(cpf, placa, r, d, Sessao.getUsuarioLogado());
            mostrarSucesso("Locação realizada! Total: R$ " + String.format("%.2f", loc.getValorTotal()));
            limpar();
            carregarVeiculosDisponiveis();
        } catch (Exception e) { mostrarAlerta(e.getMessage()); }
    }

    @FXML private void voltarAction() { voltar(btnVoltar); }

    private void atualizarValorTotal() {
        String p = campoPlaca.getText().trim().toUpperCase();
        LocalDate r = dataRetirada.getValue();
        LocalDate d = dataDevolucao.getValue();

        if (p.isEmpty() || r == null || d == null) { labelValor.setText("R$ 0,00"); return; }

        long dias = ChronoUnit.DAYS.between(r, d);
        if (dias <= 0) { labelValor.setText("Data inválida"); return; }

        try {
            Veiculo v = veiculoService.buscarPorPlaca(p);
            if (v != null) labelValor.setText("R$ " + String.format("%.2f", dias * v.getValorLocacao()));
        } catch (SQLException e) { labelValor.setText("Erro"); }
    }

    private void configurarComboVeiculos() {
        comboVeiculosDisponiveis.setItems(veiculosDisponiveis);
        comboVeiculosDisponiveis.setConverter(new StringConverter<>() {
            @Override public String toString(Veiculo v) { return v == null ? "" : v.getPlaca() + " - " + v.getModelo(); }
            @Override public Veiculo fromString(String s) { return null; }
        });
    }

    private void carregarVeiculosDisponiveis() {
        try { veiculosDisponiveis.setAll(veiculoService.listarDisponiveis()); } catch (SQLException e) { }
    }

    private void limpar() {
        campoCpf.clear(); campoPlaca.clear();
        dataRetirada.setValue(null); dataDevolucao.setValue(null);
        labelCliente.setText("-"); labelValor.setText("R$ 0,00");
        comboVeiculosDisponiveis.getSelectionModel().clearSelection();
    }
}
