package com.awn.app.movietoday.item;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.io.Serializable;

import static android.provider.BaseColumns._ID;
import static com.awn.app.movietoday.database.DatabaseContract.getColumnInt;
import static com.awn.app.movietoday.database.DatabaseContract.getColumnString;
import static com.awn.app.movietoday.database.FavoriteColumns.COLUMN_BACKDROP;
import static com.awn.app.movietoday.database.FavoriteColumns.COLUMN_OVERVIEW;
import static com.awn.app.movietoday.database.FavoriteColumns.COLUMN_POSTER;
import static com.awn.app.movietoday.database.FavoriteColumns.COLUMN_RATING;
import static com.awn.app.movietoday.database.FavoriteColumns.COLUMN_RELEASE;
import static com.awn.app.movietoday.database.FavoriteColumns.COLUMN_TITLE;

public class MovieItem implements Parcelable {
    private int id;
    private String title;
    private String rating;
    private String poster;
    private String backdrop;
    private String releaseDate;
    private String overview;

    public MovieItem(JSONObject object) {
        try {
            int id = object.getInt("id");
            String title = object.getString("title");
            double rating = object.getDouble("vote_average");
            String poster = object.getString("poster_path");
            String backdrop = object.getString("backdrop_path");
            String date = object.getString("release_date");
            String[] dates = date.split("-");
            String releaseDate;
            switch (dates[1]) {
                case "01":
                    releaseDate = dates[2] + " January " + dates[0];
                    break;
                case "02":
                    releaseDate = dates[2] + " February " + dates[0];
                    break;
                case "03":
                    releaseDate = dates[2] + " March " + dates[0];
                    break;
                case "04":
                    releaseDate = dates[2] + " April " + dates[0];
                    break;
                case "05":
                    releaseDate = dates[2] + " May " + dates[0];
                    break;
                case "06":
                    releaseDate = dates[2] + " June " + dates[0];
                    break;
                case "07":
                    releaseDate = dates[2] + " July " + dates[0];
                    break;
                case "08":
                    releaseDate = dates[2] + " August " + dates[0];
                    break;
                case "09":
                    releaseDate = dates[2] + " September " + dates[0];
                    break;
                case "10":
                    releaseDate = dates[2] + " October " + dates[0];
                    break;
                case "11":
                    releaseDate = dates[2] + " November " + dates[0];
                    break;
                case "12":
                    releaseDate = dates[2] + " December " + dates[0];
                    break;
                default:
                    releaseDate = "-";
            }
            String overview = object.getString("overview");
            this.id = id;
            this.title = title;
            this.rating = Double.toString(rating);
            this.poster = poster;
            this.backdrop = backdrop;
            this.releaseDate = releaseDate;
            this.overview = overview;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MovieItem(Cursor cursor) {
        this.id = getColumnInt(cursor, _ID);
        this.title = getColumnString(cursor, COLUMN_TITLE);
        this.rating = getColumnString(cursor, COLUMN_RATING);
        this.poster = getColumnString(cursor, COLUMN_POSTER);
        this.backdrop = getColumnString(cursor, COLUMN_BACKDROP);
        this.releaseDate = getColumnString(cursor, COLUMN_RELEASE);
        this.overview = getColumnString(cursor, COLUMN_OVERVIEW);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getRating() {
        return rating;
    }

    public String getPoster() {
        return poster;
    }

    public String getBackdrop() {
        return backdrop;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getOverview() {
        return overview;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.rating);
        dest.writeString(this.poster);
        dest.writeString(this.backdrop);
        dest.writeString(this.releaseDate);
        dest.writeString(this.overview);
    }

    protected MovieItem(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.rating = in.readString();
        this.poster = in.readString();
        this.backdrop = in.readString();
        this.releaseDate = in.readString();
        this.overview = in.readString();
    }

    public static final Parcelable.Creator<MovieItem> CREATOR = new Parcelable.Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel source) {
            return new MovieItem(source);
        }

        @Override
        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };
}