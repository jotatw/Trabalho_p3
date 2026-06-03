package com.br.ifg.luziania.trabalho_p3.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Centraliza a criação de conexões com o banco de dados PostgreSQL.
public class DBConnection {

    private static final String DB_HOST = lerVariavel("LOCADORA_DB_HOST", "localhost");
    private static final String DB_PORTA = lerVariavel("LOCADORA_DB_PORTA", "5432");
    private static final String DB_NOME = lerVariavel("LOCADORA_DB_NOME", "locadora_db");

    private static final String URL = "jdbc:postgresql://" + DB_HOST + ":" + DB_PORTA + "/" + DB_NOME;

    private static final String USUARIO = lerVariavelObrigatoria("LOCADORA_DB_USUARIO");
    private static final String SENHA = lerVariavelObrigatoria("LOCADORA_DB_SENHA");

    private DBConnection() {
        // Evita instanciar classe utilitária.
    }

    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    private static String lerVariavel(String nome, String valorPadrao) {
        String valor = System.getenv(nome);

        if (valor == null || valor.isBlank()) {
            return valorPadrao;
        }

        return valor.trim();
    }

    private static String lerVariavelObrigatoria(String nome) {
        String valor = System.getenv(nome);

        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException(
                    "Variável de ambiente obrigatória não configurada: " + nome +
                            ". Configure LOCADORA_DB_USUARIO e LOCADORA_DB_SENHA antes de executar o sistema."
            );
        }

        return valor.trim();
    }
}
