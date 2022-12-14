package com.ittu.bot;
/**
 * @author @dibyapp, Name : Dibyaprakash, Email : dibyapp@crms.app
 * @Project : Ittu AI
 */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ittu.bot.utils.CalendarUtils;
import com.ittu.bot.utils.DomUtils;
import com.ittu.bot.utils.IOUtils;

public class AIMLProcessor {
   public static AIMLProcessorExtension extension;
   public static int sraiCount = 0;
   public static int repeatCount = 0;
   public static int trace_count = 0;

   private static void categoryProcessor(Node n, ArrayList<Category> categories, String topic, String aimlFile, String language) {
      NodeList children = n.getChildNodes();
      String pattern = "*";
      String that = "*";
      String template = "";

      String mName;
      for(int j = 0; j < children.getLength(); ++j) {
         Node m = children.item(j);
         mName = m.getNodeName();
         if (!mName.equals("#text")) {
            if (mName.equals("pattern")) {
               pattern = DomUtils.nodeToString(m);
            } else if (mName.equals("that")) {
               that = DomUtils.nodeToString(m);
            } else if (mName.equals("topic")) {
               topic = DomUtils.nodeToString(m);
            } else if (mName.equals("template")) {
               template = DomUtils.nodeToString(m);
            } else {
               System.out.println("categoryProcessor: unexpected " + mName);
            }
         }
      }

      pattern = trimTag(pattern, "pattern");
      that = trimTag(that, "that");
      topic = trimTag(topic, "topic");
      template = trimTag(template, "template");
      if (language.equals("JP") || language.equals("jp")) {
         String morphPattern = JapaneseTokenizer.morphSentence(pattern);
         System.out.println("<pattern>" + pattern + "</pattern> --> <pattern>" + morphPattern + "</pattern>");
         pattern = morphPattern;
         String morphThatPattern = JapaneseTokenizer.morphSentence(that);
         System.out.println("<that>" + that + "</that> --> <that>" + morphThatPattern + "</that>");
         that = morphThatPattern;
         mName = JapaneseTokenizer.morphSentence(topic);
         System.out.println("<topic>" + topic + "</topic> --> <topic>" + mName + "</topic>");
         topic = mName;
      }

      Category c = new Category(0, pattern, that, topic, template, aimlFile);
      categories.add(c);
   }

   public static String trimTag(String s, String tagName) {
      String stag = "<" + tagName + ">";
      String etag = "</" + tagName + ">";
      if (s.startsWith(stag) && s.endsWith(etag)) {
         s = s.substring(stag.length());
         s = s.substring(0, s.length() - etag.length());
      }

      return s.trim();
   }

   public static ArrayList<Category> AIMLToCategories(String directory, String aimlFile) {
      try {
         ArrayList categories = new ArrayList();
         Node root = DomUtils.parseFile(directory + "/" + aimlFile);
         String language = MagicStrings.default_language;
         int i;
         if (root.hasAttributes()) {
            NamedNodeMap XMLAttributes = root.getAttributes();

            for(i = 0; i < XMLAttributes.getLength(); ++i) {
               if (XMLAttributes.item(i).getNodeName().equals("language")) {
                  language = XMLAttributes.item(i).getNodeValue();
               }
            }
         }

         NodeList nodelist = root.getChildNodes();

         for(i = 0; i < nodelist.getLength(); ++i) {
            Node n = nodelist.item(i);
            if (n.getNodeName().equals("category")) {
               categoryProcessor(n, categories, "*", aimlFile, language);
            } else if (n.getNodeName().equals("topic")) {
               String topic = n.getAttributes().getNamedItem("name").getTextContent();
               NodeList children = n.getChildNodes();

               for(int j = 0; j < children.getLength(); ++j) {
                  Node m = children.item(j);
                  if (m.getNodeName().equals("category")) {
                     categoryProcessor(m, categories, topic, aimlFile, language);
                  }
               }
            }
         }

         return categories;
      } catch (Exception var12) {
         System.out.println("AIMLToCategories: " + var12);
         var12.printStackTrace();
         return null;
      }
   }

