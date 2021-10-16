package com.example.rssreader.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.rssreader.view.IntroFragment;
import com.example.rssreader.view.IntroFragment2;
import com.example.rssreader.view.IntroFragment3;

import java.util.ArrayList;

public class IntroFragmentAdapter extends FragmentStateAdapter {

    private static final int NUM_PAGES = 3;

    public IntroFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new IntroFragment();
            case 1:
                return new IntroFragment2();
            default:
                return new IntroFragment3();
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }

}
