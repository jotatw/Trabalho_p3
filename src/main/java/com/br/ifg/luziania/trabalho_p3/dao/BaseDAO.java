package com.br.ifg.luziania.trabalho_p3.dao;

import com.br.ifg.luziania.trabalho_p3.util.DBConnection;
import com.br.ifg.luziania.trabalho_p3.util.LogUtil;
import com.br.ifg.luziania.trabalho_p3.util.Sessao;

import java.sql.Connection;
import java.sql.SQLException;

// Classe base dos DAOs.
// Centraliza a obtenção de conexão e o registro de erros de banco.
public abstract class BaseDAO {
    protected Connection getConnection() throws SQLException {
        return DBConnection.getConexao();
    }
    protected void registrarErro(String origem, Exception e) {
        LogUtil.registrarErro(origem, Sessao.getUsuarioLogado(), e);
    }
}
