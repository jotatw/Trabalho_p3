package com.br.ifg.luziania.trabalho_p3.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

// Responsável por centralizar a configuração e a criação de conexões com o banco de dados.
// A classe lê os dados do arquivo database.properties e monta a URL JDBC usada pelo PostgreSQL.
public class DBConnection {

    // Arquivo localizado em src/main/resources/database.properties.
    // O caminho começa com "/" porque o arquivo é carregado a partir da raiz dos resources.
    private static final String ARQUIVO_CONFIGURACAO = "/database.properties";

    // Carrega as configurações uma única vez quando a classe é inicializada.
    private static final Properties propriedades = carregarPropriedades();

    private DBConnection() {
        // Evita instanciar classe utilitária.
    }

    // Cria e retorna uma nova conexão com o banco de dados.
    // Cada DAO chama este método quando precisa executar uma operação no PostgreSQL.
    public static Connection getConexao() throws SQLException {
        String url = montarUrl();

        String usuario = obterPropriedadeObrigatoria("db.user");
        String senha = obterPropriedadeObrigatoria("db.password");

        return DriverManager.getConnection(url, usuario, senha);
    }

    // Monta a URL JDBC com base nas informações do arquivo database.properties.
    // Exemplo gerado: jdbc:postgresql://localhost:5432/locadora_db
    private static String montarUrl() {
        String driver = obterPropriedadeObrigatoria("db.driver");
        String host = obterPropriedadeObrigatoria("db.host");
        String porta = obterPropriedadeObrigatoria("db.port");
        String banco = obterPropriedadeObrigatoria("db.name");

        return "jdbc:" + driver + "://" + host + ":" + porta + "/" + banco;
    }

    // Lê o arquivo database.properties e carrega as suas chaves e valores.
    // Se o arquivo não existir, a aplicação é interrompida com uma mensagem clara.
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

    // Busca uma propriedade obrigatória no arquivo de configuração.
    // Caso a chave não exista ou esteja vazia, informa exatamente qual configuração falta.
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