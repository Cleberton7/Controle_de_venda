package com.example.vendas;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ActivityLogin extends AppCompatActivity {
    private ImageView entrar;
    private EditText edit_email,edit_senha;
    private TextView txt_tela_cadastro;
    private ProgressBar progressBar;

    String[] mensagem={"Senha ou Email Inválido","Login Realizado com Sucesso","Campo vazio"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();

        iniciarComponentes();//método para inicializar componentes do layout

        // botão entrar acionado
        //ações do botão entrar
        entrar.setOnClickListener(this::verificarUsuario);

        //botão cadastrar conta acionado
        txt_tela_cadastro.setOnClickListener(view -> {//chamar tela de cadastro
            telaCadastro();
        });
    }
    private void verificarUsuario(View v){
        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();
        Snackbar snackbar ;

        if(email.isEmpty() || senha.isEmpty()){
            snackbar = Snackbar.make(v,mensagem[2],Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(Color.WHITE);
            snackbar.setTextColor(Color.BLACK);
            snackbar.show();
        }else{
            autenticaUsuario(v,email,senha);// chamando método que vai autenticar usuário
        }
    }
    private void autenticaUsuario(View v,String email,String senha){//autenticar usuario
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,senha).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                progressBar.setVisibility(View.VISIBLE);

                new Handler().postDelayed(this::telaPrincipal,3000);
            }else{
                String erro;//string para armazenar as possiveis mensagem de erros

                try { //try para tratamentos de erros
                    throw Objects.requireNonNull(task.getException());

                }catch (Exception e){
                    erro = "Erro ao realizar login, verifique seu email ou senha";
                }
                Snackbar snackbar = Snackbar.make(v,erro,Snackbar.LENGTH_LONG);
                snackbar.setBackgroundTint(Color.WHITE);
                snackbar.setTextColor(Color.BLACK);
                snackbar.show();
            }
        });
    }

    @Override
    protected void onStart() {//método recuperar usuário logado
        super.onStart();
        FirebaseUser userAtual = FirebaseAuth.getInstance().getCurrentUser();// recuperando usuários logado
        if(userAtual != null){//se for difernte de null, é por tem um usuátio logado
            telaPrincipal();//chama a rela principal
        }
    }

    private void telaPrincipal(){
        Intent intent = new Intent(ActivityLogin.this, ActivityMenu.class);
        startActivity(intent);
        finish();
    }
    private void telaCadastro(){  Intent intent = new Intent(ActivityLogin.this, ActivityCadastro.class);
        startActivity(intent);}
    private void iniciarComponentes(){
        txt_tela_cadastro =  (TextView)findViewById(R.id.text_cadastrar);
        edit_email = findViewById(R.id.edit_email);
        edit_senha = findViewById(R.id.edit_senha);
        entrar = findViewById(R.id.bt_entrar);
        progressBar = findViewById(R.id.progresso_bar);
    }
}
