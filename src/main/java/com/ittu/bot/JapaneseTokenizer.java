package com.ittu.bot;
/**
 * @author @dibyapp, Name : Dibyaprakash, Email : dibyapp@crms.app
 * @Project : Ittu AI
 */
import java.lang.Character.UnicodeBlock;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.reduls.sanmoku.Morpheme;
import net.reduls.sanmoku.Tagger;

public class JapaneseTokenizer {
   static final Pattern tagPattern = Pattern.compile("(<.*>.*</.*>)|(<.*/>)");
   static Set<UnicodeBlock> japaneseUnicodeBlocks = new JapaneseTokenizer$1();

   public static String buildFragment(String fragment) {
      String result = "";

      Morpheme e;
      for(Iterator i$ = Tagger.parse(fragment).iterator(); i$.hasNext(); result = result + e.surface + " ") {
         e = (Morpheme)i$.next();
      }

      return result.trim();
   }

   public static String morphSentence(String sentence) {
      if (!MagicBooleans.jp_morphological_analysis) {
         return sentence;
      } else {
         String result = "";
         Matcher matcher = tagPattern.matcher(sentence);

         while(matcher.find()) {
            int i = matcher.start();
            int j = matcher.end();
            String prefix;
            if (i > 0) {
               prefix = sentence.substring(0, i - 1);
            } else {
               prefix = "";
            }

            String tag = sentence.substring(i, j);
            result = result + " " + buildFragment(prefix) + " " + tag;
            if (j < sentence.length()) {
               sentence = sentence.substring(j, sentence.length());
            } else {
               sentence = "";
            }
         }

         for(result = result + " " + buildFragment(sentence); result.contains("$ "); result = result.replace("$ ", "$")) {
         }

         while(result.contains("  ")) {
            result = result.replace("  ", " ");
         }

         return result.trim();
      }
   }
}
