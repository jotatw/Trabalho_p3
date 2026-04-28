package com.br.ifg.luziania.trabalho_p3.model;

public class Veiculo {
    private int id;
    private String marca;
    private String modelo;
    private String categoria;
    private boolean disponibilidade;
    private double valorLocacao;
    private String placa;

    public Veiculo() {}
    public Veiculo(int id, String modelo, String marca, String categoria, double valorLocacao, String placa) {
        this.id = id;
        this.modelo = modelo;
        this.marca = marca;
        this.categoria = categoria;
        this.valorLocacao = valorLocacao;
        this.placa = placa;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public boolean isDisponibilidade() {
        return disponibilidade;
    }

    public void setDisponibilidade(boolean disponibilidade) {
        this.disponibilidade = disponibilidade;
    }

    public double getValorLocacao() {
        return valorLocacao;
    }

    public void setValorLocacao(double valorLocacao) {
        this.valorLocacao = valorLocacao;
    }
}
