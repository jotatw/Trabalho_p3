package com.br.ifg.luziania.trabalho_p3;

import org.mindrot.jbcrypt.BCrypt;

public class GerarHash {
    public static void main(String[] args) {
        System.out.println("admin123:");
        System.out.println(BCrypt.hashpw("admin123", BCrypt.gensalt()));

        System.out.println("atendente123:");
        System.out.println(BCrypt.hashpw("atendente123", BCrypt.gensalt()));
    }
}