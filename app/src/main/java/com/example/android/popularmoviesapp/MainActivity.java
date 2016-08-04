package com.example.android.popularmoviesapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private MoviePosterAdapter posterAdapter;
    private String movieJsonStr;
    private MovieInfoParser movieInfoParser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // update array adapter
        posterAdapter = new MoviePosterAdapter(this, new ArrayList<String>());

        // attach adapter to GridView
        GridView gridView = (GridView) findViewById(R.id.grid_view_posters);
        gridView.setAdapter(posterAdapter);

        // invoke when an item in the list has been clicked
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Enter detail activity upon clicking on the item in the list
             *
             * @param parent the AdapterView where the click happened
             * @param view the view within the AdapterView that was clicked
             *             (this will be a view provided by the adapter)
             * @param position the position of the view in the adapter
             * @param id the row id of the item that was clicked
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: pass movieInfoParser instead of movieJsonStr - obsolete??
//                startActivity(DetailActivity.newIntent(MainActivity.this, movieJsonStr, position));
            }
        });
    }

    /* Fetches movie data upon start up. */
    @Override
    public void onStart() {
        Log.d(LOG_TAG, "onStart");
        super.onStart();
        (new FetchMovieTask(this, posterAdapter)).execute(getString(R.string.pref_sort_by_rating));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_by_popularity) {
            (new FetchMovieTask(this, posterAdapter)).execute(getString(R.string.pref_sort_by_popularity));
            return true;
        } else if (id == R.id.action_sort_by_ration) {
            (new FetchMovieTask(this, posterAdapter)).execute(getString(R.string.pref_sort_by_rating));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
