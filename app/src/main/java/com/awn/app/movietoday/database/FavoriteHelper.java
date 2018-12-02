package com.awn.app.movietoday.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.awn.app.movietoday.item.MovieItem;

import static android.provider.BaseColumns._ID;

public class FavoriteHelper {

    private Context context;
    private DatabaseHelper dbHelper;

    private SQLiteDatabase db;

    public FavoriteHelper(Context context) {
        this.context = context;
    }

//    open db
    public FavoriteHelper open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

//    close db
    public void dbClose() {
        dbHelper.close();
    }

//    query insert
    public long insert(MovieItem item){
        ContentValues cv = new ContentValues();
        cv.put(FavoriteColumns.COLUMN_ID, item.getId());
        cv.put(FavoriteColumns.COLUMN_TITLE, item.getTitle());
        cv.put(FavoriteColumns.COLUMN_RATING, item.getRating());
        cv.put(FavoriteColumns.COLUMN_POSTER, item.getPoster());
        cv.put(FavoriteColumns.COLUMN_BACKDROP, item.getBackdrop());
        cv.put(FavoriteColumns.COLUMN_RELEASE, item.getReleaseDate());
        cv.put(FavoriteColumns.COLUMN_OVERVIEW, item.getOverview());
        return db.insert(FavoriteColumns.TABEL_NAME, null, cv);
    }

//    query delete
    public int delete(int id){
        return db.delete(FavoriteColumns.TABEL_NAME, FavoriteColumns.COLUMN_ID + " = '" + id +"'",null);
    }

//    query select by id
    public Cursor queryForId(String id) {
        return db.query(FavoriteColumns.TABEL_NAME, null, FavoriteColumns.COLUMN_ID + " = ?", new String[]{id}, null, null, null);
    }

//    query select all
    public Cursor queryAll() {
        return db.query(FavoriteColumns.TABEL_NAME, null, null, null, null, null, _ID + " ASC");
    }

}
