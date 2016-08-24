package com.example.android.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    private static final String POSTER = "poster";
    private static final String TITLE = "title";
    private static final String OVERVIEW = "overview";
    private static final String RATING = "rating";
    private static final String RELEASE = "release";

    public static Intent newIntent(Context context, Uri uri) {
        Log.d(LOG_TAG, "newIntent");
        final Intent intent = new Intent(context, DetailActivity.class);
//        intent.putExtra(Intent.EXTRA_TEXT, movieJsonStr);
//        intent.putExtra("position", position);
        intent.setData(uri);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        // if there is no previously saved stated
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
