package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.Cliente;
import com.br.ifg.luziania.trabalho_p3.service.ClienteService;
import com.br.ifg.luziania.trabalho_p3.util.MascaraUtil;
import com.br.ifg.luziania.trabalho_p3.util.ValidacaoUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

public class ClienteController extends BaseController {
    private final ClienteService clienteService = new ClienteService();
    private Cliente clienteSelecionado;
    private final ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();
    private FilteredList<Cliente> listaFiltrada;

    @FXML private TextField campoNome, campoCpf, campoTelefone, campoEmail, campoCnh, campoBusca;
    @FXML private Button btnVoltar;

    @FXML private TableView<Cliente> tabelaCliente;
    @FXML private TableColumn<Cliente, String> colunaNome, colunaCpf, colunaTelefone, colunaEmail, colunaCnh;
    @FXML private TableColumn<Cliente, Boolean> colunaAtivo;

    @FXML
    public void initialize() {
        MascaraUtil.cpf(campoCpf);
        MascaraUtil.cnh(campoCnh);
        MascaraUtil.telefone(campoTelefone);

        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colunaCnh.setCellValueFactory(new PropertyValueFactory<>("cnh"));
        colunaTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        colunaEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colunaAtivo.setCellValueFactory(new PropertyValueFactory<>("ativo"));

        carregarTabela();
        configurarBusca();

        tabelaCliente.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null) {
                clienteSelecionado = selecionado;
                preencherFormulario(selecionado);
            }
        });
    }

    @FXML
    private void salvar() {
        if (validarCampos()) {
            try {
                Cliente cliente = new Cliente();
                mapearFormulario(cliente);
                clienteService.salvar(cliente);
                mostrarSucesso("Cliente salvo com sucesso!");
                limpar();
                carregarTabela();
            } catch (SQLException e) {
                mostrarErro("Erro ao salvar: " + e.getMessage());
            }
        }
    }

    @FXML
    private void atualizar() {
        if (clienteSelecionado == null) {
            mostrarAlerta("Selecione um cliente para atualizar!");
            return;
        }
        if (validarCampos()) {
            try {
                mapearFormulario(clienteSelecionado);
                clienteService.atualizar(clienteSelecionado);
                mostrarSucesso("Cliente atualizado com sucesso!");
                limpar();
                carregarTabela();
            } catch (SQLException e) {
                mostrarErro("Erro ao atualizar: " + e.getMessage());
            }
        }
    }

    @FXML
    private void inativar() {
        if (clienteSelecionado == null) {
            mostrarAlerta("Selecione um cliente para inativar!");
            return;
        }
        if (confirmarAcao("Confirmar Inativação", "Deseja inativar o cliente " + clienteSelecionado.getNome() + "?")) {
            try {
                clienteService.inativar(clienteSelecionado);
                mostrarSucesso("Cliente inativado com sucesso!");
                limpar();
                carregarTabela();
            } catch (SQLException e) {
                mostrarErro("Erro ao inativar cliente.");
            }
        }
    }

    @FXML
    private void limpar() {
        campoNome.clear();
        campoCpf.clear();
        campoCnh.clear();
        campoTelefone.clear();
        campoEmail.clear();
        campoBusca.clear();
        clienteSelecionado = null;
        tabelaCliente.getSelectionModel().clearSelection();
    }

    @FXML
    private void voltarAction() {
        voltar(btnVoltar);
    }

    private boolean validarCampos() {
        if (ValidacaoUtil.campoVazio(campoNome.getText())) { mostrarAlerta("Nome é obrigatório!"); return false; }
        if (!ValidacaoUtil.cpfValido(campoCpf.getText())) { mostrarAlerta("CPF inválido!"); return false; }
        if (!ValidacaoUtil.cnhValido(campoCnh.getText())) { mostrarAlerta("CNH inválida!"); return false; }
        if (!ValidacaoUtil.telefoneValido(campoTelefone.getText())) { mostrarAlerta("Telefone inválido!"); return false; }
        if (!ValidacaoUtil.emailValido(campoEmail.getText())) { mostrarAlerta("Email inválido!"); return false; }
        return true;
    }

    private void mapearFormulario(Cliente c) {
        c.setNome(campoNome.getText().trim());
        c.setCpf(campoCpf.getText().trim());
        c.setCnh(campoCnh.getText().trim());
        c.setTelefone(campoTelefone.getText().trim());
        c.setEmail(campoEmail.getText().trim());
    }

    private void preencherFormulario(Cliente c) {
        campoNome.setText(c.getNome());
        campoCpf.setText(c.getCpf());
        campoCnh.setText(c.getCnh());
        campoTelefone.setText(c.getTelefone());
        campoEmail.setText(c.getEmail());
    }

    private void carregarTabela() {
        try {
            listaClientes.setAll(clienteService.listarTodos());
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar clientes.");
        }
    }

    private void configurarBusca() {
        listaFiltrada = new FilteredList<>(listaClientes, p -> true);
        tabelaCliente.setItems(listaFiltrada);
        campoBusca.textProperty().addListener((obs, antigo, novo) -> {
            listaFiltrada.setPredicate(c -> {
                if (novo == null || novo.isEmpty()) return true;
                String f = novo.toLowerCase();
                return c.getNome().toLowerCase().contains(f) || c.getCpf().contains(f) || c.getEmail().toLowerCase().contains(f);
            });
        });
    }
}
