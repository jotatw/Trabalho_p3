package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.Usuario;
import com.br.ifg.luziania.trabalho_p3.service.UsuarioService;
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
// Controla a tela de gerenciamento de usuários:
// cadastro, atualização, inativação, busca e listagem.
public class UsuarioController {
    private final UsuarioService usuarioService = new UsuarioService();
    // Guarda o usuário selecionado na tabela para atualizar ou inativar.
    private Usuario usuarioSelecionado;
    // Lista principal carregada do banco e usada pela tabela.
    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();
    // Lista filtrada usada pelo campo de busca.
    private FilteredList<Usuario> listaFiltrada;

    @FXML private TextField campoNome;
    @FXML private TextField campoEmail;
    @FXML private TextField campoBusca;
    @FXML private PasswordField campoSenha;
    @FXML private ComboBox<String> cmbPerfil;
    @FXML private Button btnVoltar;
    @FXML private Button btnSalvar;
    @FXML private Button btnAtualizar;
    @FXML private Button btnInativar;
    @FXML private TableColumn<Usuario, Boolean> tabelaAtivo;

    @FXML private TableView<Usuario> tabelaUsuario;
    @FXML private TableColumn<Usuario, String> tabelaNome;
    @FXML private TableColumn<Usuario, String> tabelaEmail;
    @FXML private TableColumn<Usuario, String> tabelaPerfil;
    // Executado automaticamente ao abrir a tela.
    // Configura ComboBox, colunas, tabela, busca e seleção.
    @FXML
    private void initialize() {
        cmbPerfil.setItems(FXCollections.observableArrayList("ADMIN", "ATENDENTE"));
        // Liga cada coluna da tabela ao atributo correspondente do model Usuario.
        tabelaNome.setCellValueFactory(new PropertyValueFactory<>("nomeCompleto"));
        tabelaEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tabelaPerfil.setCellValueFactory(new PropertyValueFactory<>("perfil"));
        tabelaAtivo.setCellValueFactory(new PropertyValueFactory<>("ativo"));
        carregarTabela();
        configurarBusca();

        tabelaUsuario.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null) {
                usuarioSelecionado = selecionado;
                preencherFormulario(selecionado);
            }
        });
    }
    // Cadastra um novo usuário após validar os campos do formulário.
    @FXML
    private void salvar(){
        String nome = campoNome.getText().trim();
        String email = campoEmail.getText().trim();
        String senha = campoSenha.getText().trim();
        String perfil = cmbPerfil.getValue();

        //validações
        if (ValidacaoUtil.campoVazio(nome)) {
            mostraAlerta("Nome é obrigatório!");
            return;
        }
        if (!ValidacaoUtil.emailValido(email)) {
            mostraAlerta("E-mail inválido ou obrigatório!");
            return;
        }
        if (!ValidacaoUtil.senhaValida(senha)) {
            mostraAlerta("Senha obrigatória e deve ter pelo menos 6 caracteres!");
            return;
        }
        if (perfil == null) {
            mostraAlerta("Selecione o perfil do usuário!");
            return;
        }
        //salva no banco de dados
        try {
            Usuario usuario = new Usuario();
            usuario.setNomeCompleto(nome);
            usuario.setEmail(email);
            usuario.setSenha(senha);
            usuario.setPerfil(perfil);
            usuario.setAtivo(true);

            usuarioService.salvar(usuario);
            mostraSucesso("Usuario salvo com sucesso!");
            limpar();
            carregarTabela();
        } catch (SQLException e) {
            mostraAlerta("Erro ao salvar usuário. Verifique se o e-mail já está cadastrado.");
        }
    }
    // Atualiza os dados do usuário selecionado.
    // A senha só será alterada se o campo senha for preenchido.
    @FXML
    private void atualizar(){
        if (usuarioSelecionado == null) {
            mostraAlerta("Selecione um usuário para atualizar!");
            return;
        }
        String nome = campoNome.getText().trim();
        String email = campoEmail.getText().trim();
        String senha = campoSenha.getText().trim();
        String perfil = cmbPerfil.getValue();

        if (ValidacaoUtil.campoVazio(nome)) {
            mostraAlerta("Nome é obrigatório!");
            return;
        }
        if (!ValidacaoUtil.emailValido(email)) {
            mostraAlerta("E-mail inválido ou obrigatório!");
            return;
        }
        if (!senha.isBlank() && !ValidacaoUtil.senhaValida(senha)) {
            mostraAlerta("A nova senha deve ter pelo menos 6 caracteres!");
            return;
        }
        if (perfil == null) {
            mostraAlerta("Selecione o perfil do usuário!");
            return;
        }
        try {
            usuarioSelecionado.setNomeCompleto(nome);
            usuarioSelecionado.setEmail(email);
            usuarioSelecionado.setPerfil(perfil);
            usuarioSelecionado.setSenha(senha);

            usuarioService.atualizar(usuarioSelecionado);

            mostraSucesso("Usuário atualizado com sucesso!");
            limpar();
            carregarTabela();
        } catch (SQLException e) {
            mostraAlerta("Erro ao atualizar usuário. Verifique se o e-mail já está cadastrado.");
        }
    }
    // Inativa o usuário selecionado sem removê-lo fisicamente do banco.
    @FXML
    private void inativar(){
        if (usuarioSelecionado == null) {
            mostraAlerta("Selecione um usuário para inativar!");
            return;
        }
        if (!usuarioSelecionado.isAtivo()) {
            mostraAlerta("Usuário já está inativo!");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar inativação");
        confirmacao.setHeaderText(null);
        confirmacao.setContentText("Deseja realmente inativar o usuário " + usuarioSelecionado.getNomeCompleto() + "?");

        ButtonType resultado = confirmacao.showAndWait().orElse(ButtonType.CANCEL);
        if (resultado != ButtonType.OK) {
            return;
        }
        try {
            usuarioSelecionado.setAtivo(false);
            usuarioSelecionado.setSenha("");
            usuarioService.atualizar(usuarioSelecionado);

            mostraSucesso("Usuário inativado com sucesso!");
            limpar();
            carregarTabela();
        } catch (SQLException e) {
            mostraAlerta("Erro ao inativar usuário. Tente novamente.");
        }
    }
    @FXML
    private void limpar(){
        campoNome.clear();
        campoEmail.clear();
        campoSenha.clear();
        campoBusca.clear();
        cmbPerfil.setValue(null);

        usuarioSelecionado = null;
        tabelaUsuario.getSelectionModel().clearSelection();
    }
    @FXML
    private void voltar() {
        try {
            NavegacaoUtil.trocarTela(btnVoltar, "/fxml/Home.fxml", "Locadora - Home");
        } catch (IOException e) {
            mostraAlerta("Erro ao voltar para a tela inicial.");
        }
    }
    private void carregarTabela() {
        try {
            List<Usuario> lista = usuarioService.listarTodos();
            listaUsuarios.setAll(lista);
            if (listaFiltrada == null) {
                listaFiltrada = new FilteredList<>(listaUsuarios, usuario -> true);
                tabelaUsuario.setItems(listaFiltrada);
            }
        } catch (SQLException e) {
            mostraAlerta("Erro ao carregar usuários. Tente novamente.");
        }
    }
    // Preenche o formulário com os dados do usuário selecionado.
    // A senha não é preenchida por segurança.
    private void preencherFormulario(Usuario usuario) {
        campoNome.setText(usuario.getNomeCompleto());
        campoEmail.setText(usuario.getEmail());
        cmbPerfil.setValue(usuario.getPerfil());
        campoSenha.clear();
    }
    // Configura o filtro da tabela usando o texto digitado no campo de busca.
    private void configurarBusca() {
        listaFiltrada = new FilteredList<>(listaUsuarios, usuario -> true);
        tabelaUsuario.setItems(listaFiltrada);

        campoBusca.textProperty().addListener((obs, antigo, novo) -> {
            listaFiltrada.setPredicate(usuario -> {
                if (novo == null || novo.trim().isEmpty()) {
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
