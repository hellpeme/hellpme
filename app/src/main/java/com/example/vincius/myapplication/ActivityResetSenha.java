package com.example.vincius.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ActivityResetSenha extends AppCompatActivity {

    private  Button btnSendReset;
    private EditText emailText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_senha);
        startComponents();
        btnSendReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailUser = emailText.getText().toString();

                SendEmailtoResetPass(emailUser);
            }
        });

    }

    private void SendEmailtoResetPass(String emailUser) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(emailUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("teste", "email enviado!");
                    alert("Verifique o seu email para alterar a senha");
                }else{
                    Log.d("teste", "email nao enviado!");
                    alert("Email invalido");
                }
            }
        });
    }

    private void alert(String menssagem) {
        Toast.makeText(this, menssagem, Toast.LENGTH_SHORT).show();
    }

    private void startComponents() {
        btnSendReset = (Button)findViewById(R.id.btnSendReset);
        emailText = (EditText)findViewById(R.id.editEmailReset);
    }
}
