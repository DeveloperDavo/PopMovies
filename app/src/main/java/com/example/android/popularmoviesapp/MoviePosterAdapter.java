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
public class MoviePosterAdapter extends CursorAdapter {

    private static final String LOG_TAG = MoviePosterAdapter.class.getSimpleName();

    public MoviePosterAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.grid_item_poster, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view.findViewById(R.id.poster_image_view);
        // TODO: what if there is poster path is null
        Picasso.with(context).load(getPosterUrlFrom(cursor)).into(imageView);
    }

    private String getPosterUrlFrom(Cursor cursor) {
        return cursor.getString(MoviePostersFragment.COL_MOVIE_POSTER_PATH);
    }

}
