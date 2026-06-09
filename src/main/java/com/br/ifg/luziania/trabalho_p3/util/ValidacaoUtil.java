package com.br.ifg.luziania.trabalho_p3.util;

import java.util.regex.Pattern;

// Centraliza as validações de entrada usadas no sistema.
// Essa classe evita repetir regras de validação dentro dos controllers e services.
public class ValidacaoUtil {

    // Formato esperado: 000.000.000-00
    private static final Pattern CPF = Pattern.compile("^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$");

    // Formato esperado: 11 dígitos numéricos.
    private static final Pattern CNH = Pattern.compile("^\\d{11}$");

    // Validação básica de e-mail.
    // Exemplo aceito: usuario@email.com
    private static final Pattern EMAIL = Pattern.compile("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");

    // Aceita placa no padrão Mercosul e no padrão antigo brasileiro.
    // Exemplos aceitos: ABC1D23 ou ABC1234
    private static final Pattern PLACA = Pattern.compile("^([A-Z]{3}[0-9][A-Z][0-9]{2}|[A-Z]{3}[0-9]{4})$");

    // Formatos aceitos: (99) 99999-9999 ou (99) 3333-4444
    private static final Pattern TELEFONE = Pattern.compile("^\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}$");

    private ValidacaoUtil() {
        // Evita instanciar classe utilitária.
    }

    // Verifica se o CPF está no formato esperado.
    public static boolean cpfValido(String cpf) {
        return cpf != null && CPF.matcher(cpf.trim()).matches();
    }

    // Verifica se a CNH possui 11 dígitos numéricos.
    public static boolean cnhValido(String cnh) {
        return cnh != null && CNH.matcher(cnh.trim()).matches();
    }

    // Verifica se o e-mail possui um formato básico válido.
    public static boolean emailValido(String email) {
        return email != null && EMAIL.matcher(email.trim()).matches();
    }

    // Verifica se a placa está no padrão Mercosul ou no padrão antigo.
    public static boolean placaValido(String placa) {
        return placa != null && PLACA.matcher(placa.trim()).matches();
    }

    // Verifica se o valor informado é maior que zero.
    public static boolean valorPositivo(double valor) {
        return valor > 0;
    }

    // Verifica se um texto está nulo, vazio ou preenchido apenas com espaços.
    public static boolean campoVazio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    // Verifica se o telefone está no formato esperado.
    public static boolean telefoneValido(String telefone) {
        return telefone != null && TELEFONE.matcher(telefone.trim()).matches();
    }

    // Verifica se a senha atende ao tamanho mínimo definido para o sistema.
    public static boolean senhaValida(String senha) {
        return senha != null && senha.length() >= 6;
    }
}