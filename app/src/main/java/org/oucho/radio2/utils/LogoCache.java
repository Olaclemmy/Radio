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
