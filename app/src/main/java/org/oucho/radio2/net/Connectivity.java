package org.oucho.radio2.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import org.oucho.radio2.radio.RadioService;
import org.oucho.radio2.utils.Counter;
import org.oucho.radio2.utils.State;

public class Connectivity extends BroadcastReceiver {

   private static ConnectivityManager connectivity = null;

   private Context context;
   private RadioService radioService;
   private static final int TYPE_NONE = -1;

   private static int previous_type = TYPE_NONE;

   private Handler handler;

   public Connectivity(Context a_context, RadioService a_player) {

      context = a_context;
      radioService = a_player;

      initConnectivity(context);
      context.registerReceiver(this, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
   }


   static private void initConnectivity(Context context) {

      if ( connectivity == null )
         connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      if ( connectivity != null )
         setPreviousType(getType());
   }

   public void destroy() {

      context.unregisterReceiver(this);
   }

   static private int getType() {

      return getType(null);
   }

   static private int getType(Intent intent) {

      if (connectivity == null)
         return TYPE_NONE;

      if ( intent != null && intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false) )
         return TYPE_NONE;

      NetworkInfo network = connectivity.getActiveNetworkInfo();
      if ( network != null && network.isConnected() ) {

         int type = network.getType();
         switch (type) {
            // These cases all fall through.
            case ConnectivityManager.TYPE_WIFI:
            case ConnectivityManager.TYPE_MOBILE:
            case ConnectivityManager.TYPE_WIMAX:
               if ( network.getState() == NetworkInfo.State.CONNECTED )
                  return type;

            default:
               break;
         }
      }

      return TYPE_NONE;
   }

   public static boolean onWifi() {

      return getPreviousType() == ConnectivityManager.TYPE_WIFI;
   }

   static public boolean isConnected(Context context) {

      initConnectivity(context);

      return (getType() != TYPE_NONE);
   }



   @Override
   public void onReceive(Context context, Intent intent) {

      int type = getType(intent);
      int then = 0;

      boolean want_network_playing = State.isWantPlaying() && radioService.isNetworkUrl();

      if ( type == TYPE_NONE && getPreviousType() != TYPE_NONE && want_network_playing )
         dropped_connection();


      if ( getPreviousType() == TYPE_NONE && type != getPreviousType() && Counter.still(then) )
         restart();

      if ( getPreviousType() != TYPE_NONE && type != TYPE_NONE && type != getPreviousType() && want_network_playing )
         restart();

      setPreviousType(type);
   }


   public void dropped_connection() {

      Log.d("Connectivity", "dropped_connection(), perte de connexion");

      State.setState(context, State.STATE_DISCONNECTED, true);


      handler = new Handler();
      handler.postDelayed(() -> {

         if (State.isOnline(context)) {

            Log.d("Connectivity", "dropped_connection(), isOnline");

            Intent player = new Intent(context, RadioService.class);
            player.putExtra("action", "stop");
            context.startService(player);

            Intent player2 = new Intent(context, RadioService.class);
            player2.putExtra("action", "play");
            context.startService(player2);

            reconnect();

         } else {

            Intent player3 = new Intent(context, RadioService.class);
            player3.putExtra("action", "stop");
            context.startService(player3);
         }

      }, 2000);

   }

   private void reconnect() {

      handler = new Handler();
      handler.postDelayed(() -> {

         Log.d("Connectivity", "reconnect(int delay), reconnexion x seconde");

         if (!State.isPlaying()) {

            Intent player2 = new Intent(context, RadioService.class);
            player2.putExtra("action", "play");
            context.startService(player2);
         }
      }, 5000);

   }


   private void restart() {

      Log.d("Connectivity", "restart");

      Intent playerS = new Intent(context, RadioService.class);
      playerS.putExtra("action", "stop");
      context.startService(playerS);

      Intent playerP = new Intent(context, RadioService.class);
      playerP.putExtra("action", "play");
      context.startService(playerP);

   }

   private static int getPreviousType() {
      return previous_type;
   }

   private static void setPreviousType(int value) {
      previous_type = value;
   }
}

