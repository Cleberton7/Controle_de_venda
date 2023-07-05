package com.example.vendas;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vendas.clientes.ActivityCliente;
import com.example.vendas.produtos.ActivityProduto;
import com.example.vendas.vendas.ActivityVendas;

import java.util.Objects;

public class ActivityMenu extends AppCompatActivity {
    private View btnVendas,btnCliente, btnProduto;
    private TextView txtVendas,txtCliente,txtProduto;
    private ImageView btnPerfil;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Objects.requireNonNull(getSupportActionBar()).hide();
        inicializarComponentes();

        btnPerfil.setOnClickListener(v -> telaNavegacao(ActivityPerfil.class));
        btnVendas.setOnClickListener(v -> telaNavegacao(ActivityVendas.class));
        txtVendas.setOnClickListener(v -> telaNavegacao(ActivityVendas.class));
        btnCliente.setOnClickListener(v -> telaNavegacao(ActivityCliente.class));
        txtCliente.setOnClickListener(v -> telaNavegacao(ActivityCliente.class));
        btnProduto.setOnClickListener(v -> telaNavegacao(ActivityProduto.class));
        txtProduto.setOnClickListener(v -> telaNavegacao(ActivityProduto.class));

    }

    private void inicializarComponentes(){
        btnVendas = findViewById(R.id.container_button_vendas);
        btnProduto = findViewById(R.id.container_button_produto);
        btnCliente = findViewById(R.id.container_button_cliente);
        btnPerfil = findViewById(R.id.imagView_btn_perfil);

        txtVendas = findViewById(R.id.textVendas);
        txtCliente = findViewById(R.id.textCliente);
        txtProduto = findViewById(R.id.textProduto);
    }

    private void telaNavegacao(Class formNext){
        Intent intent = new Intent(ActivityMenu.this, formNext);
        startActivity(intent);
    }
    /*private void telaVendas(){
        Intent intent = new Intent(ActivityMenu.this, ActivityVendas.class);
        startActivity(intent);
    }
    private void telaCliente(){
        Intent intent = new Intent(ActivityMenu.this, ActivityCliente.class);
        startActivity(intent);
    }
    private void telaProduto(){
        Intent intent = new Intent(ActivityMenu.this, ActivityProduto.class);
        startActivity(intent);
    }*/
}