package com.awn.app.movietoday.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
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
import com.awn.app.movietoday.items.MovieItem;
import com.awn.app.movietoday.loader.NowPlayingLoader;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.CubeGrid;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class NowPlayingFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<MovieItem>>, SwipeRefreshLayout.OnRefreshListener {


    public NowPlayingFragment() {
        // Required empty public constructor
    }

    @BindView(R.id.rv_movie)
    RecyclerView rvMovie;

    private ArrayList<MovieItem> list;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.tv_noResult)
    TextView tvNoResult;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_now_playing, container, false);
        ButterKnife.bind(this, view);
        rvMovie.setHasFixedSize(true);
        swipeLayout.setOnRefreshListener(this);
        Sprite doubleBounce = new CubeGrid();
        progressBar.setIndeterminateDrawable(doubleBounce);


        getLoaderManager().initLoader(0, null, this);


        return view;
    }

    private void showRecyclerCardView() {
        rvMovie.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        Log.d("showRecyclerCardView", "The application stopped after this");
        MovieAdapter movieAdapter = new MovieAdapter(this.getActivity());
        movieAdapter.setListMovie(list);
        rvMovie.setAdapter(movieAdapter);
        MainActivity.mAdapter = movieAdapter;

    }

    @Override
    public Loader<ArrayList<MovieItem>> onCreateLoader(int id, Bundle args) {
        tvNoResult.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        return new NowPlayingLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<MovieItem>> loader, ArrayList<MovieItem> data) {
        if (data.isEmpty()) {
            tvNoResult.setVisibility(View.VISIBLE);
            rvMovie.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        } else {
            list = new ArrayList<>();
            list.addAll(data);

            showRecyclerCardView();

            progressBar.setVisibility(View.GONE);
            tvNoResult.setVisibility(View.GONE);
            rvMovie.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<MovieItem>> loader) {
        list.clear();
    }

    @Override
    public void onRefresh() {
        getLoaderManager().restartLoader(0, null, this);
    }

}
