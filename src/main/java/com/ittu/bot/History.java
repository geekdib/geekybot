package com.ittu.bot;
/**
 * @author @dibyapp, Name : Dibyaprakash, Email : dibyapp@crms.app
 * @Project : Ittu AI
 */
public class History<T> {
   private T[] history;
   private String name;

   public History() {
      this("unknown");
   }

   public History(String name) {
      this.name = name;
      this.history = (T[]) new Object[MagicNumbers.max_history];
   }

   public void add(T item) {
      for(int i = MagicNumbers.max_history - 1; i > 0; --i) {
         this.history[i] = this.history[i - 1];
      }

      this.history[0] = item;
   }

   public T get(int index) {
      if (index < MagicNumbers.max_history) {
         return this.history[index] == null ? null : this.history[index];
      } else {
         return null;
      }
   }

   public String getString(int index) {
      if (index < MagicNumbers.max_history) {
         return this.history[index] == null ? MagicStrings.unknown_history_item : (String)this.history[index];
      } else {
         return null;
      }
   }

   public void printHistory() {
      for(int i = 0; this.get(i) != null; ++i) {
         System.out.println(this.name + "History " + (i + 1) + " = " + this.get(i));
         System.out.println(String.valueOf(this.get(i).getClass()).contains("History"));
         if (String.valueOf(this.get(i).getClass()).contains("History")) {
            ((History)this.get(i)).printHistory();
         }
      }

   }
}
