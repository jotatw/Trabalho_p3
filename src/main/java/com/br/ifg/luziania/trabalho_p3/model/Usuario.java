package com.br.ifg.luziania.trabalho_p3.model;

public class Usuario {
    private String  nomeCompleto;
    private String  email;
    private String  senha;
    private int id;
    private String perfil; //"ADMIM" "ATENDENDENTE"
    private boolean ativo;
    public Usuario() {
    }
    public Usuario(String nomeCompleto, String email, String senha,int id, String perfil) {
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.senha = senha;
        this.id = id;
        this.perfil = perfil;

    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public int getId() {
        return id;
    }

    public void setCpf(int id) {
        this.id = id;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
