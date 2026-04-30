package com.br.ifg.luziania.trabalho_p3.dao;

import com.br.ifg.luziania.trabalho_p3.model.Cliente;
import com.br.ifg.luziania.trabalho_p3.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteDAO {
    //salva um novo cliente no banco de dados
    public void salvar(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente (nome, cpf, cnh, telefone, email)  VALUES (?, ?, ?, ?, ?)";

        Connection conn = DBConnection.getConexao();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, cliente.getNome());
        stmt.setString(2, cliente.getCpf());
        stmt.setString(3, cliente.getCnh());
        stmt.setString(4, cliente.getTelefone());
        stmt.setString(5, cliente.getEmail());
        stmt.executeUpdate();
    }
    //busca todos os dados do cliente a partir do cpf
    public Cliente buscarPorCpf(String cpf) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE cpf = ?";

        Connection conn = DBConnection.getConexao();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, cpf);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            Cliente c = new Cliente();
            c.setId(rs.getInt("id"));
            c.setNome(rs.getString("nome"));
            c.setCpf(rs.getString("cpf"));
            c.setCnh(rs.getString("cnh"));
            c.setTelefone(rs.getString("telefone"));
            c.setEmail(rs.getString("email"));
            c.setAtivo(rs.getBoolean("ativo"));
            return c;//retorna cliente
        }
        return null; //para clientes não encontrados
    }
}
