package com.example.android.popularmoviesapp.data;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.example.android.popularmoviesapp.data.MovieContract.MovieEntry;
import com.example.android.popularmoviesapp.data.MovieContract.ReviewEntry;

import static com.example.android.popularmoviesapp.data.MovieContract.VideoEntry;

/**
 * Created by David on 09/10/16.
 */
public class SingleMovieCursorBuilder {

    private final Uri uri;
    private Context context;

    public SingleMovieCursorBuilder(Context context, Uri uri) {
        this.context = context;
        this.uri = uri;
    }

    // movies._ID = ?
    private static final String SINGLE_MOVIE_SELECTION =
            MovieEntry.TABLE_NAME + "." + MovieEntry._ID + " = ?";

    // videos.site = ?
    private static final String VIDEO_SITE_SELECTION =
            VideoEntry.TABLE_NAME + "." + VideoEntry.COLUMN_VIDEO_SITE + " = ?";

    // videos.type = ?
    private static final String VIDEO_TYPE_SELECTION =
            VideoEntry.TABLE_NAME + "." + VideoEntry.COLUMN_VIDEO_TYPE + " = ?";

    private static final SQLiteQueryBuilder VIDEOS_MOVIE_FAV_JOIN;

    static {
        VIDEOS_MOVIE_FAV_JOIN = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //movies LEFT OUTER JOIN videos ON movies._ID = videos.movie_row_id
        VIDEOS_MOVIE_FAV_JOIN.setTables(
                MovieEntry.TABLE_NAME + " LEFT OUTER JOIN " +
                        VideoEntry.TABLE_NAME +
                        " ON " + MovieEntry.TABLE_NAME +
                        "." + MovieEntry._ID +
                        " = " + VideoEntry.TABLE_NAME +
                        "." + VideoEntry.COLUMN_MOVIE_ROW_ID);
    }

    private static final SQLiteQueryBuilder REVIEWS_MOVIE_FAV_JOIN;

    static {
        REVIEWS_MOVIE_FAV_JOIN = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //movies LEFT OUTER JOIN videos ON movies._ID = videos.movie_row_id
        REVIEWS_MOVIE_FAV_JOIN.setTables(
                MovieEntry.TABLE_NAME + " LEFT OUTER JOIN " +
                        ReviewEntry.TABLE_NAME +
                        " ON " + MovieEntry.TABLE_NAME +
                        "." + MovieEntry._ID +
                        " = " + ReviewEntry.TABLE_NAME +
                        "." + ReviewEntry.COLUMN_MOVIE_ROW_ID);
    }

    public Cursor build() {
        final MovieDbHelper movieDbHelper = new MovieDbHelper(context);
        final SQLiteDatabase readableDatabase = movieDbHelper.getReadableDatabase();

        final String[] allColumns = null;
        final long movieRowId = ContentUris.parseId(uri);
        final String[] selectionArgs = {String.valueOf(movieRowId)};
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

        final String[] videosSelectionArgs = {String.valueOf(movieRowId), "YouTube", "Trailer"};
        final String videosSelection = SINGLE_MOVIE_SELECTION + " AND " + VIDEO_SITE_SELECTION + " AND " + VIDEO_TYPE_SELECTION;
        final Cursor videoCursor = VIDEOS_MOVIE_FAV_JOIN.query(readableDatabase,
                allColumns,
                videosSelection,
                videosSelectionArgs,
                groupBy,
                having,
                orderBy
        );

        final Cursor reviewsCursor = REVIEWS_MOVIE_FAV_JOIN.query(readableDatabase,
                allColumns,
                SINGLE_MOVIE_SELECTION,
                selectionArgs,
                groupBy,
                having,
                orderBy
        );

        return new MergeCursor(new Cursor[]{movieCursor, videoCursor, reviewsCursor});
    }

}
