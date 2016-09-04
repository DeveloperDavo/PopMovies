package com.example.android.popularmoviesapp;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.popularmoviesapp.data.MovieContract.ReviewEntry;
import com.example.android.popularmoviesapp.data.MovieContract.VideoEntry;
import com.squareup.picasso.Picasso;

import static com.example.android.popularmoviesapp.data.MovieContract.MovieEntry;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    public static final String DETAIL_URI = "detailUri";
    private Uri detailUri;
    private ReviewAdapter reviewAdapter;

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
            ReviewEntry.COLUMN_AUTHOR,
            VideoEntry.COLUMN_VIDEO_KEY
    };

    static final int COL_MOVIE_TITLE = 2;
    static final int COL_MOVIE_POSTER_PATH = 3;
    static final int COL_MOVIE_OVERVIEW = 4;
    static final int COL_MOVIE_RATING = 5;
    static final int COL_MOVIE_RELEASE = 6;
    static final int COL_VIDEO_KEY = 7;

    private static final int REVIEW_LOADER = 1;
    static final String[] REVIEW_COLUMNS = new String[]{
            ReviewEntry.TABLE_NAME + "." + ReviewEntry._ID,
            ReviewEntry.COLUMN_AUTHOR,
            ReviewEntry.COLUMN_CONTENT
    };
    static final int COL_REVIEW_AUTHOR = 1;
    static final int COL_REVIEW_CONTENT = 2;

    /**********************************************************************************************/

    private ImageView posterView;
    private TextView titleView;
    private TextView overviewView;
    private TextView userRatingView;
    private TextView releaseDateView;
    private ListView reviewsView;
    private TextView videosView;

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

        // instantiate adapters
        reviewAdapter = new ReviewAdapter(getActivity(), null, 0);

        // query cursor, get count and pass to reviewAdapter so the adapter knows when to start using the video data
//        final Cursor cursor = getContext().getContentResolver().query(detailUri, REVIEW_COLUMNS, null, null, null);
//        int count = cursor.getCount();
//        Log.d(LOG_TAG, "detailUri query REVIEW_COLUMNS: " + DatabaseUtils.dumpCursorToString(cursor));

        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // get views
        posterView = (ImageView) rootView.findViewById(R.id.detail_image_view);
        titleView = (TextView) rootView.findViewById(R.id.title);
        overviewView = (TextView) rootView.findViewById(R.id.overview);
        userRatingView = (TextView) rootView.findViewById(R.id.rating);
        releaseDateView = (TextView) rootView.findViewById(R.id.release);
        reviewsView = (ListView) rootView.findViewById(R.id.list_view_reviews);
        videosView = (TextView) rootView.findViewById(R.id.video1);

        // attach adapters to list views
        reviewsView.setAdapter(reviewAdapter);
        reviewsView.setAdapter(reviewAdapter);

        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, // uniqueId that identifies the loader
                null, // optional arguments to supply the loader at construction
                this); // LoaderManger.LoaderCallbacks implementation
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // if detail fragment is created without data (ie two pane layout)
        if (null == detailUri) {
            return null;
        }

        switch (id) {
            case 0:
                return getDetailCursorLoader();
            case 1:
                return getReviewCursorLoader();
            default:
                Log.e(LOG_TAG, "loader id does not exist");
                return null;
        }
    }

    @Nullable
    private Loader<Cursor> getDetailCursorLoader() {
        return new CursorLoader(getActivity(),
                detailUri,
                DETAIL_COLUMNS,
                null,
                null,
                null);
    }

    @NonNull
    private Loader<Cursor> getReviewCursorLoader() {
        return new CursorLoader(getActivity(),
                detailUri,
                REVIEW_COLUMNS,
                null,
                null,
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
        loadVideo1IntoView(cursor);
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

    private void loadVideo1IntoView(Cursor cursor) {
        final String video1 = cursor.getString(COL_VIDEO_KEY);
        videosView.setText(video1);
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
            default:
                Log.e(LOG_TAG, "loader id does not exist");
                return;
        }
    }

}
