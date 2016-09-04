package com.example.android.popularmoviesapp;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class ReviewAdapter extends CursorAdapter {
    private static final String LOG_TAG = ReviewAdapter.class.getSimpleName();
//    private String text;

    public ReviewAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

//    public void setText(String text) {
//        this.text = text;
//    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_review, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = (TextView) view.findViewById(R.id.review_text_view);
        final String author = cursor.getString(DetailFragment.COL_REVIEW_AUTHOR);
        Log.d(LOG_TAG, "bindView cursor: " + DatabaseUtils.dumpCursorToString(cursor));
        Log.d(LOG_TAG, "review author: " + author);
//        Log.d(LOG_TAG, "text: " + text);
        textView.setText(author);
    }
}
