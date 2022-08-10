package com.ittu.bot;
/**
 * @author @dibyapp, Name : Dibyaprakash, Email : dibyapp@crms.app
 * @Project : Ittu AI
 */
import java.util.ArrayList;

public class Stars extends ArrayList<String> {
   public String star(int i) {
      return i < this.size() ? (String)this.get(i) : null;
   }
}
