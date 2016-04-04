package com.example.android.popularmoviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get posterUrl from MainActivity and load image
        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String posterUrl = intent.getStringExtra(Intent.EXTRA_TEXT);
            ImageView imageView = (ImageView) findViewById(R.id.detail_image_view);
            Picasso.with(this).load(posterUrl).into(imageView);
        }
    }
}
