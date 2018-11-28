package com.awn.app.movietoday.database;

/**
 * Created by adewijanugraha on 05/03/18.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, "movie_today", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE_MOVIE = "create table " + FavoriteColumns.TABEL_NAME + " (" +
                FavoriteColumns.COLUMN_ID + " integer primary key, " +
                FavoriteColumns.COLUMN_TITLE + " text not null, " +
                FavoriteColumns.COLUMN_RATING + " text not null, " +
                FavoriteColumns.COLUMN_POSTER + " text not null, " +
                FavoriteColumns.COLUMN_BACKDROP + " text not null, " +
                FavoriteColumns.COLUMN_RELEASE + " text not null, " +
                FavoriteColumns.COLUMN_OVERVIEW + " text not null " +
                ");";

        sqLiteDatabase.execSQL(CREATE_TABLE_MOVIE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE MOVIE IF EXISTS " + FavoriteColumns.TABEL_NAME);
        onCreate(sqLiteDatabase);
    }
}

