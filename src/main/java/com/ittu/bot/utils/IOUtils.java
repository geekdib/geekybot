package com.ittu.bot.utils;
/**
 * @author @dibyapp, Name : Dibyaprakash, Email : dibyapp@crms.app
 * @Project : Ittu AI
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IOUtils {
   public static String readInputTextLine() {
      BufferedReader lineOfText = new BufferedReader(new InputStreamReader(System.in));
      String textLine = null;

      try {
         textLine = lineOfText.readLine();
      } catch (IOException var3) {
         var3.printStackTrace();
      }

      return textLine;
   }

   public static String system(String evaluatedContents, String failedString) {
      Runtime rt = Runtime.getRuntime();
      System.out.println("System " + evaluatedContents);

      try {
         Process p = rt.exec(evaluatedContents);
         InputStream istrm = p.getInputStream();
         InputStreamReader istrmrdr = new InputStreamReader(istrm);
         BufferedReader buffrdr = new BufferedReader(istrmrdr);
         String result = "";

         for(String data = ""; (data = buffrdr.readLine()) != null; result = result + data + "\n") {
         }

         System.out.println("Result = " + result);
         return result;
      } catch (Exception var9) {
         var9.printStackTrace();
         return failedString;
      }
   }
}
