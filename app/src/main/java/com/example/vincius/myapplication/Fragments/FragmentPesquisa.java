package com.example.vincius.myapplication.Fragments;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.vincius.myapplication.ActivityGrupo;
import com.example.vincius.myapplication.ActivityPerfil;
import com.example.vincius.myapplication.ActivityPerfilGroup;
import com.example.vincius.myapplication.Group;
import com.example.vincius.myapplication.R;
import com.example.vincius.myapplication.User;
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
import java.util.List;


public class FragmentPesquisa extends Fragment{

    private GroupAdapter adapter;
    View view;

    public FragmentPesquisa() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView rv = view.findViewById(R.id.hits);
        adapter = new GroupAdapter();
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {
                Intent intent;
                if(item instanceof UsersItem) {
                    intent = new Intent(getActivity(), ActivityPerfil.class);
                    UsersItem userItem =(UsersItem) item;
                    intent.putExtra("user", userItem.user);
                } else{
                    intent = new Intent(getActivity(), ActivityPerfilGroup.class);
                    GroupItem groupItem = (GroupItem) item;
                    intent.putExtra("group", groupItem.group);
                }
                startActivity(intent);
            }
        });
        fetchUsers();
        fetchGroups();
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
                        adapter.add(new UsersItem(user));
                    }
                }

            }
        });
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
                        adapter.add(new GroupItem(group));
                    }
                }

            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pesquisa, container, false);
        return view;
    }

    private static class UsersItem extends Item<ViewHolder> {

        private final User user;

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
    private static class GroupItem extends Item<ViewHolder> {

        private final Group group;

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

