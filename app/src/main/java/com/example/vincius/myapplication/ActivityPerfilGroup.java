package com.example.vincius.myapplication;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ActivityPerfilGroup extends AppCompatActivity {
    private ImageView imgPerfilPhoto;
    private TextView txtNameProfileUser,txtAlunos;
    private String username, uuid, photoUrl;
    private Button btnIngress;
    private Group group;
    private HashMap<String,String> listUsersUpdate;
    private String fromId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_group);
        startComponents();

        group = getIntent().getExtras().getParcelable("group");

        photoUrl = group.getProfileUrl();
        username = group.getGroupName();
        uuid = group.getUid();
        listUsersUpdate = group.getListIDUser();

        //setando valores recebidos no imageview/textview
        Picasso.get()
                .load(photoUrl)
                .into(imgPerfilPhoto);

        txtNameProfileUser.setText(username);

        fromId = FirebaseAuth.getInstance().getUid().toString();


        txtAlunos.setText(listUsersUpdate.values().toString());
        btnIngress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(verificadorDeIngresso()){
                    FirebaseFirestore.getInstance().collection("groups")
                            .document(group.getUid())
                            .update("listIDUser", listUsersUpdate);
                    intent = new Intent(ActivityPerfilGroup.this, ActivityGrupo.class);
                    intent.putExtra("group",group);
                    startActivity(intent);
                }

            }
        });


    }

    private boolean verificadorDeIngresso() {
        int countUser = 0;
        if(group.getCurrentNumUsers() < group.getMaxUsers()){
            int currentNum = group.getCurrentNumUsers();
            listUsersUpdate.put("" + (2 + currentNum),fromId);
            FirebaseFirestore.getInstance().collection("groups")
                    .document(group.getUid())
                    .update("currentNumUsers",currentNum + 1 );
            return true;
        } else {
            Toast.makeText(ActivityPerfilGroup.this, "Desculpe, mas esta Monitoria está lotada!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    private void startComponents() {
        imgPerfilPhoto = findViewById(R.id.imagePerfilPhoto);
        txtAlunos = findViewById(R.id.txtAlunos);
        txtNameProfileUser = findViewById(R.id.textNameUserPerfil);
        btnIngress = findViewById(R.id.btnIngress);
    }


}