   public static int checkForRepeat(String input, Chat chatSession) {
      return input.equals(chatSession.inputHistory.get(1)) ? 1 : 0;
   }

   public static String respond(String input, String that, String topic, Chat chatSession) {
      return respond(input, that, topic, chatSession, 0);
   }

   public static String respond(String input, String that, String topic, Chat chatSession, int srCnt) {
      if (input == null || input.length() == 0) {
         input = MagicStrings.null_input;
      }

      sraiCount = srCnt;
      String response = MagicStrings.default_bot_response;

      try {
         Nodemapper leaf = chatSession.bot.brain.match(input, that, topic);
         if (leaf == null) {
            return response;
         }

         ParseState ps = new ParseState(0, chatSession, input, that, topic, leaf);
         response = evalTemplate(leaf.category.getTemplate(), ps);
      } catch (Exception var8) {
         var8.printStackTrace();
      }

      return response;
   }

   private static String capitalizeString(String string) {
      char[] chars = string.toLowerCase().toCharArray();
      boolean found = false;

      for(int i = 0; i < chars.length; ++i) {
         if (!found && Character.isLetter(chars[i])) {
            chars[i] = Character.toUpperCase(chars[i]);
            found = true;
         } else if (Character.isWhitespace(chars[i])) {
            found = false;
         }
      }

      return String.valueOf(chars);
   }

   private static String explode(String input) {
      String result = "";

      for(int i = 0; i < input.length(); ++i) {
         result = result + " " + input.charAt(i);
      }

      return result.trim();
   }

   public static String evalTagContent(Node node, ParseState ps, Set<String> ignoreAttributes) {
      String result = "";

      try {
         NodeList childList = node.getChildNodes();

         for(int i = 0; i < childList.getLength(); ++i) {
            Node child = childList.item(i);
            if (ignoreAttributes == null || !ignoreAttributes.contains(child.getNodeName())) {
               result = result + recursEval(child, ps);
            }
         }
      } catch (Exception var7) {
         System.out.println("Something went wrong with evalTagContent");
         var7.printStackTrace();
      }

      return result;
   }

   public static String genericXML(Node node, ParseState ps) {
      String result = evalTagContent(node, ps, (Set)null);
      return unevaluatedXML(result, node, ps);
   }

   private static String unevaluatedXML(String result, Node node, ParseState ps) {
      String nodeName = node.getNodeName();
      String attributes = "";
      if (node.hasAttributes()) {
         NamedNodeMap XMLAttributes = node.getAttributes();

         for(int i = 0; i < XMLAttributes.getLength(); ++i) {
            attributes = attributes + " " + XMLAttributes.item(i).getNodeName() + "=\"" + XMLAttributes.item(i).getNodeValue() + "\"";
         }
      }

      return result.equals("") ? "<" + nodeName + attributes + "/>" : "<" + nodeName + attributes + ">" + result + "</" + nodeName + ">";
   }

   private static String srai(Node node, ParseState ps) {
      ++sraiCount;
      if (sraiCount > MagicNumbers.max_recursion) {
         return MagicStrings.too_much_recursion;
      } else {
         String response = MagicStrings.default_bot_response;

         try {
            String result = evalTagContent(node, ps, (Set)null);
            result = result.trim();
            result = result.replaceAll("(\r\n|\n\r|\r|\n)", " ");
            result = ps.chatSession.bot.preProcessor.normalize(result);
            String topic = ps.chatSession.predicates.get("topic");
            if (MagicBooleans.trace_mode) {
               System.out.println(trace_count + ". <srai>" + result + "</srai> from " + ps.leaf.category.inputThatTopic() + " topic=" + topic + ") ");
               ++trace_count;
            }

            Nodemapper leaf = ps.chatSession.bot.brain.match(result, ps.that, topic);
            if (leaf == null) {
               return response;
            }

            response = evalTemplate(leaf.category.getTemplate(), new ParseState(ps.depth + 1, ps.chatSession, ps.input, ps.that, topic, leaf));
         } catch (Exception var6) {
            var6.printStackTrace();
         }

         return response.trim();
      }
   }

