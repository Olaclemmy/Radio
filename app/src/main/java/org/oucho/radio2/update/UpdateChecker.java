package org.oucho.radio2.update;

import android.content.Context;
import android.util.Log;

import org.oucho.radio2.radio.RadioKeys;

public class UpdateChecker implements RadioKeys {


    public static void checkForDialog(Context context) {
        if (context != null) {
            new CheckUpdateTask(context, TYPE_DIALOG, true).execute();
        } else {
            Log.e(UPDATE_TAG, "The arg context is null");
        }
    }

    public static void checkForSnack(Context context) {
        if (context != null) {
            new CheckUpdateTask(context, TYPE_SNACK, false).execute();
        } else {
            Log.e(UPDATE_TAG, "The arg context is null");
        }

    }


}
