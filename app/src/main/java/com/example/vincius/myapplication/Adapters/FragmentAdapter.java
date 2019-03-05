package com.example.vincius.myapplication.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.example.vincius.myapplication.Fragments.FragmentHome;
import com.example.vincius.myapplication.Fragments.FragmentPerfil;
import com.example.vincius.myapplication.Fragments.FragmentPesquisa;


public class FragmentAdapter extends FragmentPagerAdapter {
    public int position = 0;

    public int getPosition() {
        return position;
    }

    private String[] mTabTitles;

    public FragmentAdapter(FragmentManager fm, String[] mTabTitles) {
        super(fm);
        this.mTabTitles = mTabTitles;
    }

    @Override
    public Fragment getItem(int i) {
        position = i;
        switch (i){
            case 0:
                return new FragmentHome();
            case 1:
                return new FragmentPesquisa();
                default:
                    return null;
        }
    }

    @Override
    public int getCount() {
        return this.mTabTitles.length;
    }

    @Nullable
    @Override

    public CharSequence getPageTitle(int position) {
        return mTabTitles[position];

    }
}
