package org.oucho.radio2.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.oucho.radio2.radio.RadioService;
import org.oucho.radio2.radio.RadioKeys;

public class State implements RadioKeys {

   public static final String STATE_STOP         = "Stop";
   private static final String STATE_ERROR       = "Error";
   public static final String STATE_PAUSE        = "Pause";
   public static final String STATE_PLAY         = "Play";
   public static final String STATE_BUFFER       = "Loading...";
   private static final String STATE_COMPLETED   = "Completed";
   public static final String STATE_DUCK         = "\\_o< coin";
   public static final String STATE_DISCONNECTED = "Disconnected";

   private static String current_state = STATE_STOP;
   private static boolean current_isNetworkUrl = false;

   public static void setState(Context context, String s, boolean isNetworkUrl) {

      if ( s == null )
         return;

      current_state = s;

      current_isNetworkUrl = isNetworkUrl;

      Intent intent = new Intent(INTENT_STATE);
      intent.putExtra("state", current_state);
      intent.putExtra("url", RadioService.getUrl());
      intent.putExtra("name", RadioService.getName());

      context.sendBroadcast(intent);
   }

   public static void getState(Context context) {
        setState(context, current_state, current_isNetworkUrl);
   }

   public static boolean is(String s) {
       return current_state.equals(s);
   }

   public static boolean isPlaying() {
       return is(STATE_PLAY) || is(STATE_BUFFER) || is(STATE_DUCK);
   }

   public static boolean isStopped() {
       return State.is(STATE_STOP) || State.is(STATE_ERROR) || State.is(STATE_COMPLETED);
   }

   public static boolean isPaused() {
        return State.is(STATE_PAUSE);
    }

   public static boolean isWantPlaying() {
       return isPlaying() || is(STATE_ERROR);
   }

   public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
   }

}

