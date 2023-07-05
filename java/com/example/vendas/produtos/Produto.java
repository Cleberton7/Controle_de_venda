package com.example.vendas.produtos;

import java.io.Serializable;

public class Produto implements Serializable {

    private String codigo, nome, categoria, tamanho, imagem;
    private double valor;
    private int quantidade;


    public Produto(String codigo, String nome, String categoria, String tamanho, String imagem, double valor, int quantidade) {
        this.codigo = codigo;
        this.nome = nome;
        this.categoria = categoria;
        this.tamanho = tamanho;
        this.imagem = imagem;
        this.valor = valor;
        this.quantidade = quantidade;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategotia(String categotia) {
        this.categoria = categoria;
    }

    public String getTamanho() {
        return tamanho;
    }

    public void setTamanho(String tamanho) {
        this.tamanho = tamanho;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNone(String nome) {
        this.nome = nome;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    @Override
    public String toString() {
        return
                "{"+"Codigo: " + codigo +
                "\nNome: " + nome +
                "\nValor: " + valor +
                "\nQuantidade: " + quantidade +
                "\nCategoria: " + categoria +
                "\nTamanho: " + tamanho+"}";
    }
}