   private static String getAttributeOrTagValue(Node node, ParseState ps, String attributeName) {
      String result = "";
      Node m = node.getAttributes().getNamedItem(attributeName);
      if (m == null) {
         NodeList childList = node.getChildNodes();
         result = null;

         for(int i = 0; i < childList.getLength(); ++i) {
            Node child = childList.item(i);
            if (child.getNodeName().equals(attributeName)) {
               result = evalTagContent(child, ps, (Set)null);
            }
         }
      } else {
         result = m.getNodeValue();
      }

      return result;
   }

   private static String sraix(Node node, ParseState ps) {
      HashSet<String> attributeNames = Utilities.stringSet(new String[]{"botid", "host"});
      String host = getAttributeOrTagValue(node, ps, "host");
      String botid = getAttributeOrTagValue(node, ps, "botid");
      String hint = getAttributeOrTagValue(node, ps, "hint");
      String limit = getAttributeOrTagValue(node, ps, "limit");
      String defaultResponse = getAttributeOrTagValue(node, ps, "default");
      String result = evalTagContent(node, ps, attributeNames);
//      return Sraix.sraix(ps.chatSession, result, defaultResponse, hint, host, botid, (String)null, limit);
      return "Sorry, I didn't get that.";
   }

   private static String map(Node node, ParseState ps) {
      String result = MagicStrings.unknown_map_value;
      HashSet<String> attributeNames = Utilities.stringSet(new String[]{"name"});
      String mapName = getAttributeOrTagValue(node, ps, "name");
      String contents = evalTagContent(node, ps, attributeNames);
      if (mapName == null) {
         result = "<map>" + contents + "</map>";
      } else {
         AIMLMap map = (AIMLMap)ps.chatSession.bot.mapMap.get(mapName);
         if (map != null) {
            result = map.get(contents.toUpperCase());
         }

         if (result == null) {
            result = MagicStrings.unknown_map_value;
         }

         result = result.trim();
      }

      return result;
   }

   private static String set(Node node, ParseState ps) {
      HashSet<String> attributeNames = Utilities.stringSet(new String[]{"name", "var"});
      String predicateName = getAttributeOrTagValue(node, ps, "name");
      String varName = getAttributeOrTagValue(node, ps, "var");
      String value = evalTagContent(node, ps, attributeNames).trim();
      value = value.replaceAll("(\r\n|\n\r|\r|\n)", " ");
      if (predicateName != null) {
         ps.chatSession.predicates.put(predicateName, value);
      }

      if (varName != null) {
         ps.vars.put(varName, value);
      }

      return value;
   }

   private static String get(Node node, ParseState ps) {
      String result = MagicStrings.unknown_predicate_value;
      String predicateName = getAttributeOrTagValue(node, ps, "name");
      String varName = getAttributeOrTagValue(node, ps, "var");
      if (predicateName != null) {
         result = ps.chatSession.predicates.get(predicateName).trim();
      } else if (varName != null) {
         result = ps.vars.get(varName).trim();
      }

      return result;
   }

   private static String bot(Node node, ParseState ps) {
      String result = MagicStrings.unknown_property_value;
      String propertyName = getAttributeOrTagValue(node, ps, "name");
      if (propertyName != null) {
         result = ps.chatSession.bot.properties.get(propertyName).trim();
      }

      return result;
   }

   private static String date(Node node, ParseState ps) {
      String jformat = getAttributeOrTagValue(node, ps, "jformat");
      String locale = getAttributeOrTagValue(node, ps, "locale");
      String timezone = getAttributeOrTagValue(node, ps, "timezone");
      String dateAsString = CalendarUtils.date(jformat, locale, timezone);
      return dateAsString;
   }

