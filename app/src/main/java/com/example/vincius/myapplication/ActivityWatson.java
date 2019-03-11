package com.example.vincius.myapplication;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ibm.watson.developer_cloud.assistant.v1.model.InputData;
import com.ibm.watson.developer_cloud.assistant.v1.model.MessageOptions;
import com.ibm.watson.developer_cloud.assistant.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.assistant.v1.Assistant;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;


public class ActivityWatson extends AppCompatActivity {

    private GroupAdapter adapter;
    private String uuid;
    private ImageButton btnChat;
    private EditText editChat;
    private User me;
    private Assistant assistant;

    ConstraintSet set = new ConstraintSet();
    ConstraintLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watson);

        RecyclerView rv = findViewById(R.id.recyclerChat);

        btnChat =  findViewById(R.id.btnChat);
        editChat = findViewById(R.id.editChat);
        layout = findViewById(R.id.layout);

        set.clone(layout);

        fetchAtributes();

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });


        adapter = new GroupAdapter();

        rv.setLayoutManager( new LinearLayoutManager(  this));
        rv.setAdapter(adapter);

        welcome();

        FirebaseFirestore.getInstance().collection("/users")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        me = documentSnapshot.toObject(User.class);
                    }
                });

    }

    private void welcome() {

        final String welcome = "Olá seja bem vindo! Meu nome é Watson e sou um bot e também serei seu assistente e guia. Faça uma pergunta e qualquer dúvida digite Help ou Ajuda.";

        final Message mesgWelcome = new Message();
        mesgWelcome.setText(welcome);
        mesgWelcome.setFromId("Welcome");
        mesgWelcome.setToId(FirebaseAuth.getInstance().getUid());
        mesgWelcome.setTimestamp(System.currentTimeMillis());
        mesgWelcome.setPhotoUrl("null");

        adapter.add(new MessageItem(mesgWelcome));
    }

    private void fetchAtributes() {

        assistant = new Assistant("2019-03-08","apikey","ipq3za265_GpXGRdtMsIYtjhCkgR9EJdphWawF3gN_8o");

        assistant.setEndPoint( "https://gateway-lon.watsonplatform.net/assistant/api");

    }


    private void sendMessage() {

        final String text = editChat.getText().toString();

        editChat.setText(null);

        final String toId = "Watson";

        final long timestamp = System.currentTimeMillis();

        final String fromId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final Message message = new Message();

        message.setFromId(fromId);
        message.setToId(toId);
        message.setTimestamp(timestamp);
        message.setText(text);
        adapter.add(new MessageItem(message));


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    String worskespace = "8fe5bf73-1c77-4e21-97ef-f5ad907f96c0";



                    InputData input = new InputData.Builder(text).build();

                    MessageOptions options = new MessageOptions.Builder(worskespace)
                            .input(input)
                            .build();

                    MessageResponse response = assistant.message(options).execute();

                    Message outputMessage = new Message();
                    if(response != null &&
                            response.getOutput() != null &&
                            "text".equals(response.getOutput().getGeneric().get(0).getResponseType())
                            ){
                        outputMessage.setFromId("Watson");
                        outputMessage.setToId(fromId);
                        outputMessage.setPhotoUrl("null");
                        outputMessage.setTimestamp(timestamp);
                        outputMessage.setText(response.getOutput().getGeneric().get(0).getText());
                        adapter.add(new MessageItem(outputMessage));
                    }

                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        thread.start();



    }

    private class MessageItem extends Item<ViewHolder> {



        private Message message;


        private MessageItem(Message message) {
            this.message = message;
        }

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {
            TextView txtChat = viewHolder.itemView.findViewById(R.id.txtChat);

            if(getLayout() == R.layout.message_to_user) {
                TextView txtNameMessage = viewHolder.itemView.findViewById(R.id.txtNameUserMessage);
                txtNameMessage.setText(null);
            }

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
