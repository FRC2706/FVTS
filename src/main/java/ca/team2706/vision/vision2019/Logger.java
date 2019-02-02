package ca.team2706.vision.vision2019;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Logger {
	
	private static void log(String log,String folder) {

		try {

			File logFile = new File(folder,"log.log");

			if(!logFile.exists()) {
				
				logFile.getParentFile().mkdirs();
				logFile.createNewFile();
				
			}
			
			PrintWriter out = new PrintWriter(new FileWriter(logFile, true));

			out.println(log);

			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(log);

	}

	public static void i(String log, String folder) {

		log("Info: " + log,folder);

	}

	public static void w(String log, String folder) {

		log("Warn: " + log,folder);

	}

	public static void e(String log, String folder) {

		log("Error: " + log,folder);

	}

	public static void d(String log, String folder) {

		log("Debug: " + log,folder);

	}

}
