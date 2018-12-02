package com.awn.app.movietoday.database;

import android.database.Cursor;
import android.net.Uri;

public class DatabaseContract {

    public static final String AUTHOR = "com.awn.app.movietoday";
    public static final Uri CONTENT_URI = new Uri.Builder().scheme("content")
            .authority(AUTHOR)
            .appendPath(FavoriteColumns.TABEL_NAME)
            .build();

//    get string column from database
    public static String getColumnString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

//    get int column from database
    public static int getColumnInt(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndex(columnName));
    }

}
