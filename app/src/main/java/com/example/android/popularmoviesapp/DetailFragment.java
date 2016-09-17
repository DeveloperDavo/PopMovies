package com.example.android.popularmoviesapp;

import android.database.Cursor;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.popularmoviesapp.data.MovieContract.ReviewEntry;
import com.squareup.picasso.Picasso;

import static com.example.android.popularmoviesapp.data.MovieContract.MovieEntry;
import static com.example.android.popularmoviesapp.data.MovieContract.VideoEntry;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    public static final String MOVIE_KEY = "movie_key";
    private long movieKey;
    private ReviewAdapter reviewAdapter;
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
    };

    static final int COL_MOVIE_TITLE = 2;
    static final int COL_MOVIE_POSTER_PATH = 3;
    static final int COL_MOVIE_OVERVIEW = 4;
    static final int COL_MOVIE_RATING = 5;
    static final int COL_MOVIE_RELEASE = 6;

    private static final int REVIEW_LOADER = 1;
    static final String[] REVIEW_COLUMNS = new String[]{
            ReviewEntry.TABLE_NAME + "." + ReviewEntry._ID,
            ReviewEntry.COLUMN_AUTHOR,
            ReviewEntry.COLUMN_CONTENT
    };

    static final int COL_REVIEW_AUTHOR = 1;
    static final int COL_REVIEW_CONTENT = 2;

    private static final int VIDEO_LOADER = 2;
    static final String[] VIDEO_COLUMNS = new String[]{
            VideoEntry.TABLE_NAME + "." + VideoEntry._ID,
            VideoEntry.COLUMN_VIDEO_ID,
    };

    static final int COL_VIDEO__ID = 0;
    static final int COL_VIDEO_ID = 1;

    /**********************************************************************************************/

    private ImageView posterView;
    private TextView titleView;
    private TextView overviewView;
    private TextView userRatingView;
    private TextView releaseDateView;
    private ListView reviewsView;
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

        // instantiate adapters
        reviewAdapter = new ReviewAdapter(getActivity(), null, 0);
        detailAdapter = new DetailAdapter(getActivity(), null, 0);

        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // get views
        posterView = (ImageView) rootView.findViewById(R.id.detail_image_view);
        titleView = (TextView) rootView.findViewById(R.id.title);
        overviewView = (TextView) rootView.findViewById(R.id.overview);
        userRatingView = (TextView) rootView.findViewById(R.id.rating);
        releaseDateView = (TextView) rootView.findViewById(R.id.release);
        reviewsView = (ListView) rootView.findViewById(R.id.list_view_reviews);
        detailView = (ListView) rootView.findViewById(R.id.list_view_videos);

        // attach adapters to list views
        reviewsView.setAdapter(reviewAdapter);
        detailView.setAdapter(detailAdapter);

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, // uniqueId that identifies the loader
                null, // optional arguments to supply the loader at construction
                this); // LoaderManger.LoaderCallbacks implementation
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        getLoaderManager().initLoader(VIDEO_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // if detail fragment is created without data (ie two pane layout)
        if (movieKey == -1) {
            return null;
        }

        switch (id) {
            case 0:
                return getDetailCursorLoader();
            case 1:
                return getReviewCursorLoader();
            case 2:
                return buildVideoCursorLoader();
            default:
                Log.e(LOG_TAG, "loader id does not exist");
                return null;
        }
    }

    @Nullable
    private Loader<Cursor> getDetailCursorLoader() {

        final String selection = MovieEntry.TABLE_NAME + "." + MovieEntry._ID + " = ?";
        final String[] selectionArgs = new String[]{Long.toString(movieKey)};
        return new CursorLoader(getActivity(),
                MovieEntry.CONTENT_URI,
                DETAIL_COLUMNS,
                selection,
                selectionArgs,
                null);

    }

    @NonNull
    private Loader<Cursor> getReviewCursorLoader() {

        final String selection = ReviewEntry.TABLE_NAME + "." + ReviewEntry.COLUMN_MOVIE_KEY + " = ?";
        final String[] selectionArgs = new String[]{Long.toString(movieKey)};
        return new CursorLoader(getActivity(),
                ReviewEntry.CONTENT_URI,
                REVIEW_COLUMNS,
                selection,
                selectionArgs,
                null);
    }

    @NonNull
    private Loader<Cursor> buildVideoCursorLoader() {

        final String selection = VideoEntry.TABLE_NAME + "." + VideoEntry.COLUMN_MOVIE_KEY + " = ?";
        final String[] selectionArgs = new String[]{Long.toString(movieKey)};
        return new CursorLoader(getActivity(),
                VideoEntry.CONTENT_URI,
                VIDEO_COLUMNS,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case 0:
                loadMovieDetails(cursor);
                break;
            case 1:
                reviewAdapter.swapCursor(cursor);
                break;
            case 2:
                detailAdapter.swapCursor(cursor);
                break;
            default:
                Log.e(LOG_TAG, "loader id does not exist");
                return;
        }
    }

    private void loadMovieDetails(Cursor cursor) {


        if (!cursor.moveToFirst()) {
            Log.d(LOG_TAG, "cursor.moveToFirst returned false");
            return;
        }

        loadPosterIntoView(cursor);
        loadTitleIntoView(cursor);
        loadOverviewIntoView(cursor);
        loadRatingIntoView(cursor);
        loadReleaseIntoView(cursor);
    }

    private void loadPosterIntoView(Cursor cursor) {
        final String posterUrl = cursor.getString(COL_MOVIE_POSTER_PATH);

        if (posterUrl != null) {
            Picasso.with(getActivity()).load(posterUrl).into(posterView);
        }
    }

    private void loadTitleIntoView(Cursor cursor) {
        final String title = cursor.getString(COL_MOVIE_TITLE);
        titleView.setText(title);
    }

    private void loadOverviewIntoView(Cursor cursor) {
        final String overview = cursor.getString(COL_MOVIE_OVERVIEW);
        overviewView.setText(overview);
    }

    private void loadRatingIntoView(Cursor cursor) {
        final double userRating = cursor.getDouble(COL_MOVIE_RATING);
        userRatingView.setText(Utility.formatRating(getContext(), userRating));
    }

    private void loadReleaseIntoView(Cursor cursor) {
        final String releaseDate = cursor.getString(COL_MOVIE_RELEASE);
        releaseDateView.setText(releaseDate.substring(0, 4));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case 0:
                // TODO: what is the point of this?
//                detailUri = null;
                break;
            case 1:
                // release any resources we might be using
                reviewAdapter.swapCursor(null);
                break;
            case 2:
                // release any resources we might be using
                detailAdapter.swapCursor(null);
                break;
            default:
                Log.e(LOG_TAG, "loader id does not exist");
                return;
        }
    }

}
