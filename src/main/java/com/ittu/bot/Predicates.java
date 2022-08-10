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

public class Predicates extends HashMap<String, String> {
   public String put(String key, String value) {
      if (MagicBooleans.trace_mode) {
         System.out.println("Setting predicate " + key + " to " + value);
      }

      return (String)super.put(key, value);
   }

   public String get(String key) {
      String result = (String)super.get(key);
      return result == null ? MagicStrings.unknown_predicate_value : result;
   }

   public void getPredicateDefaultsFromInputStream(InputStream in) {
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

   public void getPredicateDefaults(String filename) {
      try {
         File file = new File(filename);
         if (file.exists()) {
            FileInputStream fstream = new FileInputStream(filename);
            this.getPredicateDefaultsFromInputStream(fstream);
            fstream.close();
         }
      } catch (Exception var4) {
         System.err.println("Error: " + var4.getMessage());
      }

   }

   // $FF: synthetic method
   // $FF: bridge method
//   public Object put(Object x0, Object x1) {
//      return this.put((String)x0, (String)x1);
//   }
//  
   
}
