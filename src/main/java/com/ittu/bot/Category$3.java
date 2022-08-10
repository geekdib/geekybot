package com.ittu.bot;
/**
 * @author @dibyapp, Name : Dibyaprakash, Email : dibyapp@crms.app
 * @Project : Ittu AI
 */
import java.util.Comparator;

final class Category$3 implements Comparator<Category> {
   public int compare(Category c1, Category c2) {
      return c1.getCategoryNumber() - c2.getCategoryNumber();
   }

   // $FF: synthetic method
   // $FF: bridge method
//   public int compare(Object x0, Object x1) {
//      return this.compare((Category)x0, (Category)x1);
//   }
}
