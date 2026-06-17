package com.br.ifg.luziania.trabalho_p3.model;

// Representa uma linha de log de uso do sistema.
// Usado para exibir os registros de auditoria na tela de logs.
public class LogSistema {

    private String dataHora;
    private String acao;
    private String usuario;
    private String detalhes;
    private String linhaCompleta;

    public LogSistema() {
    }

    public LogSistema(String dataHora, String acao, String usuario, String detalhes, String linhaCompleta) {
        this.dataHora = dataHora;
        this.acao = acao;
        this.usuario = usuario;
        this.detalhes = detalhes;
        this.linhaCompleta = linhaCompleta;
    }

    public String getDataHora() {
        return dataHora;
    }

    public void setDataHora(String dataHora) {
        this.dataHora = dataHora;
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(String detalhes) {
        this.detalhes = detalhes;
    }

    public String getLinhaCompleta() {
        return linhaCompleta;
    }

    public void setLinhaCompleta(String linhaCompleta) {
        this.linhaCompleta = linhaCompleta;
    }
}