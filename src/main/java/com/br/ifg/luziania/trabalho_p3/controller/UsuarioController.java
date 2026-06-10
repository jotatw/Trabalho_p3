package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.Usuario;
import com.br.ifg.luziania.trabalho_p3.service.UsuarioService;
import com.br.ifg.luziania.trabalho_p3.util.ValidacaoUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

// Controller responsável pela tela de gerenciamento de usuários.
// Controla cadastro, atualização, inativação, busca e preenchimento da tabela.
public class UsuarioController extends BaseController {

    private final UsuarioService usuarioService = new UsuarioService();

    private Usuario usuarioSelecionado;

    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();
    private FilteredList<Usuario> listaFiltrada;

    @FXML private TextField campoNome;
    @FXML private TextField campoEmail;
    @FXML private TextField campoBusca;
    @FXML private PasswordField campoSenha;

    @FXML private ComboBox<String> cmbPerfil;

    @FXML private Button btnVoltar;

    @FXML private TableView<Usuario> tabelaUsuario;
    @FXML private TableColumn<Usuario, String> tabelaNome;
    @FXML private TableColumn<Usuario, String> tabelaEmail;
    @FXML private TableColumn<Usuario, String> tabelaPerfil;
    @FXML private TableColumn<Usuario, Boolean> tabelaAtivo;

    // Inicializa a tela de usuários.
    // Configura perfis, colunas da tabela, busca e seleção de registros.
    @FXML
    private void initialize() {
        configurarPerfis();
        configurarTabela();
        carregarTabela();
        configurarBusca();
        configurarSelecaoTabela();
    }

    // Define os perfis permitidos para os usuários do sistema.
    private void configurarPerfis() {
        cmbPerfil.setItems(FXCollections.observableArrayList("ADMIN", "ATENDENTE"));
    }

    // Configura quais atributos do Usuario serão exibidos em cada coluna.
    private void configurarTabela() {
        tabelaNome.setCellValueFactory(new PropertyValueFactory<>("nomeCompleto"));
        tabelaEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tabelaPerfil.setCellValueFactory(new PropertyValueFactory<>("perfil"));
        tabelaAtivo.setCellValueFactory(new PropertyValueFactory<>("ativo"));
    }

    // Quando o usuário seleciona uma linha na tabela,
    // os dados são carregados no formulário para edição ou inativação.
    private void configurarSelecaoTabela() {
        tabelaUsuario.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null) {
                usuarioSelecionado = selecionado;
                preencherFormulario(selecionado);
            }
        });
    }

    // Salva um novo usuário.
    // Para cadastro, a senha é obrigatória.
    @FXML
    private void salvar() {
        if (!validarCampos(true)) {
            return;
        }

        try {
            Usuario usuario = new Usuario();

            mapearFormulario(usuario, true);
            usuario.setAtivo(true);

            usuarioService.salvar(usuario);

            mostrarSucesso("Usuário criado!");
            limpar();
            carregarTabela();

        } catch (SQLException e) {
            mostrarErro("Erro ao salvar usuário.");
        }
    }

    // Atualiza o usuário selecionado na tabela.
    // A senha só é alterada se o campo de senha for preenchido.
    @FXML
    private void atualizar() {
        if (usuarioSelecionado == null) {
            mostrarAlerta("Selecione um usuário!");
            return;
        }

        if (!validarCampos(false)) {
            return;
        }

        try {
            mapearFormulario(usuarioSelecionado, false);

            usuarioService.atualizar(usuarioSelecionado);

            mostrarSucesso("Usuário atualizado!");
            limpar();
            carregarTabela();

        } catch (SQLException e) {
            mostrarErro("Erro ao atualizar usuário.");
        }
    }

    // Inativa o usuário selecionado.
    // A inativação bloqueia o acesso sem remover o registro do banco.
    @FXML
    private void inativar() {
        if (usuarioSelecionado == null) {
            mostrarAlerta("Selecione um usuário!");
            return;
        }

        if (confirmarAcao("Inativar", "Deseja desativar o acesso de " + usuarioSelecionado.getNomeCompleto() + "?")) {
            try {
                usuarioService.inativar(usuarioSelecionado);

                mostrarSucesso("Usuário inativado!");
                limpar();
                carregarTabela();

            } catch (SQLException e) {
                mostrarErro("Erro ao inativar usuário.");
            }
        }
    }

    // Limpa o formulário, o campo de busca e a seleção da tabela.
    @FXML
    private void limpar() {
        campoNome.clear();
        campoEmail.clear();
        campoSenha.clear();
        campoBusca.clear();

        cmbPerfil.setValue(null);

        usuarioSelecionado = null;
        tabelaUsuario.getSelectionModel().clearSelection();
    }

    // Retorna para a tela Home.
    @FXML
    private void voltarAction() {
        voltar(btnVoltar);
    }

    // Valida os campos do formulário.
    // Quando novaSenha for true, a senha é obrigatória.
    private boolean validarCampos(boolean novaSenha) {
        if (ValidacaoUtil.campoVazio(campoNome.getText())) {
            mostrarAlerta("Nome obrigatório!");
            return false;
        }

        if (!ValidacaoUtil.emailValido(campoEmail.getText())) {
            mostrarAlerta("E-mail inválido!");
            return false;
        }

        if (novaSenha && !ValidacaoUtil.senhaValida(campoSenha.getText())) {
            mostrarAlerta("Senha deve ter 6+ caracteres!");
            return false;
        }

        if (cmbPerfil.getValue() == null) {
            mostrarAlerta("Selecione o perfil!");
            return false;
        }

        return true;
    }

    // Copia os dados do formulário para o objeto Usuario.
    // No cadastro, a senha sempre é copiada; na atualização, só se for informada.
    private void mapearFormulario(Usuario usuario, boolean novoUsuario) {
        usuario.setNomeCompleto(campoNome.getText().trim());
        usuario.setEmail(campoEmail.getText().trim().toLowerCase());
        usuario.setPerfil(cmbPerfil.getValue());

        if (novoUsuario || !campoSenha.getText().isBlank()) {
            usuario.setSenha(campoSenha.getText().trim());
        }
    }

    // Preenche o formulário com os dados do usuário selecionado.
    // A senha não é exibida por segurança.
    private void preencherFormulario(Usuario usuario) {
        campoNome.setText(usuario.getNomeCompleto());
        campoEmail.setText(usuario.getEmail());
        cmbPerfil.setValue(usuario.getPerfil());
        campoSenha.clear();
    }

    // Carrega todos os usuários cadastrados no banco.
    private void carregarTabela() {
        try {
            listaUsuarios.setAll(usuarioService.listarTodos());
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar usuários.");
        }
    }

    // Configura o filtro de busca da tabela.
    // A busca considera nome, e-mail, perfil e status.
    private void configurarBusca() {
        listaFiltrada = new FilteredList<>(listaUsuarios, usuario -> true);
        tabelaUsuario.setItems(listaFiltrada);

        campoBusca.textProperty().addListener((obs, antigo, novo) -> {
            listaFiltrada.setPredicate(usuario -> {
                if (novo == null || novo.isEmpty()) {
                    return true;
                }

                String filtro = novo.toLowerCase().trim();

                return usuario.getNomeCompleto().toLowerCase().contains(filtro)
                        || usuario.getEmail().toLowerCase().contains(filtro)
                        || usuario.getPerfil().toLowerCase().contains(filtro)
                        || String.valueOf(usuario.isAtivo()).contains(filtro);
            });
        });
    }
}