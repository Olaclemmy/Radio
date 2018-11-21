package org.oucho.radio2.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import org.oucho.radio2.R;
import org.oucho.radio2.update.DownloadService;

import static org.oucho.radio2.radio.RadioKeys.APK_DOWNLOAD_URL;

public class UpdateDialog {


    public static void show(final Context context, String content, final String downloadUrl) {
        if (isContextValid(context)) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.android_auto_update_dialog_title)
                    .setMessage(content)
                    .setPositiveButton(R.string.android_auto_update_dialog_btn_download, (dialog, id) -> goToDownload(context, downloadUrl))
                    .setNegativeButton(R.string.android_auto_update_dialog_btn_cancel, (dialog, id) -> {
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    private static boolean isContextValid(Context context) {
        return context instanceof Activity && !((Activity) context).isFinishing();
    }


    private static void goToDownload(Context context, String downloadUrl) {
        Intent intent = new Intent(context.getApplicationContext(), DownloadService.class);
        intent.putExtra(APK_DOWNLOAD_URL, downloadUrl);
        context.startService(intent);
    }
}
