package org.oucho.radio2.utils;

public class Counter {

   private static volatile int counter = 1;

   public static int now() {
      return counter;
   }

   public static void timePasses() {
       counter += 1;
   }

   public static boolean still(int then) {
       return then == now();
   }
}

