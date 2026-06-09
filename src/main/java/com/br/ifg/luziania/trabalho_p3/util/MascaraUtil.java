package com.br.ifg.luziania.trabalho_p3.util;

import javafx.scene.control.TextField;

// Centraliza as máscaras aplicadas nos campos de texto da interface.
// As máscaras ajudam o usuário a digitar CPF, CNH, telefone e placa no formato esperado.
public class MascaraUtil {

    private MascaraUtil() {
        // Evita instanciar classe utilitária.
    }

    // Aplica máscara de CPF no formato 000.000.000-00.
    public static void cpf(TextField campo) {
        campo.textProperty().addListener((obs, antigo, novo) -> {
            String digito = novo.replaceAll("[^0-9]", "");

            // CPF possui 11 dígitos.
            if (digito.length() > 11) {
                digito = digito.substring(0, 11);
            }

            String formatado = formatarCpf(digito);

            // Atualiza o campo apenas quando o texto formatado for diferente.
            // Isso evita loop infinito no listener.
            if (!novo.equals(formatado)) {
                campo.setText(formatado);
                campo.positionCaret(formatado.length());
            }
        });
    }

    // Permite apenas números e limita o campo CNH a 11 dígitos.
    public static void cnh(TextField campo) {
        campo.textProperty().addListener((obs, antigo, novo) -> {
            String digito = novo.replaceAll("[^0-9]", "");

            if (digito.length() > 11) {
                digito = digito.substring(0, 11);
            }

            if (!novo.equals(digito)) {
                campo.setText(digito);
                campo.positionCaret(digito.length());
            }
        });
    }

    // Aplica máscara de telefone nos formatos (99) 99999-9999 ou (99) 3333-4444.
    public static void telefone(TextField campo) {
        campo.textProperty().addListener((obs, antigo, novo) -> {
            String digito = novo.replaceAll("[^0-9]", "");

            // Limita o telefone a DDD + número, com no máximo 11 dígitos.
            if (digito.length() > 11) {
                digito = digito.substring(0, 11);
            }

            String formatado = formatarTelefone(digito);

            if (!novo.equals(formatado)) {
                campo.setText(formatado);
                campo.positionCaret(formatado.length());
            }
        });
    }

    // Padroniza a placa removendo caracteres inválidos e convertendo para letras maiúsculas.
    // Aceita até 7 caracteres, tanto para placa antiga quanto para Mercosul.
    public static void placa(TextField campo) {
        campo.textProperty().addListener((obs, antigo, novo) -> {
            String limpo = novo.toUpperCase().replaceAll("[^A-Z0-9]", "");

            if (limpo.length() > 7) {
                limpo = limpo.substring(0, 7);
            }

            if (!novo.equals(limpo)) {
                campo.setText(limpo);
                campo.positionCaret(limpo.length());
            }
        });
    }

    // Monta a formatação visual do CPF conforme os dígitos são digitados.
    private static String formatarCpf(String digito) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < digito.length(); i++) {
            if (i == 3 || i == 6) {
                sb.append('.');
            }

            if (i == 9) {
                sb.append('-');
            }

            sb.append(digito.charAt(i));
        }

        return sb.toString();
    }

    // Monta a formatação visual do telefone.
    // Para celular usa o traço após o quinto dígito do número; para fixo, após o quarto.
    private static String formatarTelefone(String digito) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < digito.length(); i++) {
            if (i == 0) {
                sb.append('(');
            }

            if (i == 2) {
                sb.append(") ");
            }

            if (i == 7 && digito.length() == 11) {
                sb.append('-');
            }

            if (i == 6 && digito.length() == 10) {
                sb.append('-');
            }

            sb.append(digito.charAt(i));
        }

        return sb.toString();
    }

    // Limpa o conteúdo de um campo de texto.
    public static void limpar(TextField campo) {
        campo.setText("");
    }
}