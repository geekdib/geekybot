package com.ittu.bot;
/**
 * @author @dibyapp, Name : Dibyaprakash, Email : dibyapp@crms.app
 * @Project : Ittu AI
 */
import java.lang.Character.UnicodeBlock;
import java.util.HashSet;

final class JapaneseTokenizer$1 extends HashSet<UnicodeBlock> {
   JapaneseTokenizer$1() {
      this.add(UnicodeBlock.HIRAGANA);
      this.add(UnicodeBlock.KATAKANA);
      this.add(UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS);
   }
}
