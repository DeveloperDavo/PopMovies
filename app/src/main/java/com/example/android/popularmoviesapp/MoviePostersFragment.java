package com.example.android.popularmoviesapp;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
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


public class MoviePostersFragment extends Fragment implements LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MoviePostersFragment.class.getSimpleName();

    /**********************************************************************************************/

    private static final int MOVIE_POSTERS_LOADER = 0;
    private static final String[] MOVIE_COLUMNS = {
            MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
            MovieEntry.COLUMN_POSTER_PATH,
    };

    static final int COL_ID = 0;
    static final int COL_MOVIE_POSTER_PATH = 1;
    private static final String SELECTION = "selection";
    private static final String SELECTION_ARGS = "selection_args";

    /**********************************************************************************************/

    private MoviePosterAdapter posterAdapter;
    private MoviePosterAdapter favPosterAdapter;
    private GridView gridView;
    private int selectedPosition = GridView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";

    public MoviePostersFragment() {
        // Required empty public constructor
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
        gridView = (GridView) rootView.findViewById(R.id.grid_view_posters);
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

                // call onItemSelected, which is overridden in MainActivity
                ((Callback) getActivity()).onItemSelected(id);
                selectedPosition = position;
            }
        });

        // if a position has already been selected, get it
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            selectedPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onStart() {
        Log.d(LOG_TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_POSTERS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
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
            updateMovies(R.string.pref_sort_by_popularity);
            final String sortOrder = MovieEntry.COLUMN_POPULARITY + " DESC";
            return setSortOrderAndRestartLoader(sortOrder);
        } else if (id == R.id.action_sort_by_rating) {
            updateMovies(R.string.pref_sort_by_rating);
            final String sortOrder = MovieEntry.COLUMN_RATING + " DESC";
            return setSortOrderAndRestartLoader(sortOrder);
        } else if (id == R.id.action_favorites) {
            restartLoaderWithArgs();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader");

        final String sortOrder = Utility.getSortOrder(getContext());

        String selection = null;
        String[] selectionArgs = null;
        if (args != null) {
            selection = args.getString(SELECTION);
            selectionArgs = args.getStringArray(SELECTION_ARGS);
        }
        return new CursorLoader(getActivity(),
                MovieEntry.CONTENT_URI,
                MOVIE_COLUMNS,
                selection,
                selectionArgs,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(LOG_TAG, "onLoaderFinished");
        posterAdapter.swapCursor(data);
//        if (selectedPosition != GridView.INVALID_POSITION) {
//            gridView.smoothScrollToPosition(selectedPosition);
//        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(LOG_TAG, "onLoaderReset");
        posterAdapter.swapCursor(null);
    }

    private void updateMovies(int prefString) {
        Log.d(LOG_TAG, "updateMovies");
        (new FetchMovieTask(getContext())).execute(getString(prefString));
    }

    private boolean setSortOrderAndRestartLoader(String sortOrder) {
        Log.d(LOG_TAG, "setSortOrderAndRestartLoader");
        Utility.setSortOrder(getContext(), sortOrder);
        getLoaderManager().restartLoader(MOVIE_POSTERS_LOADER, null, this);
        return true;
    }

    private boolean restartLoaderWithArgs() {
        Log.d(LOG_TAG, "restartLoaderWithArgs");
        final Bundle args = new Bundle();
        final String selection = MovieEntry.COLUMN_FAVORITE + " = ?";
        final String[] selectionArgs = new String[]{Integer.toString(1)};
        args.putString(SELECTION, selection);
        args.putStringArray(SELECTION_ARGS, selectionArgs);
        getLoaderManager().restartLoader(MOVIE_POSTERS_LOADER, args, this);
        return true;
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(long movieKey);
    }
}

