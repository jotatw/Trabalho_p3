package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.Cliente;
import com.br.ifg.luziania.trabalho_p3.service.ClienteService;
import com.br.ifg.luziania.trabalho_p3.util.MascaraUtil;
import com.br.ifg.luziania.trabalho_p3.util.ValidacaoUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ClienteController {
    private final ClienteService clienteService = new ClienteService();
    private Cliente clienteSelecionado;
    private ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();
    private FilteredList<Cliente> listaFiltrada;

    @FXML private TextField campoNome;
    @FXML private TextField campoCpf;
    @FXML private TextField campoTelefone;
    @FXML private TextField campoEmail;
    @FXML private TextField campoCnh;
    @FXML private TextField campoBusca;
    @FXML private Button btnVoltar;
    @FXML private Button btnAtualizar;
    @FXML private Button btnInativar;

    @FXML private TableView <Cliente> tabelaCliente;
    @FXML private TableColumn <Cliente, String> colunaNome;
    @FXML private TableColumn <Cliente, String> colunaCpf;
    @FXML private TableColumn <Cliente, String> colunaTelefone;
    @FXML private TableColumn <Cliente, String> colunaEmail;
    @FXML private TableColumn <Cliente, String> colunaCnh;
    @FXML private TableColumn <Cliente, Boolean> colunaAtivo;

    @FXML
    public void initialize() {
        //aplica as mascaras no campo de entrada
        MascaraUtil.cpf(campoCpf);
        MascaraUtil.cnh(campoCnh);
        MascaraUtil.telefone(campoTelefone);

        //configura cada coluna para pegar o atributo certo do model
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
        String nome = campoNome.getText().trim();
        String cpf = campoCpf.getText().trim();
        String cnh = campoCnh.getText().trim();
        String telefone = campoTelefone.getText().trim();
        String email = campoEmail.getText().trim();

        //validações
        if (ValidacaoUtil.campoVazio(nome)) {
            mostraAlerta("Nome e obrigatorio!");
            return;
        }
        if (!ValidacaoUtil.cpfValido(cpf)) {
            mostraAlerta(("CPF invalido!  use o formato: 000.000.000-00"));
            return;
        }
        if (!ValidacaoUtil.cnhValido(cnh)) {
            mostraAlerta("CNH invalida! informe 11 digitos numericos");
            return;
        }
        if (ValidacaoUtil.campoVazio(telefone)) {
            mostraAlerta("Telefone e obrigatorio!");
            return;
        }
        if (!ValidacaoUtil.telefoneValido(telefone)) {
            mostraAlerta("Telefone invalido! Use o formato: (00) 99999-9999 ou (00) 1111-2222");
            return;
        }
        if (ValidacaoUtil.campoVazio(email)) {
            mostraAlerta("Email e obrigatorio!");
            return;
        }
        if (!ValidacaoUtil.emailValido(email)) {
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

            clienteService.salvar(cliente);
            mostraSucesso("Cliente salvo com sucesso!");
            limpar();
            carregarTabela();
        } catch (SQLException e) {
            mostraAlerta("Erro ao salvar: " + e.getMessage());
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
    private void atualizar() {
        if (clienteSelecionado == null) {
            mostraAlerta("Selecione um cliente para atualizar!");
            return;
        }
        String nome = campoNome.getText().trim();
        String cpf = campoCpf.getText().trim();
        String cnh = campoCnh.getText().trim();
        String telefone = campoTelefone.getText().trim();
        String email = campoEmail.getText().trim();

        if (ValidacaoUtil.campoVazio(nome)) {
            mostraAlerta("Nome e obrigatorio!");
            return;
        }
        if (!ValidacaoUtil.cpfValido(cpf)) {
            mostraAlerta(("CPF invalido!  use o formato: 000.000.000-00"));
            return;
        }
        if (!ValidacaoUtil.cnhValido(cnh)) {
            mostraAlerta("CNH invalida! informe 11 digitos numericos");
            return;
        }
        if (ValidacaoUtil.campoVazio(telefone)) {
            mostraAlerta("Telefone e obrigatorio!");
            return;
        }
        if (!ValidacaoUtil.telefoneValido(telefone)) {
            mostraAlerta("Telefone invalido! Use o formato: (00) 99999-9999 ou (00) 1111-2222");
            return;
        }
        if (ValidacaoUtil.campoVazio(email)) {
            mostraAlerta("Email e obrigatorio!");
            return;
        }
        if (!ValidacaoUtil.emailValido(email)) {
            mostraAlerta("Email invalido!");
            return;
        }
        try {
            clienteSelecionado.setNome(nome);
            clienteSelecionado.setCpf(cpf);
            clienteSelecionado.setCnh(cnh);
            clienteSelecionado.setTelefone(telefone);
            clienteSelecionado.setEmail(email);

            clienteService.atualizar(clienteSelecionado);
            mostraSucesso("Cliente atualizado com sucesso!");
            limpar();
            carregarTabela();
        } catch (SQLException e) {
            mostraAlerta("Erro ao atualizar cliente. Verifique se CPF, CNH ou e-mail já estão cadastrados.");
        }
    }
    @FXML
    private void inativar() {
        if (clienteSelecionado == null) {
            mostraAlerta("Selecione um cliente para inativar!");
            return;
        }
        if (!clienteSelecionado.isAtivo()) {
            mostraAlerta("Cliente ja esta inativo!");
            return;
        }
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar inativação");
        confirmacao.setHeaderText(null);
        confirmacao.setContentText("Deseja realmente inativar o cliente " + clienteSelecionado.getNome() + "?");

        ButtonType resutado = confirmacao.showAndWait().orElse(ButtonType.CANCEL);
        if (resutado != ButtonType.OK) {
            return;
        }

        try {
           clienteSelecionado.setAtivo(false);
           clienteService.atualizar(clienteSelecionado);

           mostraSucesso("Cliente inativado com sucesso!");
           limpar();
           carregarTabela();
        } catch (SQLException e) {
            mostraAlerta("Erro ao inativar cliente. Tente novamente.");
        }
    }
    private void preencherFormulario(Cliente cliente) {
        campoNome.setText(cliente.getNome());
        campoCpf.setText(cliente.getCpf());
        campoCnh.setText(cliente.getCnh());
        campoTelefone.setText(cliente.getTelefone());
        campoEmail.setText(cliente.getEmail());
    }

    private void carregarTabela() {
        try {
            List<Cliente> lista = clienteService.listarTodos();
            listaClientes.setAll(lista);
            if (listaFiltrada == null) {
                listaFiltrada = new FilteredList<>(listaClientes, cliente -> true);
                tabelaCliente.setItems(listaFiltrada);
            }
        } catch (SQLException e) {
            mostraAlerta("Erro ao carregar cliente. Tente novamente");
        }
    }
    private void configurarBusca() {
        listaFiltrada = new FilteredList<>(listaClientes, cliente -> true);
        tabelaCliente.setItems(listaFiltrada);

        campoBusca.textProperty().addListener((obs, antigo, novo) -> {
            listaFiltrada.setPredicate(cliente -> {
                if (novo == null || novo.trim().isEmpty()) {
                    return true;
                }
                String filtro = novo.toLowerCase().trim();

                return cliente.getNome().toLowerCase().contains(filtro)
                        || cliente.getCpf().toLowerCase().contains(filtro)
                        || cliente.getCnh().toLowerCase().contains(filtro)
                        || cliente.getTelefone().toLowerCase().contains(filtro)
                        || cliente.getEmail().toLowerCase().contains(filtro)
                        || String.valueOf(cliente.isAtivo()).contains(filtro);
            });
        });
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
