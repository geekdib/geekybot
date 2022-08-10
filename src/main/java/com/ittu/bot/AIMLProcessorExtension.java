package com.ittu.bot;
/**
 * @author @dibyapp, Name : Dibyaprakash, Email : dibyapp@crms.app
 * @Project : Ittu AI
 */
import java.util.Set;
import org.w3c.dom.Node;

public interface AIMLProcessorExtension {
   Set<String> extensionTagSet();

   String recursEval(Node var1, ParseState var2);
}
