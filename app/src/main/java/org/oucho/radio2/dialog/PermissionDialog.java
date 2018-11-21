package org.oucho.radio2.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.ActivityCompat;

import org.oucho.radio2.R;
import org.oucho.radio2.radio.RadioKeys;

public class PermissionDialog implements RadioKeys {

public void check(Context context, final Activity activity) {
        DialogUtils.showPermissionDialog(activity, context.getString(R.string.permission_write_external_storage),
                (dialog, which) -> ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE));
    }

    private static class DialogUtils {

        private static void showPermissionDialog(Context context, String message, DialogInterface.OnClickListener listener) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.permission)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, listener)
                    .show();
        }
    }

}
