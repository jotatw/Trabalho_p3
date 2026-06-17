package com.br.ifg.luziania.trabalho_p3.service;

import com.br.ifg.luziania.trabalho_p3.model.LogSistema;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Responsável por ler e interpretar os logs de uso do sistema.
public class LogService {

    private static final Path ARQUIVO_USO = Path.of("logs", "uso.log");

    private static final Pattern PADRAO_LOG = Pattern.compile(
            "\\[(.*?)] ACAO='(.*?)' USUARIO='(.*?)' DETALHES='(.*?)'"
    );

    // Lê o arquivo logs/uso.log e converte cada linha em um objeto LogSistema.
    public List<LogSistema> listarLogsUso() throws IOException {
        if (!Files.exists(ARQUIVO_USO)) {
            return new ArrayList<>();
        }

        List<String> linhas = Files.readAllLines(ARQUIVO_USO);
        List<LogSistema> logs = new ArrayList<>();

        for (String linha : linhas) {
            logs.add(converterLinha(linha));
        }

        Collections.reverse(logs);

        return logs;
    }

    // Converte uma linha textual do arquivo de log em um objeto usado pela tabela.
    private LogSistema converterLinha(String linha) {
        Matcher matcher = PADRAO_LOG.matcher(linha);

        if (matcher.matches()) {
            return new LogSistema(
                    matcher.group(1),
                    matcher.group(2),
                    matcher.group(3),
                    matcher.group(4),
                    linha
            );
        }

        return new LogSistema(
                "-",
                "FORMATO_DESCONHECIDO",
                "-",
                linha,
                linha
        );
    }
}