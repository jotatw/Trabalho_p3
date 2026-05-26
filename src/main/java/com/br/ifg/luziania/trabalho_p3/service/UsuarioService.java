package com.br.ifg.luziania.trabalho_p3.service;

import com.br.ifg.luziania.trabalho_p3.dao.UsuarioDAO;
import com.br.ifg.luziania.trabalho_p3.model.Usuario;
import com.br.ifg.luziania.trabalho_p3.util.LogUtil;
import com.br.ifg.luziania.trabalho_p3.util.SenhaUtil;

import java.sql.SQLException;
import java.util.List;
// Controla as regras de negócio relacionadas aos usuários do sistema.
public class UsuarioService {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Cadastra usuário novo gerando hash BCrypt da senha.
    public void salvar(Usuario usuario) throws SQLException {
        String senhaHash = SenhaUtil.gerarHash(usuario.getSenha());
        usuario.setSenha(senhaHash);
        usuarioDAO.salvar(usuario);
        LogUtil.registrarAcao("USUARIO_CADASTRADO", "EMAIL=" + usuario.getEmail() + ", PERFIL=" + usuario.getPerfil());
    }
    public List<Usuario> listarTodos() throws SQLException {
        return usuarioDAO.listarTodos();
    }
    public Usuario buscarPorEmail(String email) throws SQLException {
        return  usuarioDAO.buscarPorEmail(email);
    }

    // Atualiza usuário. A senha só é alterada se uma nova senha for informada.
    public void atualizar(Usuario usuario) throws SQLException {
        if (usuario.getSenha() != null && !usuario.getSenha().isBlank()) {
            String senhaHash = SenhaUtil.gerarHash(usuario.getSenha());
            usuario.setSenha(senhaHash);
        }
        usuarioDAO.atualizar(usuario);
        LogUtil.registrarAcao("USUARIO_ATUALIZADO", "ID="  + usuario.getId() + ", EMAIL=" + usuario.getEmail());
    }
    public void inativar(Usuario usuario) throws SQLException {
        usuario.setAtivo(false);
        usuario.setSenha("");

        usuarioDAO.atualizar(usuario);

        LogUtil.registrarAcao("USUARIO_INATIVADO",
                "ID=" + usuario.getId() + ", EMAIL=" + usuario.getEmail());
    }
    public void deletar(Usuario usuario) throws SQLException {
        usuarioDAO.deletar(usuario);

        LogUtil.registrarAcao(
                "USUARIO_DELETADO",
                "ID=" + usuario.getId() + ", EMAIL=" + usuario.getEmail()
        );
    }
}
