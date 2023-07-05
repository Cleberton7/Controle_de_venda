package com.example.vendas.clientes;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.vendas.R;
import com.example.vendas.classes.MaskEditUtil;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Objects;


public class ActivityCadastrarCliente extends AppCompatActivity {
    private EditText edit_nome_cliente,edit_cpf,edit_telefone,edit_rua,edit_bairro,edit_numero;
    private Button btn_cadastrar_cliente;
    private View btn_view_add_imagem;
    private ImageView imagemCliente;
    private ActivityResultLauncher<String> launcher;
    private ClienteDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_cliente);

        Objects.requireNonNull(getSupportActionBar()).hide();

        iniciarComponentes();
        launcher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> imagemCliente.setImageURI(result)

        );
        btn_view_add_imagem.setOnClickListener(v -> launcher.launch("image/*"));

        btn_cadastrar_cliente.setOnClickListener(this::iniciarCadastroDeCliente);
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

        btn_cadastrar_cliente = findViewById(R.id.btn_cadastrar_cliente);
        edit_cpf.addTextChangedListener(MaskEditUtil.mask(edit_cpf, MaskEditUtil.FORMAT_CPF));
        edit_telefone.addTextChangedListener(MaskEditUtil.mask(edit_telefone, MaskEditUtil.FORMAT_FONE));
        dao = new ClienteDAO(this);
    }
    public void resetarComponentes(){
        edit_nome_cliente.setText("");
        edit_cpf.setText("");
        edit_rua.setText("");
        edit_telefone.setText("");
        edit_bairro.setText("");
        edit_numero.setText("");
        imagemCliente.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_foto));
    }
    public void iniciarCadastroDeCliente(View v){
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
            if(!verificarExistencia(cpf)){
                nomeCliente = nomeCliente.substring(0,1).toUpperCase().concat(nomeCliente.substring(1));
                rua = rua.substring(0,1).toUpperCase().concat(rua.substring(1));
                bairro = bairro.substring(0,1).toUpperCase().concat(bairro.substring(1));

                imagem = getImagemString();
                Endereco endereco = new Endereco(rua,bairro,numero);
                Cliente cliente = new Cliente(nomeCliente,cpf,telefone,imagem,endereco);
                cadastrarCliente(cliente);
                resetarComponentes();
            }

        }
    }
    public void cadastrarCliente(Cliente cliente){
        long id = dao.inserir(cliente);
        Toast.makeText(this,"cliente salvo com id: "+id,Toast.LENGTH_SHORT).show();
    }
    public boolean verificarExistencia(String cpf){
        List<Cliente> clientsExistentes;
       clientsExistentes = dao.listarTodos();

        for (Cliente cliente: clientsExistentes){
            if(cpf.equalsIgnoreCase(cliente.getCpf())){
                AlertDialog.Builder Existente = new AlertDialog.Builder(this);
                Existente.setTitle("Atenção!\n");

                Existente.setMessage("O Cliente com cpf "+cliente.getCpf()+" já está cadastrado!");
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
            Bitmap bitmap = ((BitmapDrawable) imagemCliente.getDrawable()).getBitmap();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                imagemEmString = Base64.getEncoder().encodeToString(stream.toByteArray());
            }else {
                imagemEmString = android.util.Base64.encodeToString(stream.toByteArray(), android.util.Base64.DEFAULT);
            }

            /*android.graphics.Bitmap@f1328ad*/

        }catch (Exception e){
            e.printStackTrace();
        }
        return imagemEmString;
    }
}