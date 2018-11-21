package org.oucho.radio2.utils;

import android.os.AsyncTask;

import org.oucho.radio2.utils.Counter;

public abstract class Later extends AsyncTask<Integer, Void, Void> {

   private static final int default_seconds = 120;
   private int seconds = default_seconds;

   private final int then;

   public abstract void later();

   // secs <  0: execute immédiatement
   // secs == 0: délais pour default_seconds
   // otherwise: délais pour sec

   protected Later(int secs) {
      super();

      int secondes  = secs;

      if ( secondes == 0 )
         secondes = default_seconds;
      seconds = secondes;
      then = Counter.now();
   }

   protected Later() {
      this(default_seconds);
   }

   protected Void doInBackground(Integer... args) {

      try {
         if ( 0 < seconds )
            Thread.sleep(seconds * 1000);
      }
      catch ( Exception ignored) { }
      return null;
   }

   protected void onPostExecute(Void ignored) {

      if ( ! isCancelled() && Counter.still(then) )
         later();
   }

   public AsyncTask<Integer,Void,Void> start() {
      return executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
   }
}
