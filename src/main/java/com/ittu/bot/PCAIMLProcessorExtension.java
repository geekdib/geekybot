package com.ittu.bot;
/**
 * @author @dibyapp, Name : Dibyaprakash, Email : dibyapp@crms.app
 * @Project : Ittu AI
 */
import java.util.Set;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PCAIMLProcessorExtension implements AIMLProcessorExtension {
   public Set<String> extensionTagNames = Utilities.stringSet(new String[]{"contactid", "multipleids", "displayname", "dialnumber", "emailaddress", "contactbirthday", "addinfo"});

   public Set<String> extensionTagSet() {
      return this.extensionTagNames;
   }

   private String newContact(Node node, ParseState ps) {
      NodeList childList = node.getChildNodes();
      String emailAddress = "unknown";
      String displayName = "unknown";
      String dialNumber = "unknown";
      String emailType = "unknown";
      String phoneType = "unknown";
      String birthday = "unknown";

      for(int i = 0; i < childList.getLength(); ++i) {
         if (childList.item(i).getNodeName().equals("birthday")) {
            birthday = AIMLProcessor.evalTagContent(childList.item(i), ps, (Set)null);
         }

         if (childList.item(i).getNodeName().equals("phonetype")) {
            phoneType = AIMLProcessor.evalTagContent(childList.item(i), ps, (Set)null);
         }

         if (childList.item(i).getNodeName().equals("emailtype")) {
            emailType = AIMLProcessor.evalTagContent(childList.item(i), ps, (Set)null);
         }

         if (childList.item(i).getNodeName().equals("dialnumber")) {
            dialNumber = AIMLProcessor.evalTagContent(childList.item(i), ps, (Set)null);
         }

         if (childList.item(i).getNodeName().equals("displayname")) {
            displayName = AIMLProcessor.evalTagContent(childList.item(i), ps, (Set)null);
         }

         if (childList.item(i).getNodeName().equals("emailaddress")) {
            emailAddress = AIMLProcessor.evalTagContent(childList.item(i), ps, (Set)null);
         }
      }

      System.out.println("Adding new contact " + displayName + " " + phoneType + " " + dialNumber + " " + emailType + " " + emailAddress + " " + birthday);
      new Contact(displayName, phoneType, dialNumber, emailType, emailAddress, birthday);
      return "";
   }

   private String contactId(Node node, ParseState ps) {
      String displayName = AIMLProcessor.evalTagContent(node, ps, (Set)null);
      String result = Contact.contactId(displayName);
      return result;
   }

   private String multipleIds(Node node, ParseState ps) {
      String contactName = AIMLProcessor.evalTagContent(node, ps, (Set)null);
      String result = Contact.multipleIds(contactName);
      return result;
   }

   private String displayName(Node node, ParseState ps) {
      String id = AIMLProcessor.evalTagContent(node, ps, (Set)null);
      String result = Contact.displayName(id);
      return result;
   }

   private String dialNumber(Node node, ParseState ps) {
      NodeList childList = node.getChildNodes();
      String id = "unknown";
      String type = "unknown";

      for(int i = 0; i < childList.getLength(); ++i) {
         if (childList.item(i).getNodeName().equals("id")) {
            id = AIMLProcessor.evalTagContent(childList.item(i), ps, (Set)null);
         }

         if (childList.item(i).getNodeName().equals("type")) {
            type = AIMLProcessor.evalTagContent(childList.item(i), ps, (Set)null);
         }
      }

      String result = Contact.dialNumber(type, id);
      return result;
   }

   private String emailAddress(Node node, ParseState ps) {
      NodeList childList = node.getChildNodes();
      String id = "unknown";
      String type = "unknown";

      for(int i = 0; i < childList.getLength(); ++i) {
         if (childList.item(i).getNodeName().equals("id")) {
            id = AIMLProcessor.evalTagContent(childList.item(i), ps, (Set)null);
         }

         if (childList.item(i).getNodeName().equals("type")) {
            type = AIMLProcessor.evalTagContent(childList.item(i), ps, (Set)null);
         }
      }

      String result = Contact.emailAddress(type, id);
      return result;
   }

   private String contactBirthday(Node node, ParseState ps) {
      String id = AIMLProcessor.evalTagContent(node, ps, (Set)null);
      String result = Contact.birthday(id);
      return result;
   }

   public String recursEval(Node node, ParseState ps) {
      try {
         String nodeName = node.getNodeName();
         if (nodeName.equals("contactid")) {
            return this.contactId(node, ps);
         } else if (nodeName.equals("multipleids")) {
            return this.multipleIds(node, ps);
         } else if (nodeName.equals("dialnumber")) {
            return this.dialNumber(node, ps);
         } else if (nodeName.equals("addinfo")) {
            return this.newContact(node, ps);
         } else if (nodeName.equals("displayname")) {
            return this.displayName(node, ps);
         } else if (nodeName.equals("emailaddress")) {
            return this.emailAddress(node, ps);
         } else {
            return nodeName.equals("contactbirthday") ? this.contactBirthday(node, ps) : AIMLProcessor.genericXML(node, ps);
         }
      } catch (Exception var4) {
         var4.printStackTrace();
         return "";
      }
   }
}
