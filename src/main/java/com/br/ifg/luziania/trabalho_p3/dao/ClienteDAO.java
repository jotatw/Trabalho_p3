package com.br.ifg.luziania.trabalho_p3.dao;

import com.br.ifg.luziania.trabalho_p3.model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Responsável pelo acesso à tabela cliente.
// Contém as operações de cadastro, consulta, atualização, inativação e exclusão.
public class ClienteDAO extends BaseDAO {

    // Insere um novo cliente na tabela cliente.
    public void salvar(Cliente cliente) throws SQLException {
        String sql = """
            INSERT INTO cliente (nome, cpf, cnh, telefone, email, ativo)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getCnh());
            stmt.setString(4, cliente.getTelefone());
            stmt.setString(5, cliente.getEmail());
            stmt.setBoolean(6, cliente.isAtivo());

            stmt.executeUpdate();
        } catch (SQLException e) {
            registrarErro("ClienteDAO.salvar", e);
            throw e;
        }
    }

    // Busca um cliente pelo CPF.
    // Retorna null quando nenhum cliente for encontrado.
    public Cliente buscarPorCpf(String cpf) throws SQLException {
        String sql = """
            SELECT id, nome, cpf, cnh, telefone, email, ativo
            FROM cliente
            WHERE cpf = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, cpf);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
            }
        } catch (SQLException e) {
            registrarErro("ClienteDAO.buscarPorCpf", e);
            throw e;
        }

        return null;
    }

    // Lista todos os clientes cadastrados, ordenados pelo nome.
    public List<Cliente> listarTodos() throws SQLException {
        String sql = """
            SELECT id, nome, cpf, cnh, telefone, email, ativo
            FROM cliente
            ORDER BY nome
            """;

        List<Cliente> lista = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCliente(rs));
                }
            }
        } catch (SQLException e) {
            registrarErro("ClienteDAO.listarTodos", e);
            throw e;
        }

        return lista;
    }

    // Atualiza todos os dados cadastrais do cliente.
    public void atualizar(Cliente cliente) throws SQLException {
        String sql = """
            UPDATE cliente
            SET nome = ?, cpf = ?, cnh = ?, telefone = ?, email = ?, ativo = ?
            WHERE id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getCnh());
            stmt.setString(4, cliente.getTelefone());
            stmt.setString(5, cliente.getEmail());
            stmt.setBoolean(6, cliente.isAtivo());
            stmt.setInt(7, cliente.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            registrarErro("ClienteDAO.atualizar", e);
            throw e;
        }
    }

    // Inativa o cliente sem remover o seu registro do banco.
    // Essa abordagem preserva o histórico de locações relacionadas ao cliente.
    public void inativar(Cliente cliente) throws SQLException {
        String sql = """
            UPDATE cliente
            SET ativo = false
            WHERE id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, cliente.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            registrarErro("ClienteDAO.inativar", e);
            throw e;
        }
    }

    // Remove fisicamente um cliente do banco.
    // No fluxo principal do sistema, a inativação é preferível à exclusão.
    public void deletar(Cliente cliente) throws SQLException {
        String sql = """
            DELETE FROM cliente
            WHERE id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, cliente.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            registrarErro("ClienteDAO.deletar", e);
            throw e;
        }
    }

    // Conta quantos clientes estão ativos.
    // Usado no dashboard da tela Home.
    public int contarAtivos() throws SQLException {
        String sql = """
            SELECT COUNT(*) AS total
            FROM cliente
            WHERE ativo = true
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            registrarErro("ClienteDAO.contarAtivos", e);
            throw e;
        }

        return 0;
    }

    // Converte uma linha retornada pelo ResultSet em um objeto Cliente.
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();

        cliente.setId(rs.getInt("id"));
        cliente.setNome(rs.getString("nome"));
        cliente.setCpf(rs.getString("cpf"));
        cliente.setCnh(rs.getString("cnh"));
        cliente.setTelefone(rs.getString("telefone"));
        cliente.setEmail(rs.getString("email"));
        cliente.setAtivo(rs.getBoolean("ativo"));

        return cliente;
    }
}