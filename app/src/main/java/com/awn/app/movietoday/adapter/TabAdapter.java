package com.awn.app.movietoday.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.awn.app.movietoday.fragment.FavoriteFragment;
import com.awn.app.movietoday.fragment.NowPlayingFragment;


public class TabAdapter extends FragmentPagerAdapter {

    private static final int TABS = 2;

    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new NowPlayingFragment();
            case 1:
                return new FavoriteFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return TABS;
    }
}
