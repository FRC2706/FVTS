package ca.team2706.vision.vision2019;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Logger {

	private static File LOG_FOLDER = new File("logs/");

	private static int cutoff = 400;

	private static int currSize = 0;
	private static int logFileNum = 0;

	public static void init() {

		if (!LOG_FOLDER.exists()) {
			LOG_FOLDER.mkdirs();
		}

		int highestVal = -1;

		for (File f : LOG_FOLDER.listFiles()) {

			if (f.getName().endsWith(".log")) {

				int name = Integer.valueOf(f.getName().substring(0, f.getName().indexOf(".log")));

				if (name > highestVal) {
					highestVal = name;
				}

			}

		}

		logFileNum = highestVal + 1;

	}

	private static void log(String log) {

		try {

			File logFile = new File(LOG_FOLDER, logFileNum + ".log");

			if(!logFile.exists()) {
				
				logFile.getParentFile().mkdirs();
				logFile.createNewFile();
				
			}
			
			PrintWriter out = new PrintWriter(new FileWriter(logFile, true));

			out.println(log);

			out.close();

			currSize++;

			if (currSize > cutoff) {

				logFileNum++;

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(log);

	}

	public static void i(String log) {

		log("Info: " + log);

	}

	public static void w(String log) {

		log("Warn: " + log);

	}

	public static void e(String log) {

		log("Error: " + log);

	}

	public static void d(String log) {

		log("Debug: " + log);

	}

}
