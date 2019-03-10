package com.example.vincius.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vincius.myapplication.Adapters.FragmentAdapter;
import com.example.vincius.myapplication.Fragments.FragmentHome;
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
    public MenuItem menuItemSearch, menuItemOptions;
    public GroupAdapter adapter;
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragmentsnavigation);
        adapter = new GroupAdapter();
        getSupportActionBar().setElevation(0);
        startComponents();
        fetchUsers();
        fetchGroups();
        FragmentPesquisa.setAdapter(adapter);

    }



    private void fetchGroups() {
        CollectionReference doc = FirebaseFirestore.getInstance().collection("groups");
        doc.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                } else {
                    List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot doc :
                            docs) {
                        Group group = doc.toObject(Group.class);
                        Log.d("Teste", "onEvent: " + group.getGroupName());
                        names.add(group.getGroupName());
                        adapter.add(new GroupItem(group));
                    }
                }

            }
        });
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



    private void startComponents() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.titles_bar)));
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItemSearch = menu.findItem(R.id.action_search);
        menuItemOptions = menu.findItem(R.id.action_options);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_options, menu);
        menuItemSearch = menu.findItem(R.id.action_search);
        menuItemOptions = menu.findItem(R.id.action_options);
        searchView = (SearchView) menuItemSearch.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setIconified(true);
        searchView.setMaxWidth(10000);


        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(menuItemOptions!= null){
                    getSupportActionBar().setTitle(null);
                    menuItemOptions.setVisible(false);
                }

            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                menuItemOptions.setVisible(true);
                getSupportActionBar().setTitle("Hellp.me");
                return false;
            }
        });



        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_options:
                Intent i= new Intent(this, ActivitySettings.class);
                startActivity(i);
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }
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
            fetchUpdateGroups(newList);
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

    private void fetchUpdateGroups(final List<String> newList) {
        CollectionReference doc = FirebaseFirestore.getInstance().collection("groups");
        doc.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                } else {
                    List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot doc :
                            docs) {
                        Group group = doc.toObject(Group.class);
                        if (newList.contains(group.getGroupName())) {
                            adapter.add(new GroupItem(group));
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

    public static class GroupItem extends Item<ViewHolder> {

        public final Group group;

        private GroupItem(Group group) {
            this.group = group;
        }

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {

            TextView txtGroup = viewHolder.itemView.findViewById(R.id.textNameUserPesquisa);
            ImageView imgGroup = viewHolder.itemView.findViewById(R.id.ImageView);

            txtGroup.setText(group.getGroupName());

            Picasso.get()
                    .load(group.getProfileUrl())
                    .into(imgGroup);

        }

        @Override
        public int getLayout() {
            return R.layout.item_user;
        }

    }
}
