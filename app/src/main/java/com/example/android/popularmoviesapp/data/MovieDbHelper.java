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

    private static final int DATABASE_VERSION = 5;

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
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH + " BLOB NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RATING + " REAL NOT NULL, " +
                MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MovieEntry.COLUMN_RELEASE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_FAVORITE + " INTEGER DEFAULT 0);";

        // TODO: make movie_id a foreign key
        // TODO: review_id should be text
        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY, " +
                ReviewEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                ReviewEntry.COLUMN_REVIEW_ID + " INTEGER UNIQUE NOT NULL, " +
                ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_URL + " TEXT NOT NULL);";

        final String SQL_CREATE_VIDEOS_TABLE = "CREATE TABLE " + VideoEntry.TABLE_NAME + " (" +
                VideoEntry._ID + " INTEGER PRIMARY KEY, " +
                VideoEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                VideoEntry.COLUMN_VIDEO_ID + " TEXT UNIQUE NOT NULL, " +
                VideoEntry.COLUMN_VIDEO_KEY + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_VIDEO_SITE + " TEXT NOT NULL, " +
                VideoEntry.COLUMN_VIDEO_TYPE + " TEXT NOT NULL);";

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
