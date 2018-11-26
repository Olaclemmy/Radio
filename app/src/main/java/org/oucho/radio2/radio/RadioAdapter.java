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

package org.oucho.radio2.radio;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.oucho.radio2.R;
import org.oucho.radio2.db.Radio;
import org.oucho.radio2.tunein.adapters.BaseAdapter;
import org.oucho.radio2.utils.LogoCache;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static org.oucho.radio2.radio.RadioKeys.PREF_FILE;

public class RadioAdapter extends BaseAdapter<RadioAdapter.RadioViewHolder> {

    private ArrayList<Radio> radioList = new ArrayList<>();

    public void setData(ArrayList<Radio> data) {

        radioList = data;

        notifyDataSetChanged();
    }


    @Override
    public RadioViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_radio_item, parent, false);
        return new RadioViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(RadioViewHolder viewHolder, int position) {

        Radio radio = radioList.get(position);

        viewHolder.text.setText(radio.getTitle());

        Bitmap bitmap = LogoCache.get().logo(radio);

        if (bitmap != null) {

            viewHolder.imageDefault.setVisibility(View.INVISIBLE);
            viewHolder.logoRadio.setImageBitmap(bitmap);
            viewHolder.logoRadio.setVisibility(View.VISIBLE);
        } else {
            viewHolder.imageDefault.setVisibility(View.VISIBLE);
            viewHolder.logoRadio.setVisibility(View.INVISIBLE);
        }

        SharedPreferences preferences = viewHolder.fond.getContext().getSharedPreferences(PREF_FILE, MODE_PRIVATE);
        String url = preferences.getString("url", "url");
        if (radio.getUrl().equals(url)  ) {
            viewHolder.fond.setBackgroundColor(ContextCompat.getColor(viewHolder.fond.getContext(), R.color.colorAccent));
            viewHolder.text.setTextColor(ContextCompat.getColor(viewHolder.text.getContext(), R.color.white));
            viewHolder.menu.setImageDrawable(viewHolder.menu.getContext().getApplicationContext().getDrawable(R.drawable.ic_more_vert_white_24dp));
        } else {
            viewHolder.fond.setBackgroundColor(ContextCompat.getColor(viewHolder.fond.getContext(), R.color.white));
            viewHolder.text.setTextColor(ContextCompat.getColor(viewHolder.text.getContext(), R.color.grey_800));
            viewHolder.menu.setImageDrawable(viewHolder.menu.getContext().getDrawable(R.drawable.ic_more_vert_grey_400_24dp));
        }
    }

    @Override
    public int getItemCount() {
        return radioList.size();
    }

    public Radio getItem(int position) {
        return radioList.get(position);
    }

    public class RadioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView text;
        private final ImageButton menu;

        private final ImageView imageDefault;
        private final ImageView logoRadio;

        private final RelativeLayout fond;


        RadioViewHolder(View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.textViewRadio);
            menu = itemView.findViewById(R.id.buttonMenu);
            imageDefault = itemView.findViewById(R.id.imageRadioDefault);
            logoRadio = itemView.findViewById(R.id.logoViewRadio);
            fond  = itemView.findViewById(R.id.fond);

            itemView.setOnClickListener(this);
            menu.setOnClickListener(this);
            menu.setFocusable(false);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            triggerOnItemClickListener(position, v);
        }

    }
}
