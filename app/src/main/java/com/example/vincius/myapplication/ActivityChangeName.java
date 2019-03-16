package com.example.vincius.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vincius.myapplication.Fragments.FragmentHome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ActivityChangeName extends AppCompatActivity {

    private EditText editName;
    private Button btnCancel, btnOk;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);
        startComponents();
        fetchName();



        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = editName.getText().toString();
                if(input.isEmpty()||input.equals("") || input == null){
                    Toast.makeText(ActivityChangeName.this, "Você não pode ficar sem nome", Toast.LENGTH_SHORT).show();
                }else if(input.length() >= 25){
                    Toast.makeText(ActivityChangeName.this, "Seu nome esta muito grande!", Toast.LENGTH_SHORT).show();
                }
                else{
                    changeNameInFirestore();
                    Intent intent = new Intent(ActivityChangeName.this, ActivitySettingsPerfil.class);
                    startActivity(intent);
                }

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = editName.getText().toString();
                    Intent intent = new Intent(ActivityChangeName.this, ActivitySettingsPerfil.class);
                    startActivity(intent);
            }
        });

    }

    private void fetchName() {
        uid = FirebaseAuth.getInstance().getUid();
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if(doc!= null) {
                        editName.setText(doc.getString("username"));
                        Log.i("teste", "edit text name change!");
                    }
                }
            }
        });

    }

    private void changeNameInFirestore() {
        uid = FirebaseAuth.getInstance().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference noteRef = db.collection("users")
                .document(uid);
        final String name = editName.getText().toString();

        noteRef.update("username", name).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    editName.setText(name);
                    FragmentHome.adapter.notifyDataSetChanged();
                    Log.d("teste", "Username change!");
                }
                else{
                    Log.d("teste", "Username not change at all!");
                }
            }
        });
    }

    private void startComponents() {
        editName = findViewById(R.id.editText);
        btnCancel = findViewById(R.id.btnCancel);
        btnOk = findViewById(R.id.btnOk);
    }
}
