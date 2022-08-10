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

public class Properties extends HashMap<String, String> {
   public String get(String key) {
      String result = (String)super.get(key);
      return result == null ? MagicStrings.unknown_property_value : result;
   }

   public void getPropertiesFromInputStream(InputStream in) {
      BufferedReader br = new BufferedReader(new InputStreamReader(in));

      String strLine;
      try {
         while((strLine = br.readLine()) != null) {
            if (strLine.contains(":")) {
               String property = strLine.substring(0, strLine.indexOf(":"));
               String value = strLine.substring(strLine.indexOf(":") + 1);
               this.put(property, value);
            }
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }

   }

   public void getProperties(String filename) {
      System.out.println("Get Properties: " + filename);

      try {
         File file = new File(filename);
         if (file.exists()) {
            System.out.println("Exists: " + filename);
            FileInputStream fstream = new FileInputStream(filename);
            this.getPropertiesFromInputStream(fstream);
            fstream.close();
         }
      } catch (Exception var4) {
         System.err.println("Error: " + var4.getMessage());
      }

   }
}
