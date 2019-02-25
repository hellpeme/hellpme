package com.example.vincius.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class ActivityPublico extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publico);

        ImageView imgGroup = findViewById(R.id.imgPublico);
        final EditText editNome = findViewById(R.id.editPublico);
        Button btnCreate = findViewById(R.id.btnPublico);
        Button btnImage = findViewById(R.id.btnImagePublico);

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecionarFoto();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editNome.getText().toString();
                criarGruop();
            }
        });



    }

    private void criarGruop() {
    }



    private void selecionarFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }
}
