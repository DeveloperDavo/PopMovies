package com.example.android.popularmoviesapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends AppCompatActivity implements MoviePostersFragment.Callback {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String DETAIL_FRAGMENT_TAG = "detailTag";
    private boolean twoPaneLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        if (findViewById(R.id.movie_detail_container) != null) {
            twoPaneLayout = true;

            // use a fragment transaction to replace the container with a detail fragment
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container,
                                DetailFragment.newInstance(), DETAIL_FRAGMENT_TAG)
                        .commit();
            }

        } else {
            twoPaneLayout = false;
        }

        Log.d(LOG_TAG, "density: " + getResources().getDisplayMetrics().density);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onItemSelected(Uri singleMovieUri) {

        // if two pane mode, show the detail view,
        // else launch detail activity
        if (twoPaneLayout) {
            final Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, singleMovieUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            startActivity(DetailActivity.newIntent(this, singleMovieUri));
        }
    }
}
