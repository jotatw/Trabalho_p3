package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.Veiculo;
import com.br.ifg.luziania.trabalho_p3.service.VeiculoService;
import com.br.ifg.luziania.trabalho_p3.util.MascaraUtil;
import com.br.ifg.luziania.trabalho_p3.util.NavegacaoUtil;
import com.br.ifg.luziania.trabalho_p3.util.ValidacaoUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
// Controla a tela de gerenciamento de veículos:
// cadastro, atualização, busca e listagem.
public class VeiculoController {
    private final VeiculoService veiculoService = new VeiculoService();
    // Guarda o veículo selecionado na tabela para atualização.
    private Veiculo veiculoSelecionado;
    // Lista principal carregada do banco e usada pela tabela.
    private final ObservableList<Veiculo> listaVeiculos = FXCollections.observableArrayList();
    // Lista filtrada usada pelo campo de busca.
    private FilteredList<Veiculo> listaFiltrada;
    private final ObservableList<String> categorias = FXCollections.observableArrayList(
            "Econômico",
            "Hatch",
            "Sedan",
            "SUV",
            "Picape",
            "Luxo",
            "Van")
            ;

    @FXML private TextField campoPlaca;
    @FXML private TextField campoModelo;
    @FXML private TextField campoMarca;
    @FXML private ComboBox<String> comboCategoria;
    @FXML private TextField campoValor;
    @FXML private TextField campoBusca;
    @FXML private Button btnAtualizar;
    @FXML private Button btnVoltar;
    @FXML private TableColumn <Veiculo, Boolean> colunaDisponivel;

