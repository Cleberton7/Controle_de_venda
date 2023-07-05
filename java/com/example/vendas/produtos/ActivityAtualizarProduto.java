package com.example.vendas.produtos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vendas.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Objects;

public class ActivityAtualizarProduto extends AppCompatActivity{
    private EditText edit_nome_produto,edit_codigo_produto,edit_valor_produto,edit_quantidade_produto;
    private Spinner spinnerDadosCategoria,spinnerDadosMl;
    private ImageView imagemProduto;
    private View btn_view_add_imagem;
    private Button btn_atualizar;
    ArrayAdapter<String> arrayAdapter1,arrayAdapter;
    private final String[] dadosCategoria={"Perfume","Desodorante","Creme de corpo","Creme de mão","Outros"};
    private final String[] dadosTamanho={"250 ml","500 ml","750 ml","1000 ml"};
    private Produto produto;
    private ActivityResultLauncher<String> launcher;
    public String i;
    int idRecebidoDoProduto;
    private ProdutoDAO dao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atualizar_produto);

        Objects.requireNonNull(getSupportActionBar()).hide();

        i = getIntent().getStringExtra("id");

        idRecebidoDoProduto=Integer.parseInt(i);
        iniciarComponentes();
        Log.d("id recebido",i);
        recuperarDados();

        launcher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> imagemProduto.setImageURI(result)

        );
        btn_view_add_imagem.setOnClickListener(v -> launcher.launch("image/*"));
        btn_atualizar.setOnClickListener(this::iniciarAtualizacaoDeProduto);
    }

    public void iniciarComponentes(){
        edit_codigo_produto = findViewById(R.id.edit_codigo_produto);
        edit_nome_produto = findViewById(R.id.edit_nome_produto);
        edit_quantidade_produto = findViewById(R.id.edit_quantidade);
        edit_valor_produto = findViewById(R.id.edit_valor_produto);

        spinnerDadosCategoria = findViewById(R.id.spinnerCategoria);

        btn_view_add_imagem = findViewById(R.id.viewImagemProduto);
        imagemProduto = findViewById(R.id.imagemProduto);

        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.spinnersCategoria));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDadosCategoria.setAdapter(arrayAdapter);

        spinnerDadosMl = findViewById(R.id.spinnerMl);
        arrayAdapter1 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.spinnersTamanho));
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDadosMl.setAdapter(arrayAdapter1);

        btn_atualizar = findViewById(R.id.btn_atualizar_produto);
        dao = new ProdutoDAO(this);
    }

    public void recuperarDados(){

        produto = dao.recuperaProduto(idRecebidoDoProduto);

        edit_codigo_produto.setText(produto.getCodigo());
        edit_nome_produto.setText(produto.getNome());
        edit_quantidade_produto.setText(String.valueOf(produto.getQuantidade()));
        edit_valor_produto.setText(String.valueOf(produto.getValor()));

        if(!produto.getImagem().equals("")){
            byte[] img;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                img = Base64.getDecoder().decode(produto.getImagem());
                Bitmap imagemDecodificada = BitmapFactory.decodeByteArray(img,0,img.length);
                imagemProduto.setImageBitmap(imagemDecodificada);
            }
        }

        for (int i=0; i<dadosCategoria.length;i++){
            if(dadosCategoria[i].equalsIgnoreCase(produto.getCategoria())){
                spinnerDadosCategoria.setSelection(i);
            }
        }
        for (int i=0; i<dadosTamanho.length;i++){
            if(dadosTamanho[i].equalsIgnoreCase(produto.getTamanho())){
                spinnerDadosMl.setSelection(i);
            }
        }
    }
    public void iniciarAtualizacaoDeProduto(View v){
        String nome,codigo,valor,quantidade,categoria,tamanho,imagem;
        nome = edit_nome_produto.getText().toString();
        codigo = edit_codigo_produto.getText().toString();
        quantidade = edit_quantidade_produto.getText().toString();
        valor = edit_valor_produto.getText().toString();
        Snackbar snackbar ;
        if(nome.isEmpty() || codigo.isEmpty()  || quantidade.isEmpty() ||valor.isEmpty()){
            snackbar = Snackbar.make(v,"campos vazios",Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(Color.WHITE);
            snackbar.setTextColor(Color.BLACK);
            snackbar.show();
        }else{
            categoria = spinnerDadosCategoria.getSelectedItem().toString();
            tamanho = spinnerDadosMl.getSelectedItem().toString();
            nome = nome.substring(0,1).toUpperCase().concat(nome.substring(1));
            imagem = getImagemString();
            Produto p = new Produto(codigo,nome,categoria,tamanho,imagem,Double.parseDouble(valor),Integer.parseInt(quantidade) );
            long id = dao.atualizar(p,idRecebidoDoProduto);
            Toast.makeText(ActivityAtualizarProduto.this, "produto "+ id+" atualizado!", Toast.LENGTH_SHORT).show();
            finish();
            backForm();

        }
    }
    public void backForm(){
        Intent intent = new Intent(ActivityAtualizarProduto.this, ActivityProduto.class);
        startActivity(intent);
    }
    public String getImagemString() {
        String imagemEmString="";
        try{
            Bitmap bitmap = ((BitmapDrawable) imagemProduto.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                imagemEmString = Base64.getEncoder().encodeToString(stream.toByteArray());
            }else {
                imagemEmString = android.util.Base64.encodeToString(stream.toByteArray(), android.util.Base64.DEFAULT);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return imagemEmString;
    }
}