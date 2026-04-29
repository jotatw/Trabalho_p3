package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class UsuarioController {
    @FXML private TextField campoNome;
    @FXML private TextField campoEmail;
    @FXML private TextField campoSenha;
    @FXML private ComboBox<String> cmbPerfil;

    @FXML private TableView<Usuario> tabelaUsuario;
    @FXML private TableColumn<Usuario, String> tabelaNome;
    @FXML private TableColumn<Usuario, String> tabelaEmail;
    @FXML private TableColumn<Usuario, String> tabelaPerfil;

    @FXML
    private void initialize() {
        cmbPerfil.setItems(FXCollections.observableArrayList("ADMIN", "ATENDENTE"));
    }

    @FXML
    private void salvar(){
        System.out.println("salvando Usuario ...");
    }

    @FXML
    private void limpar(){
        campoNome.clear();
        campoEmail.clear();
        campoSenha.clear();
        cmbPerfil.setValue(null);
    }
}
