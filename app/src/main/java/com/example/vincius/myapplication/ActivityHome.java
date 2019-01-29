package com.example.vincius.myapplication;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListPopupWindow;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.vincius.myapplication.Adapters.FragmentAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.widget.Toast.*;

public class ActivityHome extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    //private Button btn_create_group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        verficarAuth();
        startComponents();

    }

    private void verficarAuth() {
        if (FirebaseAuth.getInstance().getUid() == null) {
            Intent intent = new Intent(ActivityHome.this, ActivityLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }



    private void startComponents() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        //btn_create_group = (Button) findViewById(R.id.btn_creeate);
        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.titles_bar)));
        tabLayout.setupWithViewPager(viewPager);
    }



}