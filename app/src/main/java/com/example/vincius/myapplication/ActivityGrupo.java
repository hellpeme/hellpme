package com.example.vincius.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.vincius.myapplication.R;
import com.xwray.groupie.GroupAdapter;

public class ActivityGrupo extends AppCompatActivity {
    private ImageButton btnChat;
    private EditText editChat;
    private GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);
        RecyclerView rv = findViewById(R.id.recyclerChat);

        btnChat =  findViewById(R.id.btnChat);
        editChat = findViewById(R.id.editChat);
        
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

    private void sendMessage() {




    }


}
