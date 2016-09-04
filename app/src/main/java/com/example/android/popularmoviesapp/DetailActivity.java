package com.example.android.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private static long movieKey;

    public static Intent newIntent(Context context, long movieKey) {
        Log.d(LOG_TAG, "newIntent");

        DetailActivity.movieKey = movieKey;

        return new Intent(context, DetailActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        // if there is no previously saved stated, get the uri and put it in a bundle
        if (savedInstanceState == null) {

            final DetailFragment detailFragment = DetailFragment.newInstance(movieKey);

            getSupportFragmentManager().beginTransaction().
                    add(R.id.movie_detail_container, detailFragment).
                    commit();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

}
