package com.example.vincius.myapplication;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ActivityMonitoria extends AppCompatActivity {

    private GroupAdapter adapter;
    private String username;
    private String imgPhoto;
    private EditText editChat;
    private String Uuid;
    private User me;
    private String imgUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoria);
        RecyclerView rv = findViewById(R.id.recyclerChat);

        Button btnChat = (Button) findViewById(R.id.btnChat);
        editChat = findViewById(R.id.editChat);

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();

            }
        });

        if(getIntent().hasExtra("users")) {
            try {
                JSONObject mJsonObject = new JSONObject(getIntent().getStringExtra("users"));
                username = (String) mJsonObject.get("username");
                getSupportActionBar().setTitle(username);
                imgPhoto = (String) mJsonObject.get("profileUrl");
                Uuid = (String) mJsonObject.get("uid");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        adapter = new GroupAdapter();
        rv.setLayoutManager( new LinearLayoutManager(  this));
        rv.setAdapter(adapter);

        FirebaseFirestore.getInstance().collection("/users")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        me = documentSnapshot.toObject(User.class);
                        fetchMessage();
                    }
                });

    }

    private void fetchMessage() {
        if(me != null){
            String fromId = me.getUid();
            String toId = Uuid;

            FirebaseFirestore.getInstance().collection("/conversas")
                    .document(fromId)
                    .collection(toId)
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();

                            if(documentChanges != null){
                                for (DocumentChange doc : documentChanges) {
                                    if (doc.getType() == DocumentChange.Type.ADDED){
                                        Message message = doc.getDocument().toObject(Message.class);
                                        adapter.add(new MessageItem(message));
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
        String toId = Uuid;
        long timestamp = System.currentTimeMillis();
        String fromId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final Message message = new Message();
        message.setFromId(fromId);
        message.setToId(toId);
        message.setTimestamp(timestamp);
        message.setText(text);

        imgUsuario = fromId;


        //


        if(!message.getText().isEmpty()){
            getDocumentsinFirebase(message,fromId);
            FirebaseFirestore.getInstance().collection("/conversas")
                    .document(fromId)
                    .collection(toId)
                    .add(message)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d( "Teste",documentReference.getId());
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
                    .collection(fromId)
                    .add(message)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d( "Teste",documentReference.getId());
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

    private void getDocumentsinFirebase(final Message message, String fromId) {

    }

    private void alert(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT);
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


            DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(imgUsuario);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot doc = task.getResult();
                        if(doc!= null) {
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