    @FXML private TableView <Veiculo> tabelaVeiculo;
    @FXML private TableColumn<Veiculo, String> colunaPlaca;
    @FXML private TableColumn<Veiculo, String> colunaModelo;
    @FXML private TableColumn<Veiculo, String> colunaMarca;
    @FXML private TableColumn<Veiculo, String> colunaCategoria;
    @FXML private TableColumn<Veiculo, Double> colunaValor;
    // Executado automaticamente ao abrir a tela.
    // Configura máscara, colunas, tabela, busca e seleção.
    @FXML
    public void initialize() {
        //aplica a mascara de placa - aceita formato antigo e mercosul
        MascaraUtil.placa(campoPlaca);
        comboCategoria.setItems(categorias);
        // Liga cada coluna da tabela ao atributo correspondente do model Veiculo.
        colunaPlaca.setCellValueFactory(new PropertyValueFactory<>("placa"));
        colunaModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colunaMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colunaCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colunaValor.setCellValueFactory(new PropertyValueFactory<>("valorLocacao"));
        colunaDisponivel.setCellValueFactory(new PropertyValueFactory<>("disponivel"));

        tabelaVeiculo.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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
        String  placa = campoPlaca.getText().trim().toUpperCase();
        String modelo = campoModelo.getText().trim();
        String marca = campoMarca.getText().trim();
        String categoria = comboCategoria.getValue();
        String valorTexto = campoValor.getText().trim().replace(",",".");
        //validações
        if(!ValidacaoUtil.placaValido(placa)) {
            mostraAlerta("Placa inválida! Use o formato: ABC1D23 ou ABC1234");
            return;
        }
        if(ValidacaoUtil.campoVazio(modelo)) {
            mostraAlerta("Modelo é obrigatório!");
            return;
        }
        if(ValidacaoUtil.campoVazio(marca)) {
            mostraAlerta("Marca é obrigatória!");
            return;
        }
        if(categoria == null || categoria.isBlank()) {
            mostraAlerta("Selecione uma categoria!");
            return;
        }
        if(ValidacaoUtil.campoVazio(valorTexto)) {
            mostraAlerta("Informe o valor da diária!");
            return;
        }
        double valorDiaria;
        try {
            valorDiaria = Double.parseDouble(valorTexto);
            if (!ValidacaoUtil.valorPositivo(valorDiaria)) {
                mostraAlerta("O valor da diária deve ser maior que zero!");
                return;
            }
        } catch (NumberFormatException e) {
            mostraAlerta("Valor inválido! Use apenas números. Exemplo: 89.90 ou 89,90");
            return;
        }
        //salva no banco de dados
        try {
            Veiculo veiculo = new Veiculo();
            veiculo.setPlaca(placa);
            veiculo.setModelo(modelo);
            veiculo.setMarca(marca);
            veiculo.setCategoria(categoria);
            veiculo.setValorLocacao(valorDiaria);
            veiculoService.salvar(veiculo);

            mostraSucesso("Veículo cadastrado com sucesso!");
            limpar();
            carregarTabela();
        } catch (SQLException e) {
            mostraAlerta("Erro ao cadastrar veículo. Verifique se a placa já está cadastrada.");
        }
    }
    @FXML
    private void atualizar() {
        if (veiculoSelecionado == null) {
            mostraAlerta("Selecione um veículo para atualizar!");
            return;
        }
        String placa = campoPlaca.getText().trim().toUpperCase();
        String modelo = campoModelo.getText().trim();
        String marca = campoMarca.getText().trim();
        String categoria = comboCategoria.getValue();
        String valorTexto = campoValor.getText().trim().replace(",", ".");

        if(!ValidacaoUtil.placaValido(placa)) {
            mostraAlerta("Placa inválida! Use o formato: ABC1D23 ou ABC1234");
            return;
        }
        if(ValidacaoUtil.campoVazio(modelo)) {
            mostraAlerta("Modelo é obrigatório!");
            return;
        }
        if(ValidacaoUtil.campoVazio(marca)) {
            mostraAlerta("Marca é obrigatória!");
            return;
        }
        if(categoria == null || categoria.isBlank()) {
            mostraAlerta("Selecione uma categoria!");
            return;
        }
        if(ValidacaoUtil.campoVazio(valorTexto)) {
            mostraAlerta("Informe o valor da diária!");
            return;
        }
        double valorDiaria;
        try {
            valorDiaria = Double.parseDouble(valorTexto);

            if (!ValidacaoUtil.valorPositivo(valorDiaria)) {
                mostraAlerta("O valor da diária deve ser maior que zero!");
                return;
            }
        } catch (NumberFormatException e) {
            mostraAlerta("Valor inválido! Use apenas números. Exemplo: 89.90 ou 89,90");
            return;
        }
        try {
            veiculoSelecionado.setPlaca(placa);
            veiculoSelecionado.setModelo(modelo);
            veiculoSelecionado.setMarca(marca);
            veiculoSelecionado.setCategoria(categoria);
            veiculoSelecionado.setValorLocacao(valorDiaria);

            veiculoService.atualizar(veiculoSelecionado);

            mostraSucesso("Veículo atualizado com sucesso!");
            limpar();
            carregarTabela();
        } catch (SQLException e) {
            mostraAlerta("Erro ao atualizar veículo. Verifique se a placa já está cadastrada.");
        }
    }
    @FXML
    private void limpar() {
        campoPlaca.clear();
        campoModelo.clear();
        campoMarca.clear();
        comboCategoria.getSelectionModel().clearSelection();
        campoValor.clear();
        campoBusca.clear();

        veiculoSelecionado = null;
        tabelaVeiculo.getSelectionModel().clearSelection();
    }
    @FXML
    private void voltar() {
        try {
            NavegacaoUtil.trocarTela(btnVoltar, "/fxml/Home.fxml", "Locadora - Home");
        } catch (IOException e) {
            mostraAlerta("Erro ao voltar para a tela inicial.");
        }
    }
    // Preenche o formulário com os dados do veículo selecionado.
    private void preencherFormulario(Veiculo veiculo) {
        campoPlaca.setText(veiculo.getPlaca());
        campoModelo.setText(veiculo.getModelo());
        campoMarca.setText(veiculo.getMarca());
        comboCategoria.setValue(veiculo.getCategoria());
        campoValor.setText(String.format("%.2f", veiculo.getValorLocacao()).replace(".", ","));

    }
    // Busca os veículos no banco e atualiza a tabela.
    private void carregarTabela() {
        try {
            List<Veiculo> lista = veiculoService.listarTodos();
            listaVeiculos.setAll(lista);
            if (listaFiltrada == null) {
                listaFiltrada = new FilteredList<>(listaVeiculos, veiculo -> true);
                tabelaVeiculo.setItems(listaFiltrada);
            }
        } catch (SQLException e) {
            mostraAlerta("Erro ao carregar veículos. Tente novamente.");
        }
    }
    // Configura o filtro da tabela usando o texto digitado no campo de busca.
    private void configurarBusca() {
        listaFiltrada = new FilteredList<>(listaVeiculos, veiculo -> true);
        tabelaVeiculo.setItems(listaFiltrada);

        campoBusca.textProperty().addListener((obs, antigo, novo) -> {
            listaFiltrada.setPredicate(veiculo -> {
                if (novo == null || novo.trim().isEmpty()) {
                    return true;
                }
                String filtro = novo.toLowerCase().trim();
                return veiculo.getPlaca().toLowerCase().contains(filtro)
                        || veiculo.getModelo().toLowerCase().contains(filtro)
                        || veiculo.getMarca().toLowerCase().contains(filtro)
                        || veiculo.getCategoria().toLowerCase().contains(filtro)
                        || String.valueOf(veiculo.getValorLocacao()).contains(filtro)
                        || String.valueOf(veiculo.isDisponivel()).contains(filtro);

            });
        });
    }
    private void mostraAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Atenção");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    private void mostraSucesso(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
