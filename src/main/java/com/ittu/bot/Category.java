package com.ittu.bot;
/**
 * @author @dibyapp, Name : Dibyaprakash, Email : dibyapp@crms.app
 * @Project : Ittu AI
 */
import java.util.Comparator;


public class Category {
   private String pattern;
   private String that;
   private String topic;
   private String template;
   private String filename;
   private int activationCnt;
   private int categoryNumber;
   public static int categoryCnt = 0;
   private AIMLSet matches;
   public String validationMessage;
   public static Comparator<Category> ACTIVATION_COMPARATOR = new Category$1();
   public static Comparator<Category> PATTERN_COMPARATOR = new Category$2();
   public static Comparator<Category> CATEGORY_NUMBER_COMPARATOR = new Category$3();

   public AIMLSet getMatches() {
      return this.matches != null ? this.matches : new AIMLSet("No Matches");
   }

   public int getActivationCnt() {
      return this.activationCnt;
   }

   public int getCategoryNumber() {
      return this.categoryNumber;
   }

   public String getPattern() {
      return this.pattern == null ? "*" : this.pattern;
   }

   public String getThat() {
      return this.that == null ? "*" : this.that;
   }

   public String getTopic() {
      return this.topic == null ? "*" : this.topic;
   }

   public String getTemplate() {
      return this.template == null ? "" : this.template;
   }

   public String getFilename() {
      return this.filename == null ? MagicStrings.unknown_aiml_file : this.filename;
   }

   public void incrementActivationCnt() {
      ++this.activationCnt;
   }

   public void setActivationCnt(int cnt) {
      this.activationCnt = cnt;
   }

   public void setFilename(String filename) {
      this.filename = filename;
   }

   public void setTemplate(String template) {
      this.template = template;
   }

   public void setPattern(String pattern) {
      this.pattern = pattern;
   }

   public void setThat(String that) {
      this.that = that;
   }

   public void setTopic(String topic) {
      this.topic = topic;
   }

   public String inputThatTopic() {
      return Graphmaster.inputThatTopic(this.pattern, this.that, this.topic);
   }

   public void addMatch(String input) {
      if (this.matches == null) {
         String setName = this.inputThatTopic().replace("*", "STAR").replace("_", "UNDERSCORE").replace(" ", "-").replace("<THAT>", "THAT").replace("<TOPIC>", "TOPIC");
         this.matches = new AIMLSet(setName);
      }

      this.matches.add(input);
   }

   public static String templateToLine(String template) {
      String result = template.replaceAll("(\r\n|\n\r|\r|\n)", "\\#Newline");
      result = result.replaceAll(MagicStrings.aimlif_split_char, MagicStrings.aimlif_split_char_name);
      return result;
   }

   private static String lineToTemplate(String line) {
      String result = line.replaceAll("\\#Newline", "\n");
      result = result.replaceAll(MagicStrings.aimlif_split_char_name, MagicStrings.aimlif_split_char);
      return result;
   }

   public static Category IFToCategory(String IF) {
      String[] split = IF.split(MagicStrings.aimlif_split_char);
      return new Category(Integer.parseInt(split[0]), split[1], split[2], split[3], lineToTemplate(split[4]), split[5]);
   }

   public static String categoryToIF(Category category) {
      String c = MagicStrings.aimlif_split_char;
      return category.getActivationCnt() + c + category.getPattern() + c + category.getThat() + c + category.getTopic() + c + templateToLine(category.getTemplate()) + c + category.getFilename();
   }

   public static String categoryToAIML(Category category) {
      String topicStart = "";
      String topicEnd = "";
      String thatStatement = "";
      String result = "";
      String pattern = category.getPattern();
      String[] splitPattern = pattern.split(" ");
      String[] arr$ = splitPattern;
      int len$ = splitPattern.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         String w = arr$[i$];
         if (w.startsWith("<TYPE>")) {
            w = w.toLowerCase();
         }

         pattern = pattern + " " + w;
      }

      pattern = pattern.trim();
      if (pattern.contains("type")) {
         System.out.println("Rebuilt pattern " + pattern);
      }

      String NL = System.getProperty("line.separator");
      NL = "\n";

      try {
         if (!category.getTopic().equals("*")) {
            topicStart = "<topic name=\"" + category.getTopic() + "\">" + NL;
            topicEnd = "</topic>" + NL;
         }

         if (!category.getThat().equals("*")) {
            thatStatement = "<that>" + category.getThat() + "</that>";
         }

         result = topicStart + "<category><pattern>" + category.getPattern() + "</pattern>" + thatStatement + NL + "<template>" + category.getTemplate() + "</template>" + NL + "</category>" + topicEnd;
      } catch (Exception var11) {
         var11.printStackTrace();
      }

      return result;
   }

   public boolean validPatternForm(String pattern) {
      if (pattern.length() < 1) {
         this.validationMessage = this.validationMessage + "Zero length. ";
         return false;
      } else {
         String[] words = pattern.split(" ");

         for(int i = 0; i < words.length; ++i) {
         }

         return true;
      }
   }

   public boolean validate() {
      this.validationMessage = "";
      if (!this.validPatternForm(this.pattern)) {
         this.validationMessage = this.validationMessage + "Badly formatted <pattern>";
         return false;
      } else if (!this.validPatternForm(this.that)) {
         this.validationMessage = this.validationMessage + "Badly formatted <that>";
         return false;
      } else if (!this.validPatternForm(this.topic)) {
         this.validationMessage = this.validationMessage + "Badly formatted <topic>";
         return false;
      } else if (!AIMLProcessor.validTemplate(this.template)) {
         this.validationMessage = this.validationMessage + "Badly formatted <template>";
         return false;
      } else if (!this.filename.endsWith(".aiml")) {
         this.validationMessage = this.validationMessage + "Filename suffix should be .aiml";
         return false;
      } else {
         return true;
      }
   }

   public Category(int activationCnt, String pattern, String that, String topic, String template, String filename) {
      this.validationMessage = "";
      if (MagicBooleans.fix_excel_csv) {
         pattern = Utilities.fixCSV(pattern);
         that = Utilities.fixCSV(that);
         topic = Utilities.fixCSV(topic);
         template = Utilities.fixCSV(template);
         filename = Utilities.fixCSV(filename);
      }

      this.pattern = pattern.trim().toUpperCase();
      this.that = that.trim().toUpperCase();
      this.topic = topic.trim().toUpperCase();
      this.template = template.replace("& ", " and ");
      this.filename = filename;
      this.activationCnt = activationCnt;
      this.matches = null;
      this.categoryNumber = categoryCnt++;
   }

   public Category(int activationCnt, String patternThatTopic, String template, String filename) {
      this(activationCnt, patternThatTopic.substring(0, patternThatTopic.indexOf("<THAT>")), patternThatTopic.substring(patternThatTopic.indexOf("<THAT>") + "<THAT>".length(), patternThatTopic.indexOf("<TOPIC>")), patternThatTopic.substring(patternThatTopic.indexOf("<TOPIC>") + "<TOPIC>".length(), patternThatTopic.length()), template, filename);
   }
}
