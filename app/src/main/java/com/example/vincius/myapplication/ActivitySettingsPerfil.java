package com.example.vincius.myapplication;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

public class ActivitySettingsPerfil extends AppCompatActivity {

    private ImageView imageDoPerfil;
    private EditText editName;
    private Button btnChangePhoto;
    private ImageButton btnChangeName;
    private Uri selectedUri;
    private String photoPerfil;
    final String uid = FirebaseAuth.getInstance().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_perfil);
        startComponents();

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot doc = task.getResult();
                    if(doc!= null) {
                        editName.setText(doc.getString("username"));
                        Picasso.get()
                                .load(doc.getString("profileUrl"))
                                .into(imageDoPerfil);

                        Log.i("teste", "ProfileUrl: "+ doc.getString("profileUrl"));

                    }
                }
            }
        });


        btnChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecionarFoto();
            }
        });
        btnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeNameInFirestore();
            }
        });
        
    }

    private void selecionarFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    private void updatePhoto() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference noteRef = db.collection("users")
                .document(uid);
        noteRef.update("profileUrl", photoPerfil).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("teste", "Profile photo change!");
                }
                else{
                    Log.d("teste", "Profile photo not change at all!");
                }
            }
        });
    }

    private void changeNameInFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference noteRef = db.collection("users")
                .document(uid);
        final String name = editName.getText().toString();

        noteRef.update("username", name).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    editName.setText(name);
                    Log.d("teste", "Username change!");
                }
                else{
                    Log.d("teste", "Username not change at all!");
                }
            }
        });
    }

    private void startComponents() {
        imageDoPerfil = findViewById(R.id.imgPerfilPhoto);
        editName = findViewById(R.id.editTypName);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);
        btnChangeName = findViewById(R.id.btnChangeName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ContentResolver resolver = this.getContentResolver();

        if(resultCode == RESULT_OK) {
            if (data.getData() != null) {
                selectedUri = data.getData();
                changePhotoInFirestore();
            }else{
                this.finish();
            }
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(resolver, selectedUri);
                imageDoPerfil.setImageDrawable(new BitmapDrawable(bitmap));
                btnChangePhoto.setAlpha(0);
            } catch (IOException e) {
                this.finish();
            }
        }

    }

    private void changePhotoInFirestore() {
        String filename = UUID.randomUUID().toString();
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/images/" + filename);
        ref.putFile(selectedUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        photoPerfil = uri.toString();
                        updatePhoto();
                    }
                });
            }
        });
    }

}
