package com.example.vincius.myapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.algolia.instantsearch.core.helpers.Searcher;
import com.algolia.instantsearch.ui.helpers.InstantSearch;
import com.algolia.instantsearch.ui.utils.ItemClickSupport;
import com.algolia.instantsearch.ui.views.Hits;
import com.example.vincius.myapplication.ActivityPerfil;
import com.example.vincius.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;


public class FragmentPesquisa extends Fragment {

    private Searcher searcher;
    private InstantSearch helper;
    private Hits hits;

    View view;

    private static final String YourApplicationID = "BD1URWQ8QG";
    private static final String YourAPIKey = "e0a540e85a53829cd9105babe638cb14";

    public FragmentPesquisa() {
            // Required empty public constructor
        }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        searchAllUsers();
        hits = view.findViewById(R.id.hits);


        hits.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, int position, View v) {
                JSONObject hit = hits.get(position);

                Intent intent = new Intent(getContext(), ActivityPerfil.class);
                intent.putExtra("users", hit.toString());
                startActivity(intent);

            }

        });
        ;
    }

    private void searchAllUsers() {
        String uid = FirebaseAuth.getInstance().getUid();

        DocumentReference ref = FirebaseFirestore.getInstance().collection("users").document(uid);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc != null){
                        Log.i("teste","profileUrl deste usuario "+doc.getString("username"));
                    }
                }
            }
        });

        searcher = Searcher.create(YourApplicationID, YourAPIKey, "users");
        helper = new InstantSearch(getActivity(), searcher);
        
        helper.search();
    }

    @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

               }

    @Override
    public void onDestroy() {
        searcher.destroy();
        super.onDestroy();
    }

    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.fragment_pesquisa, container, false);
            return view;
        }


}

