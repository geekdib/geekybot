package com.ittu.bot;
/**
 * @author @dibyapp, Name : Dibyaprakash, Email : dibyapp@crms.app
 * @Project : Ittu AI
 */
import java.io.BufferedWriter;
import java.io.FileWriter;

import com.ittu.bot.utils.IOUtils;

public class Chat {
   public Bot bot;
   public String customerId;
   public History<History> thatHistory;
   public History<String> requestHistory;
   public History<String> responseHistory;
   public History<String> inputHistory;
   public Predicates predicates;
   public static String matchTrace = "";
   public static boolean locationKnown = false;
   public static String longitude;
   public static String latitude;

   public Chat(Bot bot) {
      this(bot, "0");
   }

   public Chat(Bot bot, String customerId) {
      this.customerId = MagicStrings.unknown_customer_id;
      this.thatHistory = new History("that");
      this.requestHistory = new History("request");
      this.responseHistory = new History("response");
      this.inputHistory = new History("input");
      this.predicates = new Predicates();
      this.customerId = customerId;
      this.bot = bot;
      History<String> contextThatHistory = new History();
      contextThatHistory.add(MagicStrings.default_that);
      this.thatHistory.add(contextThatHistory);
      this.addPredicates();
      this.predicates.put("topic", MagicStrings.default_topic);
   }

   void addPredicates() {
      try {
         this.predicates.getPredicateDefaults(MagicStrings.config_path + "/predicates.txt");
      } catch (Exception var2) {
         var2.printStackTrace();
      }

   }

   public void chat() {
      BufferedWriter bw = null;
      String logFile = MagicStrings.log_path + "/log_" + this.customerId + ".txt";

      try {
         bw = new BufferedWriter(new FileWriter(logFile, true));
         String request = "SET PREDICATES";
         this.multisentenceRespond(request);

         while(!request.equals("quit")) {
            System.out.print("Human: ");
            request = IOUtils.readInputTextLine();
            String response = this.multisentenceRespond(request);
            System.out.println("Robot: " + response);
            bw.write("Human: " + request);
            bw.newLine();
            bw.write("Robot: " + response);
            bw.newLine();
            bw.flush();
         }

         bw.close();
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   String respond(String input, String that, String topic, History contextThatHistory) {
      this.inputHistory.add(input);
      String response = AIMLProcessor.respond(input, that, topic, this);
      String normResponse = this.bot.preProcessor.normalize(response);
      normResponse = JapaneseTokenizer.morphSentence(normResponse);
      String[] sentences = this.bot.preProcessor.sentenceSplit(normResponse);

      for(int i = 0; i < sentences.length; ++i) {
         that = sentences[i];
         if (that.trim().equals("")) {
            that = MagicStrings.default_that;
         }

         contextThatHistory.add(that);
      }

      return response.trim() + "  ";
   }

   String respond(String input, History<String> contextThatHistory) {
      History hist = (History)this.thatHistory.get(0);
      String that;
      if (hist == null) {
         that = MagicStrings.default_that;
      } else {
         that = hist.getString(0);
      }

      return this.respond(input, that, this.predicates.get("topic"), contextThatHistory);
   }

   public String multisentenceRespond(String request) {
      String response = "";
      matchTrace = "";

      try {
         String norm = this.bot.preProcessor.normalize(request);
         norm = JapaneseTokenizer.morphSentence(norm);
         if (MagicBooleans.trace_mode) {
            System.out.println("normalized = " + norm);
         }

         String[] sentences = this.bot.preProcessor.sentenceSplit(norm);
         History<String> contextThatHistory = new History("contextThat");
         int i = 0;

         while(true) {
            if (i >= sentences.length) {
               this.requestHistory.add(request);
               this.responseHistory.add(response);
               this.thatHistory.add(contextThatHistory);
               break;
            }

            AIMLProcessor.trace_count = 0;
            String reply = this.respond(sentences[i], contextThatHistory);
            response = response + "  " + reply;
            ++i;
         }
      } catch (Exception var8) {
         var8.printStackTrace();
         return MagicStrings.error_bot_response;
      }

      this.bot.writeLearnfIFCategories();
      return response.trim();
   }

   public static void setMatchTrace(String newMatchTrace) {
      matchTrace = newMatchTrace;
   }
}
