package com.br.ifg.luziania.trabalho_p3.dao;

import com.br.ifg.luziania.trabalho_p3.model.Usuario;
import com.br.ifg.luziania.trabalho_p3.util.DBConnection;
import com.br.ifg.luziania.trabalho_p3.util.LogUtil;
import com.br.ifg.luziania.trabalho_p3.util.Sessao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    public void salvar(Usuario usuario) throws SQLException{
        String sql = "INSERT INTO usuario (nome, email, senha_hash, perfil) VALUES (?, ?, ?, ?)";

        try {
            Connection conn = DBConnection.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, usuario.getNomeCompleto());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getPerfil());
            stmt.executeUpdate();
        } catch (SQLException e) {
            LogUtil.registrarErro("UsuarioDAO.salvar", Sessao.getUsuarioLogado(), e);
            throw e;
        }
    }
    //busca todos os dados do usuario a partir do email
    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE email = ?";

        try {
            Connection connection = DBConnection.getConexao();
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario use =new Usuario();
                use.setId(rs.getInt("id"));
                use.setEmail(rs.getString("email"));
                use.setNomeCompleto(rs.getString("nome"));
                use.setSenha(rs.getString("senha_hash"));
                use.setPerfil(rs.getString("perfil"));
                use.setAtivo(rs.getBoolean("ativo"));
                return use;
            }
        } catch (SQLException e) {
            LogUtil.registrarErro("UsuarioDAO.buscarPorEmail", Sessao.getUsuarioLogado(), e);
            throw e;
        }
        return null; //para usuarios não encontrados
    }
    public List<Usuario> listarTodos() throws SQLException {
        String sql = "SELECT * FROM usuario ORDER BY nome";
        List<Usuario> lista = new ArrayList<>();

        try {
            Connection conn = DBConnection.getConexao();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Usuario u =new Usuario();
                u.setId(rs.getInt("id"));
                u.setNomeCompleto(rs.getString("nome"));
                u.setEmail(rs.getString("email"));
                u.setPerfil(rs.getString("perfil"));
                u.setAtivo(rs.getBoolean("ativo"));
                lista.add(u);
            }
        } catch (SQLException e) {
            LogUtil.registrarErro("UsuarioDAO.listarTodos", Sessao.getUsuarioLogado(), e);
            throw e;
        }
        return lista;
    }
}
