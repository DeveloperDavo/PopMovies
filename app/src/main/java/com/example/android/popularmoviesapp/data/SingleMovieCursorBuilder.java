package com.example.android.popularmoviesapp.data;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.example.android.popularmoviesapp.data.MovieContract.MovieEntry;

import static com.example.android.popularmoviesapp.data.MovieContract.VideoEntry;

/**
 * Created by David on 09/10/16.
 */
public class SingleMovieCursorBuilder {

    private static final SQLiteQueryBuilder VIDEOS_MOVIE_FAV_JOIN;

    private Context context;

    public SingleMovieCursorBuilder(Context context) {
        this.context = context;
    }

    // movies._ID = ?
    // TODO: update to movie_id (after updating db)
    private static final String SINGLE_MOVIE_SELECTION =
            MovieEntry.TABLE_NAME + "." + MovieEntry._ID + " = ?";

    static {
        VIDEOS_MOVIE_FAV_JOIN = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //movies LEFT OUTER JOIN videos ON movies._ID = videos.movie_key
        VIDEOS_MOVIE_FAV_JOIN.setTables(
                MovieEntry.TABLE_NAME + " LEFT OUTER JOIN " +
                        VideoEntry.TABLE_NAME +
                        " ON " + MovieEntry.TABLE_NAME +
                        "." + MovieEntry._ID +
                        " = " + VideoEntry.TABLE_NAME +
                        "." + VideoEntry.COLUMN_MOVIE_KEY);
    }

    // TODO: update selection args to take the movie id (after updating db)
    // TODO: same thing with movieKey
    public Cursor build(Uri uri, String[] selectionArgsForVideos) {
        final MovieDbHelper movieDbHelper = new MovieDbHelper(context);
        final SQLiteDatabase readableDatabase = movieDbHelper.getReadableDatabase();

        final String[] allColumns = null;
        final String[] selectionArgs = {String.valueOf(ContentUris.parseId(uri))};
        final String groupBy = null;
        final String having = null;
        final String orderBy = null;

        final Cursor movieCursor = readableDatabase.query(
                MovieEntry.TABLE_NAME,
                allColumns,
                SINGLE_MOVIE_SELECTION,
                selectionArgs,
                groupBy,
                having,
                orderBy
        );

//        final String[] selectionArgsForVideos = {String.valueOf(uri)};
        final Cursor videoCursor = VIDEOS_MOVIE_FAV_JOIN.query(readableDatabase,
                allColumns,
                SINGLE_MOVIE_SELECTION,
                selectionArgsForVideos,
                groupBy,
                having,
                orderBy
        );

        return new MergeCursor(new Cursor[]{movieCursor, videoCursor});
    }

}
