package com.awn.app.movietoday.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.awn.app.movietoday.item.MovieItem;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class NowPlayingLoader extends AsyncTaskLoader<ArrayList<MovieItem>> {
    private ArrayList<MovieItem> movieItemList;
    private Boolean mHasResult = false;

    public NowPlayingLoader(final Context context) {
        super(context);
        onContentChanged();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (takeContentChanged()) {
            forceLoad();
        } else if (mHasResult) {
            deliverResult(movieItemList);
        }
    }

    @Override
    public void deliverResult(final ArrayList<MovieItem> data) {
        movieItemList = data;
        mHasResult = true;
        super.deliverResult(data);
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        if (mHasResult) {
            onReleaseResource(movieItemList);
            movieItemList = null;
            mHasResult = false;
        }
    }

    private static final String API_KEY = "885dfd8e2dbca4e70eb8a4af4b5b6785";

    @Override
    public ArrayList<MovieItem> loadInBackground() {
        SyncHttpClient client = new SyncHttpClient();
        final ArrayList<MovieItem> items = new ArrayList<>();

        String nation = "en-US";
        String url = "https://api.themoviedb.org/3/movie/now_playing?api_key=" + API_KEY + "&language=" + nation;

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                setUseSynchronousMode(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject responseObject = new JSONObject(result);
                    JSONArray list = responseObject.getJSONArray("results");

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject movie = list.getJSONObject(i);
                        MovieItem movieItem = new MovieItem(movie);
                        items.add(movieItem);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //Jika response gagal
            }
        });

        return items;
    }

    public void onReleaseResource(ArrayList<MovieItem> data) {
        //nothing to do.
    }

}