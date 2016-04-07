package com.example.android.popularmoviesapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private MoviePosterAdapter mPosterAdapter;
    private String movieJsonStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // update array adapter
        mPosterAdapter = new MoviePosterAdapter(this, new ArrayList<String>());

        // attach adapter to GridView
        GridView gridView = (GridView) findViewById(R.id.grid_view_posters);
        gridView.setAdapter(mPosterAdapter);

        // method invoked when an item in the list has been clicked
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Enter detail upon clicking on the item in the list
             *
             * @param parent the AdapterView where the click happened
             * @param view the view within the AdapterView that was clicked
             *             (this will be a view provided by the adapter)
             * @param position the position of the view in the adapter
             * @param id the row id of the item that was clicked
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String movieData = mPosterAdapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class).putExtra(Intent.EXTRA_TEXT, movieJsonStr).putExtra("position", position);
                startActivity(intent);
//                Toast.makeText(getActivity(), weatherItem, Toast.LENGTH_SHORT).show();

            }
        });
    }

    /**
     * Fetches movie data up start up.
     */
    @Override
    public void onStart() {
        final String POPULAR = "popular";
        final String TOP_RATED = "top_rated";
        super.onStart();
        (new FetchMovieDataTask()).execute(TOP_RATED);
    }

    /**
     * Gets movie data with a http request.
     * Parses data as a JSON string on background thread and
     * publishes the result on the UI.
     */
    public class FetchMovieDataTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMovieDataTask.class.getSimpleName();

        /**
         * TODO: comment
         * HTTP request on background thread.
         * @param params
         * @return
         */
        @Override
        protected String[] doInBackground(String... params) {

            // check
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                // https://www.themoviedb.org/
                final String BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String API_PARAM = "api_key";
                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter(API_PARAM,BuildConfig.MOVIE_DB_API_KEY).build();
                URL url = new URL(builtUri.toString());
                Log.d(LOG_TAG, "url: " + url);

                // create the request to TMDb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // read the input stream into a string
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // useful for debugging
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // stream was empty
                    return null;
                }
                movieJsonStr = buffer.toString();
//                Log.d(LOG_TAG, "movieJsonStr: " + movieJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // no movie data found
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                // log posterUrls
//                String[] posterUrls = getMoviePostersFromJsonString(movieJsonStr);
//                for (String posterUrl : posterUrls) {
//                    Log.d(LOG_TAG, "posterUrl: " + posterUrl);
//                }
                return (new MovieAttributes(movieJsonStr)).getPosterUrls();
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }


        /**
         * Updates the UI after using AsyncTask.
         * @param result returned from AsyncTask
         */
        @Override
        protected void onPostExecute(String[] result) {
            String moviePosterUrl = "http://image.tmdb.org/t/p/w185/";
            if (result != null) {
                mPosterAdapter.clear();
                for (String posterUrl : result) {
                    mPosterAdapter.add(moviePosterUrl + posterUrl);
                }
            }
        }
    }
}