   private static String interval(Node node, ParseState ps) {
      HashSet<String> attributeNames = Utilities.stringSet(new String[]{"style", "jformat", "from", "to"});
      String style = getAttributeOrTagValue(node, ps, "style");
      String jformat = getAttributeOrTagValue(node, ps, "jformat");
      String from = getAttributeOrTagValue(node, ps, "from");
      String to = getAttributeOrTagValue(node, ps, "to");
      if (style == null) {
         style = "years";
      }

      if (jformat == null) {
         jformat = "MMMMMMMMM dd, yyyy";
      }

      if (from == null) {
         from = "January 1, 1970";
      }

      if (to == null) {
         to = CalendarUtils.date(jformat, (String)null, (String)null);
      }

      String result = "unknown";
      if (style.equals("years")) {
         result = "" + Interval.getYearsBetween(from, to, jformat);
      }

      if (style.equals("months")) {
         result = "" + Interval.getMonthsBetween(from, to, jformat);
      }

      if (style.equals("days")) {
         result = "" + Interval.getDaysBetween(from, to, jformat);
      }

      if (style.equals("hours")) {
         result = "" + Interval.getHoursBetween(from, to, jformat);
      }

      return result;
   }

   private static int getIndexValue(Node node, ParseState ps) {
      int index = 0;
      String value = getAttributeOrTagValue(node, ps, "index");
      if (value != null) {
         try {
            index = Integer.parseInt(value) - 1;
         } catch (Exception var5) {
            var5.printStackTrace();
         }
      }

      return index;
   }

   private static String inputStar(Node node, ParseState ps) {
      int index = getIndexValue(node, ps);
      return ps.leaf.starBindings.inputStars.star(index) == null ? "" : ps.leaf.starBindings.inputStars.star(index).trim();
   }

   private static String thatStar(Node node, ParseState ps) {
      int index = getIndexValue(node, ps);
      return ps.leaf.starBindings.thatStars.star(index) == null ? "" : ps.leaf.starBindings.thatStars.star(index).trim();
   }

   private static String topicStar(Node node, ParseState ps) {
      int index = getIndexValue(node, ps);
      return ps.leaf.starBindings.topicStars.star(index) == null ? "" : ps.leaf.starBindings.topicStars.star(index).trim();
   }

   private static String id(Node node, ParseState ps) {
      return ps.chatSession.customerId;
   }

   private static String size(Node node, ParseState ps) {
      int size = ps.chatSession.bot.brain.getCategories().size();
      return String.valueOf(size);
   }

   private static String vocabulary(Node node, ParseState ps) {
      int size = ps.chatSession.bot.brain.getVocabulary().size();
      return String.valueOf(size);
   }

   private static String program(Node node, ParseState ps) {
      return MagicStrings.programNameVersion;
   }

   private static String that(Node node, ParseState ps) {
      int index = 0;
      int jndex = 0;
      String value = getAttributeOrTagValue(node, ps, "index");
      if (value != null) {
         try {
            String[] spair = value.split(",");
            index = Integer.parseInt(spair[0]) - 1;
            jndex = Integer.parseInt(spair[1]) - 1;
            System.out.println("That index=" + index + "," + jndex);
         } catch (Exception var7) {
            var7.printStackTrace();
         }
      }

      String that = MagicStrings.unknown_history_item;
      History hist = (History)ps.chatSession.thatHistory.get(index);
      if (hist != null) {
         that = (String)hist.get(jndex);
      }

      return that.trim();
   }

   private static String input(Node node, ParseState ps) {
      int index = getIndexValue(node, ps);
      return ps.chatSession.inputHistory.getString(index);
   }

   private static String request(Node node, ParseState ps) {
      int index = getIndexValue(node, ps);
      return ps.chatSession.requestHistory.getString(index).trim();
   }

   private static String response(Node node, ParseState ps) {
      int index = getIndexValue(node, ps);
      return ps.chatSession.responseHistory.getString(index).trim();
   }

   private static String system(Node node, ParseState ps) {
      HashSet<String> attributeNames = Utilities.stringSet(new String[]{"timeout"});
      String evaluatedContents = evalTagContent(node, ps, attributeNames);
      String result = IOUtils.system(evaluatedContents, MagicStrings.system_failed);
      return result;
   }

   private static String think(Node node, ParseState ps) {
      evalTagContent(node, ps, (Set)null);
      return "";
   }

   private static String explode(Node node, ParseState ps) {
      String result = evalTagContent(node, ps, (Set)null);
      return explode(result);
   }

