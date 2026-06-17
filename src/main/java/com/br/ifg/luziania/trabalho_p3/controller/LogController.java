package com.br.ifg.luziania.trabalho_p3.controller;

import com.br.ifg.luziania.trabalho_p3.model.LogSistema;
import com.br.ifg.luziania.trabalho_p3.service.LogService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

// Controller responsável pela tela de visualização dos logs de uso.
// Permite consultar ações executadas no sistema.
public class LogController extends BaseController {

    private final LogService logService = new LogService();

    private final ObservableList<LogSistema> listaLogs = FXCollections.observableArrayList();
    private FilteredList<LogSistema> listaFiltrada;

    @FXML private TextField campoBusca;

    @FXML private Label labelTotalLogs;

    @FXML private TableView<LogSistema> tabelaLogs;
    @FXML private TableColumn<LogSistema, String> colunaDataHora;
    @FXML private TableColumn<LogSistema, String> colunaAcao;
    @FXML private TableColumn<LogSistema, String> colunaUsuario;
    @FXML private TableColumn<LogSistema, String> colunaDetalhes;

    @FXML private TextArea areaDetalhes;

    @FXML private Button btnVoltar;

    // Inicializa a tela de logs.
    // Configura tabela, busca, seleção e carrega os registros do arquivo.
    @FXML
    public void initialize() {
        configurarTabela();
        configurarBusca();
        configurarSelecaoTabela();
        carregarLogs();
    }

    // Define quais atributos do LogSistema serão exibidos em cada coluna.
    private void configurarTabela() {
        colunaDataHora.setCellValueFactory(new PropertyValueFactory<>("dataHora"));
        colunaAcao.setCellValueFactory(new PropertyValueFactory<>("acao"));
        colunaUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colunaDetalhes.setCellValueFactory(new PropertyValueFactory<>("detalhes"));
    }

    // Configura a busca por ação, usuário, detalhes ou data/hora.
    private void configurarBusca() {
        listaFiltrada = new FilteredList<>(listaLogs, log -> true);
        tabelaLogs.setItems(listaFiltrada);

        campoBusca.textProperty().addListener((obs, antigo, novo) -> {
            listaFiltrada.setPredicate(log -> {
                if (novo == null || novo.isBlank()) {
                    return true;
                }

                String filtro = novo.toLowerCase().trim();

                return log.getDataHora().toLowerCase().contains(filtro)
                        || log.getAcao().toLowerCase().contains(filtro)
                        || log.getUsuario().toLowerCase().contains(filtro)
                        || log.getDetalhes().toLowerCase().contains(filtro);
            });

            atualizarTotalLogs();
        });
    }

    // Mostra a linha completa do log quando o usuário seleciona um registro na tabela.
    private void configurarSelecaoTabela() {
        tabelaLogs.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null) {
                areaDetalhes.setText(selecionado.getLinhaCompleta());
            }
        });
    }

    // Carrega os logs de uso do arquivo logs/uso.log.
    @FXML
    private void carregarLogs() {
        try {
            listaLogs.setAll(logService.listarLogsUso());
            atualizarTotalLogs();
            areaDetalhes.clear();

        } catch (IOException e) {
            mostrarErro("Erro ao carregar logs de uso.");
        }
    }

    // Limpa o campo de busca e restaura a listagem.
    @FXML
    private void limparBusca() {
        campoBusca.clear();
        tabelaLogs.getSelectionModel().clearSelection();
        areaDetalhes.clear();
        atualizarTotalLogs();
    }

    // Atualiza o contador exibido na tela.
    private void atualizarTotalLogs() {
        labelTotalLogs.setText("Registros encontrados: " + listaFiltrada.size());
    }

    // Retorna para a tela Home.
    @FXML
    private void voltarAction() {
        voltar(btnVoltar);
    }
}