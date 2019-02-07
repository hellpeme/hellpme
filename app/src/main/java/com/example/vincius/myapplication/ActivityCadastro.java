package com.example.vincius.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ActivityCadastro extends AppCompatActivity {

    private static final String YourApplicationID = "BD1URWQ8QG";
    private static final String YourAPIKey = "f0fb0c31b0a88e819bd76c42fae84f55";
    private EditText editEmail, editSenha, editUsername;
    private Button btnCadastrar, btnSelectPhoto;
    private ImageView img_photo;
    private Uri selectedUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        startComponents();

        btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecionarFoto();
            }
        });

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editEmail.getText().toString();
                String senha = editSenha.getText().toString();
                String username = editUsername.getText().toString();
                criarUser(email, senha, username);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if (data.getData() != null) {
                selectedUri = data.getData();
        }else{
            finish();
        }
            Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedUri);
                    img_photo.setImageDrawable(new BitmapDrawable(bitmap));
                    btnSelectPhoto.setAlpha(0);
                } catch (IOException e) {
                    finish();
                }
            }
        }

    private void selecionarFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    private void criarUser(final String email, final String senha, final String username) {
        if (email == null || email.isEmpty() || senha == null || senha.isEmpty() || username == null || username.isEmpty()){
            alert("Nome, senha e email  devem ser preenchidos!");
            return;
        }
        if (selectedUri == null){
            alert("Coloque uma foto de Perfil!");
            return;
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.i("teste", task.getResult().getUser().getUid());
                            salvarUserInFirebase();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("teste", e.getMessage());
                    }
                });
    }

    private void salvarUserInFirebase() {
        String filename = UUID.randomUUID().toString();
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/images/" + filename);
        ref.putFile(selectedUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.i("teste", uri.toString());

                               String uid = FirebaseAuth.getInstance().getUid();
                               String username = editUsername.getText().toString();
                               String profileUrl = uri.toString();




                               User user =  new User(uid,username,profileUrl);

                               Client client = new Client(YourApplicationID, YourAPIKey);
                               Index index = client.getIndex("users");
                               Map<String, Object> usermap = new HashMap<>();
                               usermap.put("username", username);
                               usermap.put("uid", uid);
                               usermap.put("profileUrl", profileUrl);

                                List<JSONObject> userList = new ArrayList<>();
                                userList.add(new JSONObject(usermap));
                               index.addObjectsAsync(new JSONArray(userList), null);


                               FirebaseFirestore.getInstance().collection("users")
                                       .document(uid)
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Intent intent = new Intent(ActivityCadastro.this, ActivityFragmentsNavigation.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                alert("cadastrado com sucesso!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i("teste", e.getMessage());

                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("teste", e.getMessage());
                    }
                });
    }


    private void alert(String menssagem){
        Toast.makeText(ActivityCadastro.this,menssagem,Toast.LENGTH_SHORT).show();

    }


    private void startComponents() {
        editUsername = findViewById(R.id.viewEditUsername2);
        editEmail = findViewById(R.id.viewEditEmail2);
        editSenha = findViewById(R.id.viewEditSenha2);
        btnCadastrar = findViewById(R.id.btcadastrar);
        btnSelectPhoto = findViewById(R.id.btphoto);
        img_photo = findViewById(R.id.img_photo);

    }
}
