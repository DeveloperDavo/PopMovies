package com.example.android.popularmoviesapp;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import static com.example.android.popularmoviesapp.data.MovieContract.MovieEntry;


public class MoviePostersFragment extends Fragment {
    private static final String LOG_TAG = MoviePostersFragment.class.getSimpleName();
    private MoviePosterAdapter posterAdapter;


    public MoviePostersFragment() {
        // Required empty public constructor
    }

    public static MoviePostersFragment newInstance() {
        Log.d(LOG_TAG, "newInstance");
        return new MoviePostersFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");

        test();

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // attach adapter to GridView
        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view_posters);
        gridView.setAdapter(posterAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        Log.d(LOG_TAG, "onStart");
        super.onStart();
        (new FetchMovieTask(getContext())).execute(getString(R.string.pref_sort_by_rating));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(LOG_TAG, "onCreateOptionsMenu");
        inflater.inflate(R.menu.movie_posters, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(LOG_TAG, "onOptionsItemSelected");
        int id = item.getItemId();

        if (id == R.id.action_sort_by_popularity) {
            (new FetchMovieTask(getContext())).execute(getString(R.string.pref_sort_by_popularity));
            test();
            return true;
        } else if (id == R.id.action_sort_by_rating) {
            (new FetchMovieTask(getContext())).execute(getString(R.string.pref_sort_by_rating));
            test();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // TODO does not update the adapter
    private void test() {
        // TODO sortOrder
        final String sortOrder = null;
        final Cursor cursor = getActivity().getContentResolver().query(MovieEntry.CONTENT_URI,
                null, null, null, sortOrder);

        posterAdapter = new MoviePosterAdapter(getActivity(), cursor, 0);
    }

}
