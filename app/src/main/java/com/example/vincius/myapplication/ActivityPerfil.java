package com.example.vincius.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class ActivityPerfil extends AppCompatActivity {

    private ImageView imgPerfilPhoto;
    private TextView txtNameProfileUser;
    private String username, uuid, photoUrl;
    private Button btnIngress;
    private User user;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        startComponents();
        fetchAtributes();

        final String fromId = FirebaseAuth.getInstance().getUid().toString();

        btnIngress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityPerfil.this,ActivityMonitoria.class);
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });


    }

    private void startComponents() {
        imgPerfilPhoto = findViewById(R.id.imagePerfilPhoto);
        txtNameProfileUser = findViewById(R.id.textNameUserPerfil);
        btnIngress = findViewById(R.id.btnIngress);
    }

    private void fetchAtributes() {

        user = getIntent().getExtras().getParcelable("user");

        photoUrl = user.getProfileUrl();
        username = user.getUsername();
        uuid = user.getUid();

        //setando valores recebidos no imageview/textview
        Picasso.get()
                .load(photoUrl)
                .into(imgPerfilPhoto);

        txtNameProfileUser.setText(username);

    }
}
