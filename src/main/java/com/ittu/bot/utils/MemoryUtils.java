package com.ittu.bot.utils;
/**
 * @author @dibyapp, Name : Dibyaprakash, Email : dibyapp@crms.app
 * @Project : Ittu AI
 */
public class MemoryUtils {
   public static long totalMemory() {
      return Runtime.getRuntime().totalMemory();
   }

   public static long maxMemory() {
      return Runtime.getRuntime().maxMemory();
   }

   public static long freeMemory() {
      return Runtime.getRuntime().freeMemory();
   }
}
