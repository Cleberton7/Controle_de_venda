package com.example.vendas.vendas;

import java.io.Serializable;

public class Venda implements Serializable {

    private  String idVendedor,nomeVendedor,cpfCliente,dataVenda,nProdutos;
    int prazoParaPagar;
    private double valorTotal,desconto;
    boolean situacaoDaVenda = false;

    public Venda(String idVendedor, String nomeVendedor, String cpfCliente, String dataVenda,
                 String nProdutos, int prazoParaPagar, double valorTotal, double desconto, boolean situacaoDaVenda) {
        this.idVendedor = idVendedor;
        this.nomeVendedor = nomeVendedor;
        this.cpfCliente = cpfCliente;
        this.dataVenda = dataVenda;
        this.nProdutos = nProdutos;
        this.prazoParaPagar = prazoParaPagar;
        this.valorTotal = valorTotal;
        this.desconto = desconto;
        this.situacaoDaVenda = situacaoDaVenda;
    }

    public String getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(String idVendedor) {
        this.idVendedor = idVendedor;
    }

    public String getNomeVendedor() {
        return nomeVendedor;
    }

    public void setNomeVendedor(String nomeVendedor) {
        this.nomeVendedor = nomeVendedor;
    }

    public String getCpfCliente() {
        return cpfCliente;
    }

    public void setCpfCliente(String cpfCliente) {
        this.cpfCliente = cpfCliente;
    }

    public String getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(String dataVenda) {
        this.dataVenda = dataVenda;
    }

    public String getnProdutos() {
        return nProdutos;
    }

    public void setnProdutos(String nProdutos) {
        this.nProdutos = nProdutos;
    }

    public int getPrazoParaPagar() {
        return prazoParaPagar;
    }

    public void setPrazoParaPagar(int prazoParaPagar) {
        this.prazoParaPagar = prazoParaPagar;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public double getDesconto() {
        return desconto;
    }

    public void setDesconto(double desconto) {
        this.desconto = desconto;
    }

    public boolean isSituacaoDaVenda() {
        return situacaoDaVenda;
    }

    public void setSituacaoDaVenda(boolean situacaoDaVenda) {
        this.situacaoDaVenda = situacaoDaVenda;
    }
}
