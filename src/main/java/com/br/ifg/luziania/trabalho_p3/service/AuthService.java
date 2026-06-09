package com.br.ifg.luziania.trabalho_p3.service;

import com.br.ifg.luziania.trabalho_p3.dao.UsuarioDAO;
import com.br.ifg.luziania.trabalho_p3.model.Usuario;
import com.br.ifg.luziania.trabalho_p3.util.LogUtil;
import com.br.ifg.luziania.trabalho_p3.util.SenhaUtil;

import java.sql.SQLException;

// Responsável pelas regras de autenticação do sistema.
// Valida e-mail, senha, status do usuário e registra as tentativas de login.
public class AuthService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Realiza o login a partir do e-mail e senha informados na tela.
    // Retorna o usuário autenticado quando os dados são válidos.
    // Retorna null quando o e-mail não existe, o usuário está inativo ou a senha está incorreta.
    public Usuario login(String email, String senha) throws SQLException {
        String emailTratado = email != null ? email.trim().toLowerCase() : "";
        String senhaTratada = senha != null ? senha.trim() : "";

        // Busca o usuário no banco usando o e-mail normalizado.
        Usuario usuario = usuarioDAO.buscarPorEmail(emailTratado);

        if (usuario == null) {
            LogUtil.registrar(
                    "LOGIN_FALHOU",
                    null,
                    "Usuário não encontrado. EMAIL=" + emailTratado
            );
            return null;
        }

        // Usuários inativos não podem acessar o sistema.
        if (!usuario.isAtivo()) {
            LogUtil.registrar(
                    "LOGIN_FALHOU",
                    usuario,
                    "Usuário inativo."
            );
            return null;
        }

        // Compara a senha digitada com o hash BCrypt armazenado no banco.
        if (!SenhaUtil.verificarSenha(senhaTratada, usuario.getSenha())) {
            LogUtil.registrar(
                    "LOGIN_FALHOU",
                    usuario,
                    "Senha incorreta."
            );
            return null;
        }

        // Login validado com sucesso.
        LogUtil.registrar("LOGIN_REALIZADO", usuario);
        return usuario;
    }
}