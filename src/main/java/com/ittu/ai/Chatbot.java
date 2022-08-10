package com.ittu.ai;

import java.io.File;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ittu.bot.Bot;
import com.ittu.bot.Chat;
import com.ittu.bot.History;
import com.ittu.bot.MagicBooleans;
import com.ittu.bot.MagicStrings;

@RestController
@RequestMapping("/ittu")
public class Chatbot {
	private static final boolean TRACE_MODE = false;
	static String botName = "Ittu AI";

	@RequestMapping(value="/ask")
	public String askIttu(@RequestParam("q") String question) {
		System.out.println("Chatbot.askIttu()");
		try {

			String resourcesPath = getResourcesPath();
			System.out.println(resourcesPath);
			MagicBooleans.trace_mode = TRACE_MODE;
			Bot bot = new Bot("ittu", resourcesPath);
			Chat chatSession = new Chat(bot);
			bot.brain.nodeStats();
			String textLine = "";

			while(true) {
				System.out.print("Human : ");
//				textLine = IOUtils.readInputTextLine();
				
				textLine = question;
				
				if ((textLine == null) || (textLine.length() < 1))
					textLine = MagicStrings.null_input;
				if (textLine.equals("q")) {
					System.exit(0);
				} else if (textLine.equals("wq")) {
					bot.writeQuit();
					System.exit(0);
				} else {
					String request = textLine;
					if (MagicBooleans.trace_mode)
						System.out.println("STATE=" + request + ":THAT=" + ((History) chatSession.thatHistory.get(0)).get(0) + ":TOPIC=" + chatSession.predicates.get("topic"));
					String response = chatSession.multisentenceRespond(request);
					while (response.contains("&lt;"))
						response = response.replace("&lt;", "<");
					while (response.contains("&gt;"))
						response = response.replace("&gt;", ">");
					return response;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "Sorry, I'm unable to answer this";
	}

	private static String getResourcesPath() {
		File currDir = new File(".");
		String path = currDir.getAbsolutePath();
		path = path.substring(0, path.length() - 2);
		System.out.println(path);
		String resourcesPath = path + File.separator + "src" + File.separator + "main" + File.separator + "resources";
		return resourcesPath;
	}

}
