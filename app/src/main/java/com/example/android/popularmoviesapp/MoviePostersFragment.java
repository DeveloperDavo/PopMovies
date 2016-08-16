package com.example.android.popularmoviesapp;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import static com.example.android.popularmoviesapp.data.MovieContract.MovieEntry;


public class MoviePostersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MoviePostersFragment.class.getSimpleName();

    /**********************************************************************************************/

    private static final int MOVIE_POSTERS_LOADER = 0;
    private static final String[] MOVIE_COLUMNS = {
            MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
            MovieEntry.COLUMN_POSTER_PATH
    };

    static final int COL_ID = 0;
    static final int COL_MOVIE_POSTER_PATH = 1;

    /**********************************************************************************************/

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

        posterAdapter = new MoviePosterAdapter(getActivity(), null, 0);

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // attach adapter to GridView
        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view_posters);
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
                final Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    final Long _id = cursor.getLong(COL_ID);
                    final Uri singleMovieUri = MovieEntry.buildMovieUri(_id);
                    startActivity(DetailActivity.newIntent(getContext(), singleMovieUri));
                }
            }
        });

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
            return true;
        } else if (id == R.id.action_sort_by_rating) {
            (new FetchMovieTask(getContext())).execute(getString(R.string.pref_sort_by_rating));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_POSTERS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // TODO sortOrder
        final String sortOrder = null;
        return new CursorLoader(getActivity(),
                MovieEntry.CONTENT_URI,
                MOVIE_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        posterAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        posterAdapter.swapCursor(null);
    }
}
