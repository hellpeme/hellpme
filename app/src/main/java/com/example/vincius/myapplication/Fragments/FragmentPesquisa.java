package com.example.vincius.myapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.algolia.instantsearch.core.helpers.Searcher;
import com.algolia.instantsearch.ui.helpers.InstantSearch;
import com.algolia.instantsearch.ui.utils.ItemClickSupport;
import com.algolia.instantsearch.ui.views.Hits;
import com.example.vincius.myapplication.ActivityMonitoria;
import com.example.vincius.myapplication.ActivityPerfil;
import com.example.vincius.myapplication.R;
import com.example.vincius.myapplication.User;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;



import org.json.JSONObject;


public class FragmentPesquisa extends Fragment {

    private Searcher searcher;
    private InstantSearch helper;
    private Hits hits;

    View view;

    private static final String YourApplicationID = "BD1URWQ8QG";
    private static final String YourAPIKey = "e0a540e85a53829cd9105babe638cb14";

    public FragmentPesquisa() {
            // Required empty public constructor
        }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        hits = view.findViewById(R.id.hits);
        searcher = Searcher.create(YourApplicationID, YourAPIKey, "users");
        helper = new InstantSearch(getActivity(), searcher);
        helper.search();

        hits.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, int position, View v) {
                JSONObject hit = hits.get(position);

                Intent intent = new Intent(getContext(), ActivityMonitoria.class);
                intent.putExtra("users", hit.toString());
                startActivity(intent);

            }

        });
        ;
    }

    @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

               }

    @Override
    public void onDestroy() {
        searcher.destroy();
        super.onDestroy();
    }

    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.fragment_pesquisa, container, false);
            return view;
        }

    private class UserItem extends Item<ViewHolder>{
        private final User user;

        private UserItem(User user) {
            this.user = user;
        }

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {

            TextView txtUsername = viewHolder.itemView.findViewById(R.id.textNameUserPesquisa);
            ImageView imgUser = viewHolder.itemView.findViewById(R.id.textNameUserPesquisa);

            txtUsername.setText(user.getUsername());
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

