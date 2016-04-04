package com.example.android.popularmoviesapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
//    ArrayAdapter<Integer> mPosterAdapter;
    MoviePosterAdapter mPosterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ImageView imageView = (ImageView) findViewById(R.id.photo_image_view);
        // load Picasso image
//        Picasso.with(this).load("http://i.imgur.com/DvpvklR.png").into(imageView);

        // references to images
        Integer[] images = {
                R.drawable.picasso, R.drawable.interstellar,
                R.drawable.interstellar, R.drawable.picasso
        };

        mPosterAdapter = new MoviePosterAdapter(this, Arrays.asList(images));

        // attach adapter to GridView
        GridView gridView = (GridView) findViewById(R.id.grid_view_posters);
        gridView.setAdapter(mPosterAdapter);
    }
}