   private static String normalize(Node node, ParseState ps) {
      String result = evalTagContent(node, ps, (Set)null);
      return ps.chatSession.bot.preProcessor.normalize(result);
   }

   private static String denormalize(Node node, ParseState ps) {
      String result = evalTagContent(node, ps, (Set)null);
      return ps.chatSession.bot.preProcessor.denormalize(result);
   }

   private static String uppercase(Node node, ParseState ps) {
      String result = evalTagContent(node, ps, (Set)null);
      return result.toUpperCase();
   }

   private static String lowercase(Node node, ParseState ps) {
      String result = evalTagContent(node, ps, (Set)null);
      return result.toLowerCase();
   }

   private static String formal(Node node, ParseState ps) {
      String result = evalTagContent(node, ps, (Set)null);
      return capitalizeString(result);
   }

   private static String sentence(Node node, ParseState ps) {
      String result = evalTagContent(node, ps, (Set)null);
      return result.length() > 1 ? result.substring(0, 1).toUpperCase() + result.substring(1, result.length()) : "";
   }

   private static String person(Node node, ParseState ps) {
      String result;
      if (node.hasChildNodes()) {
         result = evalTagContent(node, ps, (Set)null);
      } else {
         result = ps.leaf.starBindings.inputStars.star(0);
      }

      result = " " + result + " ";
      result = ps.chatSession.bot.preProcessor.person(result);
      return result.trim();
   }

   private static String person2(Node node, ParseState ps) {
      String result;
      if (node.hasChildNodes()) {
         result = evalTagContent(node, ps, (Set)null);
      } else {
         result = ps.leaf.starBindings.inputStars.star(0);
      }

      result = " " + result + " ";
      result = ps.chatSession.bot.preProcessor.person2(result);
      return result.trim();
   }

   private static String gender(Node node, ParseState ps) {
      String result = evalTagContent(node, ps, (Set)null);
      result = " " + result + " ";
      result = ps.chatSession.bot.preProcessor.gender(result);
      return result.trim();
   }

   private static String random(Node node, ParseState ps) {
      NodeList childList = node.getChildNodes();
      ArrayList<Node> liList = new ArrayList();

      for(int i = 0; i < childList.getLength(); ++i) {
         if (childList.item(i).getNodeName().equals("li")) {
            liList.add(childList.item(i));
         }
      }

      return evalTagContent((Node)liList.get((int)(Math.random() * (double)liList.size())), ps, (Set)null);
   }

   private static String unevaluatedAIML(Node node, ParseState ps) {
      String result = learnEvalTagContent(node, ps);
      return unevaluatedXML(result, node, ps);
   }

   private static String recursLearn(Node node, ParseState ps) {
      String nodeName = node.getNodeName();
      if (nodeName.equals("#text")) {
         return node.getNodeValue();
      } else {
         return nodeName.equals("eval") ? evalTagContent(node, ps, (Set)null) : unevaluatedAIML(node, ps);
      }
   }

   private static String learnEvalTagContent(Node node, ParseState ps) {
      String result = "";
      NodeList childList = node.getChildNodes();

      for(int i = 0; i < childList.getLength(); ++i) {
         Node child = childList.item(i);
         result = result + recursLearn(child, ps);
      }

      return result;
   }

