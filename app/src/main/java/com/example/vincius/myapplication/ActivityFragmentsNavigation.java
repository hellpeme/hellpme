package com.example.vincius.myapplication;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.vincius.myapplication.Adapters.FragmentAdapter;
import com.google.firebase.auth.FirebaseAuth;


public class ActivityFragmentsNavigation extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragmentsnavigation);
        verficarAuth();
        startComponents();


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



}