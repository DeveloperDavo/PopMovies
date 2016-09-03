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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmoviesapp.data.MovieContract;
import com.example.android.popularmoviesapp.data.MovieContract.ReviewEntry;
import com.squareup.picasso.Picasso;

import static com.example.android.popularmoviesapp.data.MovieContract.MovieEntry;

/**
 * Created by David on 22/08/16.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    public static final String DETAIL_URI = "detailUri";
    private Uri detailUri;


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
            ReviewEntry.COLUMN_CONTENT,
            MovieContract.VideoEntry.COLUMN_VIDEO_KEY
    };

    static final int COL__ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_TITLE = 2;
    static final int COL_MOVIE_POSTER_PATH = 3;
    static final int COL_MOVIE_OVERVIEW = 4;
    static final int COL_MOVIE_RATING = 5;
    static final int COL_MOVIE_RELEASE = 6;
    static final int COL_REVIEW_CONTENT = 7;
    static final int COL_VIDEO_KEY = 8;

    /**********************************************************************************************/

    private ImageView posterView;
    private TextView titleView;
    private TextView overviewView;
    private TextView userRatingView;
    private TextView releaseDateView;
    private TextView reviewsView;
    private TextView videosView;
    private Cursor detailCursor;

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
            detailUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        posterView = (ImageView) rootView.findViewById(R.id.detail_image_view);
        titleView = (TextView) rootView.findViewById(R.id.title);
        overviewView = (TextView) rootView.findViewById(R.id.overview);
        userRatingView = (TextView) rootView.findViewById(R.id.rating);
        releaseDateView = (TextView) rootView.findViewById(R.id.release);
        reviewsView = (TextView) rootView.findViewById(R.id.review1);
        videosView = (TextView) rootView.findViewById(R.id.video1);

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
        if (null == detailUri) {
            return null;
        }

        return new CursorLoader(getActivity(),
                detailUri,
                DETAIL_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        detailCursor = cursor;
//        Log.d(LOG_TAG, "singleMovieCursor query: " + DatabaseUtils.dumpCursorToString(cursor));

        if (!detailCursor.moveToFirst()) {
            Log.d(LOG_TAG, "detailCursor.moveToFirst returned false");
            return;
        }

        loadPosterIntoView();
        loadTitleIntoView();
        loadOverviewIntoView();
        loadRatingIntoView();
        loadReleaseIntoView();
        loadReview1IntoView();
        loadVideo1IntoView();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        detailCursor = null;
    }

    private void loadPosterIntoView() {
        final String posterUrl = detailCursor.getString(COL_MOVIE_POSTER_PATH);

        if (posterUrl != null) {
            Picasso.with(getActivity()).load(posterUrl).into(posterView);
        }
    }

    private void loadTitleIntoView() {
        final String title = detailCursor.getString(COL_MOVIE_TITLE);
        titleView.setText(title);
    }

    private void loadOverviewIntoView() {
        final String overview = detailCursor.getString(COL_MOVIE_OVERVIEW);
        overviewView.setText(overview);
    }

    private void loadRatingIntoView() {
        final double userRating = detailCursor.getDouble(COL_MOVIE_RATING);
        userRatingView.setText(Utility.formatRating(getContext(), userRating));
    }

    private void loadReleaseIntoView() {
        final String releaseDate = detailCursor.getString(COL_MOVIE_RELEASE);
        releaseDateView.setText(releaseDate.substring(0, 4));
    }

    private void loadReview1IntoView() {
        final String review1 = detailCursor.getString(COL_REVIEW_CONTENT);
        reviewsView.setText(review1);
    }

    private void loadVideo1IntoView() {
        final String video1 = detailCursor.getString(COL_VIDEO_KEY);
        videosView.setText(video1);
    }
}

