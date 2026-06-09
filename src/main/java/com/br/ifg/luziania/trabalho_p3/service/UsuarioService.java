package com.br.ifg.luziania.trabalho_p3.service;

import com.br.ifg.luziania.trabalho_p3.dao.UsuarioDAO;
import com.br.ifg.luziania.trabalho_p3.model.Usuario;
import com.br.ifg.luziania.trabalho_p3.util.LogUtil;
import com.br.ifg.luziania.trabalho_p3.util.SenhaUtil;

import java.sql.SQLException;
import java.util.List;

// Responsável pelas regras de negócio relacionadas aos usuários do sistema.
// Controla cadastro, atualização, inativação e geração de hash das senhas.
public class UsuarioService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Cadastra um novo usuário.
    // Antes de salvar, a senha informada é convertida para hash BCrypt.
    public void salvar(Usuario usuario) throws SQLException {
        String senhaHash = SenhaUtil.gerarHash(usuario.getSenha());
        usuario.setSenha(senhaHash);

        usuarioDAO.salvar(usuario);

        LogUtil.registrarAcao(
                "USUARIO_CADASTRADO",
                "EMAIL=" + usuario.getEmail() + ", PERFIL=" + usuario.getPerfil()
        );
    }

    // Retorna todos os usuários cadastrados.
    // Usado para preencher a tabela da tela de usuários.
    public List<Usuario> listarTodos() throws SQLException {
        return usuarioDAO.listarTodos();
    }

    // Busca um usuário pelo e-mail.
    // Usado no login e também em validações de cadastro/atualização.
    public Usuario buscarPorEmail(String email) throws SQLException {
        return usuarioDAO.buscarPorEmail(email);
    }

    // Atualiza os dados de um usuário.
    // A senha só é alterada quando uma nova senha é informada.
    public void atualizar(Usuario usuario) throws SQLException {
        if (usuario.getSenha() != null && !usuario.getSenha().isBlank()) {
            String senhaHash = SenhaUtil.gerarHash(usuario.getSenha());
            usuario.setSenha(senhaHash);
        }

        usuarioDAO.atualizar(usuario);

        LogUtil.registrarAcao(
                "USUARIO_ATUALIZADO",
                "ID=" + usuario.getId() + ", EMAIL=" + usuario.getEmail()
        );
    }

    // Inativa um usuário sem o remover fisicamente do banco.
    // A senha é limpa para evitar que o usuário inativo continue com credencial válida.
    public void inativar(Usuario usuario) throws SQLException {
        usuario.setAtivo(false);
        usuario.setSenha("");

        usuarioDAO.atualizar(usuario);

        LogUtil.registrarAcao(
                "USUARIO_INATIVADO",
                "ID=" + usuario.getId() + ", EMAIL=" + usuario.getEmail()
        );
    }

    // Remove fisicamente o usuário do banco.
    // No fluxo principal do sistema, a inativação é mais segura que a exclusão.
    public void deletar(Usuario usuario) throws SQLException {
        usuarioDAO.deletar(usuario);

        LogUtil.registrarAcao(
                "USUARIO_DELETADO",
                "ID=" + usuario.getId() + ", EMAIL=" + usuario.getEmail()
        );
    }
}