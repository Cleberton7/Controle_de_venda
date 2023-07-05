package com.example.vendas;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Objects;

public class ActivityPerfil extends AppCompatActivity {
    private ImageView btn_deslog;
    private TextView txt_id_usuario,txt_id_email;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Objects.requireNonNull(getSupportActionBar()).hide();

        iniciarComponentes();

        btn_deslog.setOnClickListener(view -> telaLogin());//escutando botão deslogar e acionando ação
    }

    @Override
    protected void onStart() {//recurperando o usuario atual
        super.onStart();
        String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();// pegando o usuario atual pelo id

        DocumentReference documentReference = db.collection("Usuarios").document(userID);// coletado do banco de dados o usuario atual
        documentReference.addSnapshotListener((documentSnapshot, error) -> {
            if(documentSnapshot != null){//verifica se há um usuario
               txt_id_usuario.setText(documentSnapshot.getString("nome"));
               txt_id_email.setText(email);
            }
        });
    }
    private void telaLogin(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(ActivityPerfil.this, ActivityLogin.class);
        startActivity(intent);
    }
    private void iniciarComponentes(){
        btn_deslog = findViewById(R.id.btn_deslog);
        txt_id_email = findViewById(R.id.txt_imagem_email);
        txt_id_usuario = findViewById(R.id.txt_imagem_user);
    }
}