   private static String learn(Node node, ParseState ps) {
      NodeList childList = node.getChildNodes();
      String pattern = "";
      String that = "*";
      String template = "";

      for(int i = 0; i < childList.getLength(); ++i) {
         if (childList.item(i).getNodeName().equals("category")) {
            NodeList grandChildList = childList.item(i).getChildNodes();

            for(int j = 0; j < grandChildList.getLength(); ++j) {
               if (grandChildList.item(j).getNodeName().equals("pattern")) {
                  pattern = recursLearn(grandChildList.item(j), ps);
               } else if (grandChildList.item(j).getNodeName().equals("that")) {
                  that = recursLearn(grandChildList.item(j), ps);
               } else if (grandChildList.item(j).getNodeName().equals("template")) {
                  template = recursLearn(grandChildList.item(j), ps);
               }
            }

            pattern = pattern.substring("<pattern>".length(), pattern.length() - "</pattern>".length());
            if (template.length() >= "<template></template>".length()) {
               template = template.substring("<template>".length(), template.length() - "</template>".length());
            }

            if (that.length() >= "<that></that>".length()) {
               that = that.substring("<that>".length(), that.length() - "</that>".length());
            }

            pattern = pattern.toUpperCase();
            that = that.toUpperCase();
            if (MagicBooleans.trace_mode) {
               System.out.println("Learn Pattern = " + pattern);
               System.out.println("Learn That = " + that);
               System.out.println("Learn Template = " + template);
            }

            Category c;
            if (node.getNodeName().equals("learn")) {
               c = new Category(0, pattern, that, "*", template, MagicStrings.null_aiml_file);
            } else {
               c = new Category(0, pattern, that, "*", template, MagicStrings.learnf_aiml_file);
               ps.chatSession.bot.learnfGraph.addCategory(c);
            }

            ps.chatSession.bot.brain.addCategory(c);
         }
      }

      return "";
   }

   private static String loopCondition(Node node, ParseState ps) {
      boolean loop = true;
      String result = "";

      byte loopCnt;
      String loopResult;
      for(loopCnt = 0; loop && loopCnt < MagicNumbers.max_loops; result = result + loopResult) {
         loopResult = condition(node, ps);
         if (loopResult.trim().equals(MagicStrings.too_much_recursion)) {
            return MagicStrings.too_much_recursion;
         }

         if (loopResult.contains("<loop/>")) {
            loopResult = loopResult.replace("<loop/>", "");
            loop = true;
         } else {
            loop = false;
         }
      }

      if (loopCnt >= MagicNumbers.max_loops) {
         result = MagicStrings.too_much_looping;
      }

      return result;
   }

   private static String condition(Node node, ParseState ps) {
      String result = "";
      NodeList childList = node.getChildNodes();
      ArrayList<Node> liList = new ArrayList();
      String predicate = null;
      String varName = null;
      String value = null;
      HashSet<String> attributeNames = Utilities.stringSet(new String[]{"name", "var", "value"});
      predicate = getAttributeOrTagValue(node, ps, "name");
      varName = getAttributeOrTagValue(node, ps, "var");

      int i;
      for(i = 0; i < childList.getLength(); ++i) {
         if (childList.item(i).getNodeName().equals("li")) {
            liList.add(childList.item(i));
         }
      }

      if (liList.size() == 0 && (value = getAttributeOrTagValue(node, ps, "value")) != null && predicate != null && ps.chatSession.predicates.get(predicate).equals(value)) {
         return evalTagContent(node, ps, attributeNames);
      } else if (liList.size() == 0 && (value = getAttributeOrTagValue(node, ps, "value")) != null && varName != null && ps.vars.get(varName).equals(value)) {
         return evalTagContent(node, ps, attributeNames);
      } else {
         i = 0;

         while(true) {
            if (i < liList.size() && result.equals("")) {
               Node n = (Node)liList.get(i);
               String liPredicate = predicate;
               String liVarName = varName;
               if (predicate == null) {
                  liPredicate = getAttributeOrTagValue(n, ps, "name");
               }

               if (varName == null) {
                  liVarName = getAttributeOrTagValue(n, ps, "var");
               }

               value = getAttributeOrTagValue(n, ps, "value");
               if (value == null) {
                  return evalTagContent(n, ps, attributeNames);
               }

               if (liPredicate != null && value != null && (ps.chatSession.predicates.get(liPredicate).equals(value) || ps.chatSession.predicates.containsKey(liPredicate) && value.equals("*"))) {
                  return evalTagContent(n, ps, attributeNames);
               }

               if (liVarName == null || value == null || !ps.vars.get(liVarName).equals(value) && (!ps.vars.containsKey(liPredicate) || !value.equals("*"))) {
                  ++i;
                  continue;
               }

               return evalTagContent(n, ps, attributeNames);
            }

            return "";
         }
      }
   }

