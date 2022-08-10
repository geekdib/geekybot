package com.ittu.bot;
/**
 * @author @dibyapp, Name : Dibyaprakash, Email : dibyapp@crms.app
 * @Project : Ittu AI
 */
import java.util.ArrayList;
import java.util.HashMap;

public class Nodemapper {
   public Category category = null;
   public int height;
   public StarBindings starBindings;
   public HashMap<String, Nodemapper> map;
   public String key;
   public Nodemapper value;
   public boolean shortCut;
   public ArrayList<String> sets;

   public Nodemapper() {
      this.height = MagicNumbers.max_graph_height;
      this.starBindings = null;
      this.map = null;
      this.key = null;
      this.value = null;
      this.shortCut = false;
   }
}
