package com.example.vincius.myapplication.Fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.algolia.search.saas.Index;
import com.example.vincius.myapplication.ActivityLogin;
import com.example.vincius.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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

import static android.app.Activity.RESULT_OK;

public class FragmentPerfil extends Fragment {
    private ImageView imageDoPerfil;
    private TextView txtUsernamePerfil;
    private EditText editName;
    private Button btnlogOf, btnDeleteUser, btnChangeName;
    private ImageButton btnChangePhoto;
    private Uri selectedUri;
    private String photoPerfil;
    //algolia api
    private Index index;
    View view;

    final String uid = FirebaseAuth.getInstance().getUid();

    public FragmentPerfil() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    private void startComponents() {
        imageDoPerfil = (ImageView) view.findViewById(R.id.imgPerfilPhoto);
        txtUsernamePerfil = (TextView) view.findViewById(R.id.txtPerfilUsername);
        editName = (EditText) view.findViewById(R.id.editTypName);
        btnlogOf = (Button) view.findViewById(R.id.btnLogOf);
        btnChangeName = (Button) view.findViewById(R.id.btnChangeName);
        btnDeleteUser = (Button) view.findViewById(R.id.btnDeleteUser);
        btnChangePhoto = (ImageButton) view.findViewById(R.id.btnChangePhoto);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_perfil, container, false);
        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ContentResolver resolver = getActivity().getContentResolver();

        if(resultCode == RESULT_OK) {
            if (data.getData() != null) {
                selectedUri = data.getData();
                changePhotoInFirestore();
            }else{
                getActivity().finish();
            }
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(resolver, selectedUri);
                imageDoPerfil.setImageDrawable(new BitmapDrawable(bitmap));
            } catch (IOException e) {
                getActivity().finish();
            }
        }

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

                        Log.i("teste", "este é o link da miass "+ doc.getString("profileUrl"));

                    }
                }
            }
        });

        btnlogOf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), ActivityLogin.class);
                startActivity(intent);
            }
        });

        btnDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityLogin.class);
                deleteAuth();
                deleteUser();
                startActivity(intent);
                FirebaseAuth.getInstance().signOut();
            }
        });

        btnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeNameInFirestore();

            }
        });

        btnChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecionarFoto();
            }
        });
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


    private void selecionarFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    private void updateName(final String name) {
        txtUsernamePerfil.setText(name);
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
                    updateName(name);
                    Log.d("teste", "Username change!");
                }
                else{
                    Log.d("teste", "Username not change at all!");
                }
            }
        });
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
