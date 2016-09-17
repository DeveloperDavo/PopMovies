package com.example.android.popularmoviesapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by David on 17/09/16.
 */
public class DetailAdapter extends CursorAdapter {
    private static final String LOG_TAG = DetailAdapter.class.getSimpleName();
    private static final int VIEW_TYPE_MOVIE_DETAILS = 0;

    public DetailAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final int viewType = getItemViewType(cursor.getPosition());
//        if (viewType == VIEW_TYPE_MOVIE_DETAILS) {
//            return LayoutInflater.from(context).inflate(R.layout.list_item_movie, parent, false);
//        }
        return LayoutInflater.from(context).inflate(R.layout.list_item_video, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = (TextView) view.findViewById(R.id.video_text_view);
//        final String videoPosition = cursor.getString(DetailFragment.COL_VIDEO__ID);
//        Log.d(LOG_TAG, "bindView cursor: " + DatabaseUtils.dumpCursorToString(cursor));
        textView.setText("Video");
    }
}
