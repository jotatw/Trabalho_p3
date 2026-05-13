package com.br.ifg.luziania.trabalho_p3.dao;

import com.br.ifg.luziania.trabalho_p3.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO extends BaseDAO {
    public void salvar(Usuario usuario) throws SQLException{
        String sql = """
            INSERT INTO usuario (nome, email, senha_hash, perfil) VALUES (?, ?, ?, ?)
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
    //busca todos os dados do usuario a partir do email
    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = """
            SELECT id, nome, email, senha_hash, perfil, ativo FROM usuario WHERE email = ?
            """;

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)
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
        return null; //para usuarios não encontrados
    }
    public List<Usuario> listarTodos() throws SQLException {
        String sql = """
            SELECT id, nome, email, perfil, ativo FROM usuario ORDER BY nome
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
        }catch (SQLException e) {
            registrarErro("UsuarioDAO.listarTodos", e);
            throw e;
        }
        return lista;
    }
    public void atualizar (Usuario usuario) throws SQLException {
        String sql = """
                UPDATE usuario SET nome = ?, email = ?, senha_hash = ?, perfil = ?, ativo = ? WHERE id = ?
                """;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, usuario.getNomeCompleto());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getPerfil());
            stmt.setBoolean(5, usuario.isAtivo());
            stmt.setInt(6, usuario.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            registrarErro("UsuarioDAO.atualizar", e);
            throw e;
        }
    }
    public void deletar(Usuario usuario) throws SQLException {
        String sql = """
            DELETE FROM usuario WHERE id = ?
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
