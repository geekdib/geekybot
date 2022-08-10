package com.ittu.bot;
/**
 * @author @dibyapp, Name : Dibyaprakash, Email : dibyapp@crms.app
 * @Project : Ittu AI
 */
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Contact {
   public static int contactCount = 0;
   public static HashMap<String, Contact> idContactMap = new HashMap();
   public static HashMap<String, String> nameIdMap = new HashMap();
   public String contactId;
   public String displayName;
   public String birthday;
   public HashMap<String, String> phones;
   public HashMap<String, String> emails;

   public static String multipleIds(String contactName) {
      String patternString;
      for(patternString = " (" + contactName.toUpperCase() + ") "; patternString.contains(" "); patternString = patternString.replace(" ", "(.*)")) {
      }

      Pattern pattern = Pattern.compile(patternString);
      Set<String> keys = nameIdMap.keySet();
      String result = "";
      int idCount = 0;
      Iterator i$ = keys.iterator();

      while(i$.hasNext()) {
         String key = (String)i$.next();
         Matcher m = pattern.matcher(key);
         if (m.find()) {
            result = result + (String)nameIdMap.get(key.toUpperCase()) + " ";
            ++idCount;
         }
      }

      if (idCount <= 1) {
         result = "false";
      }

      return result.trim();
   }

   public static String contactId(String contactName) {
      String patternString;
      for(patternString = " " + contactName.toUpperCase() + " "; patternString.contains(" "); patternString = patternString.replace(" ", ".*")) {
      }

      Pattern pattern = Pattern.compile(patternString);
      Set<String> keys = nameIdMap.keySet();
      String result = "unknown";
      Iterator i$ = keys.iterator();

      while(i$.hasNext()) {
         String key = (String)i$.next();
         Matcher m = pattern.matcher(key);
         if (m.find()) {
            result = (String)nameIdMap.get(key.toUpperCase()) + " ";
         }
      }

      return result.trim();
   }

   public static String displayName(String id) {
      Contact c = (Contact)idContactMap.get(id.toUpperCase());
      String result = "unknown";
      if (c != null) {
         result = c.displayName;
      }

      return result;
   }

   public static String dialNumber(String type, String id) {
      String result = "unknown";
      Contact c = (Contact)idContactMap.get(id.toUpperCase());
      if (c != null) {
         String dialNumber = (String)c.phones.get(type.toUpperCase());
         if (dialNumber != null) {
            result = dialNumber;
         }
      }

      return result;
   }

   public static String emailAddress(String type, String id) {
      String result = "unknown";
      Contact c = (Contact)idContactMap.get(id.toUpperCase());
      if (c != null) {
         String emailAddress = (String)c.emails.get(type.toUpperCase());
         if (emailAddress != null) {
            result = emailAddress;
         }
      }

      return result;
   }

   public static String birthday(String id) {
      Contact c = (Contact)idContactMap.get(id.toUpperCase());
      return c == null ? "unknown" : c.birthday;
   }

   public Contact(String displayName, String phoneType, String dialNumber, String emailType, String emailAddress, String birthday) {
      this.contactId = "ID" + contactCount;
      ++contactCount;
      this.phones = new HashMap();
      this.emails = new HashMap();
      idContactMap.put(this.contactId.toUpperCase(), this);
      this.addPhone(phoneType, dialNumber);
      this.addEmail(emailType, emailAddress);
      this.addName(displayName);
      this.addBirthday(birthday);
   }

   public void addPhone(String type, String dialNumber) {
      this.phones.put(type.toUpperCase(), dialNumber);
   }

   public void addEmail(String type, String emailAddress) {
      this.emails.put(type.toUpperCase(), emailAddress);
   }

   public void addName(String name) {
      this.displayName = name;
      nameIdMap.put(this.displayName.toUpperCase(), this.contactId);
   }

   public void addBirthday(String birthday) {
      this.birthday = birthday;
   }
}
