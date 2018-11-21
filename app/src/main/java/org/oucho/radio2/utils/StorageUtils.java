package org.oucho.radio2.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;

public final class StorageUtils {

    /**
     * Obtenir le répertoire de cache de l'application
     */
    public static File getCacheDirectory(Context context) {
        File appCacheDir = context.getCacheDir();
        if (appCacheDir == null) {
            Log.w("StorageUtils", "Can't define system cache directory! The app should be re-installed.");
        }
        return appCacheDir;
    }

}
