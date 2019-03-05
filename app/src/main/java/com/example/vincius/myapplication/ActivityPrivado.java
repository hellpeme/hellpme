package com.example.vincius.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class ActivityPrivado extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private List<String> names = new ArrayList<>();
    private GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privado);

        RecyclerView rv = findViewById(R.id.recyclePrivate);

        adapter = new GroupAdapter();
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {
                Intent intent = new Intent(ActivityPrivado.this, ActivityMonitoria.class);
                UsersItem userItem = (UsersItem) item;
                intent.putExtra("user", userItem.user);
                startActivity(intent);
            }
        });
        fetchUsers();


    }

    private void fetchUsers() {
        CollectionReference doc = FirebaseFirestore.getInstance().collection("users");
        doc.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                } else {
                    List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot doc :
                            docs) {
                        User user = doc.toObject(User.class);
                        Log.d("Teste", "onEvent: " + user.getUsername());
                        names.add(user.getUsername());
                        adapter.add(new UsersItem(user));
                    }
                }

            }
        });
    }

    private void fetchUpdateUsers(final List<String> newList){
        CollectionReference doc = FirebaseFirestore.getInstance().collection("users");
        doc.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                } else {
                    List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                    adapter.clear();
                    for (DocumentSnapshot doc :
                            docs) {
                        User user = doc.toObject(User.class);
                        if(newList.contains(user.getUsername())){
                            adapter.add(new UsersItem(user));
                            adapter.notifyDataSetChanged();
                        }

                    }
                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(ActivityPrivado.this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userinput = newText.toLowerCase();
        List<String> newList = new ArrayList<>();

        Log.d("teste", "ALL USERS: "+ names.toString());

        for (String name: names) {
            if(name.toLowerCase().contains(userinput)){
                newList.add(name);
                Log.d("teste", "FILTERS USERS: "+ newList.toString());
            }
            fetchUpdateUsers(newList);
        }



        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    private class UsersItem extends Item<ViewHolder> {

        private User user;

        private UsersItem(User user){
            this.user = user;
        }


        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {

            TextView txtUser =  viewHolder.itemView.findViewById(R.id.textNameUserPesquisa);
            ImageView imgUser = viewHolder.itemView.findViewById(R.id.ImageView);

            txtUser.setText(user.getUsername());

            Picasso.get()
                    .load(user.getProfileUrl())
                    .into(imgUser);

        }


        @Override
        public int getLayout() {
            return R.layout.item_user;
        }
    }

}
