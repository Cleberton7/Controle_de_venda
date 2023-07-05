package com.example.vendas.vendas;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vendas.R;

public class ActivityVendas extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_padrao);


        iniciarComponentes();
        telaRegistrarVendas();
    }

    private void telaRegistrarVendas() {
        Intent intent = new Intent(ActivityVendas.this, ActivityRegistrarVendas.class);
       // intent.putExtra("produto","");
        startActivity(intent);
        finish();
    }


    public void iniciarComponentes() {

    }
}
