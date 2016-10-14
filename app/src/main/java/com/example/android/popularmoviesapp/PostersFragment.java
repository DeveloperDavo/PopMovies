package com.example.android.popularmoviesapp;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import static com.example.android.popularmoviesapp.data.MovieContract.MovieEntry;


public class PostersFragment extends Fragment implements LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = PostersFragment.class.getSimpleName();

    /**********************************************************************************************/

    private static final int MOVIE_POSTERS_LOADER = 0;

    /**********************************************************************************************/

    private PosterAdapter posterAdapter;
    private GridView gridView;
    private int selectedPosition = GridView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";

    public PostersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (selectedPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, selectedPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        posterAdapter = new PosterAdapter(getActivity(), null, 0);

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // attach adapter to GridView
        gridView = (GridView) rootView.findViewById(R.id.grid_view_posters);
        gridView.setAdapter(posterAdapter);

        openDetailViewOnClick();

        // if a position has already been selected, get it
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            selectedPosition = savedInstanceState.getInt(SELECTED_KEY);
        } else {
            selectedPosition = Integer.parseInt(Utility.getPosition(getContext()));
        }

        return rootView;
    }

    private void openDetailViewOnClick() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Enter detail activity upon clicking on the item in the list
             *
             * @param parent the AdapterView where the click happened
             * @param view the view within the AdapterView that was clicked
             *             (this will be a view provided by the adapter)
             * @param position the position of the cursor in the adapter
             * @param id the cursor id of the item that was clicked. In this case it is the movie._ID
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedPosition = position;

                final long movieId = Utility.getMovieIdFromMovieKey(getContext(), id);

                // onItemSelected is overridden in MainActivity
                ((Callback) getActivity()).onItemSelected(id);

                new FetchReviewsTask(getContext(), id, movieId).execute();

                // persist position so it can be later restored
                Utility.setPosition(getContext(), position);
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_POSTERS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.movie_posters, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        PopMoviesSyncAdapter.syncImmediately(getContext());

        if (id == R.id.action_sort_by_popularity) {
            setPopularPrefs();
        } else if (id == R.id.action_sort_by_rating) {
            setRatingPrefs();
        } else if (id == R.id.action_favorites) {
            setFavoritesPrefs();
        } else {
            return super.onOptionsItemSelected(item);
        }

        getLoaderManager().restartLoader(MOVIE_POSTERS_LOADER, null, this);
        return true;

    }

    private void setPopularPrefs() {
        final String sortOrder = Utility.SORT_BY_POPULARITY_DESC;
        setPreferences(null, null, sortOrder);
    }

    private void setRatingPrefs() {
        setPreferences(null, null, Utility.SORT_BY_RATING_DESC);
    }

    private void setFavoritesPrefs() {
        final String selection = MovieEntry.COLUMN_FAVORITE + " = ?";
        final String selectionArg = Integer.toString(1);
        setPreferences(selection, selectionArg, Utility.SORT_BY_POPULARITY_DESC);
    }

    private void setPreferences(String selection, String selectionArg, String sortOrder) {
        Utility.setSelection(getContext(), selection);
        Utility.setSelectionArg(getContext(), selectionArg);
        Utility.setSortOrder(getContext(), sortOrder);
        Utility.setPosition(getContext(), GridView.INVALID_POSITION);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String[] projection = {
                MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
                MovieEntry.COLUMN_POSTER,
        };

        final String selection = Utility.getSelection(getContext());
        final String selectionArg = Utility.getSelectionArg(getContext());

        String[] selectionArgs;
        if (selectionArg == null) {
            selectionArgs = null;
        } else {
            selectionArgs = new String[]{selectionArg};
        }

        final String sortOrder = Utility.getSortOrder(getContext());

        return new CursorLoader(getActivity(),
                MovieEntry.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        posterAdapter.swapCursor(data);

        if (selectedPosition != GridView.INVALID_POSITION) {
            gridView.smoothScrollToPosition(selectedPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        posterAdapter.swapCursor(null);
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

