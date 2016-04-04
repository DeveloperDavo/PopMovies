package com.example.android.popularmoviesapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by David on 04/04/16.
 */
public class MoviePosterAdapter extends ArrayAdapter<Integer> {
    public MoviePosterAdapter(Activity context, List<Integer> moviePosters) {
        super(context, 0, moviePosters);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Integer moviePoster = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.grid_item_poster, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.photo_image_view);
        imageView.setImageResource(moviePoster);

        return convertView;
    }
}
