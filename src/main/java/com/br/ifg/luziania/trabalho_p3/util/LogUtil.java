package com.br.ifg.luziania.trabalho_p3.util;

import com.br.ifg.luziania.trabalho_p3.model.Usuario;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Responsável por registrar logs de uso e logs de erro do sistema.
// Os logs de uso ficam em logs/uso.log e os logs de erro ficam em logs/erros.log.
public class LogUtil {

    private static final String DIR = "logs";
    private static final String ARQ_USO = "logs/uso.log";
    private static final String ARQ_ERROS = "logs/erros.log";

    // Formato usado para registrar data e hora em cada linha de log.
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    static {
        criarDiretorioLogs();
    }

    private LogUtil() {
        // Evita instanciar classe utilitária.
    }

    // Cria a pasta de logs automaticamente caso ela ainda não exista.
    private static void criarDiretorioLogs() {
        try {
            if (!Files.exists(Paths.get(DIR))) {
                Files.createDirectories(Paths.get(DIR));
            }
        } catch (IOException e) {
            System.err.println("Erro ao criar pasta de logs: " + e.getMessage());
        }
    }

    // Registra uma ação simples executada por um usuário informado.
    public static void registrar(String acao, Usuario usuario) {
        registrar(acao, usuario, null);
    }

    // Registra uma ação com detalhes adicionais.
    // Usado para auditoria das principais operações do sistema.
    public static void registrar(String acao, Usuario usuario, String detalhes) {
        String linha = String.format(
                "[%s] ACAO='%s' USUARIO='%s' DETALHES='%s'",
                LocalDateTime.now().format(FMT),
                acao,
                usuario != null ? usuario.getEmail() : "anonimo",
                detalhes != null ? detalhes : "-"
        );

        gravar(ARQ_USO, linha);
    }

    // Registra uma ação usando automaticamente o usuário atualmente logado na sessão.
    public static void registrarAcao(String acao) {
        registrar(acao, Sessao.getUsuarioLogado(), null);
    }

    // Registra uma ação com detalhes usando automaticamente o usuário logado.
    public static void registrarAcao(String acao, String detalhes) {
        registrar(acao, Sessao.getUsuarioLogado(), detalhes);
    }

    // Registra uma exceção no arquivo de erros.
    // Inclui ação executada, usuário, descrição do erro e stack trace completo.
    public static void registrarErro(String acao, Usuario usuario, Exception ex) {
        String linha = String.format(
                "[%s] ERRO ACAO='%s' USUARIO='%s' DESCRICAO='%s'",
                LocalDateTime.now().format(FMT),
                acao,
                usuario != null ? usuario.getEmail() : "anonimo",
                ex.getMessage()
        );

        gravar(ARQ_ERROS, linha);
        gravar(ARQ_ERROS, stackTraceParaTexto(ex));
    }

    // Converte o stack trace da exceção para texto.
    // Isso facilita salvar o erro completo dentro do arquivo erros.log.
    private static String stackTraceParaTexto(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        ex.printStackTrace(pw);

        return sw.toString();
    }

    // Grava uma linha no arquivo informado.
    // O método é synchronized para evitar conflitos caso duas ações sejam registradas ao mesmo tempo.
    private static synchronized void gravar(String arquivo, String conteudo) {
        try (FileWriter fw = new FileWriter(arquivo, true);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println(conteudo);

        } catch (IOException e) {
            System.err.println("Erro ao gravar logs: " + e.getMessage());
        }
    }
}