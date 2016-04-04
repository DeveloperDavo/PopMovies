package com.example.android.popularmoviesapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by David on 04/04/16.
 */
public class MoviePosterAdapter extends ArrayAdapter<String> {
    public MoviePosterAdapter(Activity context, List<String> moviePosterUrls) {
        super(context, 0, moviePosterUrls);
    }

    /**
     * Custom array adapter used when returning more than a simple text view.
     *
     * @param position of item
     * @param convertView old view to reuse, if possible
     * @param parent view that this will eventually be attached to
     * @return View of image views
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String moviePosterUrl = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.grid_item_poster, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.photo_image_view);
        Picasso.with(getContext()).load(moviePosterUrl).into(imageView);

        return convertView;
    }
}
