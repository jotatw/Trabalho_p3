package com.br.ifg.luziania.trabalho_p3.dao;

import com.br.ifg.luziania.trabalho_p3.util.DBConnection;
import com.br.ifg.luziania.trabalho_p3.util.LogUtil;
import com.br.ifg.luziania.trabalho_p3.util.Sessao;

import java.sql.Connection;
import java.sql.SQLException;

// Classe base para todos os DAOs do sistema.
// Centraliza a obtenção de conexão e o registro de erros de banco de dados.
public abstract class BaseDAO {

    // Retorna uma nova conexão com o banco de dados.
    // Os DAOs filhos usam este método para executar comandos SQL.
    protected Connection getConnection() throws SQLException {
        return DBConnection.getConexao();
    }

    // Registra erros ocorridos na camada DAO.
    // O usuário logado é incluído automaticamente no log quando existir sessão ativa.
    protected void registrarErro(String origem, Exception e) {
        LogUtil.registrarErro(origem, Sessao.getUsuarioLogado(), e);
    }
}