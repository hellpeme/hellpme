package com.example.vincius.myapplication;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityPerfil extends AppCompatActivity {

    private ImageView imgPerfilPhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        imgPerfilPhoto = findViewById(R.id.imgPerfilPhoto);

        if(getIntent().hasExtra("users")) {
            try {
                JSONObject mJsonObject = new JSONObject(getIntent().getStringExtra("users"));
                String username = (String) mJsonObject.get("username");
                String imgPhoto = (String) mJsonObject.get("profileUrl");
                getSupportActionBar().setTitle(username);

                Picasso.get()
                        .load(imgPhoto)
                        .into(imgPerfilPhoto);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        startComponents();

    }

    private void startComponents() {
        imgPerfilPhoto = findViewById(R.id.imgPerfilPhoto);
    }
}
