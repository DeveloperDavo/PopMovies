package com.example.android.popularmoviesapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
//    ArrayAdapter<Integer> mPosterAdapter;
    ImageAdapter mPosterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ImageView imageView = (ImageView) findViewById(R.id.photo_image_view);
        // load Picasso image
//        Picasso.with(this).load("http://i.imgur.com/DvpvklR.png").into(imageView);

        mPosterAdapter = new ImageAdapter(this);

        // attach adapter to GridView
        GridView gridView = (GridView) findViewById(R.id.grid_view_posters);
        gridView.setAdapter(mPosterAdapter);
    }

    public class ImageAdapter extends BaseAdapter {
        private Activity mActivity;

        public ImageAdapter(Activity c) {
            mActivity = c;
        }

        public int getCount() {
            return mThumbIds.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            View gridView = inflater.inflate(R.layout.grid_item_poster, parent, false);
            ImageView imageView = (ImageView) gridView.findViewById(R.id.photo_image_view);

            imageView.setImageResource(mThumbIds[position]);
            return gridView;
        }

        // references to images
        Integer[] mThumbIds = {
                R.drawable.picasso, R.drawable.interstellar,
                R.drawable.interstellar, R.drawable.picasso
        };
    }
}
