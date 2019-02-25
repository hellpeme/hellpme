package com.example.vincius.myapplication;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityPerfil extends AppCompatActivity {

    private ImageView imgPerfilPhoto;
    private TextView txtNameProfileUser;
    private String username, uuid, photoUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        startComponents();
        fetchAtributes();



    }


    private void startComponents() {
        imgPerfilPhoto = findViewById(R.id.imagePerfilPhoto);
        txtNameProfileUser = findViewById(R.id.textNameUserPerfil);
    }

    private void fetchAtributes() {

        User user = getIntent().getExtras().getParcelable("user");
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
