package com.ittu.bot;
/**
 * @author @dibyapp, Name : Dibyaprakash, Email : dibyapp@crms.app
 * @Project : Ittu AI
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class NodemapperOperator {
   public static int size(Nodemapper node) {
      HashSet set = new HashSet();
      if (node.shortCut) {
         set.add("<THAT>");
      }

      if (node.key != null) {
         set.add(node.key);
      }

      if (node.map != null) {
         set.addAll(node.map.keySet());
      }

      return set.size();
   }

   public static void put(Nodemapper node, String key, Nodemapper value) {
      if (node.map != null) {
         node.map.put(key, value);
      } else {
         node.key = key;
         node.value = value;
      }

   }

   public static Nodemapper get(Nodemapper node, String key) {
      if (node.map != null) {
         return (Nodemapper)node.map.get(key);
      } else {
         return key.equals(node.key) ? node.value : null;
      }
   }

   public static boolean containsKey(Nodemapper node, String key) {
      if (node.map != null) {
         return node.map.containsKey(key);
      } else {
         return key.equals(node.key);
      }
   }

   public static void printKeys(Nodemapper node) {
      Set set = keySet(node);
      Iterator iter = set.iterator();

      while(iter.hasNext()) {
         System.out.println("" + iter.next());
      }

   }

   public static Set<String> keySet(Nodemapper node) {
      if (node.map != null) {
         return node.map.keySet();
      } else {
         Set set = new HashSet();
         if (node.key != null) {
            set.add(node.key);
         }

         return set;
      }
   }

   public static boolean isLeaf(Nodemapper node) {
      return node.category != null;
   }

   public static void upgrade(Nodemapper node) {
      node.map = new HashMap();
      node.map.put(node.key, node.value);
      node.key = null;
      node.value = null;
   }
}
