package com.ittu.bot;
/**
 * @author @dibyapp, Name : Dibyaprakash, Email : dibyapp@crms.app
 * @Project : Ittu AI
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Character.UnicodeBlock;
import java.util.HashSet;

import com.ittu.bot.utils.CalendarUtils;

public class Utilities {
   public static String fixCSV(String line) {
      while(line.endsWith(";")) {
         line = line.substring(0, line.length() - 1);
      }

      if (line.startsWith("\"")) {
         line = line.substring(1, line.length());
      }

      if (line.endsWith("\"")) {
         line = line.substring(0, line.length() - 1);
      }

      line = line.replaceAll("\"\"", "\"");
      return line;
   }

   public static String tagTrim(String xmlExpression, String tagName) {
      String stag = "<" + tagName + ">";
      String etag = "</" + tagName + ">";
      if (xmlExpression.length() >= (stag + etag).length()) {
         xmlExpression = xmlExpression.substring(stag.length());
         xmlExpression = xmlExpression.substring(0, xmlExpression.length() - etag.length());
      }

      return xmlExpression;
   }

   public static HashSet<String> stringSet(String... strings) {
      HashSet<String> set = new HashSet();
      String[] arr$ = strings;
      int len$ = strings.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         String s = arr$[i$];
         set.add(s);
      }

      return set;
   }

   public static String getFileFromInputStream(InputStream in) {
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String contents = "";

      String strLine;
      try {
         while((strLine = br.readLine()) != null) {
            if (strLine.length() == 0) {
               contents = contents + "\n";
            } else {
               contents = contents + strLine + "\n";
            }
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      return contents.trim();
   }

   public static String getFile(String filename) {
      String contents = "";

      try {
         File file = new File(filename);
         if (file.exists()) {
            FileInputStream fstream = new FileInputStream(filename);
            contents = getFileFromInputStream(fstream);
            fstream.close();
         }
      } catch (Exception var4) {
         System.err.println("Error: " + var4.getMessage());
      }

      return contents;
   }

   public static String getCopyrightFromInputStream(InputStream in) {
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String copyright = "";

      String strLine;
      try {
         while((strLine = br.readLine()) != null) {
            if (strLine.length() == 0) {
               copyright = copyright + "\n";
            } else {
               copyright = copyright + "<!-- " + strLine + " -->\n";
            }
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      return copyright;
   }

   public static String getCopyright(Bot bot, String AIMLFilename) {
      String copyright = "";
      String year = CalendarUtils.year();
      String date = CalendarUtils.date();

      try {
         copyright = getFile(MagicStrings.config_path + "/copyright.txt");
         String[] splitCopyright = copyright.split("\n");
         copyright = "";

         for(int i = 0; i < splitCopyright.length; ++i) {
            copyright = copyright + "<!-- " + splitCopyright[i] + " -->\n";
         }

         copyright = copyright.replace("[url]", bot.properties.get("url"));
         copyright = copyright.replace("[date]", date);
         copyright = copyright.replace("[YYYY]", year);
         copyright = copyright.replace("[version]", bot.properties.get("version"));
         copyright = copyright.replace("[botname]", bot.name.toUpperCase());
         copyright = copyright.replace("[filename]", AIMLFilename);
         copyright = copyright.replace("[botmaster]", bot.properties.get("botmaster"));
         copyright = copyright.replace("[organization]", bot.properties.get("organization"));
      } catch (Exception var7) {
         System.err.println("Error: " + var7.getMessage());
      }

      return copyright;
   }

   public static String getPannousAPIKey() {
      String apiKey = getFile(MagicStrings.config_path + "/pannous-apikey.txt");
      if (apiKey.equals("")) {
         apiKey = MagicStrings.pannous_api_key;
      }

      return apiKey;
   }

   public static String getPannousLogin() {
      String login = getFile(MagicStrings.config_path + "/pannous-login.txt");
      if (login.equals("")) {
         login = MagicStrings.pannous_login;
      }

      return login;
   }

   public static boolean isCharCJK(char c) {
      return UnicodeBlock.of(c) == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || UnicodeBlock.of(c) == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || UnicodeBlock.of(c) == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B || UnicodeBlock.of(c) == UnicodeBlock.CJK_COMPATIBILITY_FORMS || UnicodeBlock.of(c) == UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || UnicodeBlock.of(c) == UnicodeBlock.CJK_RADICALS_SUPPLEMENT || UnicodeBlock.of(c) == UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || UnicodeBlock.of(c) == UnicodeBlock.ENCLOSED_CJK_LETTERS_AND_MONTHS;
   }
}
