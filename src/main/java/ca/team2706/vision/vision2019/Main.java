package ca.team2706.vision.vision2019;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import ca.team2706.vision.core.ConfigParser;
import ca.team2706.vision.core.Constants;
import ca.team2706.vision.core.ImageDumpScheduler;
import ca.team2706.vision.core.Log;
import ca.team2706.vision.core.MainThread;
import ca.team2706.vision.core.NetworkTablesManager;
import ca.team2706.vision.core.Utils;
import ca.team2706.vision.core.VisionCameraServer;
import ca.team2706.vision.core.VisionData;
import ca.team2706.vision.core.params.Attribute;
import ca.team2706.vision.core.params.AttributeOptions;
import ca.team2706.vision.core.params.VisionParams;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Main {

	public static String filename = "";
	public static int timestamp = 0;
	public static File timestampfile;
	public static NetworkTable loggingTable;
	public static File visionParamsFile;
	public static boolean developmentMode = false;
	public static List<AttributeOptions> options;
	public static List<VisionData> data;
	public static Lock lock;

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

	/**
	 * Initilizes the Network Tables WARNING! Change 127.0.0.1 to the robot ip
	 * before it is on master or it will not be fun :)
	 */
	public static void initNetworkTables(String ip) {

		// Tells the NetworkTable class that this is a client
		NetworkTable.setClientMode();
		// Sets the interval for updating NetworkTables
		NetworkTable.setUpdateRate(0.02);
		// Sets the vision table to the "vision" table that is in NetworkTables
		loggingTable = NetworkTable.getTable("logging-level");

		boolean use_GUI = true;

		// If on Linux don't use guis
		if (System.getProperty("os.arch").toLowerCase().indexOf("arm") != -1) {
			use_GUI = false;
		}

		if (!use_GUI && ip.equals("")) {

			// Sets the team number
			NetworkTable.setTeam(2706); // Use this for the robit
			// Enables DSClient
			NetworkTable.setDSClientEnabled(true); // and this for the robit

		} else {

			if (ip.equals("")) {
				ip = "localhost";
			}

			// Sets the IP adress to connect to
			NetworkTable.setIPAddress(ip); // Use this for testing

		}

		// Initilizes NetworkTables
		NetworkTable.initialize();
	}

	public static void sendVisionParams() {

		for (VisionParams params : visionParamsList) {

			for (Attribute a : params.getAttribs()) {
				if (a.getName().equals("name")) {
					NetworkTable visionTable = NetworkTablesManager.tables.get(params.getByName("name").getValue());
					visionTable.putString(a.getName(), a.getValue());
				}
			}
		}
	}

	/**
	 * Turns all the vision data into packets that kno da wae to get to the robo rio
	 * :]
	 *
	 * @param visionData
	 */
	public static void sendVisionDataOverNetworkTables(VisionData visionData) {
		NetworkTable visionTable = NetworkTablesManager.tables.get(visionData.params.getByName("name").getValue());
		// Sends the data
		// Puts the fps into the vision table
		visionTable.putNumber("fps", visionData.fps);
		// Puts the number of targets found into the vision table
		visionTable.putNumber("numTargetsFound", visionData.targetsFound.size());

		// If there is a target
		if (visionData.preferredTarget != null) {
			// Put the normalized x into the vision table
			visionTable.putNumber("ctrX", visionData.preferredTarget.xCentreNorm);
			// Puts the normalized area into the vision table
			visionTable.putNumber("area", visionData.preferredTarget.areaNorm);

			visionTable.putNumber("angle", visionData.preferredTarget.xCentreNorm * 45);
			
			visionTable.putNumber("distance", visionData.preferredTarget.distance);
		}
	}

	public static boolean b = true;

	/**
	 * The main method! Very important Do not delete! :] :]
	 *
	 * 
	 * @param The command line arguments
	 * @throws Exception
	 */

	public static void main(String[] args) throws Exception {
		System.out.println("Vision2019 Main "+Constants.VERSION_STRING+" developed by "+Constants.AUTHOR);
		
		// Must be included!
		// Loads OpenCV
		System.loadLibrary(Constants.OPENCV_LIBRARY);

		Options options = new Options();

		Option ip = new Option("ip", true, "The IP address of the NetworkTables server");
		options.addOption(ip);
		Option developmentMode = new Option("dev", "development", false, "Puts Vision2019 in development mode");
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
			formatter.printHelp("Vision2019", options);
			System.exit(1);
		}
		Main.developmentMode = cmd.hasOption("development");

		// Connect NetworkTables, and get access to the publishing table
		initNetworkTables(cmd.getOptionValue("ip", ""));

		visionParamsFile = new File(cmd.getOptionValue("config", "visionParams.properties"));

		// read the vision calibration values from file.
		Utils.loadVisionParams();

		Map<String, String> masterConfig = ConfigParser.getProperties(new File("master.cf"), "config");

		Map<String, String> masterEnabled = ConfigParser.getProperties(new File("master.cf"), "enabled");

		// Go through and enable the configs
		for (String s : masterEnabled.keySet()) {
			for (VisionParams params : visionParamsList) {
				if (params.getByName("name").getValue().equals(s)) {
					params.putAttrib(new Attribute("enabled", masterEnabled.get(s)));
				}
			}
		}

		String allowOverride = masterConfig.get("allowOverride");

		if (allowOverride == null || allowOverride.equals("")) {

			allowOverride = "true";
		}
		// Should network tables be started so that the settings can be overridden?
		boolean allowOverrideB = Boolean.valueOf(allowOverride);

		if (allowOverrideB)
			NetworkTablesManager.init();

		ImageDumpScheduler.start();

		VisionCameraServer.startServer();

		for (VisionParams params : visionParamsList) {
			try {

				String s = params.getByName("enabled").getValue();

				if (s == null || s.equals("")) {
					s = "true";
				}
				boolean enabled = Boolean.valueOf(s);
				
				Log.i(params.getByName("name").getValue()+" enabled: "+enabled,true);
				
				MainThread thread = new MainThread(params,true);
				if (enabled) {
					thread.start();
				}
				threads.add(thread);
			} catch (Exception e) {
				Log.e(e.getMessage(), true);
			}

		} // end main vision startup loop
	}
}
