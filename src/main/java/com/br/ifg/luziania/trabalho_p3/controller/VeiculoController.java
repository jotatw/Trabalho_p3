package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.Veiculo;
import com.br.ifg.luziania.trabalho_p3.service.VeiculoService;
import com.br.ifg.luziania.trabalho_p3.util.MascaraUtil;
import com.br.ifg.luziania.trabalho_p3.util.ValidacaoUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

// Controller responsável pela tela de gerenciamento de veículos.
// Controla cadastro, atualização, busca e preenchimento da tabela de veículos.
public class VeiculoController extends BaseController {

    private final VeiculoService veiculoService = new VeiculoService();

    private Veiculo veiculoSelecionado;

    private final ObservableList<Veiculo> listaVeiculos = FXCollections.observableArrayList();
    private FilteredList<Veiculo> listaFiltrada;

    @FXML private TextField campoPlaca;
    @FXML private TextField campoModelo;
    @FXML private TextField campoMarca;
    @FXML private TextField campoValor;
    @FXML private TextField campoBusca;

    @FXML private ComboBox<String> comboCategoria;

    @FXML private Button btnVoltar;

    @FXML private TableView<Veiculo> tabelaVeiculo;
    @FXML private TableColumn<Veiculo, String> colunaPlaca;
    @FXML private TableColumn<Veiculo, String> colunaModelo;
    @FXML private TableColumn<Veiculo, String> colunaMarca;
    @FXML private TableColumn<Veiculo, String> colunaCategoria;
    @FXML private TableColumn<Veiculo, Double> colunaValor;
    @FXML private TableColumn<Veiculo, Boolean> colunaDisponivel;

    // Inicializa a tela de veículos.
    // Configura máscara, categorias, colunas da tabela, busca e seleção.
    @FXML
    public void initialize() {
        MascaraUtil.placa(campoPlaca);

        configurarCategorias();
        configurarTabela();
        carregarTabela();
        configurarBusca();
        configurarSelecaoTabela();
    }

    // Define as categorias disponíveis para classificação dos veículos.
    private void configurarCategorias() {
        comboCategoria.setItems(FXCollections.observableArrayList(
                "Econômico",
                "Hatch",
                "Sedan",
                "SUV",
                "Picape",
                "Luxo",
                "Van"
        ));
    }

    // Configura quais atributos do Veículo serão exibidos em cada coluna.
    private void configurarTabela() {
        colunaPlaca.setCellValueFactory(new PropertyValueFactory<>("placa"));
        colunaModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colunaMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colunaCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colunaValor.setCellValueFactory(new PropertyValueFactory<>("valorLocacao"));
        colunaDisponivel.setCellValueFactory(new PropertyValueFactory<>("disponivel"));
    }

    // Quando o usuário seleciona um veículo na tabela,
    // os dados são carregados no formulário para edição.
    private void configurarSelecaoTabela() {
        tabelaVeiculo.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null) {
                veiculoSelecionado = selecionado;
                preencherFormulario(selecionado);
            }
        });
    }

    // Salva um novo veículo após validar os campos.
    @FXML
    private void salvar() {
        if (!validarCampos()) {
            return;
        }

        try {
            Veiculo veiculo = new Veiculo();

            mapearFormulario(veiculo);
            veiculoService.salvar(veiculo);

            mostrarSucesso("Veículo cadastrado!");
            limpar();
            carregarTabela();

        } catch (SQLException e) {
            mostrarErro("Erro ao cadastrar veículo.");
        }
    }

    // Atualiza o veículo selecionado na tabela.
    @FXML
    private void atualizar() {
        if (veiculoSelecionado == null) {
            mostrarAlerta("Selecione um veículo!");
            return;
        }

        if (!validarCampos()) {
            return;
        }

        try {
            mapearFormulario(veiculoSelecionado);
            veiculoService.atualizar(veiculoSelecionado);

            mostrarSucesso("Veículo atualizado!");
            limpar();
            carregarTabela();

        } catch (SQLException e) {
            mostrarErro("Erro ao atualizar veículo.");
        }
    }

    // Limpa o formulário, o campo de busca e a seleção da tabela.
    @FXML
    private void limpar() {
        campoPlaca.clear();
        campoModelo.clear();
        campoMarca.clear();
        campoValor.clear();
        campoBusca.clear();

        comboCategoria.getSelectionModel().clearSelection();

        veiculoSelecionado = null;
        tabelaVeiculo.getSelectionModel().clearSelection();
    }

    // Retorna para a tela Home.
    @FXML
    private void voltarAction() {
        voltar(btnVoltar);
    }

    // Valida os campos do formulário antes de salvar ou atualizar.
    private boolean validarCampos() {
        if (!ValidacaoUtil.placaValido(campoPlaca.getText())) {
            mostrarAlerta("Placa inválida!");
            return false;
        }

        if (ValidacaoUtil.campoVazio(campoModelo.getText())) {
            mostrarAlerta("Modelo obrigatório!");
            return false;
        }

        if (ValidacaoUtil.campoVazio(campoMarca.getText())) {
            mostrarAlerta("Marca obrigatória!");
            return false;
        }

        if (comboCategoria.getValue() == null) {
            mostrarAlerta("Selecione a categoria!");
            return false;
        }

        try {
            double valor = Double.parseDouble(campoValor.getText().replace(",", "."));

            if (valor <= 0) {
                mostrarAlerta("Valor da diária deve ser maior que zero!");
                return false;
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Valor da diária inválido!");
            return false;
        }

        return true;
    }

    // Copia os dados do formulário para o objeto Veiculo.
    private void mapearFormulario(Veiculo veiculo) {
        veiculo.setPlaca(campoPlaca.getText().trim().toUpperCase());
        veiculo.setModelo(campoModelo.getText().trim());
        veiculo.setMarca(campoMarca.getText().trim());
        veiculo.setCategoria(comboCategoria.getValue());
        veiculo.setValorLocacao(Double.parseDouble(campoValor.getText().replace(",", ".")));
    }

    // Preenche o formulário com os dados do veículo selecionado na tabela.
    private void preencherFormulario(Veiculo veiculo) {
        campoPlaca.setText(veiculo.getPlaca());
        campoModelo.setText(veiculo.getModelo());
        campoMarca.setText(veiculo.getMarca());
        comboCategoria.setValue(veiculo.getCategoria());
        campoValor.setText(String.format("%.2f", veiculo.getValorLocacao()));
    }

    // Carrega todos os veículos cadastrados no banco.
    private void carregarTabela() {
        try {
            listaVeiculos.setAll(veiculoService.listarTodos());
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar frota.");
        }
    }

    // Configura o filtro de busca da tabela.
    // A busca considera placa, modelo, marca, categoria, valor e disponibilidade.
    private void configurarBusca() {
        listaFiltrada = new FilteredList<>(listaVeiculos, veiculo -> true);
        tabelaVeiculo.setItems(listaFiltrada);

        campoBusca.textProperty().addListener((obs, antigo, novo) -> {
            listaFiltrada.setPredicate(veiculo -> {
                if (novo == null || novo.isEmpty()) {
                    return true;
                }

                String filtro = novo.toLowerCase();

                return veiculo.getPlaca().toLowerCase().contains(filtro)
                        || veiculo.getModelo().toLowerCase().contains(filtro)
                        || veiculo.getMarca().toLowerCase().contains(filtro)
                        || veiculo.getCategoria().toLowerCase().contains(filtro)
                        || String.valueOf(veiculo.getValorLocacao()).contains(filtro)
                        || String.valueOf(veiculo.isDisponivel()).contains(filtro);
            });
        });
    }
}