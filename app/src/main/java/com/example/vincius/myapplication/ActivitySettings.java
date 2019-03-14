package com.example.vincius.myapplication;

import android.app.Activity;
import android.content.ContentResolver;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.algolia.search.saas.Index;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

public class ActivitySettings extends AppCompatActivity {

    private ImageView imageDoPerfil;
    private TextView txtUsernamePerfil;
    private ListView listView;
    private String photoPerfil;
    private String items[] = new String[]{"Mudar a senha", "Mudar o nome", "Apagar minha conta","Reportar Problema", "Sair"};
    final String uid = FirebaseAuth.getInstance().getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        startComponents();

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if(doc!= null) {
                        photoPerfil = doc.getString("photoUrl");
                        txtUsernamePerfil.setText(doc.getString("username"));
                        Picasso.get()
                                .load(doc.getString("profileUrl"))
                                .into(imageDoPerfil);
                        Log.i("teste", "profile Url: "+ doc.getString("profileUrl"));
                    }
                }
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent intent1 = new Intent(ActivitySettings.this, ActivityAlterarSenha.class);
                        startActivity(intent1);
                        break;
                    case 1:
                        Intent intent2 = new Intent(ActivitySettings.this, ActivitySettingsPerfil.class);
                        startActivity(intent2);
                        break;
                    case 2:
                        Intent intent = new Intent(ActivitySettings.this, ActivityLogin.class);
                        deleteAuth();
                        deleteUser();
                        startActivity(intent);
                        FirebaseAuth.getInstance().signOut();
                        break;
                    case 3:
                        Intent intent3 = new Intent(ActivitySettings.this, ActivityReportarProblema.class);
                        startActivity(intent3);
                        break;
                    case 4:
                        FirebaseAuth.getInstance().signOut();
                        Intent inten = new Intent(ActivitySettings.this, ActivityLogin.class);
                        startActivity(inten);
                        break;
                        default:
                            break;
                }
            }
        });

    }


    private void startComponents() {
        listView = findViewById(R.id.listSettings);
        imageDoPerfil = findViewById(R.id.imgPerfilPhoto);
        txtUsernamePerfil = findViewById(R.id.txtPerfilUsername);
    }

    private void deleteAuth() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("teste", "User account deleted!!");
                }
            }
        });
    }

    private void deleteUser() {
        FirebaseFirestore.getInstance().collection("users").document(uid)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("teste", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("teste", "Error deleting document", e);
                    }
                });
        }
}
