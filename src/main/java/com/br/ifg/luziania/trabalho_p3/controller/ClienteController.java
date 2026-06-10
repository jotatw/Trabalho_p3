package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.Cliente;
import com.br.ifg.luziania.trabalho_p3.service.ClienteService;
import com.br.ifg.luziania.trabalho_p3.util.MascaraUtil;
import com.br.ifg.luziania.trabalho_p3.util.ValidacaoUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

// Controller responsável pela tela de gerenciamento de clientes.
// Controla cadastro, atualização, inativação, busca e preenchimento da tabela.
public class ClienteController extends BaseController {

    private final ClienteService clienteService = new ClienteService();

    private Cliente clienteSelecionado;

    private final ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();
    private FilteredList<Cliente> listaFiltrada;

    @FXML private TextField campoNome;
    @FXML private TextField campoCpf;
    @FXML private TextField campoTelefone;
    @FXML private TextField campoEmail;
    @FXML private TextField campoCnh;
    @FXML private TextField campoBusca;

    @FXML private Button btnVoltar;

    @FXML private TableView<Cliente> tabelaCliente;
    @FXML private TableColumn<Cliente, String> colunaNome;
    @FXML private TableColumn<Cliente, String> colunaCpf;
    @FXML private TableColumn<Cliente, String> colunaTelefone;
    @FXML private TableColumn<Cliente, String> colunaEmail;
    @FXML private TableColumn<Cliente, String> colunaCnh;
    @FXML private TableColumn<Cliente, Boolean> colunaAtivo;

    // Inicializa a tela de clientes.
    // Configura máscaras, colunas da tabela, busca e seleção de registros.
    @FXML
    public void initialize() {
        MascaraUtil.cpf(campoCpf);
        MascaraUtil.cnh(campoCnh);
        MascaraUtil.telefone(campoTelefone);

        configurarTabela();
        carregarTabela();
        configurarBusca();
        configurarSelecaoTabela();
    }

    // Configura quais atributos do Cliente serão exibidos em cada coluna.
    private void configurarTabela() {
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colunaCnh.setCellValueFactory(new PropertyValueFactory<>("cnh"));
        colunaTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        colunaEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colunaAtivo.setCellValueFactory(new PropertyValueFactory<>("ativo"));
    }

    // Quando o usuário seleciona um cliente na tabela,
    // os dados são carregados no formulário para edição ou inativação.
    private void configurarSelecaoTabela() {
        tabelaCliente.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null) {
                clienteSelecionado = selecionado;
                preencherFormulario(selecionado);
            }
        });
    }

    // Salva um novo cliente após validar os campos obrigatórios.
    @FXML
    private void salvar() {
        if (!validarCampos()) {
            return;
        }

        try {
            Cliente cliente = new Cliente();

            mapearFormulario(cliente);
            clienteService.salvar(cliente);

            mostrarSucesso("Cliente salvo com sucesso!");
            limpar();
            carregarTabela();

        } catch (SQLException e) {
            mostrarErro("Erro ao salvar cliente.");
        }
    }

    // Atualiza o cliente selecionado na tabela.
    @FXML
    private void atualizar() {
        if (clienteSelecionado == null) {
            mostrarAlerta("Selecione um cliente para atualizar!");
            return;
        }

        if (!validarCampos()) {
            return;
        }

        try {
            mapearFormulario(clienteSelecionado);
            clienteService.atualizar(clienteSelecionado);

            mostrarSucesso("Cliente atualizado com sucesso!");
            limpar();
            carregarTabela();

        } catch (SQLException e) {
            mostrarErro("Erro ao atualizar cliente.");
        }
    }

    // Inativa o cliente selecionado.
    // O registro permanece no banco para preservar histórico.
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

    // Limpa o formulário, o campo de busca e a seleção da tabela.
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

    // Retorna para a tela Home.
    @FXML
    private void voltarAction() {
        voltar(btnVoltar);
    }

    // Valida os campos do formulário antes de salvar ou atualizar.
    private boolean validarCampos() {
        if (ValidacaoUtil.campoVazio(campoNome.getText())) {
            mostrarAlerta("Nome é obrigatório!");
            return false;
        }

        if (!ValidacaoUtil.cpfValido(campoCpf.getText())) {
            mostrarAlerta("CPF inválido!");
            return false;
        }

        if (!ValidacaoUtil.cnhValido(campoCnh.getText())) {
            mostrarAlerta("CNH inválida!");
            return false;
        }

        if (!ValidacaoUtil.telefoneValido(campoTelefone.getText())) {
            mostrarAlerta("Telefone inválido!");
            return false;
        }

        if (!ValidacaoUtil.emailValido(campoEmail.getText())) {
            mostrarAlerta("E-mail inválido!");
            return false;
        }

        return true;
    }

    // Copia os dados do formulário para o objeto Cliente.
    private void mapearFormulario(Cliente cliente) {
        cliente.setNome(campoNome.getText().trim());
        cliente.setCpf(campoCpf.getText().trim());
        cliente.setCnh(campoCnh.getText().trim());
        cliente.setTelefone(campoTelefone.getText().trim());
        cliente.setEmail(campoEmail.getText().trim().toLowerCase());
    }

    // Preenche o formulário com os dados do cliente selecionado na tabela.
    private void preencherFormulario(Cliente cliente) {
        campoNome.setText(cliente.getNome());
        campoCpf.setText(cliente.getCpf());
        campoCnh.setText(cliente.getCnh());
        campoTelefone.setText(cliente.getTelefone());
        campoEmail.setText(cliente.getEmail());
    }

    // Carrega todos os clientes cadastrados no banco.
    private void carregarTabela() {
        try {
            listaClientes.setAll(clienteService.listarTodos());
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar clientes.");
        }
    }

    // Configura o filtro de busca da tabela.
    // A busca considera nome, CPF, CNH, telefone, e-mail e status.
    private void configurarBusca() {
        listaFiltrada = new FilteredList<>(listaClientes, cliente -> true);
        tabelaCliente.setItems(listaFiltrada);

        campoBusca.textProperty().addListener((obs, antigo, novo) -> {
            listaFiltrada.setPredicate(cliente -> {
                if (novo == null || novo.isEmpty()) {
                    return true;
                }

                String filtro = novo.toLowerCase();

                return cliente.getNome().toLowerCase().contains(filtro)
                        || cliente.getCpf().toLowerCase().contains(filtro)
                        || cliente.getCnh().toLowerCase().contains(filtro)
                        || cliente.getTelefone().toLowerCase().contains(filtro)
                        || cliente.getEmail().toLowerCase().contains(filtro)
                        || String.valueOf(cliente.isAtivo()).contains(filtro);
            });
        });
    }
}