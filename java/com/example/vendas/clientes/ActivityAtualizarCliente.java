package com.example.vendas.clientes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vendas.R;
import com.example.vendas.classes.MaskEditUtil;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Objects;

public class ActivityAtualizarCliente extends AppCompatActivity {
    private EditText edit_nome_cliente,edit_cpf,edit_telefone,edit_rua,edit_bairro,edit_numero;
    private Button btn_atualizar_cliente;
    private View btn_view_add_imagem;
    private ImageView imagemCliente;
    private ActivityResultLauncher<String> launcher;
    private Cliente cliente;
    private ClienteDAO dao;
    public String i;
    int idRecebidoDoCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atualizar_cliente);

        Objects.requireNonNull(getSupportActionBar()).hide();

        i = getIntent().getStringExtra("id");

        idRecebidoDoCliente = Integer.parseInt(i);
        iniciarComponentes();
        Log.d("id recebido",i);

        recuperarDados();

        launcher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> imagemCliente.setImageURI(result)

        );
        btn_view_add_imagem.setOnClickListener(v -> launcher.launch("image/*"));

        btn_atualizar_cliente.setOnClickListener(this::iniciarAtualizacaoDeCliente);


    }
    public void iniciarComponentes(){
        edit_nome_cliente = findViewById(R.id.edit_nome_cliente);
        edit_cpf = findViewById(R.id.edit_cpf);
        edit_telefone = findViewById(R.id.edit_numero_tel);
        edit_rua = findViewById(R.id.edit_rua);
        edit_bairro = findViewById(R.id.edit_bairro);
        edit_numero = findViewById(R.id.edit_numero);

        imagemCliente = findViewById(R.id.imagemCliente);
        btn_view_add_imagem = findViewById(R.id.viewImagemCliente);

        btn_atualizar_cliente = findViewById(R.id.btn_atualizar_cliente);
        edit_cpf.addTextChangedListener(MaskEditUtil.mask(edit_cpf, MaskEditUtil.FORMAT_CPF));
        edit_telefone.addTextChangedListener(MaskEditUtil.mask(edit_telefone, MaskEditUtil.FORMAT_FONE));
        dao = new ClienteDAO(this);
    }
    public void recuperarDados(){

        cliente = dao.recuperarCliente(idRecebidoDoCliente);

        edit_nome_cliente.setText(cliente.getNome());
        edit_cpf.setText(cliente.getCpf());
        edit_telefone.setText(cliente.getTelefone());
        edit_rua.setText(cliente.getEndereco().getRua());
        edit_bairro.setText(cliente.getEndereco().getBairro());
        edit_numero.setText(cliente.getEndereco().getNumero());

        if(!cliente.getImagem().equals("")){
            byte[] img;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                img = Base64.getDecoder().decode(cliente.getImagem());
                Bitmap imagemDecodificada = BitmapFactory.decodeByteArray(img,0,img.length);
                imagemCliente.setImageBitmap(imagemDecodificada);
            }
        }
    }
    public void iniciarAtualizacaoDeCliente(View v){
        String nomeCliente,cpf,telefone,rua,bairro,numero,imagem;
        nomeCliente = edit_nome_cliente.getText().toString();
        cpf = edit_cpf.getText().toString();
        telefone = edit_telefone.getText().toString();
        rua = edit_rua.getText().toString();
        bairro = edit_bairro.getText().toString();
        numero = edit_numero.getText().toString();

        if(nomeCliente.isEmpty()||cpf.isEmpty()||telefone.isEmpty()||rua.isEmpty()||bairro.isEmpty()||numero.isEmpty()){
            Snackbar snackbar;
            snackbar = Snackbar.make(v,"campos vazios",Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(Color.WHITE);
            snackbar.setTextColor(Color.BLACK);
            snackbar.show();
        }else{

            nomeCliente = nomeCliente.substring(0,1).toUpperCase().concat(nomeCliente.substring(1));
            rua = rua.substring(0,1).toUpperCase().concat(rua.substring(1));
            bairro = bairro.substring(0,1).toUpperCase().concat(bairro.substring(1));

            imagem = getImagemString();

            Endereco endereco = new Endereco(rua,bairro,numero);
            Cliente cliente = new Cliente(nomeCliente,cpf,telefone,imagem,endereco);

            long id = dao.atualizar(cliente,idRecebidoDoCliente);

            Toast.makeText(ActivityAtualizarCliente.this, "Cliente "+id+" atualizado com sucesso", Toast.LENGTH_SHORT).show();
            finish();
            backForm();

        }
    }
    public void backForm(){
        Intent intent = new Intent(ActivityAtualizarCliente.this, ActivityCliente.class);
        startActivity(intent);
    }
    public String getImagemString() {
        String imagemEmString="";
        try{
            Bitmap bitmap = ((BitmapDrawable) imagemCliente.getDrawable()).getBitmap();
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