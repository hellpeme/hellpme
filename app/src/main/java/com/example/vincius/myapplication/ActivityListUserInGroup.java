package com.example.vincius.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.vincius.myapplication.Fragments.ContactGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActivityListUserInGroup extends AppCompatActivity {

    private ArrayList<User> users;
    private ListView listAlunos;
    private Button btnIngress;
    private Group group;
    private ContactGroup groupFromContact;
    private HashMap<String,String> listUsersUpdate;
    private String fromId;
    private AdapterUsername adapter;
    private String nameGroup, uuid, photoUrl,adminUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user_in_group);
        getSupportActionBar().setTitle("Usuarios do grupo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        startComponents();
        fetchAtributes();
        seachListUsers();

        adapter = new AdapterUsername(ActivityListUserInGroup.this, users);
        listAlunos.setAdapter(adapter);
    }

    private void startComponents() {
        listAlunos = findViewById(R.id.listAlunosGroup);
    }


    private void fetchAtributes() {
        group = getIntent().getExtras().getParcelable("group");
        if(group != null) {
            photoUrl = group.getProfileUrl();
            adminUserId = group.getAdminUser();
            listUsersUpdate = group.getListIDUser();
        }else{
            groupFromContact = getIntent().getExtras().getParcelable("group2");
            photoUrl = groupFromContact.getPhotoUrl();
            adminUserId = groupFromContact.getAdminUser();
            listUsersUpdate = groupFromContact.getListIDUser();
        }
    }

    private void seachListUsers() {
        users = new ArrayList<User>();

        FirebaseFirestore.getInstance().collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot doc : docs){
                            User user = doc.toObject(User.class);
                            if(user.getUid().equals(adminUserId)){
                            } else if(listUsersUpdate.containsValue(user.getUid())){
                                users.add(user);
                                Log.d("teste perfil", user.getUsername());
                            }
                        }
                    }
                });


    }


    public class AdapterUsername extends ArrayAdapter<User>{
        private class ViewHolder{
            TextView nome;
            ImageView imageView;
        }

        private AdapterUsername(Context context, ArrayList<User> users){
            super(context,R.layout.item_user,users);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            User user = getItem(position);
            AdapterUsername.ViewHolder viewHolder;

            if(convertView == null){
                viewHolder = new AdapterUsername.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.item_user,parent,false);
                viewHolder.nome = (TextView) convertView.findViewById(R.id.textNameUserPesquisa);
                viewHolder.imageView =(ImageView) convertView.findViewById(R.id.ImageView);

                convertView.setTag(viewHolder);
            } else{
                viewHolder = (AdapterUsername.ViewHolder) convertView.getTag();
            }
            Picasso.get()
                    .load(user.getProfileUrl())
                    .into(viewHolder.imageView);
            viewHolder.nome.setText(user.getUsername());

            return convertView;
        }
    }

}
