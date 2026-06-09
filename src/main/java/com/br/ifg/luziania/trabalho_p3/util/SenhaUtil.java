package com.br.ifg.luziania.trabalho_p3.util;

import org.mindrot.jbcrypt.BCrypt;

// Centraliza as operações relacionadas às senhas dos usuários.
// O sistema não armazena senhas em texto puro, apenas hashes gerados com BCrypt.
public class SenhaUtil {

    private SenhaUtil() {
        // Evita instanciar classe utilitária.
    }

    // Gera um hash BCrypt a partir da senha informada.
    // Esse hash é o valor que deve ser salvo no banco de dados.
    public static String gerarHash(String senha) {
        return BCrypt.hashpw(senha, BCrypt.gensalt());
    }

    // Compara a senha digitada pelo usuário com o hash armazenado no banco.
    // Retorna false se algum dos valores estiver nulo, evitando erro na verificação.
    public static boolean verificarSenha(String senhaDigitada, String senhaHash) {
        if (senhaDigitada == null || senhaHash == null) {
            return false;
        }

        return BCrypt.checkpw(senhaDigitada, senhaHash);
    }
}