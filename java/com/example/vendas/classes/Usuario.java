package com.example.vendas.classes;

import java.io.Serializable;

public class Usuario implements Serializable {
    private final String nome;
    private final String email;
    private final String senha;
    public String getNome() {
        return nome;
    }
    public String getEmail() {
        return email;
    }
    public String getSenha() {
        return senha;
    }

    public Usuario(String nome,String email,String senha){
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

}
