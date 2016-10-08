package com.example.android.popularmoviesapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

/**
 * Created by David on 04/04/16.
 */
// TODO: rename as PosterAdapter
class MoviePosterAdapter extends CursorAdapter {

    private static final String LOG_TAG = MoviePosterAdapter.class.getSimpleName();

    MoviePosterAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.grid_item_poster, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view.findViewById(R.id.poster_image_view);
        imageView.setImageBitmap(Utility.getBitmapFromBlob(cursor));
    }

}
