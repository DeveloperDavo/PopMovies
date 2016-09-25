package com.example.android.popularmoviesapp.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by David on 25/09/16.
 */

public class PopMoviesSyncService extends Service {
    private static final String LOG_TAG = PopMoviesSyncService.class.getSimpleName();

    private static final Object syncAdapterLock = new Object();
    private static PopMoviesSyncAdapter moviesSyncAdapter = null;

    @Override
    public void onCreate() {
//        Log.d(LOG_TAG, "onCreate");
        synchronized (syncAdapterLock) {
            if (moviesSyncAdapter == null) {
                moviesSyncAdapter = new PopMoviesSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return moviesSyncAdapter.getSyncAdapterBinder();
    }
}
