package com.ittu.bot.utils;
/**
 * @author @dibyapp, Name : Dibyaprakash, Email : dibyapp@crms.app
 * @Project : Ittu AI
 */
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarUtils {
   public static int timeZoneOffset() {
      Calendar cal = Calendar.getInstance();
      int offset = (cal.get(15) + cal.get(16)) / '\uea60';
      return offset;
   }

   public static String year() {
      Calendar cal = Calendar.getInstance();
      return String.valueOf(cal.get(1));
   }

   public static String date() {
      Calendar cal = Calendar.getInstance();
      int year = cal.get(1);
      SimpleDateFormat dateFormat = new SimpleDateFormat("MMMMMMMMM dd, yyyy");
      dateFormat.setCalendar(cal);
      return dateFormat.format(cal.getTime());
   }

   public static String date(String jformat, String locale, String timezone) {
      if (jformat == null) {
         jformat = "EEE MMM dd HH:mm:ss zzz yyyy";
      }

      if (locale == null) {
         locale = Locale.US.getISO3Country();
      }

      if (timezone == null) {
         timezone = TimeZone.getDefault().getDisplayName();
      }

      String dateAsString = (new Date()).toString();

      try {
         SimpleDateFormat simpleDateFormat = new SimpleDateFormat(jformat);
         dateAsString = simpleDateFormat.format(new Date());
      } catch (Exception var5) {
         System.out.println("CalendarUtils.date Bad date: Format = " + jformat + " Locale = " + locale + " Timezone = " + timezone);
      }

      System.out.println("CalendarUtils.date: " + dateAsString);
      return dateAsString;
   }
}
