package com.example.android.popularmoviesapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ReviewAdapter extends CursorAdapter {
    private static final String LOG_TAG = ReviewAdapter.class.getSimpleName();

    public ReviewAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_review, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = (TextView) view.findViewById(R.id.review_text_view);
//        final String author = cursor.getString(DetailFragment.COL_REVIEW_AUTHOR);
//        Log.d(LOG_TAG, "bindView cursor: " + DatabaseUtils.dumpCursorToString(cursor));
//        textView.setText(author);
    }
}
