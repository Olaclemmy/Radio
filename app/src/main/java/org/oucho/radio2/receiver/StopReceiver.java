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
