package com.example.android.popularmoviesapp;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static com.example.android.popularmoviesapp.data.MovieContract.MovieEntry;

public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    private static final String POSTER = "poster";
    private static final String TITLE = "title";
    private static final String OVERVIEW = "overview";
    private static final String RATING = "rating";
    private static final String RELEASE = "release";

    public static Intent newIntent(Context context, Uri uri) {
        Log.d(LOG_TAG, "newIntent");
        final Intent intent = new Intent(context, DetailActivity.class);
//        intent.putExtra(Intent.EXTRA_TEXT, movieJsonStr);
//        intent.putExtra("position", position);
        intent.setData(uri);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    // TODO "this" only recognises non support version of LoaderCallbacks
    public static class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        private Uri singleMovieUri;
//        private MovieInfoParser movieInfoParser;


        /**********************************************************************************************/

        private static final int DETAIL_LOADER = 0;
        private static final String[] MOVIE_COLUMNS = {
                MovieEntry.TABLE_NAME + "." + MovieEntry._ID,
                MovieEntry.COLUMN_TITLE,
                MovieEntry.COLUMN_POSTER_PATH,
                MovieEntry.COLUMN_OVERVIEW,
                MovieEntry.COLUMN_RATING,
                MovieEntry.COLUMN_RELEASE
        };

        static final int COL__ID = 0;
        static final int COL_MOVIE_TITLE = 1;
        static final int COL_MOVIE_POSTER_PATH = 2;
        static final int COL_MOVIE_OVERVIEW = 3;
        static final int COL_MOVIE_RATING = 4;
        static final int COL_MOVIE_RELEASE = 5;

        /**********************************************************************************************/

        private ImageView posterView;
        private TextView titleView;
        private TextView overviewView;
        private TextView userRatingView;
        private TextView releaseDateView;

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

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            posterView = (ImageView) rootView.findViewById(R.id.detail_image_view);
            titleView = (TextView) rootView.findViewById(R.id.title);
            overviewView = (TextView) rootView.findViewById(R.id.overview);
            userRatingView = (TextView) rootView.findViewById(R.id.rating);
            releaseDateView = (TextView) rootView.findViewById(R.id.release);

//        parseMovieInfo();

//        loadViews();

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
            final Intent intent = getActivity().getIntent();

            return new CursorLoader(getActivity(),
                    intent.getData(),
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            loadPosterIntoView(data);
            loadTitleIntoView(data);
            loadOverviewIntoView(data);
            loadRatingIntoView(data);
            loadReleaseIntoView(data);
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
            userRatingView.setText(Double.toString(userRating));
        }

        private void loadReleaseIntoView(Cursor cursor) {
            final String releaseDate = cursor.getString(COL_MOVIE_RELEASE);
            releaseDateView.setText(releaseDate);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }


/*        private void parseMovieInfo() {

            final Intent intent = this.getIntent();

            // get position of movie in grid (default to -1)
            final int position = intent.getIntExtra("position", -1);

            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                String dataJsonStr = intent.getStringExtra(Intent.EXTRA_TEXT);

                try {
                    movieInfoParser = new MovieInfoParser(dataJsonStr, position);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            }
        }

        private void loadViews() {
            loadPosterIntoView();
            loadTitleIntoView();
            loadOverviewIntoView();
            loadRatingIntoView();
            loadReleaseIntoView();
        }

        private void loadPosterIntoView() {
            final ImageView imageView = (ImageView) findViewById(R.id.detail_image_view);
            final String posterUrl = parse(POSTER);

            if (posterUrl != null) {
                Picasso.with(this).load(posterUrl).into(imageView);
            }
        }

        private void loadTitleIntoView() {
            final String title = parse(TITLE);
            final TextView titleView = (TextView) findViewById(R.id.title);
            titleView.setText(title);
        }

        private void loadOverviewIntoView() {
            final String overview = parse(OVERVIEW);
            final TextView overviewView = (TextView) findViewById(R.id.overview);
            overviewView.setText(overview);

        }

        private void loadRatingIntoView() {
            final String userRating = parse(RATING);
            final TextView userRatingView = (TextView) findViewById(R.id.rating);
            userRatingView.setText(userRating);
        }

        private void loadReleaseIntoView() {
            final String releaseDate = parse(RELEASE);
            final TextView releaseDateView = (TextView) findViewById(R.id.release);
            releaseDateView.setText(releaseDate);
        }


        private String parse(String movieInfoAttribute) {
            try {
                if (movieInfoAttribute == POSTER) {
                    return movieInfoParser.parsePosterUrl();
                } else if (movieInfoAttribute == TITLE) {
                    return movieInfoParser.parseOriginalTitle();
                } else if (movieInfoAttribute == OVERVIEW) {
                    return movieInfoParser.parseOverview();
                } else if (movieInfoAttribute == RATING) {
                    return movieInfoParser.parseRating();
                } else if (movieInfoAttribute == RELEASE) {
                    return movieInfoParser.parseRelease();
                }

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }*/
    }
}
