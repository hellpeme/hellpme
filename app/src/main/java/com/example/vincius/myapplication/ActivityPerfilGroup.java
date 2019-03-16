package com.example.vincius.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.Toast;
import com.example.vincius.myapplication.Fragments.ContactGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActivityPerfilGroup extends AppCompatActivity {

    private ImageView imgPerfilPhoto;
    private TextView txtNameGroup,txtMonitor, alunos;
    private ListView listAlunos;
    private AdapterUsername adapter;
    private ArrayList<User> users;

    ConstraintSet set = new ConstraintSet();
    ConstraintLayout layout;

    private String nameGroup, uuid, photoUrl,adminUserId;
    private Button btnIngress;
    private Group group;
    private ContactGroup groupFromContact;
    private HashMap<String,String> listUsersUpdate;
    private String fromId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_group);
        startComponents();

        fetchAtributes();
        Picasso.get()
                .load(photoUrl)
                .into(imgPerfilPhoto);

        txtNameGroup.setText(nameGroup);

        fromId = FirebaseAuth.getInstance().getUid();

        seachListUsers();

        set.clone(layout);
        adapter = new AdapterUsername(ActivityPerfilGroup.this, users);
        listAlunos.setAdapter(adapter);

        if(adminUserId.equals(fromId) || listUsersUpdate.containsValue(fromId)){
            btnIngress.setText("Entrar");
        }

        btnIngress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(verificadorDeIngresso()){
                    FirebaseFirestore.getInstance().collection("groups")
                            .document(group.getUid())
                            .update("listIDUser", listUsersUpdate);
                    intent = new Intent(ActivityPerfilGroup.this, ActivityGrupo.class);
                    if(group != null)
                    intent.putExtra("group",group);
                    else{
                        intent.putExtra("group2",groupFromContact);

                    }
                    startActivity(intent);
                }

            }
        });


        alunos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityPerfilGroup.this, ActivityListUserInGroup.class);
                if(group!=null){
                    intent.putExtra("group", group);
                }else{
                    intent.putExtra("group2", groupFromContact);
                }
                startActivity(intent);
            }
        });

    }



    private void fetchAtributes() {
        group = getIntent().getExtras().getParcelable("group");
        if(group instanceof  Group) {
            photoUrl = group.getProfileUrl();
            nameGroup = group.getGroupName();
            uuid = group.getUid();
            adminUserId = group.getAdminUser();
            listUsersUpdate = group.getListIDUser();
        }else{
            groupFromContact = getIntent().getExtras().getParcelable("group2");
            photoUrl = groupFromContact.getPhotoUrl();
            nameGroup = groupFromContact.getUsername();
            uuid = groupFromContact.getUid();
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
                                    txtMonitor.setText(user.getUsername());
                                } else if(listUsersUpdate.containsValue(user.getUid())){
                                    users.add(user);
                                    Log.d("teste perfil", user.getUsername());
                                }
                            }
                        }
                    });


    }

    private boolean verificadorDeIngresso() {
        if((group.getCurrentNumUsers() < group.getMaxUsers()) &&
                (!adminUserId.equals(fromId) && !group.getListIDUser().containsValue(fromId))){
            int currentNum = group.getCurrentNumUsers();
            listUsersUpdate.put("" + (1 + currentNum),fromId);
            FirebaseFirestore.getInstance().collection("groups")
                    .document(group.getUid())
                    .update("currentNumUsers",currentNum + 1 );
            return true;
        } else {
            if(adminUserId.equals(fromId) || group.getListIDUser().containsValue(fromId)){
                return true;
            } else {
                Toast.makeText(ActivityPerfilGroup.this, "Desculpe, mas esta Monitoria está lotada!", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    }


    private void startComponents() {
        alunos = findViewById(R.id.textView4);
        imgPerfilPhoto = findViewById(R.id.imagePerfilPhoto);
        txtMonitor = findViewById(R.id.txtMonitor);
        listAlunos = findViewById(R.id.listAlunos);
        txtNameGroup = findViewById(R.id.textNameUserPerfil);
        btnIngress = findViewById(R.id.btnIngress);
        layout = findViewById(R.id.layoutPG);
    }



    public class AdapterUsername extends ArrayAdapter<User>{
        private class ViewHolder{
            TextView nome;
        }

        private AdapterUsername(Context context, ArrayList<User> users){
            super(context,R.layout.item_user,users);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            User user = getItem(position);
            ViewHolder viewHolder;

            if(convertView == null){
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.item_user,parent,false);
                viewHolder.nome = (TextView) convertView.findViewById(R.id.textNameUserPesquisa);

                convertView.setTag(viewHolder);
            } else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.nome.setText(user.getUsername());

            return convertView;
        }
    }
}
