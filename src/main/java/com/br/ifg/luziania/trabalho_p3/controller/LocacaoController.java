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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Controller responsável pela tela de locação.
// Controla busca de cliente, seleção de veículo, cálculo de valor e confirmação da locação.
public class LocacaoController extends BaseController {

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

    // Inicializa a tela de locação.
    // Configura máscaras, combo de veículos disponíveis e atualização automática do valor.
    @FXML
    public void initialize() {
        MascaraUtil.cpf(campoCpf);
        MascaraUtil.placa(campoPlaca);

        configurarComboVeiculos();
        carregarVeiculosDisponiveis();
        configurarAtualizacaoValor();
    }

    // Configura os eventos que atualizam o valor total sempre que dados importantes mudam.
    private void configurarAtualizacaoValor() {
        dataRetirada.valueProperty().addListener((obs, antigo, novo) -> atualizarValorTotal());
        dataDevolucao.valueProperty().addListener((obs, antigo, novo) -> atualizarValorTotal());
        campoPlaca.textProperty().addListener((obs, antigo, novo) -> atualizarValorTotal());

        comboVeiculosDisponiveis.valueProperty().addListener((obs, antigo, veiculo) -> {
            if (veiculo != null) {
                campoPlaca.setText(veiculo.getPlaca());
                labelVeiculo.setText(veiculo.getModelo() + " - " + veiculo.getMarca());
                atualizarValorTotal();
            }
        });
    }

    // Busca o cliente pelo CPF informado e exibe o nome encontrado na tela.
    @FXML
    private void buscarCliente() {
        String cpf = campoCpf.getText();

        if (!ValidacaoUtil.cpfValido(cpf)) {
            mostrarAlerta("CPF inválido!");
            return;
        }

        try {
            Cliente cliente = clienteService.buscarPorCpf(cpf);

            if (cliente == null) {
                labelCliente.setText("Não encontrado");
                mostrarAlerta("Cliente não encontrado!");
                return;
            }

            if (!cliente.isAtivo()) {
                labelCliente.setText("Cliente inativo");
                mostrarAlerta("Cliente inativo não pode realizar locação!");
                return;
            }

            labelCliente.setText(cliente.getNome());

        } catch (SQLException e) {
            mostrarErro("Erro ao buscar cliente.");
        }
    }

    // Busca o veículo pela placa informada e verifica se ele está disponível.
    @FXML
    private void buscarVeiculo() {
        String placa = campoPlaca.getText().trim().toUpperCase();

        if (placa.isEmpty()) {
            mostrarAlerta("Informe a placa!");
            return;
        }

        try {
            Veiculo veiculo = veiculoService.buscarPorPlaca(placa);

            if (veiculo == null) {
                labelVeiculo.setText("Não encontrado");
                mostrarAlerta("Veículo não encontrado!");
                return;
            }

            if (!veiculo.isDisponivel()) {
                labelVeiculo.setText("Indisponível");
                mostrarAlerta("Veículo indisponível!");
                return;
            }

            labelVeiculo.setText(veiculo.getModelo() + " - " + veiculo.getMarca());
            atualizarValorTotal();

        } catch (SQLException e) {
            mostrarErro("Erro ao buscar veículo.");
        }
    }

    // Confirma a locação usando CPF, placa, datas e o usuário logado.
    @FXML
    private void confirmarLocacao() {
        String cpf = campoCpf.getText().trim();
        String placa = campoPlaca.getText().trim().toUpperCase();
        LocalDate retirada = dataRetirada.getValue();
        LocalDate devolucao = dataDevolucao.getValue();

        if (cpf.isEmpty() || placa.isEmpty() || retirada == null || devolucao == null) {
            mostrarAlerta("Preencha todos os campos obrigatórios.");
            return;
        }

        try {
            Locacao locacao = locacaoService.realizarLocacao(
                    cpf,
                    placa,
                    retirada,
                    devolucao,
                    Sessao.getUsuarioLogado()
            );

            mostrarSucesso("Locação realizada! Total: R$ " + String.format("%.2f", locacao.getValorTotal()));

            limpar();
            carregarVeiculosDisponiveis();

        } catch (IllegalArgumentException e) {
            mostrarAlerta(e.getMessage());
        } catch (SQLException e) {
            mostrarErro("Erro ao registrar locação no banco de dados.");
        }
    }

    // Retorna para a tela Home.
    @FXML
    private void voltarAction() {
        voltar(btnVoltar);
    }

    // Calcula uma prévia do valor total da locação.
    // O cálculo considera a quantidade de dias e o valor da diária do veículo.
    private void atualizarValorTotal() {
        String placa = campoPlaca.getText().trim().toUpperCase();
        LocalDate retirada = dataRetirada.getValue();
        LocalDate devolucao = dataDevolucao.getValue();

        if (placa.isEmpty() || retirada == null || devolucao == null) {
            labelValor.setText("R$ 0,00");
            return;
        }

        long dias = ChronoUnit.DAYS.between(retirada, devolucao);

        if (dias <= 0) {
            labelValor.setText("Data inválida");
            return;
        }

        try {
            Veiculo veiculo = veiculoService.buscarPorPlaca(placa);

            if (veiculo != null) {
                double valorTotal = dias * veiculo.getValorLocacao();
                labelValor.setText("R$ " + String.format("%.2f", valorTotal));
            }

        } catch (SQLException e) {
            labelValor.setText("Erro");
        }
    }

    // Configura a forma como os veículos aparecem no ComboBox.
    private void configurarComboVeiculos() {
        comboVeiculosDisponiveis.setItems(veiculosDisponiveis);

        comboVeiculosDisponiveis.setConverter(new StringConverter<>() {
            @Override
            public String toString(Veiculo veiculo) {
                if (veiculo == null) {
                    return "";
                }

                return veiculo.getPlaca() + " - " + veiculo.getModelo();
            }

            @Override
            public Veiculo fromString(String texto) {
                return null;
            }
        });
    }

    // Carrega no ComboBox apenas os veículos disponíveis para locação.
    private void carregarVeiculosDisponiveis() {
        try {
            veiculosDisponiveis.setAll(veiculoService.listarDisponiveis());
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar veículos disponíveis.");
        }
    }

    // Limpa todos os campos da tela após uma locação ou cancelamento.
    private void limpar() {
        campoCpf.clear();
        campoPlaca.clear();

        dataRetirada.setValue(null);
        dataDevolucao.setValue(null);

        labelCliente.setText("-");
        labelVeiculo.setText("-");
        labelValor.setText("R$ 0,00");

        comboVeiculosDisponiveis.getSelectionModel().clearSelection();
    }
}