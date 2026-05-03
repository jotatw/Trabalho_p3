package com.br.ifg.luziania.trabalho_p3.service;

import com.br.ifg.luziania.trabalho_p3.model.Usuario;
import com.br.ifg.luziania.trabalho_p3.dao.UsuarioDAO;
import com.br.ifg.luziania.trabalho_p3.util.LogUtil;
import java.sql.SQLException;

public class AuthService {
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario login(String email, String senha) throws SQLException {
        //busco usuario pelo email
        Usuario usuario = usuarioDAO.buscarPorEmail(email);

        //se não for encontrado retorna null
        if (usuario == null) {
            return null;
        }

        //verifica se o usuario esta ativo
        if(!usuario.isAtivo()){
            return null;
        }

        //verifica senha
        if (!usuario.getSenha().equals(senha)) {
            return null;
        }
        //login bem sucedido
        LogUtil.registrar("LOGIN", usuario);
        return usuario;
    }
}
