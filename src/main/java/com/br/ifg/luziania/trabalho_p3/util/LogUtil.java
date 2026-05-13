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

public class LogUtil {
    private static final String DIR = "logs";
    private static final String ARQ_USO = "logs/uso.log";
    private static final String ARQ_ERROS = "logs/erros.log";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    static {
        try {
            if (!Files.exists(Paths.get(DIR))) {
                Files.createDirectories(Paths.get(DIR));
            }
        } catch (IOException e) {
            System.err.println("Erro ao criar pasta de logs: " + e.getMessage());
        }
    }
    public static void registrar(String acao, Usuario usuario) {
        registrar(acao, usuario, null);
    }
    public static void registrar(String acao, Usuario usuario, String detalhes) {
       String linha = String.format("[%s] ACAO='%s' USUARIO='%s' DETALHES='%s'",
               LocalDateTime.now().format(FMT),
               acao,
               usuario != null ? usuario.getEmail() : "anonimo",
               detalhes != null ? detalhes : "-");
       gravar(ARQ_USO, linha);
    }
    public static void registrarAcao(String acao) {
        registrar(acao, Sessao.getUsuarioLogado(), null);
    }
    public static void registrarAcao(String acao, String detalhes) {
        registrar(acao, Sessao.getUsuarioLogado(), detalhes);
    }
    public static void registrarErro(String acao,Usuario usuario, Exception ex) {
        String linha = String.format("[%s] ERRO acao='%s' usuario='%s' descricao='%s'",
                LocalDateTime.now().format(FMT),
                acao, usuario != null ? usuario.getEmail() : "anonimo",
                ex.getMessage());
        gravar(ARQ_ERROS, linha);
        gravar(ARQ_ERROS, stackTraceParaTexto(ex));
    }
    private static String stackTraceParaTexto(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }
    private static synchronized void gravar(String arquivo, String conteudo) {
        try (FileWriter fw = new FileWriter(arquivo, true);
        PrintWriter pw = new PrintWriter(fw)) {
            pw.println(conteudo);
        } catch (IOException e) {
            System.err.println("Erro ao gravar logs: " + e.getMessage());
        }
    }
}
