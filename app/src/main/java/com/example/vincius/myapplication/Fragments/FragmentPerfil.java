package com.example.vincius.myapplication.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vincius.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class FragmentPerfil extends Fragment {
    ImageView imageDoPerfil;
    TextView txtUsernamePerfil;
    View view;

    public FragmentPerfil() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startComponents();
    }

    private void startComponents() {
        imageDoPerfil = view.findViewById(R.id.imgPerfilPhoto);
        txtUsernamePerfil = view.findViewById(R.id.txtPerfilUsername);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_perfil, container, false);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String uid = FirebaseFirestore.getInstance().toString();

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if(doc!= null ) {
                        String imagem = doc.getString("profileUrl");
                        String texto = doc.getString("username");
                        txtUsernamePerfil.setText(texto);
                        Picasso.get()
                                .load(imagem)
                                .into(imageDoPerfil);
                        Log.i("teste", "este é o link da imagem  "+ doc.getString("profileUrl"));

                    }
                }
            }
        });

    }
}
