package com.example.android.popularmoviesapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by David on 04/04/16.
 */
class PosterAdapter extends CursorAdapter {

    private static final String LOG_TAG = PosterAdapter.class.getSimpleName();

    PosterAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.grid_item_poster, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view.findViewById(R.id.poster_image_view);
        Picasso.with(context).load(Utility.getPosterPathFrom(cursor)).into(imageView);
    }

}
