package org.oucho.radio2.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.oucho.radio2.R;
import org.oucho.radio2.db.Radio;
import org.oucho.radio2.radio.RadioKeys;
import org.oucho.radio2.utils.ImageFactory;


public class NewRadioReceiver extends BroadcastReceiver implements RadioKeys {

    @Override
    public void onReceive(Context context, Intent intent) {

        String etat = intent.getAction();

        Log.w("RadioReceiver", etat);

        if ( INTENT_ADD_RADIO.equals(etat) ) {

            String name = intent.getStringExtra("name");

            if (intent.getStringExtra("url") == null) {
                String text = context.getResources().getString(R.string.addRadio_error_url);
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                return;
            }

            String url = intent.getStringExtra("url");
            String image = intent.getStringExtra("image");
            byte[] img = null;

            if (image != null)
                img = ImageFactory.stringToByte(image);

            Radio newRadio = new Radio(url, name, img);
            Radio.addNewRadio(context, newRadio);
        }
    }
}
