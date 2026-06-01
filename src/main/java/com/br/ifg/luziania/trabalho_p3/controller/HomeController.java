package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.Locacao;
import com.br.ifg.luziania.trabalho_p3.model.Usuario;
import com.br.ifg.luziania.trabalho_p3.service.ClienteService;
import com.br.ifg.luziania.trabalho_p3.service.LocacaoService;
import com.br.ifg.luziania.trabalho_p3.service.VeiculoService;
import com.br.ifg.luziania.trabalho_p3.util.LogUtil;
import com.br.ifg.luziania.trabalho_p3.util.Sessao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class HomeController extends BaseController {
    private final ClienteService clienteService = new ClienteService();
    private final VeiculoService veiculoService = new VeiculoService();
    private final LocacaoService locacaoService = new LocacaoService();

    private final ObservableList<Locacao> locacoesAtivas = FXCollections.observableArrayList();
    private final DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private VBox sidebar;
    @FXML private Button btnVeiculos;
    @FXML private Button btnClientes;
    @FXML private Button btnLocacao;
    @FXML private Button btnDevolucao;
    @FXML private Button btnUsuarios;
    @FXML private Button btnSair;

    @FXML private Label labelBoasVindas;
    @FXML private Label labelPerfil;
    @FXML private Label labelClientesAtivos;
    @FXML private Label labelVeiculosDisponiveis;
    @FXML private Label labelLocacoesAtivas;

    @FXML private TableView<Locacao> tabelaLocacoesAtivas;
    @FXML private TableColumn<Locacao, String> colunaClienteLocacao;
    @FXML private TableColumn<Locacao, String> colunaVeiculoLocacao;
    @FXML private TableColumn<Locacao, String> colunaPlacaLocacao;
    @FXML private TableColumn<Locacao, String> colunaDevolucaoLocacao;

    private boolean sidebarExpandida = true;

    @FXML
    public void initialize() {
        carregarUsuarioLogado();
        aplicarPermissoes();
        configurarTabelaLocacoes();
        carregarResumoSistema();
        carregaLocacoesAtivas();
    }

    private void carregarUsuarioLogado() {
        Usuario usuario = Sessao.getUsuarioLogado();
        if (usuario == null) {
            labelBoasVindas.setText("Bem-vindo ao sistema");
            labelPerfil.setText("Perfil: -");
            return;
        }
        labelBoasVindas.setText("Olá, " + usuario.getNomeCompleto());
        labelPerfil.setText(usuario.getPerfil());
    }

    private void aplicarPermissoes() {
        Usuario usuario = Sessao.getUsuarioLogado();
        boolean admin = usuario != null && "ADMIN".equalsIgnoreCase(usuario.getPerfil());
        btnUsuarios.setVisible(admin);
        btnUsuarios.setManaged(admin);
    }

    @FXML private void abrirVeiculos() { abrirTela(btnVeiculos, "/fxml/Veiculo.fxml", "Locadora - Veículos"); }
    @FXML private void abrirClientes() { abrirTela(btnClientes, "/fxml/Cliente.fxml", "Locadora - Clientes"); }
    @FXML private void abrirLocacao() { abrirTela(btnLocacao, "/fxml/Locacao.fxml", "Locadora - Locação"); }
    @FXML private void abrirDevolucao() { abrirTela(btnDevolucao, "/fxml/Devolucao.fxml", "Locadora - Devolução"); }
    @FXML private void abrirUsuarios() { abrirTela(btnUsuarios, "/fxml/Usuario.fxml", "Locadora - Usuários"); }

    @FXML
    private void sair() {
        if (confirmarAcao("Sair", "Deseja realmente encerrar a sessão?")) {
            LogUtil.registrarAcao("LOGOUT");
            Sessao.encerrar();
            abrirTela(btnSair, "/fxml/Login.fxml", "Locadora - Login");
        }
    }

    @FXML
    private void toggleSidebar() {
        sidebarExpandida = !sidebarExpandida;
        if (sidebarExpandida) {
            sidebar.setPrefWidth(240);
            sidebar.getStyleClass().remove("sidebar-collapsed");
        } else {
            sidebar.setPrefWidth(70);
            sidebar.getStyleClass().add("sidebar-collapsed");
        }
    }

    private void carregarResumoSistema() {
        try {
            labelClientesAtivos.setText(String.valueOf(clienteService.contarAtivos()));
            labelVeiculosDisponiveis.setText(String.valueOf(veiculoService.contarDisponiveis()));
            labelLocacoesAtivas.setText(String.valueOf(locacaoService.contarAtivas()));
        } catch (SQLException e) {
            mostrarAlerta("Erro ao carregar resumo do sistema.");
        }
    }

    private void configurarTabelaLocacoes() {
        colunaClienteLocacao.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getCliente().getNome()));
        colunaVeiculoLocacao.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getVeiculo().getModelo()));
        colunaPlacaLocacao.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getVeiculo().getPlaca()));
        colunaDevolucaoLocacao.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getDataDevolucaoPrevista().format(formatadorData)));
        tabelaLocacoesAtivas.setItems(locacoesAtivas);
    }

    private void carregaLocacoesAtivas() {
        try {
            locacoesAtivas.setAll(locacaoService.listarAtivasResumo());
        } catch (SQLException e) {
            mostrarAlerta("Erro ao carregar locações ativas.");
        }
    }
}
