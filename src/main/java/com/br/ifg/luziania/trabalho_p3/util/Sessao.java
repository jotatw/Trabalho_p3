package com.br.ifg.luziania.trabalho_p3.util;

import com.br.ifg.luziania.trabalho_p3.model.Usuario;

public class Sessao {
    private static Usuario usuarioLogado;

    public static void inicia(Usuario usuario) {
        usuarioLogado = usuario;
    }
    public static Usuario getUsuarioLogado() {
        return usuarioLogado;
    }
    public static void encerrar() {
        usuarioLogado = null;
    }
}
