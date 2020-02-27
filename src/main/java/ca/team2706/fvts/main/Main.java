package ca.team2706.fvts.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import ca.team2706.fvts.core.ConfigParser;
import ca.team2706.fvts.core.Constants;
import ca.team2706.fvts.core.ImageDumpScheduler;
import ca.team2706.fvts.core.Log;
import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.NetworkTablesManager;
import ca.team2706.fvts.core.Utils;
import ca.team2706.fvts.core.VisionCameraServer;
import ca.team2706.fvts.core.params.Attribute;
import ca.team2706.fvts.core.params.VisionParams;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Main {

	public static final File MASTER_CONFIG_FILE = new File("master.cf");

	public static String filename = "";
	public static int timestamp = 0;
	public static File timestampfile;
	public static NetworkTable loggingTable;
	public static File visionParamsFile;
	public static boolean developmentMode = false;
	public static int runID;
	public static String serverIp = "";

	public static List<MainThread> threads = new ArrayList<MainThread>();

	public static void reloadConfig() {
		visionParamsList.clear();
		Utils.loadVisionParams();
		for (MainThread thread : threads) {
			String name = thread.visionParams.getByName("name").getValue();
			boolean found = false;
			for (VisionParams params : visionParamsList) {
				if (!found) {
					if (params.getByName("name").getValue().equals(name)) {
						thread.updateParams(params);
						found = true;
					}
				}

			}

		}
	}

	// Camera Type (set in visionParams.properties)
	// Set to 1 for USB camera, set to 0 for webcam, I think 0 is USB if
	// there is no webcam :/
	/** The vision parameters, this is used by the vision pipeline **/
	public static List<VisionParams> visionParamsList = new ArrayList<VisionParams>();

	public static boolean b = true;

	/**
	 * The main method! Very important Do not delete! :] :]
	 *
	 * 
	 * @param The command line arguments
	 * @throws Exception
	 */

	public static void main(String[] args) throws Exception {
		System.out.println("FVTS Main " + Constants.VERSION_STRING + " developed by " + Constants.AUTHOR);

		// Must be included!
		// Loads OpenCV
		System.loadLibrary(Constants.OPENCV_LIBRARY);

		Options options = new Options();

		Option ip = new Option("ip", true, "The IP address of the NetworkTables server");
		options.addOption(ip);
		Option developmentMode = new Option("dev", "development", false, "Puts FVTS in development mode");
		options.addOption(developmentMode);
		Option configFile = new Option("conf", "config", true, "Specifies an alternative config file");
		options.addOption(configFile);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (Exception e) {
			Log.e(e.getMessage(), true);
			formatter.printHelp("FVTS", options);
			System.exit(1);
		}
		Main.developmentMode = cmd.hasOption("development");

		// Connect NetworkTables, and get access to the publishing table
		serverIp = cmd.getOptionValue("ip", "");

		visionParamsFile = new File(cmd.getOptionValue("config", "visionParams.properties"));

		// read the vision calibration values from file.
		visionParamsList = Utils.loadVisionParams();

		Map<String, String> masterConfig = ConfigParser.getPropertiesM(MASTER_CONFIG_FILE, "config");

		Map<String, String> masterEnabled = ConfigParser.getPropertiesM(MASTER_CONFIG_FILE, "enabled");

		// Go through and enable the configs
		for (String s : masterEnabled.keySet()) {
			for (VisionParams params : visionParamsList) {
				if (params.getByName("name").getValue().equals(s)) {
					params.putAttrib(new Attribute("enabled", masterEnabled.get(s)));
				}
			}
		}
		runID = Utils.findFirstAvailable(masterConfig.get("logFile"));
		CLI.logFile = new File(masterConfig.get("logFile").replaceAll("\\$1", "" + runID));

		String allowOverride = masterConfig.get("allowOverride");

		if (allowOverride == null || allowOverride.equals("")) {

			allowOverride = "true";
		}

		ImageDumpScheduler.start();

		VisionCameraServer.startServer();

		for (VisionParams params : visionParamsList) {
			try {

				String s = params.getByName("enabled").getValue();

				if (s == null || s.equals("")) {
					s = "true";
				}
				boolean enabled = Boolean.valueOf(s);

				Log.i(params.getByName("name").getValue() + " enabled: " + enabled, true);

				MainThread thread = new MainThread(params);
				if (enabled) {
					thread.start();
				}
				threads.add(thread);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(e.getMessage(), true);
			}

		} // end main vision startup loop
			// Should network tables be started so that the settings can be overridden?
		boolean allowOverrideB = Boolean.valueOf(allowOverride);

		if (allowOverrideB)
			NetworkTablesManager.init();
	}
}
