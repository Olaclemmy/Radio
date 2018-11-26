/*
 * Radio - Internet radio for android
 * Copyright (C) 2017  Old-Geek
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
