package org.oucho.radio2.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import org.oucho.radio2.BuildConfig;
import org.oucho.radio2.R;

import java.util.Objects;

public class AboutDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder about = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        @SuppressLint("InflateParams")
        View dialoglayout = getActivity().getLayoutInflater().inflate(R.layout.aboutdialog, null);

        String versionName = BuildConfig.VERSION_NAME;
        TextView versionView = dialoglayout.findViewById(R.id.version);
        versionView.setText(versionName);

        about.setView(dialoglayout);

        return about.create();
    }

}
