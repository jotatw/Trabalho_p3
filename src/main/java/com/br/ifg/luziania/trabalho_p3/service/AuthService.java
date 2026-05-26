package com.br.ifg.luziania.trabalho_p3.service;

import com.br.ifg.luziania.trabalho_p3.model.Usuario;
import com.br.ifg.luziania.trabalho_p3.dao.UsuarioDAO;
import com.br.ifg.luziania.trabalho_p3.util.LogUtil;
import com.br.ifg.luziania.trabalho_p3.util.SenhaUtil;

import java.sql.SQLException;

public class AuthService {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario login(String email, String senha) throws SQLException {
        //busco usuario pelo email
        String emailTratado = email != null ? email.trim().toLowerCase() : "";
        String senhaTratada = senha != null ? senha.trim() : "";

        Usuario usuario = usuarioDAO.buscarPorEmail(emailTratado);

        //se não for encontrado retorna null
        if (usuario == null) {
            LogUtil.registrar("LOGIN_FALHOU", null, "Usuario nao encontrado. EMAIL: " + emailTratado);
            return null;
        }
        //verifica se o usuario esta ativo
        if(!usuario.isAtivo()){
            LogUtil.registrar("LOGIN_FALHOU", usuario, "Usuario não esta ativo");
            return null;
        }

        //verifica senha
        if (!SenhaUtil.verificarSenha(senhaTratada, usuario.getSenha())) {
            LogUtil.registrar("LOGIN_FALHOU", usuario, "Senha incorreta");
            return null;
        }
        //login bem sucedido
        LogUtil.registrar("LOGIN_REALIZADO", usuario);
        return usuario;
    }
}
