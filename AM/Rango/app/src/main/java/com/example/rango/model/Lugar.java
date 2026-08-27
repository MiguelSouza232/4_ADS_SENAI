package com.example.rango.model;

public class Lugar {
    private String nome;
    private String categoria;
    private double preco;
    private String observacao;
    private int votos;

    public Lugar(String nome, String categoria, double preco, String observacao, int votos) {
        this.nome = nome;
        this.categoria = categoria;
        this.preco = preco;
        this.observacao = observacao;
        this.votos = votos;
    }

    // Constructor for compatibility with 4-argument calls
    public Lugar(String nome, String categoria, double preco, String observacao) {
        this(nome, categoria, preco, observacao, 0);
    }

    public String getNome() {
        return nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public double getPreco() {
        return preco;
    }

    public String getObservacao() {
        return observacao;
    }

    public int getVotos() {
        return votos;
    }

    public void setVotos(int votos) {
        this.votos = votos;
    }
}
