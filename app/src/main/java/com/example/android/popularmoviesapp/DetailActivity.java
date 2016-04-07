package com.example.android.popularmoviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

public class DetailActivity extends AppCompatActivity {
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // get posterUrl from MainActivity and load image
        Intent intent = this.getIntent();
        int position = intent.getIntExtra("position", -1);

        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String dataJsonStr = intent.getStringExtra(Intent.EXTRA_TEXT);

            MovieAttributes movieAttributes = null;
            try {
                movieAttributes = new MovieAttributes(dataJsonStr, position);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // show movie poster
            String posterUrl = movieAttributes.getPosterUrl();
            ImageView imageView = (ImageView) findViewById(R.id.detail_image_view);
            Picasso.with(this).load(posterUrl).into(imageView);

            // show other attributes
            String title = movieAttributes.getOriginalTitle();
            Log.d(LOG_TAG, "title : " + title);
        }
    }
}
