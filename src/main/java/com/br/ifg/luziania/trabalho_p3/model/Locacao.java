package com.br.ifg.luziania.trabalho_p3.model;

import java.time.LocalDate;

public class Locacao {
    private int id;
    private Cliente cliente;
    private Usuario usuario;
    private Veiculo veiculo;
    private LocalDate dataRetirada;
    private LocalDate dataDevolucaoPrevista;
    private LocalDate dataDevolucaoReal;
    private double valorTotal;
    private double multas;
    private String status; //"ATIVA", "ENCERRADA", "CANCELADA"

    public Locacao() {
    }

    public Locacao(double valorTotal, LocalDate dataDevolucaoPrevista, LocalDate dataRetirada, Veiculo veiculo, Usuario usuario, Cliente cliente) {
        this.status = "ATIVA";
        this.multas = 0.0;
        this.valorTotal = valorTotal;
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
        this.dataRetirada = dataRetirada;
        this.veiculo = veiculo;
        this.usuario = usuario;
        this.cliente = cliente;
    }

    public Locacao(Cliente cliente, Veiculo veiculo, Usuario usuarioLogado, LocalDate dataRetirada, LocalDate dataDevolucao, double valorTotal) {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDate getDataRetirada() {
        return dataRetirada;
    }

    public void setDataRetirada(LocalDate dataRetirada) {
        this.dataRetirada = dataRetirada;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public LocalDate getDataDevolucaoPrevista() {
        return dataDevolucaoPrevista;
    }

    public void setDataDevolucaoPrevista(LocalDate dataDevolucaoPrevista) {
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
    }

    public LocalDate getDataDevolucaoReal() {
        return dataDevolucaoReal;
    }

    public void setDataDevolucaoReal(LocalDate dataDevolucaoReal) {
        this.dataDevolucaoReal = dataDevolucaoReal;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public double getMultas() {
        return multas;
    }

    public void setMultas(double multas) {
        this.multas = multas;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
