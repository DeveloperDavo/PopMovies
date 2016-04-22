package com.example.android.popularmoviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

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

            // get attributes
            MovieAttributes movieAttributes = null;
            String posterUrl = null;
            String title = null;
            String overview = null;
            String userRating = null;
            String releaseDate = null;
            try {
                movieAttributes = new MovieAttributes(dataJsonStr, position);
                posterUrl = movieAttributes.parsePosterUrl();
                title = movieAttributes.parseOriginalTitle();
                overview = movieAttributes.parseOverview();
                userRating = movieAttributes.parseRating();
                releaseDate = movieAttributes.parseRelease();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ImageView imageView = (ImageView) findViewById(R.id.detail_image_view);
            Picasso.with(this).load(posterUrl).into(imageView);

            // set text views
            TextView titleView = (TextView) findViewById(R.id.title);
            titleView.setText(title);
            TextView overviewView = (TextView) findViewById(R.id.overview);
            overviewView.setText(overview);
            TextView userRatingView = (TextView) findViewById(R.id.rating);
            userRatingView.setText(userRating);
            TextView releaseDateView = (TextView) findViewById(R.id.release);
            releaseDateView.setText(releaseDate);
        }
    }
}
