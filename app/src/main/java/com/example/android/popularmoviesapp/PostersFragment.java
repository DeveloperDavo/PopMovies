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


public class PostersFragment extends Fragment implements LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = PostersFragment.class.getSimpleName();

    /**********************************************************************************************/

    private static final int MOVIE_POSTERS_LOADER = 0;

    /**********************************************************************************************/

    private PosterAdapter posterAdapter;
    private GridView gridView;
    private int selectedPosition = GridView.INVALID_POSITION;
//    Parcelable state;

//    private static final String SELECTED_KEY = "selected_position";

    public PostersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState");
//        if (selectedPosition != GridView.INVALID_POSITION) {
//            outState.putInt(SELECTED_KEY, selectedPosition);
//        }
//        state = gridView.onSaveInstanceState();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");

        posterAdapter = new PosterAdapter(getActivity(), null, 0);

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // attach adapter to GridView
        gridView = (GridView) rootView.findViewById(R.id.grid_view_posters);
        gridView.setAdapter(posterAdapter);

        openDetailViewOnClick();

        // if a position has already been selected, get it
        // FIXME: savedInstanceSate is null upon reload
//        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
//            selectedPosition = savedInstanceState.getInt(SELECTED_KEY);
//        }
        // TODO: alternative solution??
//        if (state != null) {
//            gridView.onRestoreInstanceState(state);
//        }

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
             * @param position the position of the view in the adapter
             * @param id the row id of the item that was clicked
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // call onItemSelected, which is overridden in MainActivity
                ((Callback) getActivity()).onItemSelected(id);
                selectedPosition = position;
                // TODO: workaround for savedInstanceState
                Utility.setPosition(getContext(), selectedPosition);
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
//        Log.d(LOG_TAG, "onCreateOptionsMenu");
        inflater.inflate(R.menu.movie_posters, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        Log.d(LOG_TAG, "onOptionsItemSelected");
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

    // TODO: workaround for savedInstanceState
    private void setPreferences(String selection, String selectionArg, String sortOrder) {
//        Log.d(LOG_TAG, "setPreferences");
        Utility.setSelection(getContext(), selection);
        Utility.setSelectionArg(getContext(), selectionArg);
        Utility.setSortOrder(getContext(), sortOrder);
        Utility.setPosition(getContext(), GridView.INVALID_POSITION);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        Log.d(LOG_TAG, "onCreateLoader");

        final String[] projection = {
                MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
                MovieEntry.COLUMN_POSTER,
        };

        // TODO: workaround for savedInstanceState
        String selection = Utility.getSelection(getContext());
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
//        Log.d(LOG_TAG, "onLoaderFinished");

        posterAdapter.swapCursor(data);


        // TODO: workaround for savedInstanceState
        gridView.smoothScrollToPosition(Integer.parseInt(Utility.getPosition(getContext())));

//        if (selectedPosition != GridView.INVALID_POSITION) {
//            gridView.smoothScrollToPosition(selectedPosition);
//        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(LOG_TAG, "onLoaderReset");
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

