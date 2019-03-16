package com.example.vincius.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vincius.myapplication.Fragments.Contact;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class ActivityPerfil extends AppCompatActivity {

    private ImageView imgPerfilPhoto;
    private TextView txtNameProfileUser, txtPontosUser;
    private String username, uuid, photoUrl, fromID;
    private Button btnIngress, btnMais, btnMenos;
    private User user;
    private Contact userFromContact;
    private int valor;
    private Integer pontos = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        startComponents();
        fetchAtributes();
        fetchPontos();
        checkPointsState();

        Picasso.get()
                .load(photoUrl)
                .into(imgPerfilPhoto);

        txtNameProfileUser.setText(username);
        btnIngress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityPerfil.this,ActivityMonitoria.class);
                if(user instanceof User)
                intent.putExtra("user", user);
                else
                    intent.putExtra("user2", userFromContact);
                startActivity(intent);
            }
        });

        btnMais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valor = 1;
                updatePontos(valor);
                btnMais.setVisibility(View.INVISIBLE);
                btnMenos.setVisibility(View.VISIBLE);
            }
        });

        btnMenos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valor = -1;
                Log.d("teste", "vc clicou em menos");
                updatePontos(valor);
                btnMais.setVisibility(View.VISIBLE);
                btnMenos.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void checkPointsState() {
        CollectionReference ref = FirebaseFirestore.getInstance().collection("pontos");
        fromID = FirebaseAuth.getInstance().getUid();

            ref.document(fromID)
                    .collection(uuid)
                    .document("PointState").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    Point point = documentSnapshot.toObject(Point.class);
                    if (point != null) {
                        if (point.isSend()) {
                            btnMais.setVisibility(View.INVISIBLE);
                            btnMenos.setVisibility(View.VISIBLE);
                        } else {
                            btnMais.setVisibility(View.VISIBLE);
                            btnMenos.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            });

        }

    private void updatePontos(int valor) {
        //save state
        fromID = FirebaseAuth.getInstance().getUid();
        boolean pontuou = false;
        final Point point = new Point();
        Log.d("pontos","valor = "+valor);
        if(valor == 1) {
            pontuou = true;
        }

        Log.d("pontos","pontuou = "+pontuou);
        point.setSend(pontuou);
        point.setToID(uuid);
        point.setTimestamp(System.currentTimeMillis());

        FirebaseFirestore.getInstance().collection("pontos")
                .document(fromID)
                .collection(uuid)
                .document("PointState")
                .set(point).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });

        FirebaseFirestore.getInstance().collection("users").document(uuid).update("pontos", pontos + valor)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("teste", "onSuccess: Atualizado!");
                    }
                });
    }

    private void fetchPontos() {
        FirebaseFirestore.getInstance().collection("users").document(uuid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot != null) {
                    User user = documentSnapshot.toObject(User.class);
                    pontos = user.getPontos();
                    txtPontosUser.setText(pontos.toString());
                }
            }
        });
    }

    private void startComponents() {
        imgPerfilPhoto = findViewById(R.id.imagePerfilPhoto);
        txtNameProfileUser = findViewById(R.id.textNameUserPerfil);
        txtPontosUser = findViewById(R.id.textPontos);
        btnIngress = findViewById(R.id.btnIngress);
        btnMais = findViewById(R.id.btnPontuarMais);
        btnMenos = findViewById(R.id.btnPontuarMenos);
    }

    private void fetchAtributes() {

        user = getIntent().getExtras().getParcelable("user");
        if(user != null){
            photoUrl = user.getProfileUrl();
            username = user.getUsername();
            uuid = user.getUid();
        }else{
            userFromContact = getIntent().getExtras().getParcelable("user2");
            photoUrl = userFromContact.getPhotoUrl();
            username = userFromContact.getUsername();
            uuid = userFromContact.getUid();
        }


    }
}
