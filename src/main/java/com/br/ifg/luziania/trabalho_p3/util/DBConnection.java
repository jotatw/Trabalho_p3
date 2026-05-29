package com.br.ifg.luziania.trabalho_p3.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = getEnvOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/locadora_db");
    private static final String USUARIO = getEnvOrDefault("DB_USER", "postgres");
    private static final String SENHA = getEnvOrDefault("DB_PASSWORD", "123456");

    private DBConnection() {}

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return value != null ? value : defaultValue;
    }

    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
