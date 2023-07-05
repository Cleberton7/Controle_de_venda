package com.example.vendas;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.example.vendas.classes.Usuario;


public class ActivityCadastro extends AppCompatActivity {
    String[] mensagem={"Preencha todos os campos","Cadastro realizado com sucesso"};
    private EditText edit_nome,edit_email,edit_senha;
    private Button btn_cadastrar;
    String usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        Objects.requireNonNull(getSupportActionBar()).hide();//retirar nome do app da tela
        inciarComponentes(); //inicializar os elementos do layout

        //acionando o botão cadastrar
        //ação do botão cadastrar
        btn_cadastrar.setOnClickListener(this::verificarCampos);
    }
    private void cadastrarUsuario(View v, Usuario user){//função cadastrar (recebe a view para informar possíveis erros)

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(// instancia para autenticação do usuário
                user.getEmail(),user.getSenha()).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){//verifica se o cadastro foi um sucesso
                        salvarDadosUsuario(user);
                        Snackbar snackbar = Snackbar.make(v, mensagem[1], Snackbar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(Color.WHITE);
                        snackbar.setTextColor(Color.BLACK);
                        snackbar.show();
                    }else{// caso ocorra erros, serão tratados partir daqui
                        String erro;//string para armazenar as possiveis mensagem de erros

                        try { //try para tratamentos de erros
                            throw Objects.requireNonNull(task.getException());
                        }catch (FirebaseAuthWeakPasswordException e) {//erro de senha curta
                            erro = "Senha curta, digite no mínimo 6 caracteres";
                        }catch (FirebaseAuthUserCollisionException e){//erro de conta existente
                            erro = "Email já esta em uso";
                        }catch (FirebaseAuthInvalidCredentialsException e){//erro de email
                            erro = "Email inválido";
                        }
                        catch (Exception e){
                            erro = "Erro ao cadastrar usário";
                        }
                        Snackbar snackbar = Snackbar.make(v, erro, Snackbar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(Color.WHITE);
                        snackbar.setTextColor(Color.BLACK);
                        snackbar.show();
                    }
                });

    }
    private void verificarCampos(View v){
        //capturando e transformando em string as informações digitadas pelo usuario
        String nome = edit_nome.getText().toString();
        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();
        //

        if(nome.isEmpty() || email.isEmpty() || senha.isEmpty()){//verificando se os campos estão vazios
            Snackbar snackbar = Snackbar.make(v, mensagem[0], Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(Color.WHITE);
            snackbar.setTextColor(Color.BLACK);
            snackbar.show();
        }else{//caso estejam preeenchidos, é chamado a função que irar realizar o cadastro
            Usuario user = new Usuario(nome,email,senha);//instacio o objeto usuário
            cadastrarUsuario(v,user);// função cadastrar repassar a view parar informar possiveis erros e informações
        }
    }
    private void salvarDadosUsuario(Usuario user){
        FirebaseFirestore db = FirebaseFirestore.getInstance(); //instaciando o banco de dados
        Map<String,Object> usuarios = new HashMap<>();// criando a
        usuarios.put("nome",user.getNome());

        usuarioId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        DocumentReference documentReference = db.collection("Usuarios").document(usuarioId);
        documentReference.set(usuarios).addOnSuccessListener(unused -> Log.d("db","Sucesso ao salvar dados")).addOnFailureListener(e -> Log.d("db_error","Erro ao salvar dados"+ e));
    }
    private void inciarComponentes(){//captura dos elementos do layout
        btn_cadastrar = findViewById(R.id.bt_cadastrar);
        edit_nome = findViewById(R.id.edit_nome);
        edit_email = findViewById(R.id.edit_email);
        edit_senha = findViewById(R.id.edit_senha);

    }
}