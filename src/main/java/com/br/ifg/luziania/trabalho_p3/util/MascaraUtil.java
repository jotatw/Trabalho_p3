package com.br.ifg.luziania.trabalho_p3.util;

import javafx.scene.control.TextField;

public class MascaraUtil {
    public static void cpf (TextField campo){
        campo.textProperty().addListener((obs, antigo, novo) -> {
            //extrai so os digitos
            String digito = novo.replaceAll("[^0-9]", "");

            //limita a 11 digitos
            if (digito.length() > 11) {
                digito = digito.substring(0, 11);
            };

            //formata e atualiza só se mudou para evitar loop infinito
            String formatado = formatarCpf(digito);
            if (!novo.equals(formatado)) {
                campo.setText(formatado);
                campo.positionCaret(formatado.length());
            }
        });
    }
    public static void cnh (TextField campo){
        campo.textProperty().addListener((obs, ontigo, novo) -> {
            String digito = novo.replaceAll("[^0-9]", "");

            //limita a 11 digitos
            if (novo.length() > 11) {
                digito = digito.substring(0, 11);
            };

            if (!novo.equals(digito)) {
                campo.setText(digito);
                campo.positionCaret(digito.length());
            }
        });
    }
    public static void telefone (TextField campo){
        campo.textProperty().addListener((obs, antigo, novo) -> {
            String digito = novo.replaceAll("[^0-9]", "");

            //limita a 11digitos (DDD + 9 digitos)
            if (novo.length() > 11) {
                digito = digito.substring(0, 11);
            };

            String formatado = formatarTelefone(novo);
            if (!novo.equals(formatado)) {
                campo.setText(formatado);
                campo.positionCaret(formatado.length());
            }
        });
    }
    public static void placa (TextField campo){
        campo.textProperty().addListener((obs, antigo, novo) -> {
            //converte para maiusculo e remove caracteres invalidos
            String limpo = novo.toUpperCase().replaceAll("[^A-Z0-9]", "");

            //limita a 7 cacarteres (tamanho de qualquer placa)
            if (novo.length() > 7) {
                limpo = novo.substring(0, 7);
            };

            if (!novo.equals(limpo)) {
                campo.setText(limpo);
                campo.positionCaret(limpo.length());
            }
        });
    }
    private static String formatarCpf(String digito){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digito.length(); i++) {
            if (i == 3 || i == 6) sb.append('.');
            if (i == 9) sb.append('-');
            sb.append(digito.charAt(i));
        }
        return sb.toString();
    }
    private static String formatarTelefone(String digito){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digito.length(); i++) {
            if (i == 0) sb.append('(');
            if (i == 2) sb.append(") ");
            //celular: traço na posição 7 (9 digitos); fixo: posição 6 (8 digitos)
            if (i == 7 && digito.length() == 11) sb.append('-');
            if (i == 6 && digito.length() == 10) sb.append('-');
            sb.append(digito.charAt(i));
        }
        return sb.toString();
    }
    public static void limpar (TextField campo){
        campo.setText("");
    }
}
