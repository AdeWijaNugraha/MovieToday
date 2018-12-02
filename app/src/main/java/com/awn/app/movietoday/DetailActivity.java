package com.awn.app.movietoday;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
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
import com.awn.app.movietoday.download.DownloadTask;
import com.awn.app.movietoday.item.MovieItem;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

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

    public static String KEY_MOVIE = "key_movie";
    private MovieItem item;
    private FavoriteHelper favoriteHelper;
    private Boolean isFavorite = false;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

//        set up permission
        int PERMISSION_ALL = 99;
        String[] PERMISSIONS = {
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
        };

//        check permission
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

//        recive data from main actvity
        item = (MovieItem) getIntent().getParcelableExtra(KEY_MOVIE);

//        binding view
        ButterKnife.bind(this);

//        set up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        set up collapsing_toolbar
        collapsing_toolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

//        load movie's data
        loadData();

//        set up sharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

//        jika bintang favorit diclick
        iv_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavorite){
//                    hapus data dari database favorit
                    favoriteHelper.delete(item.getId());

//                    set gambar bintang kosong
                    iv_favorite.setImageResource(R.drawable.ic_star_border);
                } else {
//                    insert data ke database favorit
                    favoriteHelper.insert(item);

//                    set gambar bintang penuh
                    iv_favorite.setImageResource(R.drawable.ic_star_full);
                }
            }
        });

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                cek permission
                int permissionCheck = ContextCompat.checkSelfPermission(DetailActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    if (isConnectingToInternet()) {

//                        download gambar
                        new DownloadTask(DetailActivity.this, btn_download, item.getPoster(), item.getTitle());
                    } else
                        Toast.makeText(DetailActivity.this, getString(R.string.cant_download), Toast.LENGTH_LONG).show();
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
//        set up database
        favoriteHelper = new FavoriteHelper(this);
        favoriteHelper.open();

//        select data pada database favorit berdasarkan id
        Cursor cursor = favoriteHelper.queryForId(String.valueOf(item.getId()));

//        jika ada data yg berhasil diambil, set bintang favorit penuh
        if (cursor != null) {
            if (cursor.moveToFirst()) isFavorite = true;
            cursor.close();
        }

//        set toolbar title
        getSupportActionBar().setTitle(item.getTitle());

//        set title
        tv_title.setText(item.getTitle());

//        set bintang favorit
        if (isFavorite) {
            iv_favorite.setImageResource(R.drawable.ic_star_full);
        } else {
            iv_favorite.setImageResource(R.drawable.ic_star_border);
        }

//        set rating
        double userRating = Double.parseDouble(item.getRating()) / 2;
        int integerPart = (int) Math.floor(userRating);
//        Fill stars
        for (int i = 0; i < integerPart; i++) {
            img_vote.get(i).setImageResource(R.drawable.ic_star_full);
        }
//        Fill half star
        if (Math.round(userRating) > integerPart) {
            img_vote.get(integerPart).setImageResource(R.drawable.ic_star_half);
        }

//        set rating
        tv_rating.setText(getString(R.string.rating) + " (" + item.getRating() + ")");

//        set backdrop
        Glide.with(this)
                .load("http://image.tmdb.org/t/p/w342" + item.getBackdrop())
                .into(iv_backdrop);

//        set tanggal rilis
        tv_release.setText(item.getReleaseDate());

//        set overview
        tv_overview.setText(item.getOverview());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setColors();
    }

//    mengatur warna berdasarkan preference
    private void setColors(){
        collapsing_toolbar.setContentScrimColor(sharedPreferences.getInt(getString(R.string.keyColorPrimaryPreference),  ContextCompat.getColor(getBaseContext(), R.color.colorPrimary)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        close database
        if (favoriteHelper != null) favoriteHelper.dbClose();
    }

//    jika tombol back pada toolbar diclick maka menutup activity detail dan kembali ke main activity
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

//    cek koneksi internet
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
