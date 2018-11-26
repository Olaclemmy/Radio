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
