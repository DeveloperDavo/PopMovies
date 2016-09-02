package com.example.android.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    public static Intent newIntent(Context context, Uri uri) {
        Log.d(LOG_TAG, "newIntent");

        final Intent intent = new Intent(context, DetailActivity.class);
        intent.setData(uri);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        // if there is no previously saved stated, get the uri and put it in a bundle
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());

            final DetailFragment detailFragment = DetailFragment.newInstance();
            detailFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction().
                    add(R.id.movie_detail_container, detailFragment).
                    commit();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

}
