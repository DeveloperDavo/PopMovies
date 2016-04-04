package com.example.android.popularmoviesapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
//    ArrayAdapter<Integer> mPosterAdapter;
    MoviePosterAdapter mPosterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ImageView imageView = (ImageView) findViewById(R.id.photo_image_view);
        // load Picasso image
//        Picasso.with(this).load("http://i.imgur.com/DvpvklR.png").into(imageView);

        // references to images
        Integer[] images = {
                R.drawable.picasso, R.drawable.interstellar,
                R.drawable.interstellar, R.drawable.picasso
        };

        mPosterAdapter = new MoviePosterAdapter(this, Arrays.asList(images));

        // attach adapter to GridView
        GridView gridView = (GridView) findViewById(R.id.grid_view_posters);
        gridView.setAdapter(mPosterAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        (new FetchMovieData()).execute();
    }

    /**
     * Gets movie data with a http request.
     * Parses data as a JSON string on background thread and
     * publishes the result on the UI.
     */
    public class FetchMovieData extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = FetchMovieData.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // will contain the raw JSON response as a string.
            String popularMovies = null;

            try {
                // https://www.themoviedb.org/
                String baseUrl = "http://api.themoviedb.org/3/movie/popular";
                String apiKey = "?api_key=" + BuildConfig.MOVIE_DB_API_KEY;
                URL url = new URL(baseUrl.concat(apiKey));

                // create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // read the input stream into a String
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
                popularMovies = buffer.toString();
                Log.d(LOG_TAG, "popularMovies: " + popularMovies);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data
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
            return null;
        }
    }
}
