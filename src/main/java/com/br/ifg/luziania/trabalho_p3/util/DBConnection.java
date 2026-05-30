package com.br.ifg.luziania.trabalho_p3.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // 🛡️ Sentinel: Database configuration is now strictly managed via environment variables.
    // Hardcoded credentials have been removed to prevent security leaks.
    private static final String URL = System.getenv("DB_URL");
    private static final String USUARIO = System.getenv("DB_USER");
    private static final String SENHA = System.getenv("DB_PASSWORD");

    private DBConnection() {}

    public static Connection getConexao() throws SQLException {
        if (URL == null || USUARIO == null || SENHA == null) {
            throw new SQLException("Configuração do banco de dados incompleta. " +
                    "Defina as variáveis de ambiente: DB_URL, DB_USER e DB_PASSWORD.");
        }
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
