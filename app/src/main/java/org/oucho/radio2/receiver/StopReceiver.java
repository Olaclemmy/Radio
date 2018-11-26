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

package org.oucho.radio2.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.oucho.radio2.radio.RadioService;
import org.oucho.radio2.radio.RadioKeys;
import org.oucho.radio2.utils.State;

public class StopReceiver extends BroadcastReceiver implements RadioKeys {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if ( "org.oucho.radio2.STOP".equals(action) && ( State.isPlaying() || State.isPaused() ) ) {

            String halt = intent.getStringExtra("halt");
            Intent player = new Intent(context, RadioService.class);
            player.putExtra("action", halt);
            context.startService(player);
        }
    }
}
