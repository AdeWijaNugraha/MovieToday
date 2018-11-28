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

import static com.awn.app.movietoday.database.DatabaseContract.CONTENT_URI;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {


    public FavoriteFragment() {
        // Required empty public constructor
    }

    private FavoriteHelper favoriteHelper;
    private Cursor cur;
    private FavoriteAdapter favoriteAdapter;
    private Context context;
    private Unbinder unbind;

    @BindView(R.id.rv_favorite)
    RecyclerView rv_favorite;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        context = view.getContext();
        unbind = ButterKnife.bind(this, view);
        favoriteAdapter = new FavoriteAdapter(cur);
        rv_favorite.setLayoutManager(new LinearLayoutManager(context));
        rv_favorite.setAdapter(favoriteAdapter);
        new loadData().execute();
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
        new loadData().execute();
    }

    private class loadData extends AsyncTask<Void, Void, Cursor> {
        @Override
        protected Cursor doInBackground(Void... voids) {
            favoriteHelper = new FavoriteHelper(getContext());
            favoriteHelper.open();
            return favoriteHelper.queryAll();
        }
        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            cur = cursor;
            favoriteAdapter.replaceAll(cur);
        }
    }

}
