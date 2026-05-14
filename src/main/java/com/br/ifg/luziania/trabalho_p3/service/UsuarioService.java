package com.br.ifg.luziania.trabalho_p3.service;

import com.br.ifg.luziania.trabalho_p3.dao.UsuarioDAO;
import com.br.ifg.luziania.trabalho_p3.model.Usuario;
import com.br.ifg.luziania.trabalho_p3.util.LogUtil;
import com.br.ifg.luziania.trabalho_p3.util.SenhaUtil;

import java.sql.SQLException;
import java.util.List;

public class UsuarioService {
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

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
    public void atualizar(Usuario usuario) throws SQLException {
        if (usuario.getSenha() != null && !usuario.getSenha().isBlank()) {
            String senhaHash = SenhaUtil.gerarHash(usuario.getSenha());
            usuario.setSenha(senhaHash);
        }
        usuarioDAO.atualizar(usuario);
        LogUtil.registrarAcao("USUARIO_ATUALIZADO", "ID="  + usuario.getId() + ", EMAIL=" + usuario.getEmail());
    }
    public void deletar(Usuario usuario) throws SQLException {
        usuarioDAO.deletar(usuario);
        LogUtil.registrarAcao("USUARIO_DELETADO", "ID=" + usuario.getId() + ", EMAIL=" + usuario.getEmail());
    }
}
