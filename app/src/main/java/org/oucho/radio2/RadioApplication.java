package org.oucho.radio2;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RadioApplication extends Application {

    private static RadioApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
       if (LeakCanary.isInAnalyzerProcess(this)) {

            return;
       }
       LeakCanary.install(this);

        setInstance(this);

        int pool = (Runtime.getRuntime().availableProcessors() * 2) +1 ;

        Log.i("Radio", "Number of core : " + Runtime.getRuntime().availableProcessors() );

        Picasso picasso = new Picasso.Builder(this)
                .memoryCache(new LruCache(16777216)) // en octet
                .defaultBitmapConfig(Bitmap.Config.RGB_565) // 2 bit par pixel, peu gourmand
                .executor(new ThreadPoolExecutor(pool, pool, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>()))
                .build();

        Picasso.setSingletonInstance(picasso);
    }

    public static synchronized RadioApplication getInstance() {
        return sInstance;
    }

    private static void setInstance(RadioApplication value) {
        sInstance = value;
    }
}