   public static boolean evalTagForLoop(Node node) {
      NodeList childList = node.getChildNodes();

      for(int i = 0; i < childList.getLength(); ++i) {
         if (childList.item(i).getNodeName().equals("loop")) {
            return true;
         }
      }

      return false;
   }

   private static String recursEval(Node node, ParseState ps) {
      try {
         String nodeName = node.getNodeName();
         if (nodeName.equals("#text")) {
            return node.getNodeValue();
         } else if (nodeName.equals("#comment")) {
            return "";
         } else if (nodeName.equals("template")) {
            return evalTagContent(node, ps, (Set)null);
         } else if (nodeName.equals("random")) {
            return random(node, ps);
         } else if (nodeName.equals("condition")) {
            return loopCondition(node, ps);
         } else if (nodeName.equals("srai")) {
            return srai(node, ps);
         } else if (nodeName.equals("sr")) {
            return respond(ps.leaf.starBindings.inputStars.star(0), ps.that, ps.topic, ps.chatSession, sraiCount);
         } else if (nodeName.equals("sraix")) {
            return sraix(node, ps);
         } else if (nodeName.equals("set")) {
            return set(node, ps);
         } else if (nodeName.equals("get")) {
            return get(node, ps);
         } else if (nodeName.equals("map")) {
            return map(node, ps);
         } else if (nodeName.equals("bot")) {
            return bot(node, ps);
         } else if (nodeName.equals("id")) {
            return id(node, ps);
         } else if (nodeName.equals("size")) {
            return size(node, ps);
         } else if (nodeName.equals("vocabulary")) {
            return vocabulary(node, ps);
         } else if (nodeName.equals("program")) {
            return program(node, ps);
         } else if (nodeName.equals("date")) {
            return date(node, ps);
         } else if (nodeName.equals("interval")) {
            return interval(node, ps);
         } else if (nodeName.equals("think")) {
            return think(node, ps);
         } else if (nodeName.equals("system")) {
            return system(node, ps);
         } else if (nodeName.equals("explode")) {
            return explode(node, ps);
         } else if (nodeName.equals("normalize")) {
            return normalize(node, ps);
         } else if (nodeName.equals("denormalize")) {
            return denormalize(node, ps);
         } else if (nodeName.equals("uppercase")) {
            return uppercase(node, ps);
         } else if (nodeName.equals("lowercase")) {
            return lowercase(node, ps);
         } else if (nodeName.equals("formal")) {
            return formal(node, ps);
         } else if (nodeName.equals("sentence")) {
            return sentence(node, ps);
         } else if (nodeName.equals("person")) {
            return person(node, ps);
         } else if (nodeName.equals("person2")) {
            return person2(node, ps);
         } else if (nodeName.equals("gender")) {
            return gender(node, ps);
         } else if (nodeName.equals("star")) {
            return inputStar(node, ps);
         } else if (nodeName.equals("thatstar")) {
            return thatStar(node, ps);
         } else if (nodeName.equals("topicstar")) {
            return topicStar(node, ps);
         } else if (nodeName.equals("that")) {
            return that(node, ps);
         } else if (nodeName.equals("input")) {
            return input(node, ps);
         } else if (nodeName.equals("request")) {
            return request(node, ps);
         } else if (nodeName.equals("response")) {
            return response(node, ps);
         } else if (!nodeName.equals("learn") && !nodeName.equals("learnf")) {
            return extension != null && extension.extensionTagSet().contains(nodeName) ? extension.recursEval(node, ps) : genericXML(node, ps);
         } else {
            return learn(node, ps);
         }
      } catch (Exception var3) {
         var3.printStackTrace();
         return "";
      }
   }

   private static String evalTemplate(String template, ParseState ps) {
      String response = MagicStrings.template_failed;

      try {
         template = "<template>" + template + "</template>";
         Node root = DomUtils.parseString(template);
         response = recursEval(root, ps);
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      return response;
   }

   public static boolean validTemplate(String template) {
      try {
         template = "<template>" + template + "</template>";
         DomUtils.parseString(template);
         return true;
      } catch (Exception var2) {
         System.out.println("Invalid Template " + template);
         return false;
      }
   }
}
