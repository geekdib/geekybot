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
import java.util.HashMap;

public class AIMLMap extends HashMap<String, String> {
   public String mapName;
   String host;
   String botid;
   boolean isExternal = false;

   public AIMLMap(String name) {
      this.mapName = name;
   }

   public String get(String key) {
      int number;
      if (this.mapName.equals(MagicStrings.map_successor)) {
         try {
            number = Integer.parseInt(key);
            return String.valueOf(number + 1);
         } catch (Exception var5) {
            return MagicStrings.unknown_map_value;
         }
      } else if (this.mapName.equals(MagicStrings.map_predecessor)) {
         try {
            number = Integer.parseInt(key);
            return String.valueOf(number - 1);
         } catch (Exception var6) {
            return MagicStrings.unknown_map_value;
         }
      } else {
         String value;
         if (this.isExternal && MagicBooleans.enable_external_sets) {
            String query = this.mapName.toUpperCase() + " " + key;
//            String response = Sraix.sraix((Chat)null, query, MagicStrings.unknown_map_value, (String)null, this.host, this.botid, (String)null, "0");
            String response = "Sorry, I didn't get that.";
            System.out.println("External " + this.mapName + "(" + key + ")=" + response);
            value = response;
         } else {
            value = (String)super.get(key);
         }

         if (value == null) {
            value = MagicStrings.unknown_map_value;
         }

         System.out.println("AIMLMap get " + key + "=" + value);
         return value;
      }
   }

   public String put(String key, String value) {
      return (String)super.put(key, value);
   }

   public int readAIMLMapFromInputStream(InputStream in, Bot bot) {
      int cnt = 0;
      BufferedReader br = new BufferedReader(new InputStreamReader(in));

      String strLine;
      try {
         while((strLine = br.readLine()) != null && strLine.length() > 0) {
            String[] splitLine = strLine.split(":");
            if (splitLine.length >= 2) {
               ++cnt;
               if (strLine.startsWith(MagicStrings.remote_map_key)) {
                  if (splitLine.length >= 3) {
                     this.host = splitLine[1];
                     this.botid = splitLine[2];
                     this.isExternal = true;
                     System.out.println("Created external map at " + this.host + " " + this.botid);
                  }
               } else {
                  String key = splitLine[0].toUpperCase();
                  String value = splitLine[1];
                  this.put(key, value);
               }
            }
         }
      } catch (Exception var9) {
         var9.printStackTrace();
      }

      return cnt;
   }

   public void readAIMLMap(Bot bot) {
      System.out.println("Reading AIML Map " + MagicStrings.maps_path + "/" + this.mapName + ".txt");

      try {
         File file = new File(MagicStrings.maps_path + "/" + this.mapName + ".txt");
         if (file.exists()) {
            FileInputStream fstream = new FileInputStream(MagicStrings.maps_path + "/" + this.mapName + ".txt");
            this.readAIMLMapFromInputStream(fstream, bot);
            fstream.close();
         } else {
            System.out.println(MagicStrings.maps_path + "/" + this.mapName + ".txt not found");
         }
      } catch (Exception var4) {
         System.err.println("Error: " + var4.getMessage());
      }

   }

}
