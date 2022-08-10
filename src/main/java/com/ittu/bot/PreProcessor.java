package com.ittu.bot;
/**
 * @author @dibyapp, Name : Dibyaprakash, Email : dibyapp@crms.app
 * @Project : Ittu AI
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreProcessor {
   public int normalCount = 0;
   public int denormalCount = 0;
   public int personCount = 0;
   public int person2Count = 0;
   public int genderCount = 0;
   public String[] normalSubs;
   public Pattern[] normalPatterns;
   public String[] denormalSubs;
   public Pattern[] denormalPatterns;
   public String[] personSubs;
   public Pattern[] personPatterns;
   public String[] person2Subs;
   public Pattern[] person2Patterns;
   public String[] genderSubs;
   public Pattern[] genderPatterns;

   public PreProcessor(Bot bot) {
      this.normalSubs = new String[MagicNumbers.max_substitutions];
      this.normalPatterns = new Pattern[MagicNumbers.max_substitutions];
      this.denormalSubs = new String[MagicNumbers.max_substitutions];
      this.denormalPatterns = new Pattern[MagicNumbers.max_substitutions];
      this.personSubs = new String[MagicNumbers.max_substitutions];
      this.personPatterns = new Pattern[MagicNumbers.max_substitutions];
      this.person2Subs = new String[MagicNumbers.max_substitutions];
      this.person2Patterns = new Pattern[MagicNumbers.max_substitutions];
      this.genderSubs = new String[MagicNumbers.max_substitutions];
      this.genderPatterns = new Pattern[MagicNumbers.max_substitutions];
      this.normalCount = this.readSubstitutions(MagicStrings.config_path + "/normal.txt", this.normalPatterns, this.normalSubs);
      this.denormalCount = this.readSubstitutions(MagicStrings.config_path + "/denormal.txt", this.denormalPatterns, this.denormalSubs);
      this.personCount = this.readSubstitutions(MagicStrings.config_path + "/person.txt", this.personPatterns, this.personSubs);
      this.person2Count = this.readSubstitutions(MagicStrings.config_path + "/person2.txt", this.person2Patterns, this.person2Subs);
      this.genderCount = this.readSubstitutions(MagicStrings.config_path + "/gender.txt", this.genderPatterns, this.genderSubs);
      System.out.println("Preprocessor: " + this.normalCount + " norms " + this.personCount + " persons " + this.person2Count + " person2 ");
   }

   public String normalize(String request) {
      return this.substitute(request, this.normalPatterns, this.normalSubs, this.normalCount);
   }

   public String denormalize(String request) {
      return this.substitute(request, this.denormalPatterns, this.denormalSubs, this.denormalCount);
   }

   public String person(String input) {
      return this.substitute(input, this.personPatterns, this.personSubs, this.personCount);
   }

   public String person2(String input) {
      return this.substitute(input, this.person2Patterns, this.person2Subs, this.person2Count);
   }

   public String gender(String input) {
      return this.substitute(input, this.genderPatterns, this.genderSubs, this.genderCount);
   }

   String substitute(String request, Pattern[] patterns, String[] subs, int count) {
      String result = " " + request + " ";

      try {
         for(int i = 0; i < count; ++i) {
            String replacement = subs[i];
            Pattern p = patterns[i];
            Matcher m = p.matcher(result);
            if (m.find()) {
               result = m.replaceAll(replacement);
            }
         }

         while(result.contains("  ")) {
            result = result.replace("  ", " ");
         }

         result = result.trim();
      } catch (Exception var10) {
         var10.printStackTrace();
      }

      return result.trim();
   }

   public int readSubstitutionsFromInputStream(InputStream in, Pattern[] patterns, String[] subs) {
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      int subCount = 0;

      String strLine;
      try {
         while((strLine = br.readLine()) != null) {
            strLine = strLine.trim();
            Pattern pattern = Pattern.compile("\"(.*?)\",\"(.*?)\"", 32);
            Matcher matcher = pattern.matcher(strLine);
            if (matcher.find() && subCount < MagicNumbers.max_substitutions) {
               subs[subCount] = matcher.group(2);
               String quotedPattern = Pattern.quote(matcher.group(1));
               patterns[subCount] = Pattern.compile(quotedPattern, 2);
               ++subCount;
            }
         }
      } catch (Exception var10) {
         var10.printStackTrace();
      }

      return subCount;
   }

   int readSubstitutions(String filename, Pattern[] patterns, String[] subs) {
      int subCount = 0;

      try {
         File file = new File(filename);
         if (file.exists()) {
            FileInputStream fstream = new FileInputStream(filename);
            subCount = this.readSubstitutionsFromInputStream(fstream, patterns, subs);
            fstream.close();
         }
      } catch (Exception var7) {
         System.err.println("Error: " + var7.getMessage());
      }

      return subCount;
   }

   public String[] sentenceSplit(String line) {
      line = line.replace("。", ".");
      line = line.replace("？", "?");
      line = line.replace("！", "!");
      String[] result = line.split("[\\.!\\?]");

      for(int i = 0; i < result.length; ++i) {
         result[i] = result[i].trim();
      }

      return result;
   }

   public void normalizeFile(String infile, String outfile) {
      try {
         BufferedWriter bw = null;
         FileInputStream fstream = new FileInputStream(infile);
         BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
         bw = new BufferedWriter(new FileWriter(outfile));

         String strLine;
         while((strLine = br.readLine()) != null) {
            strLine = this.normalize(strLine);
            bw.write(strLine);
            bw.newLine();
         }

         bw.close();
         br.close();
      } catch (Exception var7) {
         var7.printStackTrace();
      }

   }
}
