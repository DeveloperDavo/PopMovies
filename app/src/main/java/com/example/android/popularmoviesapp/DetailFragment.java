package com.example.android.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import static com.example.android.popularmoviesapp.data.MovieContract.MovieEntry;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    public static final String MOVIE_ROW_ID = "movie_row_id";
    private long movieRowId;
    private DetailAdapter detailAdapter;

    private static final int DETAIL_LOADER = 0;

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
            movieRowId = arguments.getLong(DetailFragment.MOVIE_ROW_ID);
        }

        // instantiate adapter
        detailAdapter = new DetailAdapter(getActivity(), null, 0);

        // inflate root view
        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // attach adapter to list view
        detailView = (ListView) rootView.findViewById(R.id.list_view_details);
        detailView.setAdapter(detailAdapter);

        openVideoOnClick();


        return rootView;

    }

    private void openVideoOnClick() {
        detailView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final Cursor cursor = getSingleMovieCursorAndMoveToPosition(
                        getContext(), position, movieRowId);

                if (Utility.isVideosView(cursor)) {
                    final Uri videoUri = getVideoUriFrom(cursor);
                    startExternalIntent(videoUri);
                } else {

                }
            }
        });
    }

    @NonNull
    private Cursor getSingleMovieCursorAndMoveToPosition(Context context, int position, long movieRowId) {
        final Cursor cursor = Utility.querySingleMovieUri(context, movieRowId);
        cursor.moveToPosition(position);
        return cursor;
    }


    private Uri getVideoUriFrom(Cursor cursor) {
        final String videoKey = Utility.getVideoKeyFrom(cursor);
        return Uri.parse("https://www.youtube.com/watch?v=" + videoKey);
    }

    private void startExternalIntent(Uri videoUri) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, videoUri);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(LOG_TAG, "Couldn't open YouTube");
        }
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
        if (movieRowId == -1) {
            return null;
        }

        return buildDetailCursorLoader();
    }

    @Nullable
    private Loader<Cursor> buildDetailCursorLoader() {

        // This tells query which uri to use so it can build the cursor in SingleMovieCursorBuilder
        final Uri uri = MovieEntry.buildMovieUri(movieRowId);

        // The values are determined while the cursor is being built in SingleMovieCursorBuilder
        final String[] projection = null;
        final String selection = null;
        final String[] selectionArgs = null;
        final String sortOrder = null;

        return new CursorLoader(getActivity(),
                uri,
                projection,
                selection,
                selectionArgs,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        detailAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        detailAdapter.swapCursor(null);
    }

}
