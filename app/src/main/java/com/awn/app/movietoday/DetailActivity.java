package com.awn.app.movietoday;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.awn.app.movietoday.database.FavoriteHelper;
import com.awn.app.movietoday.download.CheckForSDCard;
import com.awn.app.movietoday.download.DownloadTask;
import com.awn.app.movietoday.items.MovieItem;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    public static String KEY_MOVIE = "key_movie";
    private MovieItem item;
    private FavoriteHelper favoriteHelper;
    private Boolean isFavorite = false;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsing_toolbar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.iv_favorite)
    ImageView iv_favorite;

    @BindView(R.id.tv_detail_title)
    TextView tv_title;

    @BindViews({
            R.id.img_star1,
            R.id.img_star2,
            R.id.img_star3,
            R.id.img_star4,
            R.id.img_star5
    })
    List<ImageView> img_vote;

    @BindView(R.id.tv_detail_rating)
    TextView tv_rating;

    @BindView(R.id.iv_detail_backdrop)
    ImageView iv_backdrop;

    @BindView(R.id.tv_detail_release)
    TextView tv_release;

    @BindView(R.id.tv_detail_overview)
    TextView tv_overview;

    @BindView(R.id.btn_download)
    Button btn_download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        item = (MovieItem) getIntent().getSerializableExtra(KEY_MOVIE);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsing_toolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
        loadData();

        iv_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavorite){
                    favoriteHelper.delete(item.getId());
                    iv_favorite.setImageResource(R.drawable.ic_star_border);
                } else {
                    long a = favoriteHelper.insert(item);
                    Log.e("LOL", "onClick: "+a);
                    iv_favorite.setImageResource(R.drawable.ic_star_full);
                }
            }
        });

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int permissionCheck = ContextCompat.checkSelfPermission(DetailActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    if (isConnectingToInternet()) {
                        new DownloadTask(DetailActivity.this, btn_download, item.getBackdrop(), item.getTitle());

                    } else
                        Toast.makeText(DetailActivity.this, "Oops!! There is no internet connection. Please enable internet connection and try again.", Toast.LENGTH_SHORT).show();

                }

            }

        });
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void loadData() {
        favoriteHelper = new FavoriteHelper(this);
        favoriteHelper.open();
        Cursor cursor = favoriteHelper.queryForId(String.valueOf(item.getId()));
        if (cursor != null) {
            if (cursor.moveToFirst()) isFavorite = true;
            cursor.close();
        }
        getSupportActionBar().setTitle(item.getTitle());
        tv_title.setText(item.getTitle());
        if (isFavorite) {
            iv_favorite.setImageResource(R.drawable.ic_star_full);
        } else {
            iv_favorite.setImageResource(R.drawable.ic_star_border);
        }

        double userRating = Double.parseDouble(item.getRating()) / 2;
        int integerPart = (int) userRating;
        // Fill stars
        for (int i = 0; i < integerPart; i++) {
            img_vote.get(i).setImageResource(R.drawable.ic_star_full);
        }
        // Fill half star
        if (Math.round(userRating) > integerPart) {
            img_vote.get(integerPart).setImageResource(R.drawable.ic_star_half);
        }

        tv_rating.setText(getString(R.string.rating) + " (" + item.getRating() + ")");

        Glide.with(this)
                .load("http://image.tmdb.org/t/p/w342" + item.getBackdrop())
                .into(iv_backdrop);
        tv_release.setText(item.getReleaseDate());
        tv_overview.setText(item.getOverview());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (favoriteHelper != null) favoriteHelper.dbClose();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Check if internet is present or not
    private boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
}
