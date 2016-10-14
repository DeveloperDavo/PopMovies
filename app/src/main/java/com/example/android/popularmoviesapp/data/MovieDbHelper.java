package com.example.android.popularmoviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.example.android.popularmoviesapp.data.MovieContract.*;
import static com.example.android.popularmoviesapp.data.MovieContract.MovieEntry;
import static com.example.android.popularmoviesapp.data.MovieContract.ReviewEntry;

/**
 * Created by David on 10/07/16.
 * Manages database creation and version management.
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = MovieDbHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 8;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    private void createTables(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER, " +
                MovieEntry.COLUMN_TITLE + " TEXT, " +
                MovieEntry.COLUMN_POSTER + " TEXT, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                MovieEntry.COLUMN_RATING + " REAL, " +
                MovieEntry.COLUMN_POPULARITY + " REAL, " +
                MovieEntry.COLUMN_RELEASE + " TEXT, " +
                MovieEntry.COLUMN_FAVORITE + " INTEGER DEFAULT 0);";

        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY, " +
                ReviewEntry.COLUMN_MOVIE_KEY + " INTEGER, " +
                ReviewEntry.COLUMN_REVIEW_ID + " INTEGER, " +
                ReviewEntry.COLUMN_AUTHOR + " TEXT, " +
                ReviewEntry.COLUMN_CONTENT + " TEXT, " +
                ReviewEntry.COLUMN_URL + " TEXT);";

        final String SQL_CREATE_VIDEOS_TABLE = "CREATE TABLE " + VideoEntry.TABLE_NAME + " (" +
                VideoEntry._ID + " INTEGER PRIMARY KEY, " +
                VideoEntry.COLUMN_MOVIE_KEY + " INTEGER, " +
                VideoEntry.COLUMN_VIDEO_ID + " TEXT, " +
                VideoEntry.COLUMN_VIDEO_KEY + " TEXT, " +
                VideoEntry.COLUMN_VIDEO_SITE + " TEXT, " +
                VideoEntry.COLUMN_VIDEO_TYPE + " TEXT);";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_REVIEWS_TABLE);
        db.execSQL(SQL_CREATE_VIDEOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " +
                newVersion + ". OLD DATA WILL BE DESTROYED");
        // should be your top priority before modifying this method.
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + VideoEntry.TABLE_NAME);
        onCreate(db);
    }
}
