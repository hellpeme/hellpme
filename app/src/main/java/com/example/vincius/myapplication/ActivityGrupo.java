package com.example.vincius.myapplication;

import android.content.Intent;
import android.os.Parcelable;
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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vincius.myapplication.Fragments.Contact;
import com.example.vincius.myapplication.Fragments.ContactGroup;
import com.example.vincius.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityGrupo extends AppCompatActivity {
    private ImageButton btnChat;
    private EditText editChat;
    private TextView txtNameGroup;
    private ImageView imageGroup;
    private GroupAdapter adapter;
    private User me;
    private Group group;
    private ContactGroup groupFromContact;
    private String uuid,adminUser,groupName,profileUrl;
    private int currentNumUser, usersMax;
    private HashMap<String,String> listIDUser;
    ConstraintSet set = new ConstraintSet();
    ConstraintLayout layout;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);
        RecyclerView rv = findViewById(R.id.recyclerChat);
        startComponents();
        setSupportActionBar(toolbar);
        set.clone(layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fetchAtributes();

        txtNameGroup.setText(groupName);
        Picasso.get()
                .load(profileUrl)
                .into(imageGroup);

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        txtNameGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityGrupo.this, ActivityPerfilGroup.class);
                if(group != null)
                    intent.putExtra("group", group);
                else
                    intent.putExtra("group2", groupFromContact);
                startActivity(intent);
            }
        });
        adapter = new GroupAdapter();
        
        rv.setLayoutManager( new LinearLayoutManager(  this));
        rv.setAdapter(adapter);

        FirebaseFirestore.getInstance().collection("/groups")
                .document(uuid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        me = documentSnapshot.toObject(User.class);
                        fetchMessage();
                    }
                });
    }

    private void startComponents() {
        toolbar = findViewById(R.id.toolbar_grupo);
        btnChat =  findViewById(R.id.btnChat);
        editChat = findViewById(R.id.editChat);
        layout = findViewById(R.id.layout);
        txtNameGroup = findViewById(R.id.textNameGrupo);
        imageGroup = findViewById(R.id.imageGrupo);
    }

    private void fetchAtributes(){
        group = getIntent().getExtras().getParcelable("group");
        if(group != null) {
            uuid = group.getUid();
            groupName = group.getGroupName();
            profileUrl = group.getProfileUrl();
            adminUser = group.getAdminUser();
            currentNumUser = group.getCurrentNumUsers();
            usersMax = group.getMaxUsers();
            listIDUser = group.getListIDUser();
        }else{
            groupFromContact = getIntent().getExtras().getParcelable("group2");
            uuid = groupFromContact.getUid();
            groupName = groupFromContact.getUsername();
            profileUrl = groupFromContact.getPhotoUrl();
            adminUser = groupFromContact.getAdminUser();
            currentNumUser = groupFromContact.getCurrentNumUsers();
            usersMax = groupFromContact.getMaxUsers();
            listIDUser = groupFromContact.getListIDUser();
        }
    }

    private void fetchMessage() {
        if(me != null){
            String fromId = me.getUid();
            String toId = uuid;

            FirebaseFirestore.getInstance().collection("/conversas")
                    .document(toId)
                    .collection("/AllMensagens")
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();

                            if(documentChanges != null){
                                for (DocumentChange doc : documentChanges) {
                                    if (doc.getType() == DocumentChange.Type.ADDED){
                                        Message message = doc.getDocument().toObject(Message.class);
                                        adapter.add(new ActivityGrupo.MessageItem(message));
                                        set.clear(R.id.txtChat, ConstraintSet.TOP);
                                    }
                                }
                            }
                        }
                    });

        }

    }

    private void sendMessage() {

        String text = editChat.getText().toString();


        editChat.setText(null);
        final String toId = uuid;
        long timestamp = System.currentTimeMillis();
        final String fromId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final Message message = new Message();
        message.setFromId(fromId);
        message.setToId(toId);
        message.setTimestamp(timestamp);
        message.setText(text);


            FirebaseFirestore.getInstance().collection("/conversas")
                    .document(toId)
                    .collection("/AllMensagens")
                    .add(message)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d( "Teste",documentReference.getId());

                            ContactGroup contact = new ContactGroup();
                            contact.setUid(toId);
                            contact.setTimestamp(message.getTimestamp());
                            contact.setLastMessage(message.getText());
                            contact.setUsername(groupName);
                            contact.setPhotoUrl(profileUrl);
                            contact.setAdminUser(adminUser);
                            contact.setCurrentNumUsers(currentNumUser);
                            contact.setMaxUsers(usersMax);
                            contact.setListIDUser(listIDUser);

                            //Vai mandar a mensagem para o Admin da Monitoria
                            FirebaseFirestore.getInstance().collection("/last-messages")
                                    .document(adminUser)
                                    .collection("contactsgroups")
                                    .document(uuid)
                                    .set(contact);

                            //Manda Mensagem para todos os Usuarios do Grupo
                            if(currentNumUser > 0) {
                                for (String userIdGroup : listIDUser.values()) {
                                    if (!userIdGroup.isEmpty() || userIdGroup != null) {
                                        FirebaseFirestore.getInstance().collection("/last-messages")
                                                .document(userIdGroup)
                                                .collection("contactsgroups")
                                                .document(uuid)
                                                .set(contact);
                                    }
                                }
                            } else{
                                Toast.makeText(ActivityGrupo.this,
                                        "Ainda não existe alunos nesta Monitoria, espere até que algum aluno entre para iniciar a Monitoria!",Toast.LENGTH_SHORT);
                            }


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Teste", e.getMessage());
                        }
                    });


    }

    private class MessageItem extends Item<ViewHolder> {



        private final Message message;

        private MessageItem(Message message) {
            this.message = message;
        }


        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {
            final TextView txtChat = viewHolder.itemView.findViewById(R.id.txtChat);
            TextView timestamp = viewHolder.itemView.findViewById(R.id.timestamp);
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            Date d = new Date(message.getTimestamp());
            String date = format.format(d);

            if(getLayout() == R.layout.message_to_user) {
                final TextView txtNameMessage = viewHolder.itemView.findViewById(R.id.txtNameUserMessage);



                timestamp.setText(date);

                FirebaseFirestore.getInstance().collection("users")
                        .document(message.getFromId())
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                                txtNameMessage.setText(documentSnapshot.getString("username"));
                            }
                        });

            }

            txtChat.setText(message.getText());
            timestamp.setText(date);
        }

        @Override
        public int getLayout() {
            return message.getFromId().equals(FirebaseAuth.getInstance().getUid())
                    ? R.layout.message_from_user
                    : R.layout.message_to_user;
        }
    }


}
