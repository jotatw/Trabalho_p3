package com.br.ifg.luziania.trabalho_p3.dao;

import com.br.ifg.luziania.trabalho_p3.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Responsável pelo acesso à tabela usuario.
// Contém as operações de cadastro, consulta, atualização e exclusão de usuários.
public class UsuarioDAO extends BaseDAO {

    // Insere um novo usuário na tabela usuario.
    // A senha recebida aqui já deve estar em formato de hash BCrypt.
    public void salvar(Usuario usuario) throws SQLException {
        String sql = """
            INSERT INTO usuario (nome, email, senha_hash, perfil)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, usuario.getNomeCompleto());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getPerfil());

            stmt.executeUpdate();
        } catch (SQLException e) {
            registrarErro("UsuarioDAO.salvar", e);
            throw e;
        }
    }

    // Busca um usuário pelo e-mail.
    // Esse método retorna também a senha_hash, pois ela é necessária para validar o login.
    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = """
            SELECT id, nome, email, senha_hash, perfil, ativo
            FROM usuario
            WHERE email = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            registrarErro("UsuarioDAO.buscarPorEmail", e);
            throw e;
        }

        return null;
    }

    // Lista todos os usuários cadastrados, ordenados pelo nome.
    // A senha não é carregada nessa listagem por segurança.
    public List<Usuario> listarTodos() throws SQLException {
        String sql = """
            SELECT id, nome, email, perfil, ativo
            FROM usuario
            ORDER BY nome
            """;

        List<Usuario> lista = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearUsuarioSemSenha(rs));
                }
            }
        } catch (SQLException e) {
            registrarErro("UsuarioDAO.listarTodos", e);
            throw e;
        }

        return lista;
    }

    // Atualiza os dados de um usuário.
    // Quando a senha estiver vazia, mantém a senha já cadastrada no banco.
    public void atualizar(Usuario usuario) throws SQLException {
        boolean atualizarSenha = usuario.getSenha() != null && !usuario.getSenha().isBlank();

        String sql;

        if (atualizarSenha) {
            sql = """
                UPDATE usuario
                SET nome = ?, email = ?, senha_hash = ?, perfil = ?, ativo = ?
                WHERE id = ?
                """;
        } else {
            sql = """
                UPDATE usuario
                SET nome = ?, email = ?, perfil = ?, ativo = ?
                WHERE id = ?
                """;
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, usuario.getNomeCompleto());
            stmt.setString(2, usuario.getEmail());

            if (atualizarSenha) {
                stmt.setString(3, usuario.getSenha());
                stmt.setString(4, usuario.getPerfil());
                stmt.setBoolean(5, usuario.isAtivo());
                stmt.setInt(6, usuario.getId());
            } else {
                stmt.setString(3, usuario.getPerfil());
                stmt.setBoolean(4, usuario.isAtivo());
                stmt.setInt(5, usuario.getId());
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            registrarErro("UsuarioDAO.atualizar", e);
            throw e;
        }
    }

    // Remove fisicamente um usuário do banco.
    // No fluxo principal do sistema, a inativação é preferível à exclusão.
    public void deletar(Usuario usuario) throws SQLException {
        String sql = """
            DELETE FROM usuario
            WHERE id = ?
            """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, usuario.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            registrarErro("UsuarioDAO.deletar", e);
            throw e;
        }
    }

    // Converte uma linha do ResultSet em um objeto Usuario completo.
    // Usado no login, pois inclui a senha_hash.
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();

        usuario.setId(rs.getInt("id"));
        usuario.setNomeCompleto(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenha(rs.getString("senha_hash"));
        usuario.setPerfil(rs.getString("perfil"));
        usuario.setAtivo(rs.getBoolean("ativo"));

        return usuario;
    }

    // Converte uma linha do ResultSet em um objeto Usuario sem senha.
    // Usado para exibição na tabela de usuários.
    private Usuario mapearUsuarioSemSenha(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();

        usuario.setId(rs.getInt("id"));
        usuario.setNomeCompleto(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setPerfil(rs.getString("perfil"));
        usuario.setAtivo(rs.getBoolean("ativo"));

        return usuario;
    }
}