package com.example.vincius.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class ActivityPerfilGroup extends AppCompatActivity  implements  SeachUsersInterface{
    private ImageView imgPerfilPhoto;
    private TextView txtNameProfileUser,txtAlunos;
    private String username, uuid, photoUrl;
    private Button btnIngress;
    private Group group;
    private HashMap<String,String> listUsersUpdate;
    private List<String> users;
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

        seachListUsers();

        if(listUsersUpdate.containsValue(fromId)){
            btnIngress.setText("Entrar");
        }

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

    private void seachListUsers() {
       SeachUsers seach = new SeachUsers(this,this);
       seach.execute(users);
    }

    private boolean verificadorDeIngresso() {
        if((group.getCurrentNumUsers() < group.getMaxUsers()) &&
                !group.getListIDUser().containsValue(fromId)){
            int currentNum = group.getCurrentNumUsers();
            listUsersUpdate.put("" + (2 + currentNum),fromId);
            FirebaseFirestore.getInstance().collection("groups")
                    .document(group.getUid())
                    .update("currentNumUsers",currentNum + 1 );
            return true;
        } else {
            if(group.getListIDUser().containsValue(fromId)){
                return true;
            } else {
                Toast.makeText(ActivityPerfilGroup.this, "Desculpe, mas esta Monitoria está lotada!", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    }


    private void startComponents() {
        imgPerfilPhoto = findViewById(R.id.imagePerfilPhoto);
        txtAlunos = findViewById(R.id.txtAlunos);
        txtNameProfileUser = findViewById(R.id.textNameUserPerfil);
        btnIngress = findViewById(R.id.btnIngress);
    }

    @Override
    public void posSeach(List<String> listUsernames) {
        txtAlunos.setText(listUsernames.toString());
    }


    private class SeachUsers extends AsyncTask<List<String>,String,List<String>> {

        private Context context;
        private ProgressBar progress;
        private SeachUsersInterface sUI;

        private SeachUsers(Context context,SeachUsersInterface sUI) {
            this.context = context;
            this.sUI = sUI;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressBar(context);
            progress.setVisibility(View.GONE);

        }

        @Override
        protected List<String> doInBackground(List<String>... strings) {
            for(String id : listUsersUpdate.values()){
                FirebaseFirestore.getInstance().collection("users")
                        .document(id)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User user = documentSnapshot.toObject(User.class);
                                users.add(user.getUsername());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });
            }
            return users;
        }


        @Override
        protected void onPostExecute(List<String> users) {
            super.onPostExecute(users);
            sUI.posSeach(users);
            progress.setVisibility(View.INVISIBLE);
        }
    }


}
