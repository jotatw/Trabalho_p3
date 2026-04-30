package com.br.ifg.luziania.trabalho_p3.dao;

import com.br.ifg.luziania.trabalho_p3.model.Usuario;
import com.br.ifg.luziania.trabalho_p3.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {
    //busca todos os dados do usuario a partir do email
    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE email = ?";

        Connection connection = DBConnection.getConexao();
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            Usuario use =new Usuario();
            use.setEmail(rs.getString("email"));
            use.setNomeCompleto(rs.getString("nome"));
            use.setCpf(rs.getInt("cpf"));
            use.setSenha(rs.getString("senha_hash"));
            use.setPerfil(rs.getString("perfil"));
            use.setAtivo(rs.getBoolean("ativo"));
            return use;

        }
        return null; //para usuarios não encontrados
    }

}
