package com.awn.app.movietoday.fragment;


import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.awn.app.movietoday.R;
import com.awn.app.movietoday.adapter.FavoriteAdapter;
import com.awn.app.movietoday.database.FavoriteHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {

    @BindView(R.id.rv_favorite)
    RecyclerView rv_favorite;

    private FavoriteHelper favoriteHelper;
    private Cursor cursor;
    private FavoriteAdapter favoriteAdapter;
    private Context context;
    private Unbinder unbind;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        context = view.getContext();

//        binding fragment's view
        unbind = ButterKnife.bind(this, view);

//        set up recycler view
        rv_favorite.setLayoutManager(new LinearLayoutManager(context));
        favoriteAdapter = new FavoriteAdapter(cursor);
        rv_favorite.setAdapter(favoriteAdapter);

        new LoadData().execute();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (favoriteHelper != null) favoriteHelper.dbClose();
        unbind.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        new LoadData().execute();
    }

//    load data from database in background
    private class LoadData extends AsyncTask<Void, Void, Cursor> {
        @Override
        protected Cursor doInBackground(Void... voids) {
            favoriteHelper = new FavoriteHelper(getContext());
            favoriteHelper.open();
            return favoriteHelper.queryAll();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            FavoriteFragment.this.cursor = cursor;
            favoriteAdapter.replaceAll(FavoriteFragment.this.cursor);
        }
    }

}
