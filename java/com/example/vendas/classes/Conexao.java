package com.example.vendas.classes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class Conexao extends SQLiteOpenHelper {

    private static final String name = "controleDeVendas.db";
    private static final int version = 1;

    public Conexao(Context context) {
        super(context, name, null, version);
    }




    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS produtos(id integer PRIMARY KEY AUTOINCREMENT,"+
                "codigoProduto varchar UNIQUE NOT NULL,nome varchar NOT NULL ,valor double NOT NULL,quantidade integer NOT NULL,categoria varchar,tamanho varchar,imagem varchar)");
        db.execSQL("CREATE TABLE IF NOT EXISTS clientes(id integer PRIMARY KEY AUTOINCREMENT," +
                "nome varchar NOT NULL, cpf varchar UNIQUE NOT NULL,telefone varchar NOT NULL,imagem varchar,endereco integer NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS endereco(id integer PRIMARY KEY AUTOINCREMENT," +
                "rua varchar NOT NULL,bairro varchar NOT NULL,numero varchar NOT NULL )");
        db.execSQL("CREATE TABLE IF NOT EXISTS vendas(id integer PRIMARY KEY AUTOINCREMENT," +
                "idVendedor varchar NOT NULL,nomeVendedor varchar,cpfCliente varchar , " +
                "valor Double NOT NULL, dataVenda varchar, prazoVencimento integer," +
                "desconto double,nProdutos varchar,situacao boolean , av double )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS produtos");
        db.execSQL("DROP TABLE IF EXISTS clientes");
        db.execSQL("DROP TABLE IF EXISTS endereco");
        db.execSQL("DROP TABLE IF EXISTS vendas");

        onCreate(db);
    }
}
