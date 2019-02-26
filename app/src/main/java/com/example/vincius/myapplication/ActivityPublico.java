package com.example.vincius.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.BitSet;
import java.util.UUID;

public class ActivityPublico extends AppCompatActivity {
    private Uri selectedUri;
    private ImageView imgGroup;
    private EditText editNome;
    private Button btnCreate;
    private Button btnImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publico);

        imgGroup = findViewById(R.id.imgPublico);
        editNome = findViewById(R.id.editPublico);
        btnCreate = findViewById(R.id.btnPublico);
        btnImage = findViewById(R.id.btnImagePublico);

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
                criarGrupo(name);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(data.getData() != null){
                selectedUri = data.getData();
            }else{
                finish();
            }

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedUri);
                imgGroup.setImageDrawable(new BitmapDrawable(bitmap));
                btnImage.setAlpha(0);
            }catch (IOException e){
                finish();
            }
        }
    }

    private void criarGrupo(String nome) {

        if(nome == null || nome.isEmpty()){
            Toast.makeText( ActivityPublico.this,"Nome deve ser preenchido!",Toast.LENGTH_SHORT).show();
        }

        if(selectedUri == null){
            Toast.makeText( ActivityPublico.this,"Coloque uma foto para o Grupo!",Toast.LENGTH_SHORT).show();
        }

        salvarGruopoInFirebase();


    }

    private void selecionarFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }


    private void salvarGruopoInFirebase() {
        String filename = UUID.randomUUID().toString();
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/images/" + filename);
        ref.putFile(selectedUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String uid = FirebaseAuth.getInstance().getUid();
                                String groupname = editNome.getText().toString();
                                String profileUrl = uri.toString();
                                String adminUser = FirebaseAuth.getInstance().getCurrentUser().toString();

                                final Group group = new Group(groupname,uid,profileUrl,adminUser);
                                FirebaseFirestore.getInstance().collection("groups")
                                        .document(uid)
                                        .set(group)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Intent intent = new Intent(ActivityPublico.this,ActivityGrupo.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.putExtra("group", group);
                                                startActivity(intent);
                                                Toast.makeText( ActivityPublico.this,"Grupo Criado!",Toast.LENGTH_SHORT).show();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.i("teste", e.getMessage());

                                    }
                                });
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("teste", e.getMessage());
            }
        });
    }
}
