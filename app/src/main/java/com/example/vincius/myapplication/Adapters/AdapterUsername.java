package com.example.vincius.myapplication.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.vincius.myapplication.R;
import com.example.vincius.myapplication.User;

import java.util.ArrayList;

public class AdapterUsername extends ArrayAdapter<User> {
    private static class ViewHolder{
        TextView nome;
    }

    private AdapterUsername(Context context, ArrayList<User> users){
        super(context,R.layout.item_user,users);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        User user = getItem(position);
        ViewHolder viewHolder;

        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_user,parent,false);
            viewHolder.nome = (TextView) convertView.findViewById(R.id.textNameUserPesquisa);

            convertView.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.nome.setText("Jubileu");

        return convertView;
    }
}
