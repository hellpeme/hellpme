package com.example.vincius.myapplication;

import android.app.ActionBar;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vincius.myapplication.Fragments.APIService;
import com.example.vincius.myapplication.Fragments.Contact;
import com.example.vincius.myapplication.Notifications.Client;
import com.example.vincius.myapplication.Notifications.Data;
import com.example.vincius.myapplication.Notifications.MyResponse;
import com.example.vincius.myapplication.Notifications.Sender;
import com.example.vincius.myapplication.Notifications.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityMonitoria extends AppCompatActivity {

    private GroupAdapter adapter;
    private TextView txtnameUser;
    private ImageView imageUser;
    private RecyclerView rv;
    private String username, uuid,photoUrl;
    private ImageButton btnChat;
    private EditText editChat;
    private User me, user;
    private Contact userFromContact;
    private Toolbar toolbar;
    boolean dontsend = false;
    APIService apiService;
    boolean notify = false;
    ConstraintSet set = new ConstraintSet();
    ConstraintLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoria);
        startComponents();
        setSupportActionBar(toolbar);
        set.clone(layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fetchAtributes();

        txtnameUser.setText(username);

        Picasso.get()
                .load(photoUrl)
                .into(imageUser);

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                sendMessage();
            }
        });

        txtnameUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityMonitoria.this, ActivityPerfil.class);
                if(user != null)
                intent.putExtra("user", user);
                else
                    intent.putExtra("user2", userFromContact);
                startActivity(intent);
            }
        });

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


        apiService = Client.getCliente("https://fcm.googleapis.com/").create(APIService.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem denuncia = menu.findItem(R.id.denuncia);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.denuncia:
                Intent i = new Intent(ActivityMonitoria.this, ActivityDenunciar.class);
                i.putExtra("user", user);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startComponents() {
        toolbar = findViewById(R.id.toolbar_monitoria);
        rv = findViewById(R.id.recyclerChat);
        btnChat =  findViewById(R.id.btnChat);
        editChat = findViewById(R.id.editChat);
        layout = findViewById(R.id.layout);
        imageUser = findViewById(R.id.imageUserMonitoria);
        txtnameUser = findViewById(R.id.textNameUserMonitoria);
    }

    private void fetchAtributes() {
        user = getIntent().getExtras().getParcelable("user");
        if(user != null) {
            username = user.getUsername();
            uuid = user.getUid();
            photoUrl = user.getProfileUrl();
        }else {
            userFromContact = getIntent().getExtras().getParcelable("user2");
            username = userFromContact.getUsername();
            uuid = userFromContact.getUid();
            photoUrl = userFromContact.getPhotoUrl();
        }
    }

    private void fetchMessage() {
        if(me != null){
            String fromId = me.getUid();
            String toId = uuid;

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

                                        rv.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                // Call smooth scroll
                                                rv.smoothScrollToPosition(adapter.getItemCount() - 1);
                                            }
                                        });

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

        if(uuid == fromId){
            boolean dontsend = true;
        }

        //

        if(!message.getText().isEmpty()) {

            FirebaseFirestore.getInstance().collection("/conversas")
                    .document(fromId)
                    .collection(toId)
                    .add(message)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("Teste", documentReference.getId());

                            rv.post(new Runnable() {
                                @Override
                                public void run() {
                                    // Call smooth scroll
                                    rv.smoothScrollToPosition(adapter.getItemCount() - 1);
                                }
                            });

                            // create last messages
                            Contact contact = new Contact();
                            contact.setUid(toId);
                            contact.setUsername(username);
                            contact.setPhotoUrl(photoUrl);
                            contact.setTimestamp(message.getTimestamp());
                            contact.setLastMessage(message.getText());

                            FirebaseFirestore.getInstance().collection("/last-messages")
                                    .document(fromId)
                                    .collection("contacts")
                                    .document(toId)
                                    .set(contact);

                            final String msg = message.toString();

                            DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(fromId);
                            reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                    User user = documentSnapshot.toObject(User.class);
                                    if (notify) {
                                        sendNotification(toId, user.getUsername(), msg);
                                    }
                                    notify = false;
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Teste", e.getMessage());
                        }
                    });
            if (dontsend) {
                FirebaseFirestore.getInstance().collection("/conversas")
                        .document(toId)
                        .collection(fromId)
                        .add(message)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("Teste", documentReference.getId());

                                rv.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Call smooth scroll
                                        rv.smoothScrollToPosition(adapter.getItemCount() - 1);
                                    }
                                });

                                final Contact contact = new Contact();
                                contact.setUid(fromId);
                                contact.setUsername(me.getUsername());
                                contact.setPhotoUrl(me.getProfileUrl());
                                contact.setTimestamp(message.getTimestamp());
                                contact.setLastMessage(message.getText());

                                FirebaseFirestore.getInstance().collection("/last-messages")
                                        .document(toId)
                                        .collection("contacts")
                                        .document(fromId)
                                        .set(contact);

                                final String msg = message.toString();

                                DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(toId);
                                reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                        User user = documentSnapshot.toObject(User.class);
                                        if (notify) {
                                            sendNotification(username, user.getUsername(), msg);
                                        }
                                        notify = false;
                                    }
                                });


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


    }

    private void sendNotification(final String username, final String username1, final String msg) {
        FirebaseFirestore.getInstance().collection("Tokens")
                .document(uuid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot doc, @Nullable FirebaseFirestoreException e) {
                Token token =  doc.toObject(Token.class);
                Data data = new Data(FirebaseAuth.getInstance().getUid(), R.mipmap.ic_launcher, username1+": lhe enviou uma mensagem!","Nova Mensagem!",uuid);
                Sender sender = new Sender(data, token.getToken());

                apiService.sendNotification(sender)
                        .enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                if(response.code() == 200){
                                    if(response.body().success != 1){
                                        Toast.makeText(ActivityMonitoria.this, "Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                Log.d("tokenwhy", t.getMessage());
                            }
                        });
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
            TextView txtChat = viewHolder.itemView.findViewById(R.id.txtChat);
            TextView timestamp = viewHolder.itemView.findViewById(R.id.timestamp);

            if(getLayout() == R.layout.message_to_user) {
                TextView txtNameMessage = viewHolder.itemView.findViewById(R.id.txtNameUserMessage);
                txtNameMessage.setText(null);
            }

            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            Date d = new Date(message.getTimestamp());
            String date = format.format(d);
            timestamp.setText(date);
            txtChat.setText(message.getText());
        }

        @Override
        public int getLayout() {
            return message.getFromId().equals(FirebaseAuth.getInstance().getUid())
                    ? R.layout.message_from_user
                    : R.layout.message_to_user;
        }
    }
}
