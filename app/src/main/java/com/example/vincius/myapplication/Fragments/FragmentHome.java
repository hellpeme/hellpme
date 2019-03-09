package com.example.vincius.myapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vincius.myapplication.ActivityGrupo;
import com.example.vincius.myapplication.ActivityMonitoria;
import com.example.vincius.myapplication.ActivityPerfil;
import com.example.vincius.myapplication.ActivityPrivado;
import com.example.vincius.myapplication.ActivityPublico;
import com.example.vincius.myapplication.ActivityWatson;
import com.example.vincius.myapplication.R;
import com.example.vincius.myapplication.User;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class FragmentHome extends Fragment {

    private GroupAdapter adapter;
    View view;
    public FragmentHome() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FloatingActionButton btnPrivate = view.findViewById(R.id.fabMonitoriaPrivada);
        FloatingActionButton btnPublico = view.findViewById(R.id.fabMonitoriaPublica);
        FloatingActionButton watson = view.findViewById(R.id.fabWatson);

        RecyclerView rv = view.findViewById(R.id.lastMessages);
        adapter = new GroupAdapter();

        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));


        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {
                Intent intent;

                if (item instanceof ContactItem ){
                    ContactItem contact = (ContactItem) item;
                    intent = new Intent(getActivity(), ActivityMonitoria.class);
                    intent.putExtra("user2", contact.contact);
                } else {
                    ContactGroupItem contact = (ContactGroupItem) item;
                    intent = new Intent(getActivity(), ActivityGrupo.class);
                    intent.putExtra("group2", contact.contact);
                }

                startActivity(intent);
            }
        });

        fetchLastMessages();
        fetchLastMessagesGroup();

        btnPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ActivityPrivado.class);
                startActivity(intent);

            }
        });

        btnPublico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ActivityPublico.class);
                startActivity(intent);

            }
        });

        watson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ActivityWatson.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void fetchLastMessages() {
        String uid = FirebaseAuth.getInstance().getUid();

        FirebaseFirestore.getInstance().collection("/last-messages")
                .document(uid)
                .collection("contacts")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();
                        if(documentChanges != null){
                            for (DocumentChange doc: documentChanges) {
                                if(doc.getType() == DocumentChange.Type.ADDED){
                                    Contact contact = doc.getDocument().toObject(Contact.class);
                                    adapter.add(new ContactItem(contact));

                                }
                            }
                        }

                    }
                });
    }

    private void fetchLastMessagesGroup() {
        String uid = FirebaseAuth.getInstance().getUid();

        FirebaseFirestore.getInstance().collection("/last-messages")
                .document(uid)
                .collection("contactsgroups")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();
                        if(documentChanges != null){
                            for (DocumentChange doc: documentChanges) {
                                if(doc.getType() == DocumentChange.Type.ADDED){
                                    ContactGroup contact = doc.getDocument().toObject(ContactGroup.class);
                                    adapter.add(new ContactGroupItem(contact));

                                }
                            }
                        }

                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }


    private class ContactItem extends Item<ViewHolder> {

        private final Contact contact;

        private ContactItem(Contact contact) {
            this.contact = contact;
        }

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {
            TextView username = viewHolder.itemView.findViewById(R.id.txtNameMessages);
            TextView message = viewHolder.itemView.findViewById(R.id.txtContentMessages);
            TextView txtTimestamp = viewHolder.itemView.findViewById(R.id.txtTimestamp);
            ImageView imgPhoto = viewHolder.itemView.findViewById(R.id.imageLastMessages);


            SimpleDateFormat formatToday = new SimpleDateFormat("HH:mm");
            Date d = new Date(contact.getTimestamp());
            String date = formatToday.format(d);

            txtTimestamp.setText(date);
            username.setText(contact.getUsername());
            message.setText(contact.getLastMessage());
            Picasso.get()
                    .load(contact.getPhotoUrl())
                    .into(imgPhoto);
        }

        @Override
        public int getLayout() {
            return R.layout.item_messages;
        }
    }

    private class ContactGroupItem extends Item<ViewHolder> {

        private final ContactGroup contact;

        private ContactGroupItem(ContactGroup contact) {
            this.contact = contact;
        }

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {
            TextView username = viewHolder.itemView.findViewById(R.id.txtNameMessages);
            TextView message = viewHolder.itemView.findViewById(R.id.txtContentMessages);
            TextView txtTimestamp = viewHolder.itemView.findViewById(R.id.txtTimestamp);
            ImageView imgPhoto = viewHolder.itemView.findViewById(R.id.imageLastMessages);

            //convert timestamp to date
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            Date d = new Date(contact.getTimestamp());
            String date = format.format(d);


            if(contact.getPhotoUrl() == null)
                contact.setPhotoUrl("https://firebasestorage.googleapis.com/v0/b/hellpme-5afb2.appspot.com/o/images%2Fno-avatar.jpg?alt=media&token=0971d28b-d919-47ac-b04b-857e7ed0639d");

            txtTimestamp.setText(date);
            username.setText(contact.getUsername());
            message.setText(contact.getLastMessage());
            Picasso.get()
                    .load(contact.getPhotoUrl())
                    .into(imgPhoto);
        }

        @Override
        public int getLayout() {
            return R.layout.item_messages;
        }
    }

}
