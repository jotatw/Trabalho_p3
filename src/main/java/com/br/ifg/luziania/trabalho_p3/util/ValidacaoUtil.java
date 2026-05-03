package com.br.ifg.luziania.trabalho_p3.util;

import java.util.regex.Pattern;

public class ValidacaoUtil {
    // CPF: 000.000.000-00
    private static final Pattern CPF = Pattern.compile("^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$");

    // CNH: 11 digitos numericos
    private static final Pattern CNH = Pattern.compile("^\\d{11}$");

    // E-mail basico
    private static final Pattern EMAIL = Pattern.compile("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");

    // Placa Mercosul: ABC1D23
    private static final Pattern PLACA = Pattern.compile("^[A-Z]{3}[0-9][A-Z][0-9]{2}$");

    public static boolean cpfValido(String cpf) {
        return cpf != null && CPF.matcher(cpf.trim()).matches();
    }
    public static boolean cnhValido(String cnh) {
        return cnh != null && CNH.matcher(cnh.trim()).matches();
    }
    public static boolean emailValido(String email) {
        return email != null && EMAIL.matcher(email.trim()).matches();
    }
    public static boolean placaValido(String placa) {
        return placa != null && PLACA.matcher(placa.trim()).matches();
    }
    public static boolean valorPositivo(double valor) {
        return valor > 0;
    }
    public static boolean campoVazio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

}
