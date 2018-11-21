package org.oucho.radio2.widget;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.oucho.radio2.radio.RadioService;

public class WidgetService extends Service {

    public WidgetService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent player = new Intent(getApplicationContext(), RadioService.class);
        getApplicationContext().startService(player);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
