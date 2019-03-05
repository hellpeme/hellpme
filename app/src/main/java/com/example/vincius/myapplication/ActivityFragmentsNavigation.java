package com.example.vincius.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vincius.myapplication.Adapters.FragmentAdapter;
import com.example.vincius.myapplication.Fragments.FragmentPesquisa;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import java.util.ArrayList;
import java.util.List;


public class ActivityFragmentsNavigation extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private List<String> names = new ArrayList<>();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public MenuItem menuItem;
    public GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragmentsnavigation);
        adapter = new GroupAdapter();
        getSupportActionBar().setElevation(0);
        verficarAuth();
        startComponents();
        fetchUsers();
        FragmentPesquisa.setAdapter(adapter);


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

    private void verficarAuth() {
        if (FirebaseAuth.getInstance().getUid() == null) {
            Intent intent = new Intent(ActivityFragmentsNavigation.this, ActivityLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }


    private void startComponents() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.titles_bar)));
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_options, menu);
        menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        String userinput = s.toLowerCase();
        List<String> newList = new ArrayList<>();

        Log.d("teste", "ALL USERS: " + names.toString());

        for (String name : names) {
            if (name.toLowerCase().contains(userinput)) {
                newList.add(name);
                Log.d("teste", "FILTERS USERS: " + newList.toString());
            }
            fetchUpdateUsers(newList);
        }

        return false;
    }

    private void fetchUpdateUsers(final List<String> newList) {
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
                        if (newList.contains(user.getUsername())) {
                            adapter.add(new UsersItem(user));
                            adapter.notifyDataSetChanged();
                        }

                    }
                }
            }
        });
    }

    public static class UsersItem extends Item<ViewHolder> {

        public final User user;

        private UsersItem(User user) {
            this.user = user;
        }

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {

            TextView txtUser = viewHolder.itemView.findViewById(R.id.textNameUserPesquisa);
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
