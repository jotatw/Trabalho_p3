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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

// Controller responsável pela tela Home.
// Carrega dados do dashboard, tabela de locações ativas, permissões e navegação principal.
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

    // Inicializa a tela Home assim que o FXML é carregado.
    @FXML
    public void initialize() {
        carregarUsuarioLogado();
        aplicarPermissoes();
        configurarTabelaLocacoes();
        carregarResumoSistema();
        carregarLocacoesAtivas();
    }

    // Exibe o nome e o perfil do usuário atualmente logado.
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

    // Aplica regras de permissão na tela.
    // Apenas usuários ADMIN podem acessar o gerenciamento de usuários.
    private void aplicarPermissoes() {
        Usuario usuario = Sessao.getUsuarioLogado();
        boolean admin = usuario != null && "ADMIN".equalsIgnoreCase(usuario.getPerfil());

        btnUsuarios.setVisible(admin);
        btnUsuarios.setManaged(admin);
    }

    // Abre a tela de gerenciamento de veículos.
    @FXML
    private void abrirVeiculos() {
        abrirTela(btnVeiculos, "/fxml/Veiculo.fxml", "Locadora - Veículos");
    }

    // Abre a tela de gerenciamento de clientes.
    @FXML
    private void abrirClientes() {
        abrirTela(btnClientes, "/fxml/Cliente.fxml", "Locadora - Clientes");
    }

    // Abre a tela de nova locação.
    @FXML
    private void abrirLocacao() {
        abrirTela(btnLocacao, "/fxml/Locacao.fxml", "Locadora - Locação");
    }

    // Abre a tela de devolução.
    @FXML
    private void abrirDevolucao() {
        abrirTela(btnDevolucao, "/fxml/Devolucao.fxml", "Locadora - Devolução");
    }

    // Abre a tela de gerenciamento de usuários.
    @FXML
    private void abrirUsuarios() {
        abrirTela(btnUsuarios, "/fxml/Usuario.fxml", "Locadora - Usuários");
    }

    // Encerra a sessão atual e retorna para a tela de login.
    @FXML
    private void sair() {
        if (confirmarAcao("Sair", "Deseja realmente encerrar a sessão?")) {
            LogUtil.registrarAcao("LOGOUT");
            Sessao.encerrar();
            abrirTela(btnSair, "/fxml/Login.fxml", "Locadora - Login");
        }
    }

    // Recolhe ou expande o menu lateral.
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

    // Carrega os números exibidos nos cards do dashboard.
    private void carregarResumoSistema() {
        try {
            labelClientesAtivos.setText(String.valueOf(clienteService.contarAtivos()));
            labelVeiculosDisponiveis.setText(String.valueOf(veiculoService.contarDisponiveis()));
            labelLocacoesAtivas.setText(String.valueOf(locacaoService.contarAtivas()));
        } catch (SQLException e) {
            mostrarAlerta("Erro ao carregar resumo do sistema.");
        }
    }

    // Configura as colunas da tabela de locações ativas.
    private void configurarTabelaLocacoes() {
        colunaClienteLocacao.setCellValueFactory(
                cd -> new SimpleStringProperty(cd.getValue().getCliente().getNome())
        );

        colunaVeiculoLocacao.setCellValueFactory(
                cd -> new SimpleStringProperty(cd.getValue().getVeiculo().getModelo())
        );

        colunaPlacaLocacao.setCellValueFactory(
                cd -> new SimpleStringProperty(cd.getValue().getVeiculo().getPlaca())
        );

        colunaDevolucaoLocacao.setCellValueFactory(
                cd -> new SimpleStringProperty(
                        cd.getValue().getDataDevolucaoPrevista().format(formatadorData)
                )
        );

        tabelaLocacoesAtivas.setItems(locacoesAtivas);
    }

    // Carrega as locações em andamento exibidas na tabela da Home.
    private void carregarLocacoesAtivas() {
        try {
            locacoesAtivas.setAll(locacaoService.listarAtivasResumo());
        } catch (SQLException e) {
            mostrarAlerta("Erro ao carregar locações ativas.");
        }
    }
}