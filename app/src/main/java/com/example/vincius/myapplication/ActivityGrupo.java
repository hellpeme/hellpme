package com.example.vincius.myapplication;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vincius.myapplication.Fragments.Contact;
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

import java.util.List;

public class ActivityGrupo extends AppCompatActivity {
    private ImageButton btnChat;
    private EditText editChat;
    private GroupAdapter adapter;
    private User me;
    private Group group;
    private String uuid;

    ConstraintSet set = new ConstraintSet();
    ConstraintLayout layout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);
        RecyclerView rv = findViewById(R.id.recyclerChat);

        btnChat =  findViewById(R.id.btnChat);
        editChat = findViewById(R.id.editChat);

        set.clone(layout);
        layout = findViewById(R.id.layout);

        group = getIntent().getExtras().getParcelable("group");
        uuid = group.getGroupName().toString();

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        
        adapter = new GroupAdapter();
        
        rv.setLayoutManager( new LinearLayoutManager(  this));
        rv.setAdapter(adapter);

        
    }

    private void fetchMessage() {
        if(me != null){
            String fromId = me.getUid();
            String toId = uuid;

            FirebaseFirestore.getInstance().collection("/conversas")
                    .document(toId)
                    .collection("/AllUsers")
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



        //

        if(!message.getText().isEmpty()){

            FirebaseFirestore.getInstance().collection("/conversas")
                    .document(fromId)
                    .collection(toId)
                    .add(message)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d( "Teste",documentReference.getId());

                            // create last messages
                            Contact contact = new Contact();
                            contact.setUid(toId);
                            contact.setUsername(group.getGroupName());
                            contact.setPhotoUrl(group.getProfileUrl());
                            contact.setTimestamp(message.getTimestamp());
                            contact.setLastMessage(message.getText());

                            FirebaseFirestore.getInstance().collection("/last-messages")
                                    .document(fromId)
                                    .collection("contacts")
                                    .document(toId)
                                    .set(contact);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Teste", e.getMessage());
                        }
                    });

            FirebaseFirestore.getInstance().collection("/conversas")
                    .document(toId)
                    .collection("/AllUsers")
                    .add(message)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d( "Teste",documentReference.getId());

                            Contact contact = new Contact();
                            contact.setUid(toId);
                            contact.setUsername(group.getGroupName());
                            contact.setPhotoUrl(group.getProfileUrl());
                            contact.setTimestamp(message.getTimestamp());
                            contact.setLastMessage(message.getText());

                            FirebaseFirestore.getInstance().collection("/last-messages")
                                    .document(toId)
                                    .set(contact);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Teste", e.getMessage());
                        }
                    });

        }


    }

    private class MessageItem extends Item<ViewHolder> {



        private final Message message;

        private MessageItem(Message message) {
            this.message = message;
        }


        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {
            TextView txtChat = viewHolder.itemView.findViewById(R.id.txtChat);
            final ImageView imgChat = viewHolder.itemView.findViewById(R.id.ImgChat);
            txtChat.setText(message.getText());

            DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(message.getFromId());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if(doc!= null ) {
                            Picasso.get()
                                    .load(doc.getString("profileUrl"))
                                    .into(imgChat);
                            Log.i("teste", "este é o link da imagem  "+ message.getPhotoUrl());

                        }
                    }
                }
            });


        }

        @Override
        public int getLayout() {
            return message.getFromId().equals(FirebaseAuth.getInstance().getUid())
                    ? R.layout.message_from_user
                    : R.layout.message_to_user;
        }
    }


}
