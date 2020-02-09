package ca.team2706.fvts.main;

import java.io.File;
import java.util.Map;
import java.util.Scanner;

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
import ca.team2706.fvts.core.VisionData;
import ca.team2706.fvts.core.VisionData.Target;
import ca.team2706.fvts.core.params.Attribute;
import ca.team2706.fvts.core.params.VisionParams;
import ca.team2706.fvts.main.Main;

public class DataViewer {
	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		System.out.println("FVTS DataViewer " + Constants.VERSION_STRING + " developed by " + Constants.AUTHOR);

		// Must be included!
		// Loads OpenCV
		System.loadLibrary(Constants.OPENCV_LIBRARY);
		
		Log.silence();

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

		Main.visionParamsFile = new File(cmd.getOptionValue("config", "visionParams.properties"));

		// read the vision calibration values from file.
		Utils.loadVisionParams();

		Map<String, String> masterConfig = ConfigParser.getProperties(new File("master.cf"), "config");

		Map<String, String> masterEnabled = ConfigParser.getProperties(new File("master.cf"), "enabled");

		// Go through and enable the configs
		for (String s : masterEnabled.keySet()) {
			for (VisionParams params : Main.visionParamsList) {
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

		for (VisionParams params : Main.visionParamsList) {
			try {

				String s = params.getByName("enabled").getValue();

				if (s == null || s.equals("")) {
					s = "true";
				}
				boolean enabled = Boolean.valueOf(s);

				Log.i(params.getByName("name").getValue() + " enabled: " + enabled, true);

				MainThread thread = new MainThread(params, false);
				if (enabled) {
					thread.start();
				}
				Main.threads.add(thread);
			} catch (Exception e) {
				Log.e(e.getMessage(), true);
			}
		} // end main vision startup loop
		Scanner in = new Scanner(System.in);
		System.out.print("Constant or triggered? C/T: ");
		boolean constant = in.nextLine().equalsIgnoreCase("C");
		if (constant) {
			while (true) {
				for(MainThread thread : Main.threads) {
					if(thread.lock == null)
						continue;
					thread.lock.lock();
					VisionData data = thread.lastFrame;
					thread.lastFrame = null;
					thread.lock.unlock();
					System.out.println("Data (" + data.params.getByName("name").getValue() + "):");
					System.out.println("FPS: " + data.fps);
					System.out.println("Number of targets found: " + data.targetsFound.size());
					System.out.println("Preffered target found: " + (data.preferredTarget != null));
					if (data.preferredTarget != null) {
						System.out.println("Preffered target: ");
						System.out.println("Distance: " + data.preferredTarget.distance);
						System.out.println("X-Centre: " + data.preferredTarget.xCentreNorm);
						System.out.println("Y-Centre: " + data.preferredTarget.yCentreNorm);
						System.out.println("Area: " + data.preferredTarget.areaNorm);
					}
					for (int i = 0; i < data.targetsFound.size(); i++) {
						Target t = data.targetsFound.get(i);
						System.out.println("Target #" + (i + 1) + ": ");
						System.out.println("Distance: " + t.distance);
						System.out.println("X-Centre: " + t.xCentreNorm);
						System.out.println("Y-Centre: " + t.yCentreNorm);
						System.out.println("Area: " + t.areaNorm);
					}
					data = null;
				}
			} // end data collection loop
		}else {
			System.out.println("Hit enter to trigger a capture!");
			while(true) {
				in.nextLine();
				for(MainThread thread : Main.threads) {
					if(thread.lock == null)
						continue;
					thread.lock.lock();
					if(thread.lastFrame == null)
						continue;
					VisionData data = thread.lastFrame;
					thread.lastFrame = null;
					thread.lock.unlock();
					System.out.println("Data (" + data.params.getByName("name").getValue() + "):");
					System.out.println("FPS: " + data.fps);
					System.out.println("Number of targets found: " + data.targetsFound.size());
					System.out.println("Preffered target found: " + (data.preferredTarget != null));
					if (data.preferredTarget != null) {
						System.out.println("Preffered target: ");
						System.out.println("Distance: " + data.preferredTarget.distance);
						System.out.println("X-Centre: " + data.preferredTarget.xCentreNorm);
						System.out.println("Y-Centre: " + data.preferredTarget.yCentreNorm);
						System.out.println("Area: " + data.preferredTarget.areaNorm);
					}
					for (int i = 0; i < data.targetsFound.size(); i++) {
						Target t = data.targetsFound.get(i);
						System.out.println("Target #" + (i + 1) + ": ");
						System.out.println("Distance: " + t.distance);
						System.out.println("X-Centre: " + t.xCentreNorm);
						System.out.println("Y-Centre: " + t.yCentreNorm);
						System.out.println("Area: " + t.areaNorm);
					}
					data = null;
				}
			}
		}
	}
}
