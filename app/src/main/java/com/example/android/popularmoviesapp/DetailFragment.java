package com.example.android.popularmoviesapp;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import static com.example.android.popularmoviesapp.data.MovieContract.MovieEntry;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    public static final String MOVIE_KEY = "movie_key";
    private long movieKey;
    private DetailAdapter detailAdapter;

    /**********************************************************************************************/

    private static final int DETAIL_LOADER = 0;
    static final String[] DETAIL_COLUMNS = new String[]{
            MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
            MovieEntry.COLUMN_MOVIE_ID,
            MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_POSTER_PATH,
            MovieEntry.COLUMN_OVERVIEW,
            MovieEntry.COLUMN_RATING,
            MovieEntry.COLUMN_RELEASE,
            MovieEntry.COLUMN_FAVORITE
    };

    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_TITLE = 2;
    static final int COL_MOVIE_POSTER_PATH = 3;
    static final int COL_MOVIE_OVERVIEW = 4;
    static final int COL_MOVIE_RATING = 5;
    static final int COL_MOVIE_RELEASE = 6;
    static final int COL_MOVIE_FAVORITE = 7;

    /**********************************************************************************************/

    private ListView detailView;

    public static DetailFragment newInstance() {
        Log.d(LOG_TAG, "newInstance");
        return new DetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final Bundle arguments = getArguments();
        if (arguments != null) {
            movieKey = arguments.getLong(DetailFragment.MOVIE_KEY);
        }

        // instantiate adapter
        detailAdapter = new DetailAdapter(getActivity(), null, 0);

        // inflate root view
        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // attach adapter to list view
        detailView = (ListView) rootView.findViewById(R.id.list_view_details);
        detailView.setAdapter(detailAdapter);

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, // uniqueId that identifies the loader
                null, // optional arguments to supply the loader at construction
                this); // LoaderManger.LoaderCallbacks implementation
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // if detail fragment is created without data (ie two pane layout)
        if (movieKey == -1) {
            return null;
        }

        return buildDetailCursorLoader();
    }

    @Nullable
    private Loader<Cursor> buildDetailCursorLoader() {

        final Uri uri = MovieEntry.buildMovieUri(movieKey);
        return new CursorLoader(getActivity(),
                uri,
                DETAIL_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        detailAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        detailAdapter.swapCursor(null);
//        }
    }

}
