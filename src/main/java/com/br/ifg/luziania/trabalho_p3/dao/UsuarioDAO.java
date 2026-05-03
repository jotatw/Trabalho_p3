package com.br.ifg.luziania.trabalho_p3.dao;

import com.br.ifg.luziania.trabalho_p3.model.Usuario;
import com.br.ifg.luziania.trabalho_p3.util.DBConnection;
import com.br.ifg.luziania.trabalho_p3.util.LogUtil;
import com.br.ifg.luziania.trabalho_p3.util.Sessao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {
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
}
