package com.example.vincius.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

public class ActivityMonitoria extends AppCompatActivity {

    private GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoria);
        RecyclerView rv = findViewById(R.id.recyclerChat);

        adapter = new GroupAdapter();
        rv.setLayoutManager( new LinearLayoutManager(  this));
        rv.setAdapter(adapter);

        adapter.add(new MessageItem(true));
        adapter.add(new MessageItem(true));
        adapter.add(new MessageItem(false));
        adapter.add(new MessageItem(true));
        adapter.add(new MessageItem(false));
        adapter.add(new MessageItem(true));
        adapter.add(new MessageItem(false));
        adapter.add(new MessageItem(false));

    }

    private class MessageItem extends Item<ViewHolder> {

        private final boolean is_Right;

        private MessageItem(boolean is_right) {
            is_Right = is_right;
        }


        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {

        }

        @Override
        public int getLayout() {
            return is_Right ? R.layout.message_from_user : R.layout.message_to_user;
        }
    }
}
