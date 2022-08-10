package com.ittu.bot;
/**
 * @author @dibyapp, Name : Dibyaprakash, Email : dibyapp@crms.app
 * @Project : Ittu AI
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Bot {
   public final Properties properties;
   public final PreProcessor preProcessor;
   public final Graphmaster brain;
   public final Graphmaster inputGraph;
   public final Graphmaster learnfGraph;
   public final Graphmaster patternGraph;
   public final Graphmaster deletedGraph;
   public Graphmaster unfinishedGraph;
   public ArrayList<Category> suggestedCategories;
   public String name;
   public HashMap<String, AIMLSet> setMap;
   public HashMap<String, AIMLMap> mapMap;
   static int leafPatternCnt = 0;
   static int starPatternCnt = 0;

   public void setAllPaths(String root, String name) {
      MagicStrings.bot_path = root + "/bots";
      MagicStrings.bot_name_path = MagicStrings.bot_path + "/" + name;
      System.out.println("Name = " + name + " Path = " + MagicStrings.bot_name_path);
      MagicStrings.aiml_path = MagicStrings.bot_name_path + "/aiml";
      MagicStrings.aimlif_path = MagicStrings.bot_name_path + "/aimlif";
      MagicStrings.config_path = MagicStrings.bot_name_path + "/config";
      MagicStrings.log_path = MagicStrings.bot_name_path + "/logs";
      MagicStrings.sets_path = MagicStrings.bot_name_path + "/sets";
      MagicStrings.maps_path = MagicStrings.bot_name_path + "/maps";
      System.out.println(MagicStrings.root_path);
      System.out.println(MagicStrings.bot_path);
      System.out.println(MagicStrings.bot_name_path);
      System.out.println(MagicStrings.aiml_path);
      System.out.println(MagicStrings.aimlif_path);
      System.out.println(MagicStrings.config_path);
      System.out.println(MagicStrings.log_path);
      System.out.println(MagicStrings.sets_path);
      System.out.println(MagicStrings.maps_path);
   }

   public Bot() {
      this(MagicStrings.default_bot);
   }

   public Bot(String name) {
      this(name, MagicStrings.root_path);
   }

   public Bot(String name, String path) {
      this(name, path, "auto");
   }

   public Bot(String name, String path, String action) {
      this.properties = new Properties();
      this.name = MagicStrings.unknown_bot_name;
      this.setMap = new HashMap();
      this.mapMap = new HashMap();
      this.name = name;
      this.setAllPaths(path, name);
      this.brain = new Graphmaster(this);
      this.inputGraph = new Graphmaster(this);
      this.learnfGraph = new Graphmaster(this);
      this.deletedGraph = new Graphmaster(this);
      this.patternGraph = new Graphmaster(this);
      this.unfinishedGraph = new Graphmaster(this);
      this.suggestedCategories = new ArrayList();
      this.preProcessor = new PreProcessor(this);
      this.addProperties();
      this.addAIMLSets();
      this.addAIMLMaps();
      AIMLSet number = new AIMLSet(MagicStrings.natural_number_set_name);
      this.setMap.put(MagicStrings.natural_number_set_name, number);
      AIMLMap successor = new AIMLMap(MagicStrings.map_successor);
      this.mapMap.put(MagicStrings.map_successor, successor);
      AIMLMap predecessor = new AIMLMap(MagicStrings.map_predecessor);
      this.mapMap.put(MagicStrings.map_predecessor, predecessor);
      Date aimlDate = new Date((new File(MagicStrings.aiml_path)).lastModified());
      Date aimlIFDate = new Date((new File(MagicStrings.aimlif_path)).lastModified());
      System.out.println("AIML modified " + aimlDate + " AIMLIF modified " + aimlIFDate);
      this.readDeletedIFCategories();
      this.readUnfinishedIFCategories();
      MagicStrings.pannous_api_key = Utilities.getPannousAPIKey();
      MagicStrings.pannous_login = Utilities.getPannousLogin();
      if (action.equals("aiml2csv")) {
         this.addCategoriesFromAIML();
      } else if (action.equals("csv2aiml")) {
         this.addCategoriesFromAIMLIF();
      } else if (aimlDate.after(aimlIFDate)) {
         System.out.println("AIML modified after AIMLIF");
         this.addCategoriesFromAIML();
         this.writeAIMLIFFiles();
      } else {
         this.addCategoriesFromAIMLIF();
         if (this.brain.getCategories().size() == 0) {
            System.out.println("No AIMLIF Files found.  Looking for AIML");
            this.addCategoriesFromAIML();
         }
      }

      System.out.println("--> Bot " + name + " " + this.brain.getCategories().size() + " completed " + this.deletedGraph.getCategories().size() + " deleted " + this.unfinishedGraph.getCategories().size() + " unfinished");
   }

   void addMoreCategories(String file, ArrayList<Category> moreCategories) {
      Iterator i$;
      Category c;
      if (file.contains(MagicStrings.deleted_aiml_file)) {
         i$ = moreCategories.iterator();

         while(i$.hasNext()) {
            c = (Category)i$.next();
            this.deletedGraph.addCategory(c);
         }
      } else if (file.contains(MagicStrings.unfinished_aiml_file)) {
         i$ = moreCategories.iterator();

         while(i$.hasNext()) {
            c = (Category)i$.next();
            if (this.brain.findNode(c) == null) {
               this.unfinishedGraph.addCategory(c);
            } else {
               System.out.println("unfinished " + c.inputThatTopic() + " found in brain");
            }
         }
      } else if (file.contains(MagicStrings.learnf_aiml_file)) {
         System.out.println("Reading Learnf file");
         i$ = moreCategories.iterator();

         while(i$.hasNext()) {
            c = (Category)i$.next();
            this.brain.addCategory(c);
            this.learnfGraph.addCategory(c);
            this.patternGraph.addCategory(c);
         }
      } else {
         i$ = moreCategories.iterator();

         while(i$.hasNext()) {
            c = (Category)i$.next();
            this.brain.addCategory(c);
            this.patternGraph.addCategory(c);
         }
      }

   }

   void addCategoriesFromAIML() {
      Timer timer = new Timer();
      timer.start();

      try {
         File folder = new File(MagicStrings.aiml_path);
         if (folder.exists()) {
            File[] listOfFiles = folder.listFiles();
            System.out.println("Loading AIML files from " + MagicStrings.aiml_path);
            File[] arr$ = listOfFiles;
            int len$ = listOfFiles.length;

            for(int i$ = 0; i$ < len$; ++i$) {
               File listOfFile = arr$[i$];
               if (listOfFile.isFile()) {
                  String file = listOfFile.getName();
                  if (file.endsWith(".aiml") || file.endsWith(".AIML")) {
                     System.out.println(file);

                     try {
                        ArrayList<Category> moreCategories = AIMLProcessor.AIMLToCategories(MagicStrings.aiml_path, file);
                        this.addMoreCategories(file, moreCategories);
                     } catch (Exception var10) {
                        System.out.println("Problem loading " + file);
                        var10.printStackTrace();
                     }
                  }
               }
            }
         } else {
            System.out.println("addCategories: " + MagicStrings.aiml_path + " does not exist.");
         }
      } catch (Exception var11) {
         var11.printStackTrace();
      }

      System.out.println("Loaded " + this.brain.getCategories().size() + " categories in " + timer.elapsedTimeSecs() + " sec");
   }

   void addCategoriesFromAIMLIF() {
      Timer timer = new Timer();
      timer.start();

      try {
         File folder = new File(MagicStrings.aimlif_path);
         if (folder.exists()) {
            File[] listOfFiles = folder.listFiles();
            System.out.println("Loading AIML files from " + MagicStrings.aimlif_path);
            File[] arr$ = listOfFiles;
            int len$ = listOfFiles.length;

            for(int i$ = 0; i$ < len$; ++i$) {
               File listOfFile = arr$[i$];
               if (listOfFile.isFile()) {
                  String file = listOfFile.getName();
                  if (file.endsWith(MagicStrings.aimlif_file_suffix) || file.endsWith(MagicStrings.aimlif_file_suffix.toUpperCase())) {
                     try {
                        ArrayList<Category> moreCategories = this.readIFCategories(MagicStrings.aimlif_path + "/" + file);
                        this.addMoreCategories(file, moreCategories);
                     } catch (Exception var10) {
                        System.out.println("Problem loading " + file);
                        var10.printStackTrace();
                     }
                  }
               }
            }
         } else {
            System.out.println("addCategories: " + MagicStrings.aimlif_path + " does not exist.");
         }
      } catch (Exception var11) {
         var11.printStackTrace();
      }

      System.out.println("Loaded " + this.brain.getCategories().size() + " categories in " + timer.elapsedTimeSecs() + " sec");
   }

   public void readDeletedIFCategories() {
      this.readCertainIFCategories(this.deletedGraph, MagicStrings.deleted_aiml_file);
   }

   public void readUnfinishedIFCategories() {
      this.readCertainIFCategories(this.unfinishedGraph, MagicStrings.unfinished_aiml_file);
   }

   public void updateUnfinishedCategories() {
      ArrayList<Category> unfinished = this.unfinishedGraph.getCategories();
      this.unfinishedGraph = new Graphmaster(this);
      Iterator i$ = unfinished.iterator();

      while(i$.hasNext()) {
         Category c = (Category)i$.next();
         if (!this.brain.existsCategory(c)) {
            this.unfinishedGraph.addCategory(c);
         }
      }

   }

   public void writeQuit() {
      this.writeAIMLIFFiles();
      System.out.println("Wrote AIMLIF Files");
      this.writeAIMLFiles();
      System.out.println("Wrote AIML Files");
      this.writeDeletedIFCategories();
      this.updateUnfinishedCategories();
      this.writeUnfinishedIFCategories();
   }

   public void readCertainIFCategories(Graphmaster graph, String fileName) {
      File file = new File(MagicStrings.aimlif_path + "/" + fileName + MagicStrings.aimlif_file_suffix);
      if (file.exists()) {
         try {
            ArrayList<Category> deletedCategories = this.readIFCategories(MagicStrings.aimlif_path + "/" + fileName + MagicStrings.aimlif_file_suffix);
            Iterator i$ = deletedCategories.iterator();

            while(i$.hasNext()) {
               Category d = (Category)i$.next();
               graph.addCategory(d);
            }

            System.out.println("readCertainIFCategories " + graph.getCategories().size() + " categories from " + fileName + MagicStrings.aimlif_file_suffix);
         } catch (Exception var7) {
            System.out.println("Problem loading " + fileName);
            var7.printStackTrace();
         }
      } else {
         System.out.println("No " + MagicStrings.deleted_aiml_file + MagicStrings.aimlif_file_suffix + " file found");
      }

   }

   public void writeCertainIFCategories(Graphmaster graph, String file) {
      if (MagicBooleans.trace_mode) {
         System.out.println("writeCertainIFCaegories " + file + " size= " + graph.getCategories().size());
      }

      this.writeIFCategories(graph.getCategories(), file + MagicStrings.aimlif_file_suffix);
      File dir = new File(MagicStrings.aimlif_path);
      dir.setLastModified((new Date()).getTime());
   }

   public void writeDeletedIFCategories() {
      this.writeCertainIFCategories(this.deletedGraph, MagicStrings.deleted_aiml_file);
   }

   public void writeLearnfIFCategories() {
      this.writeCertainIFCategories(this.learnfGraph, MagicStrings.learnf_aiml_file);
   }

   public void writeUnfinishedIFCategories() {
      this.writeCertainIFCategories(this.unfinishedGraph, MagicStrings.unfinished_aiml_file);
   }

   public void writeIFCategories(ArrayList<Category> cats, String filename) {
      BufferedWriter bw = null;
      File existsPath = new File(MagicStrings.aimlif_path);
      if (existsPath.exists()) {
         try {
            bw = new BufferedWriter(new FileWriter(MagicStrings.aimlif_path + "/" + filename));
            Iterator i$ = cats.iterator();

            while(i$.hasNext()) {
               Category category = (Category)i$.next();
               bw.write(Category.categoryToIF(category));
               bw.newLine();
            }
         } catch (FileNotFoundException var17) {
            var17.printStackTrace();
         } catch (IOException var18) {
            var18.printStackTrace();
         } finally {
            try {
               if (bw != null) {
                  bw.flush();
                  bw.close();
               }
            } catch (IOException var16) {
               var16.printStackTrace();
            }

         }
      }

   }

   public void writeAIMLIFFiles() {
      System.out.println("writeAIMLIFFiles");
      HashMap<String, BufferedWriter> fileMap = new HashMap();
      if (this.deletedGraph.getCategories().size() > 0) {
         this.writeDeletedIFCategories();
      }

      ArrayList<Category> brainCategories = this.brain.getCategories();
      Collections.sort(brainCategories, Category.CATEGORY_NUMBER_COMPARATOR);
      Iterator i$ = brainCategories.iterator();

      while(i$.hasNext()) {
         Category c = (Category)i$.next();

         try {
            String fileName = c.getFilename();
            BufferedWriter bw;
            if (fileMap.containsKey(fileName)) {
               bw = (BufferedWriter)fileMap.get(fileName);
            } else {
               bw = new BufferedWriter(new FileWriter(MagicStrings.aimlif_path + "/" + fileName + MagicStrings.aimlif_file_suffix));
               fileMap.put(fileName, bw);
            }

            bw.write(Category.categoryToIF(c));
            bw.newLine();
         } catch (Exception var9) {
            var9.printStackTrace();
         }
      }

      Set set = fileMap.keySet();
      i$ = set.iterator();

      while(i$.hasNext()) {
         Object aSet = i$.next();
         BufferedWriter bw = (BufferedWriter)fileMap.get(aSet);

         try {
            if (bw != null) {
               bw.flush();
               bw.close();
            }
         } catch (IOException var8) {
            var8.printStackTrace();
         }
      }

      File dir = new File(MagicStrings.aimlif_path);
      dir.setLastModified((new Date()).getTime());
   }

   public void writeAIMLFiles() {
      HashMap<String, BufferedWriter> fileMap = new HashMap();
      Category b = new Category(0, "BUILD", "*", "*", (new Date()).toString(), "update.aiml");
      this.brain.addCategory(b);
      b = new Category(0, "DELEVLOPMENT ENVIRONMENT", "*", "*", MagicStrings.programNameVersion, "update.aiml");
      this.brain.addCategory(b);
      ArrayList<Category> brainCategories = this.brain.getCategories();
      Collections.sort(brainCategories, Category.CATEGORY_NUMBER_COMPARATOR);
      Iterator i$ = brainCategories.iterator();

      while(i$.hasNext()) {
         Category c = (Category)i$.next();
         if (!c.getFilename().equals(MagicStrings.null_aiml_file)) {
            try {
               String fileName = c.getFilename();
               BufferedWriter bw;
               if (fileMap.containsKey(fileName)) {
                  bw = (BufferedWriter)fileMap.get(fileName);
               } else {
                  String copyright = Utilities.getCopyright(this, fileName);
                  bw = new BufferedWriter(new FileWriter(MagicStrings.aiml_path + "/" + fileName));
                  fileMap.put(fileName, bw);
                  bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<aiml>\n");
                  bw.write(copyright);
               }

               bw.write(Category.categoryToAIML(c) + "\n");
            } catch (Exception var10) {
               var10.printStackTrace();
            }
         }
      }

      Set set = fileMap.keySet();
      i$ = set.iterator();

      while(i$.hasNext()) {
         Object aSet = i$.next();
         BufferedWriter bw = (BufferedWriter)fileMap.get(aSet);

         try {
            if (bw != null) {
               bw.write("</aiml>\n");
               bw.flush();
               bw.close();
            }
         } catch (IOException var9) {
            var9.printStackTrace();
         }
      }

      File dir = new File(MagicStrings.aiml_path);
      dir.setLastModified((new Date()).getTime());
   }

   void addProperties() {
      try {
         this.properties.getProperties(MagicStrings.config_path + "/properties.txt");
      } catch (Exception var2) {
         var2.printStackTrace();
      }

   }

   public void findPatterns() {
      this.findPatterns(this.inputGraph.root, "");
      System.out.println(leafPatternCnt + " Leaf Patterns " + starPatternCnt + " Star Patterns");
   }

   void findPatterns(Nodemapper node, String partialPatternThatTopic) {
      if (NodemapperOperator.isLeaf(node) && node.category.getActivationCnt() > MagicNumbers.node_activation_cnt) {
         ++leafPatternCnt;

         try {
            String categoryPatternThatTopic = "";
            if (node.shortCut) {
               categoryPatternThatTopic = partialPatternThatTopic + " <THAT> * <TOPIC> *";
            } else {
               categoryPatternThatTopic = partialPatternThatTopic;
            }

            Category c = new Category(0, categoryPatternThatTopic, MagicStrings.blank_template, MagicStrings.unknown_aiml_file);
            if (!this.brain.existsCategory(c) && !this.deletedGraph.existsCategory(c) && !this.unfinishedGraph.existsCategory(c)) {
               this.patternGraph.addCategory(c);
               this.suggestedCategories.add(c);
            }
         } catch (Exception var7) {
            var7.printStackTrace();
         }
      }

      if (NodemapperOperator.size(node) > MagicNumbers.node_size) {
         ++starPatternCnt;

         try {
            Category c = new Category(0, partialPatternThatTopic + " * <THAT> * <TOPIC> *", MagicStrings.blank_template, MagicStrings.unknown_aiml_file);
            if (!this.brain.existsCategory(c) && !this.deletedGraph.existsCategory(c) && !this.unfinishedGraph.existsCategory(c)) {
               this.patternGraph.addCategory(c);
               this.suggestedCategories.add(c);
            }
         } catch (Exception var6) {
            var6.printStackTrace();
         }
      }

      Iterator i$ = NodemapperOperator.keySet(node).iterator();

      while(i$.hasNext()) {
         String key = (String)i$.next();
         Nodemapper value = NodemapperOperator.get(node, key);
         this.findPatterns(value, partialPatternThatTopic + " " + key);
      }

   }

   public void classifyInputs(String filename) {
      try {
         FileInputStream fstream = new FileInputStream(filename);
         BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

         String strLine;
         for(int var5 = 0; (strLine = br.readLine()) != null; ++var5) {
            if (strLine.startsWith("Human: ")) {
               strLine = strLine.substring("Human: ".length(), strLine.length());
            }

            Nodemapper match = this.patternGraph.match(strLine, "unknown", "unknown");
            match.category.incrementActivationCnt();
         }

         br.close();
      } catch (Exception var7) {
         System.err.println("Error: " + var7.getMessage());
      }

   }

   public void graphInputs(String filename) {
      try {
         FileInputStream fstream = new FileInputStream(filename);
         BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

         String strLine;
         while((strLine = br.readLine()) != null) {
            Category c = new Category(0, strLine, "*", "*", "nothing", MagicStrings.unknown_aiml_file);
            Nodemapper node = this.inputGraph.findNode(c);
            if (node == null) {
               this.inputGraph.addCategory(c);
               c.incrementActivationCnt();
            } else {
               node.category.incrementActivationCnt();
            }
         }

         br.close();
      } catch (Exception var7) {
         System.err.println("Error: " + var7.getMessage());
      }

   }

   public ArrayList<Category> readIFCategories(String filename) {
      ArrayList categories = new ArrayList();

      try {
         FileInputStream fstream = new FileInputStream(filename);
         BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

         String strLine;
         while((strLine = br.readLine()) != null) {
            try {
               Category c = Category.IFToCategory(strLine);
               categories.add(c);
            } catch (Exception var7) {
               System.out.println("Invalid AIMLIF in " + filename + " line " + strLine);
            }
         }

         br.close();
      } catch (Exception var8) {
         System.err.println("Error: " + var8.getMessage());
      }

      return categories;
   }

   public void shadowChecker() {
      this.shadowChecker(this.brain.root);
   }

   void shadowChecker(Nodemapper node) {
      String that;
      if (NodemapperOperator.isLeaf(node)) {
         String input = node.category.getPattern().replace("*", "XXX").replace("_", "XXX");
         that = node.category.getThat().replace("*", "XXX").replace("_", "XXX");
         String topic = node.category.getTopic().replace("*", "XXX").replace("_", "XXX");
         Nodemapper match = this.brain.match(input, that, topic);
         if (match != node) {
            System.out.println("" + Graphmaster.inputThatTopic(input, that, topic));
            System.out.println("MATCHED:     " + match.category.inputThatTopic());
            System.out.println("SHOULD MATCH:" + node.category.inputThatTopic());
         }
      } else {
         Iterator i$ = NodemapperOperator.keySet(node).iterator();

         while(i$.hasNext()) {
            that = (String)i$.next();
            this.shadowChecker(NodemapperOperator.get(node, that));
         }
      }

   }

   void addAIMLSets() {
      Timer timer = new Timer();
      timer.start();

      try {
         File folder = new File(MagicStrings.sets_path);
         if (folder.exists()) {
            File[] listOfFiles = folder.listFiles();
            System.out.println("Loading AIML Sets files from " + MagicStrings.sets_path);
            File[] arr$ = listOfFiles;
            int len$ = listOfFiles.length;

            for(int i$ = 0; i$ < len$; ++i$) {
               File listOfFile = arr$[i$];
               if (listOfFile.isFile()) {
                  String file = listOfFile.getName();
                  if (file.endsWith(".txt") || file.endsWith(".TXT")) {
                     System.out.println(file);
                     String setName = file.substring(0, file.length() - ".txt".length());
                     System.out.println("Read AIML Set " + setName);
                     AIMLSet aimlSet = new AIMLSet(setName);
                     aimlSet.readAIMLSet(this);
                     this.setMap.put(setName, aimlSet);
                  }
               }
            }
         } else {
            System.out.println("addAIMLSets: " + MagicStrings.sets_path + " does not exist.");
         }
      } catch (Exception var11) {
         var11.printStackTrace();
      }

   }

   void addAIMLMaps() {
      Timer timer = new Timer();
      timer.start();

      try {
         File folder = new File(MagicStrings.maps_path);
         if (folder.exists()) {
            File[] listOfFiles = folder.listFiles();
            System.out.println("Loading AIML Map files from " + MagicStrings.maps_path);
            File[] arr$ = listOfFiles;
            int len$ = listOfFiles.length;

            for(int i$ = 0; i$ < len$; ++i$) {
               File listOfFile = arr$[i$];
               if (listOfFile.isFile()) {
                  String file = listOfFile.getName();
                  if (file.endsWith(".txt") || file.endsWith(".TXT")) {
                     System.out.println(file);
                     String mapName = file.substring(0, file.length() - ".txt".length());
                     System.out.println("Read AIML Map " + mapName);
                     AIMLMap aimlMap = new AIMLMap(mapName);
                     aimlMap.readAIMLMap(this);
                     this.mapMap.put(mapName, aimlMap);
                  }
               }
            }
         } else {
            System.out.println("addCategories: " + MagicStrings.aiml_path + " does not exist.");
         }
      } catch (Exception var11) {
         var11.printStackTrace();
      }

   }
}
