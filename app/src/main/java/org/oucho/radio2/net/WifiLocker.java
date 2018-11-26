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

package org.oucho.radio2.net;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

import org.oucho.radio2.radio.RadioKeys;


public class WifiLocker implements RadioKeys {

   private static WifiLock lock = null;
   private static WifiManager manager = null;

   public static void lock(Context context) {

      if ( manager == null )
         manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

      if ( manager != null && lock == null )
         lock = manager.createWifiLock(WifiManager.WIFI_MODE_FULL, APPLICATION_NAME);

      if ( lock == null )
         return;

      if ( ! Connectivity.onWifi() ) {
          unlock();
          return;
      }

      if ( lock.isHeld() )
         return;

      lock.acquire();

   }

   public static void unlock() {

      if ( lock != null && lock.isHeld() )
         lock.release();
   }
}
