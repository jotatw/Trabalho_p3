package com.br.ifg.luziania.trabalho_p3.util;

import com.br.ifg.luziania.trabalho_p3.model.Usuario;

// Controla a sessão atual da aplicação.
// Guarda em memória o usuário autenticado enquanto o sistema estiver aberto.
public class Sessao {

    // Usuário que realizou login no sistema.
    // Quando não há usuário logado, o valor permanece null.
    private static Usuario usuarioLogado;

    private Sessao() {
        // Evita instanciar classe utilitária.
    }

    // Inicia a sessão armazenando o usuário autenticado.
    // Este método é chamado após o login ser validado com sucesso.
    public static void inicia(Usuario usuario) {
        usuarioLogado = usuario;
    }

    // Retorna o usuário atualmente logado.
    // É usado por controllers, services e logs para identificar quem executou a ação.
    public static Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    // Encerra a sessão removendo o usuário logado da memória.
    // Este método é chamado no logout.
    public static void encerrar() {
        usuarioLogado = null;
    }
}