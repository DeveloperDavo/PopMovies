package com.example.android.popularmoviesapp.data;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.popularmoviesapp.Utility;

import static com.example.android.popularmoviesapp.data.MovieContract.MovieEntry;

/**
 * Created by David on 09/10/16.
 */
class MovieCursorBuilder {
    private static final String LOG_TAG = MovieCursorBuilder.class.getSimpleName();

    private int increments = 1;
    static final String RATING = "rating";
    static final String POPULARITY = "popularity";

    private SQLiteDatabase readableDatabase;
    private String[] projection;
    private String initialSelection;
    private String[] selectionArgs;
    private String sortOrder;

    MovieCursorBuilder(
            SQLiteDatabase readableDatabase, String[] projection,
            String selection, String[] selectionArgs, String sortOrder) {

        this.readableDatabase = readableDatabase;
        this.projection = projection;
        this.initialSelection = selection;
        this.selectionArgs = selectionArgs;
        this.sortOrder = sortOrder;
    }

    /**
     * In order to avoid the warning 'CursorWindow: Window is ful' the queries have been broken up
     * into `increments` smaller queries and then merged.
     */
    public Cursor build() {
        Cursor cursor;
        if ((Utility.SORT_BY_RATING_DESC).equals(sortOrder)) {
            cursor =  buildMergeCursorIncrementingBy(RATING);
        } else if ((Utility.SORT_BY_POPULARITY_DESC).equals(sortOrder)) {
            cursor =  buildMergeCursorIncrementingBy(POPULARITY);
        } else {
            cursor =  buildCursor(initialSelection);
        }
//        Log.d(LOG_TAG, "cursorDump: " + DatabaseUtils.dumpCursorToString(cursor));
        return cursor;
    }

    @NonNull
    private Cursor buildMergeCursorIncrementingBy(String incrementType) {
        Cursor[] cursors = new Cursor[increments];
        for (int i = 0; i < increments; i++) {
            String selection = setUpSelection(initialSelection); // reset at the beginning of each loop
            selection = incrementBy(incrementType, i, selection);
            final Cursor currentCursor = buildCursor(selection);
            Log.d(LOG_TAG, "currentCursorDump: " + DatabaseUtils.dumpCursorToString(currentCursor));
            cursors[i] = currentCursor;
//            return currentCursor;
        }
        MergeCursor mergeCursor = new MergeCursor(cursors);
        mergeCursor.requery();
        return mergeCursor;
    }

    /**
     * Selects all rows within a certain range. 0.00 is never captured in the selection.
     */
    @NonNull
    String incrementBy(String incrementType, int i, String selection) {
        String column;
        if (RATING.equals(incrementType)) {
            column = MovieEntry.COLUMN_RATING;
            selection += column + " > " + (10.00 - 10.00 / increments * (i + 1)) +
                    " AND " + column + " <= " + (10.00 - 10.00 / increments * i);
        } else {
            column = MovieEntry.COLUMN_POPULARITY;
            selection += column + " > " + (100.00 - 100.00 / increments * (i + 1) +
                    " AND " + column + " <= " + (100.00 - 100.00 / increments * i));
        }
        return selection;
    }

    @NonNull
    String setUpSelection(String selection) {
        if (selection != null) {
            selection += " AND ";
        } else {
            selection = "";
        }
        return selection;
    }

    Cursor buildCursor(String selection) {
        return readableDatabase.query(MovieEntry.TABLE_NAME,
                projection, selection, selectionArgs, null, null, sortOrder);
    }

    void setIncrements(int increments) {
        this.increments = increments;
    }
}
