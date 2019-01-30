package com.example.vincius.myapplication.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.algolia.instantsearch.core.helpers.Searcher;
import com.algolia.instantsearch.ui.helpers.InstantSearch;
import com.example.vincius.myapplication.R;
import com.example.vincius.myapplication.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.support.constraint.Constraints.TAG;


public class FragmentPesquisa extends Fragment {
    private Searcher searcher;
    InstantSearch helper;
    View view;
    private FirebaseFirestore firebaseRef;
    private EditText editTextSearch;
    private static final String YourApplicationID = "BD1URWQ8QG";
    private static final String YourAPIKey = "e0a540e85a53829cd9105babe638cb14";

    public FragmentPesquisa() {
            // Required empty public constructor
        }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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

