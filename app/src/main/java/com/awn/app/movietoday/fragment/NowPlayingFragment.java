package com.awn.app.movietoday.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.awn.app.movietoday.MainActivity;
import com.awn.app.movietoday.R;
import com.awn.app.movietoday.adapter.MovieAdapter;
import com.awn.app.movietoday.item.MovieItem;
import com.awn.app.movietoday.loader.NowPlayingLoader;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class NowPlayingFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<MovieItem>>, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.rv_movie)
    RecyclerView rvMovie;

    @BindView(R.id.progress_bar)
    SpinKitView progressBar;

    @BindView(R.id.tv_noResult)
    TextView tvNoResult;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeLayout;

    private ArrayList<MovieItem> movieItemList;

    private static final String KEY = "MovieItems";

    private SharedPreferences sharedPreferences;

    public NowPlayingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_now_playing, container, false);

//        binding fragment's view
        ButterKnife.bind(this, view);

        rvMovie.setHasFixedSize(true);

//        set refresh when it's swiped
        swipeLayout.setOnRefreshListener(this);

//        set up progress bar when loading
        Sprite doubleBounce = new CubeGrid();
        progressBar.setIndeterminateDrawable(doubleBounce);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

//        set up data when phone is rotated
        if (savedInstanceState != null) {

//            jika sudah ada data yg sisimpan pada save instance state
            movieItemList = savedInstanceState.getParcelableArrayList(KEY);
            showRecyclerCardView();
        } else {

//            jalankan loader jika tidak ada data yg disimpan
            getLoaderManager().initLoader(0, null, this);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setColors();
    }

//    mengatur warna berdasarkan preference
    private void setColors(){
        progressBar.setColor(sharedPreferences.getInt(getString(R.string.keyColorAccentPreference),  ContextCompat.getColor(getContext(), R.color.colorAccent)));
    }

//    start load data
    @Override
    public Loader<ArrayList<MovieItem>> onCreateLoader(int id, Bundle args) {
//        tampilkan progress bar
        tvNoResult.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        return new NowPlayingLoader(getActivity());
    }

//    finish load data
    @Override
    public void onLoadFinished(Loader<ArrayList<MovieItem>> loader, ArrayList<MovieItem> data) {
        if (data.isEmpty()) {

//            tampilkan tulisan error load data
            tvNoResult.setVisibility(View.VISIBLE);
            rvMovie.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        } else {

//            tampilkan data, hilangkan progress bar
            movieItemList = new ArrayList<>();
            movieItemList.addAll(data);

            showRecyclerCardView();

            progressBar.setVisibility(View.GONE);
            tvNoResult.setVisibility(View.GONE);
            rvMovie.setVisibility(View.VISIBLE);
        }
    }

//    set up recycler view
    private void showRecyclerCardView() {
        rvMovie.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        MovieAdapter movieAdapter = new MovieAdapter(this.getActivity());
        movieAdapter.setListMovie(movieItemList);
        rvMovie.setAdapter(movieAdapter);
        MainActivity.mAdapter = movieAdapter;
    }

//    reset loader
    @Override
    public void onLoaderReset(Loader<ArrayList<MovieItem>> loader) {
        movieItemList.clear();
    }

//    restart loader when it's swiped
    @Override
    public void onRefresh() {
        getLoaderManager().restartLoader(0, null, this);
        swipeLayout.setRefreshing(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY, movieItemList);
    }
}