package org.oucho.radio2.radio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.oucho.radio2.MainActivity;
import org.oucho.radio2.db.Radio;


public class RadioActivity extends Activity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String action = intent != null ? intent.getAction(): null;

        if (Intent.ACTION_VIEW.equals(action) && intent.getData() != null) {

            intent.setDataAndType(intent.getData(), intent.getType());

            Radio newRadio = new Radio(intent.getData().toString(), intent.getData().toString() , null);
            Radio.addNewRadio(getApplicationContext(), newRadio);

        }

        assert intent != null;
        startActivity(intent.setClass(this, MainActivity.class));

        finish();
    }
}
