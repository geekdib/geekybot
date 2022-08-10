package com.ittu.bot.utils;
/**
 * @author @dibyapp, Name : Dibyaprakash, Email : dibyapp@crms.app
 * @Project : Ittu AI
 */
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class DomUtils {
   public static Node parseFile(String fileName) throws Exception {
      File file = new File(fileName);
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(file);
      doc.getDocumentElement().normalize();
      Node root = doc.getDocumentElement();
      return root;
   }

   public static Node parseString(String string) throws Exception {
      InputStream is = new ByteArrayInputStream(string.getBytes("UTF-16"));
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(is);
      doc.getDocumentElement().normalize();
      Node root = doc.getDocumentElement();
      return root;
   }

   public static String nodeToString(Node node) {
      StringWriter sw = new StringWriter();

      try {
         Transformer t = TransformerFactory.newInstance().newTransformer();
         t.setOutputProperty("omit-xml-declaration", "yes");
         t.setOutputProperty("indent", "no");
         t.transform(new DOMSource(node), new StreamResult(sw));
      } catch (TransformerException var3) {
         System.out.println("nodeToString Transformer Exception");
      }

      return sw.toString();
   }
}
