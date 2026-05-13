package com.br.ifg.luziania.trabalho_p3.util;

import org.mindrot.jbcrypt.BCrypt;

public class SenhaUtil {
    private SenhaUtil() {}

    public static String gerarHash(String senha) {
        return BCrypt.hashpw(senha, BCrypt.gensalt());
    }
    public static boolean verificarSenha(String senhaDigitada, String senhaHash) {
        if(senhaDigitada == null || senhaHash == null) {return false;}
        return BCrypt.checkpw(senhaDigitada, senhaHash);
    }
}
