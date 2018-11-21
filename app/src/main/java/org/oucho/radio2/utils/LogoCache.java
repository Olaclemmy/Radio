package org.oucho.radio2.utils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import org.oucho.radio2.db.Radio;

public class LogoCache {

    private static LogoCache instance;

    private final LruCache<String, Bitmap> mMemoryCache;


    private LogoCache() {

        final int cacheSize = 8388608;  // in byte

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static LogoCache get() {

        if (instance == null)
            instance = new LogoCache();

        return instance;
    }

    public Bitmap logo(Radio radio) {

        byte[] logo = radio.getLogo();

        String key = radio.getUrl();

        if (logo != null) {

            if (mMemoryCache.get(key) == null) {

                Bitmap bitmap = ImageFactory.getImage(logo);
                add(radio.getUrl(), bitmap);
            }

            return mMemoryCache.get(key);

        } else {

            return null;
        }
    }


    private void add(String key, Bitmap bitmap) {
            mMemoryCache.put(key, bitmap);
    }

    public void invalidate(String key) {
        mMemoryCache.remove(key);
    }

    public synchronized void clear() {
        mMemoryCache.evictAll();
    }

}
