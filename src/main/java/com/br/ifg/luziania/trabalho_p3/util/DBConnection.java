package com.br.ifg.luziania.trabalho_p3.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

// Centraliza a configuração e a criação de conexões com o banco de dados.
public class DBConnection {

    private static final String ARQUIVO_CONFIGURACAO = "/database.properties";
    private static final Properties propriedades = carregarPropriedades();

    private DBConnection() {
        // Evita instanciar classe utilitária.
    }

    public static Connection getConexao() throws SQLException {
        String url = montarUrl();

        String usuario = obterPropriedadeObrigatoria("db.user");
        String senha = obterPropriedadeObrigatoria("db.password");

        return DriverManager.getConnection(url, usuario, senha);
    }

    private static String montarUrl() {
        String driver = obterPropriedadeObrigatoria("db.driver");
        String host = obterPropriedadeObrigatoria("db.host");
        String porta = obterPropriedadeObrigatoria("db.port");
        String banco = obterPropriedadeObrigatoria("db.name");

        return "jdbc:" + driver + "://" + host + ":" + porta + "/" + banco;
    }

    private static Properties carregarPropriedades() {
        Properties props = new Properties();

        try (InputStream input = DBConnection.class.getResourceAsStream(ARQUIVO_CONFIGURACAO)) {
            if (input == null) {
                throw new IllegalStateException(
                        "Arquivo de configuração não encontrado: " + ARQUIVO_CONFIGURACAO
                );
            }

            props.load(input);
            return props;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Erro ao carregar o arquivo de configuração do banco de dados.",
                    e
            );
        }
    }

    private static String obterPropriedadeObrigatoria(String chave) {
        String valor = propriedades.getProperty(chave);

        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException(
                    "Propriedade obrigatória não configurada no database.properties: " + chave
            );
        }

        return valor.trim();
    }
}