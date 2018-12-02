package com.awn.app.movietoday;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.awn.app.movietoday.adapter.MovieAdapter;
import com.awn.app.movietoday.adapter.TabAdapter;
import com.awn.app.movietoday.preference.SettingsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        TabLayout.OnTabSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.navigation)
    NavigationView navigation;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @BindView(R.id.pager)
    ViewPager viewPager;

    public static MovieAdapter mAdapter;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        binding view
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

//        set up navigation drawer
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigation.setNavigationItemSelectedListener(this);

//        set up tab
        viewPager.setAdapter(new TabAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_movie);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_star_border_white);
        tabLayout.setOnTabSelectedListener(this);

        selectNav(0);

//        set up sharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setColors();
    }

//    mengatur warna berdasarkan preference
    private void setColors() {
        toolbar.setBackgroundColor(sharedPreferences.getInt(getString(R.string.keyColorPrimaryPreference), ContextCompat.getColor(getBaseContext(), R.color.colorPrimary)));
        tabLayout.setBackgroundColor(sharedPreferences.getInt(getString(R.string.keyColorPrimaryPreference), ContextCompat.getColor(getBaseContext(), R.color.colorPrimary)));
        tabLayout.setSelectedTabIndicatorColor(sharedPreferences.getInt(getString(R.string.keyColorAccentPreference), ContextCompat.getColor(getBaseContext(), R.color.colorAccent)));
        int states[][] = new int[][] { new int[] { -android.R.attr.state_checked}, new int[] {android.R.attr.state_checked}};
        int colors[] = new int[] {R.color.colorPrimaryDark, sharedPreferences.getInt(getString(R.string.keyColorPrimaryPreference), ContextCompat.getColor(getBaseContext(), R.color.colorPrimary))};
        ColorStateList colorStateList = new ColorStateList(states, colors);
        navigation.setItemTextColor(colorStateList);
        navigation.setItemIconTintList(colorStateList);
    }

//    ketika button back ditekan maka menutup navigation drawer
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    set up toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        intent ke menu setting
        if (id == R.id.menu_setting) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    ketika salah menu di navigation drawer ditekan maka tab berganti sesuai dengan tab yang dituju dan menutup navigation drawer
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.tab_movie:
                selectTab(0);
                break;
            case R.id.tab_favorite:
                selectTab(1);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

//    berganti tab sesuai tab yg dipilih
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        selectNav(tab.getPosition());
        switch (tab.getPosition()) {
            case 0:
                toolbar.setTitle("Now Playing");
                break;
            case 1:
                toolbar.setTitle("Favorite");
                break;
            default:
                toolbar.setTitle(R.string.app_name);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

//    ketika menu dipilih pada navigation drawer
    private void selectNav(int navNumber) {
        navigation.getMenu().getItem(navNumber).setChecked(true);
    }

    private void selectTab(int tabNumber) {
        tabLayout.getTabAt(tabNumber).select();
    }
}