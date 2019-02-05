package com.example.vincius.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ActivityLogin extends AppCompatActivity {

    private TextView textRegister, textForgotPass;
    private EditText editEmail, editSenha;
    private Button btlogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        startComponents();


        btlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString();
                String senha = editSenha.getText().toString();
                login(email,senha);
            }
        });

        textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ActivityLogin.this, ActivityCadastro.class);
                startActivity(i);
            }

        });

        textForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ActivityLogin.this, ActivityResetSenha.class);
                startActivity(i);
            }

        });

    }

    private void login(String email, String senha) {
        if (email == null || email.isEmpty() || senha == null || senha.isEmpty()) {
            alert("Login e senha devem ser preechidos");
            return;
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha)
               .addOnCompleteListener(ActivityLogin.this, new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()){
                           Intent i = new Intent(ActivityLogin.this, ActivityHome.class);
                           i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                           startActivity(i);
                           alert("Login realizado com sucesso!");
                       }else{
                           alert("email ou senha errados!");
                       }
                   }
               });
    }

    private void verficarAuth() {
        if (FirebaseAuth.getInstance().getUid() != null) {
            Intent intent = new Intent(ActivityLogin.this, ActivityHome.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void alert(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void startComponents() {
            editEmail = findViewById(R.id.viewEditEmail);
            editSenha = findViewById(R.id.viewEditSenha);
            btlogin = findViewById(R.id.btlogin);
            textRegister = findViewById(R.id.textRegister);
            textForgotPass = findViewById(R.id.textForgotPass);
            verficarAuth();
    }
}
