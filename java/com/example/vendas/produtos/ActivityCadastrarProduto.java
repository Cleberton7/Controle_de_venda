package com.example.vendas.produtos;

import android.app.AlertDialog;
import android.graphics.Bitmap;
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
import androidx.core.content.ContextCompat;

import com.example.vendas.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

public class ActivityCadastrarProduto extends AppCompatActivity {
    private EditText edit_nome_produto,edit_codigo_produto,edit_valor_produto,edit_quantidade_produto;
    private View btn_view_add_imagem;
    private ImageView imagemProduto;
    private Button btn_cadastrar;
    private Spinner spinnerDadosCategoria,spinnerDadosMl;
    private ProdutoDAO dao;
    private ActivityResultLauncher<String> launcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_produto);
        Objects.requireNonNull(getSupportActionBar()).hide();

        iniciarComponentes();
        launcher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> imagemProduto.setImageURI(result)

        );
        btn_view_add_imagem.setOnClickListener(v -> launcher.launch("image/*"));
        btn_cadastrar.setOnClickListener(v -> {
           iniciarCadastroDeProduto(v);
        });
    }
    public void iniciarComponentes(){
        edit_codigo_produto = findViewById(R.id.edit_codigo_produto);
        edit_nome_produto = findViewById(R.id.edit_nome_produto);
        edit_quantidade_produto = findViewById(R.id.edit_quantidade);
        edit_valor_produto = findViewById(R.id.edit_valor_produto);

        btn_view_add_imagem = findViewById(R.id.viewImagemProduto);
        imagemProduto = findViewById(R.id.imagemProduto);


        spinnerDadosCategoria = findViewById(R.id.spinnerCategoria);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.spinnersCategoria));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDadosCategoria.setAdapter(arrayAdapter);

        spinnerDadosMl = findViewById(R.id.spinnerMl);
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.spinnersTamanho));
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDadosMl.setAdapter(arrayAdapter1);

        btn_cadastrar = findViewById(R.id.btn_cadastrar_produto);
        dao = new ProdutoDAO(this);

    }
    public void resetarComponentes(){
        edit_codigo_produto.setText("");
        edit_valor_produto.setText("");
        edit_nome_produto.setText("");
        edit_quantidade_produto.setText("");


        imagemProduto.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_foto));
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.spinnersTamanho));
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDadosMl.setAdapter(arrayAdapter1);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.spinnersCategoria));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDadosCategoria.setAdapter(arrayAdapter);


    }
    public void iniciarCadastroDeProduto(View v){
        String nome,codigo,valor,quantidade,categoria,tamanho,imagem;
        categoria = spinnerDadosCategoria.getSelectedItem().toString();
        tamanho = spinnerDadosMl.getSelectedItem().toString();
        nome = edit_nome_produto.getText().toString();
        codigo = edit_codigo_produto.getText().toString();
        quantidade = edit_quantidade_produto.getText().toString();
        valor = edit_valor_produto.getText().toString();

        Snackbar snackbar ;
        if(nome.isEmpty() || codigo.isEmpty() || quantidade.isEmpty() || valor.isEmpty()){
            snackbar = Snackbar.make(v,"campos vazios",Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(Color.WHITE);
            snackbar.setTextColor(Color.BLACK);
            snackbar.show();
        }else{
            if(!verificarExistencia(codigo)){
                imagem = getImagemString();
                nome = nome.substring(0,1).toUpperCase().concat(nome.substring(1));

                Log.d("IMAGEM",imagem);
                Produto produto = new Produto(codigo,nome,categoria,tamanho,imagem,Double.parseDouble(valor),Integer.parseInt(quantidade) );
                cadastrarProduto(produto);
                resetarComponentes();
            }
        }
    }
    public void cadastrarProduto(Produto produto){
        long id = dao.inserir(produto);
        Toast.makeText(this,"produto salvo com id: "+id,Toast.LENGTH_SHORT).show();
    }
    public boolean verificarExistencia(String codigo){
        List<Produto> produtosExistentes;
        produtosExistentes = dao.listarTodos();

        for (Produto produto: produtosExistentes){
            if(codigo.equalsIgnoreCase(produto.getCodigo())){
                AlertDialog.Builder Existente = new AlertDialog.Builder(this);
                Existente.setTitle("Atenção!\n");

                Existente.setMessage("O produto de código "+produto.getCodigo()+" já está cadastrado!");
                Existente.setCancelable(false);
                Existente.setPositiveButton("Ok",null );
                Existente.create().show();
                return true;
            }
        }
        return false;
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