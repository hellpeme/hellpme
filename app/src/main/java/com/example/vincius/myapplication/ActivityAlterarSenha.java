package com.example.vincius.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivityAlterarSenha extends AppCompatActivity {
    private EditText txtAntigaSenha, txtNovaSenha,txtConfirmarNovaSenha;
    private Button btnAlterarSenha2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_senha);
        starComponents();
        btnAlterarSenha2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlterarSenha();
            }
        });
    }

    private void starComponents(){
        txtAntigaSenha = (EditText) findViewById(R.id.txtAntigaSenha);
        txtNovaSenha = (EditText) findViewById(R.id.txtNovaSenha);
        txtConfirmarNovaSenha = (EditText) findViewById(R.id.txtConfirmarNovaSenha);
        btnAlterarSenha2 = (Button)findViewById(R.id.btnAlterarSenha2);


    }


    public void AlterarSenha(){
        Log.d("teste", "Button");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String antigaSenha = txtAntigaSenha.getText().toString();

        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(),antigaSenha);
        currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String novaSenha = txtNovaSenha.getText().toString();
                String confirmarSenha = txtConfirmarNovaSenha.getText().toString();
                if(task.isSuccessful()) {
                    Log.d("teste", "User re-authenticated");

                    if (novaSenha.equals(confirmarSenha)) {
                        FirebaseAuth.getInstance().getCurrentUser().updatePassword(novaSenha);
                        Intent intent = new Intent(ActivityAlterarSenha.this, ActivityFragmentsNavigation.class);
                        alert("Sua senha foi alterada com sucesso");
                        startActivity(intent);
                    } else {
                        alert("As senhas não coincidem");
                    }
                }else {
                    Log.d("teste", "User re-authenticated error");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("teste", "The authetification failed");
                alert("Senha errada");
            }
        });
    }

    private void alert(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}