package com.br.ifg.luziania.trabalho_p3.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = System.getenv("DB_URL");
    private static final String USUARIO = System.getenv("DB_USER");
    private static final String SENHA = System.getenv("DB_PASSWORD");

    private DBConnection() {}

    public static Connection getConexao() throws SQLException {
        if (URL == null || USUARIO == null || SENHA == null) {
            throw new SQLException("Database connection parameters (DB_URL, DB_USER, DB_PASSWORD) must be provided as environment variables.");
        }
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
