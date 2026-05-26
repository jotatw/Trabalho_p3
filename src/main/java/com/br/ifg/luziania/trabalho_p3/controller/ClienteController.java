package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.Cliente;
import com.br.ifg.luziania.trabalho_p3.service.ClienteService;
import com.br.ifg.luziania.trabalho_p3.util.MascaraUtil;
import com.br.ifg.luziania.trabalho_p3.util.NavegacaoUtil;
import com.br.ifg.luziania.trabalho_p3.util.ValidacaoUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

// Controla a tela de gerenciamento de clientes:
// cadastro, atualização, inativação, busca e listagem.
public class ClienteController {
    // Service responsável pelas regras de negócio e acesso ao DAO.
    private final ClienteService clienteService = new ClienteService();
    // Guarda o cliente selecionado na tabela para atualizar ou inativar.
    private Cliente clienteSelecionado;
    // Lista principal carregada do banco e usada pela tabela.
    private final ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();
    // Lista filtrada usada pelo campo de busca.
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

    // Executado automaticamente ao abrir a tela.
    // Aplica máscaras nos campos de CPF, CNH e telefone.
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
        tabelaCliente.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        carregarTabela();
        configurarBusca();

        tabelaCliente.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null) {
                clienteSelecionado = selecionado;
                preencherFormulario(selecionado);
            }
        });
    }
    // Cadastra um novo cliente após validar os campos do formulário.
    @FXML
    private void salvar() {
        String nome = campoNome.getText().trim();
        String cpf = campoCpf.getText().trim();
        String cnh = campoCnh.getText().trim();
        String telefone = campoTelefone.getText().trim();
        String email = campoEmail.getText().trim();

        //validações
        if (ValidacaoUtil.campoVazio(nome)) {
            mostraAlerta("Nome é obrigatório!");
            return;
        }
        if (!ValidacaoUtil.cpfValido(cpf)) {
            mostraAlerta(("CPF inválido! use o formato: 000.000.000-00"));
            return;
        }
        if (!ValidacaoUtil.cnhValido(cnh)) {
            mostraAlerta("CNH inválida! informe 11 digitos numéricos");
            return;
        }
        if (ValidacaoUtil.campoVazio(telefone)) {
            mostraAlerta("Telefone é obrigatório!");
            return;
        }
        if (!ValidacaoUtil.telefoneValido(telefone)) {
            mostraAlerta("Telefone inválido! Use o formato: (00) 99999-9999 ou (00) 1111-2222");
            return;
        }
        if (ValidacaoUtil.campoVazio(email)) {
            mostraAlerta("Email é obrigatório!");
            return;
        }
        if (!ValidacaoUtil.emailValido(email)) {
            mostraAlerta("Email inválido!");
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
            NavegacaoUtil.trocarTela(btnVoltar, "/fxml/Home.fxml", "Locadora - Home");
        } catch (IOException e) {
            mostraAlerta("Erro ao voltar para a tela inicial.");
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
            mostraAlerta("Nome é obrigatório!");
            return;
        }
        if (!ValidacaoUtil.cpfValido(cpf)) {
            mostraAlerta(("CPF inválido! Use o formato: 000.000.000-00"));
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
            mostraAlerta("Email é obrigatorio!");
            return;
        }
        if (!ValidacaoUtil.emailValido(email)) {
            mostraAlerta("E-mail inválido!");
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
    // Inativa o cliente selecionado sem removê-lo fisicamente do banco.
    @FXML
    private void inativar() {
        if (clienteSelecionado == null) {
            mostraAlerta("Selecione um cliente para inativar!");
            return;
        }
        if (!clienteSelecionado.isAtivo()) {
            mostraAlerta("Cliente já está inativo!");
            return;
        }
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar inativação");
        confirmacao.setHeaderText(null);
        confirmacao.setContentText("Deseja realmente inativar o cliente " + clienteSelecionado.getNome() + "?");

        ButtonType resultado = confirmacao.showAndWait().orElse(ButtonType.CANCEL);
        if (resultado != ButtonType.OK) {
            return;
        }

        try {
           clienteService.inativar(clienteSelecionado);

           mostraSucesso("Cliente inativado com sucesso!");
           limpar();
           carregarTabela();
        } catch (SQLException e) {
            mostraAlerta("Erro ao inativar cliente. Tente novamente.");
        }
    }
    // Preenche o formulário com os dados do cliente selecionado.
    private void preencherFormulario(Cliente cliente) {
        campoNome.setText(cliente.getNome());
        campoCpf.setText(cliente.getCpf());
        campoCnh.setText(cliente.getCnh());
        campoTelefone.setText(cliente.getTelefone());
        campoEmail.setText(cliente.getEmail());
    }
    // Busca os clientes no banco e atualiza a tabela.
    private void carregarTabela() {
        try {
            List<Cliente> lista = clienteService.listarTodos();
            listaClientes.setAll(lista);
            if (listaFiltrada == null) {
                listaFiltrada = new FilteredList<>(listaClientes, cliente -> true);
                tabelaCliente.setItems(listaFiltrada);
            }
        } catch (SQLException e) {
            mostraAlerta("Erro ao carregar clientes. Tente novamente");
        }
    }
    // Configura o filtro da tabela usando o texto digitado no campo de busca.
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
