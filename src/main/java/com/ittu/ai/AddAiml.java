package com.ittu.ai;

import java.io.File;

import com.ittu.bot.Bot;
import com.ittu.bot.MagicBooleans;

public class AddAiml {

	private static final boolean TRACE_MODE = false;
	static String botName = "ittu";

	public static void createAIML() {
		try {

			String resourcesPath = getResourcesPath();
			System.out.println(resourcesPath);
			MagicBooleans.trace_mode = TRACE_MODE;
			Bot bot = new Bot("ittu", resourcesPath);
			
			bot.writeAIMLFiles();


		} catch (Exception e) {
			e.printStackTrace();
		}
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
