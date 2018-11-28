package com.awn.app.movietoday.database;

/**
 * Created by adewijanugraha on 05/03/18.
 */

import android.provider.BaseColumns;

public class FavoriteColumns implements BaseColumns {
    public static String TABEL_NAME = "movie_today";

    public static String COLUMN_ID = "_id";
    public static String COLUMN_TITLE = "title";
    public static String COLUMN_RATING = "rating";
    public static String COLUMN_POSTER = "poster";
    public static String COLUMN_BACKDROP = "backdrop";
    public static String COLUMN_RELEASE = "release";
    public static String COLUMN_OVERVIEW = "overview";
}

