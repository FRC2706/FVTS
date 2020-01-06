package ca.team2706.vision.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

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
import ca.team2706.vision.core.VisionData.Target;
import ca.team2706.vision.core.params.Attribute;
import ca.team2706.vision.core.params.VisionParams;
import ca.team2706.vision.vision2019.Main;

public class DataViewer {
	public static void main(String[] args) throws Exception{
		System.out.println("Vision2019 DataViewer "+Constants.VERSION_STRING+" developed by "+Constants.AUTHOR);
		
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
		
		Main.data = new ArrayList<VisionData>();
		Main.lock = new ReentrantLock();
		
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
				
				Log.i(params.getByName("name").getValue()+" enabled: "+enabled,true);
				
				MainThread thread = new MainThread(params,false);
				if (enabled) {
					thread.start();
				}
				Main.threads.add(thread);
			} catch (Exception e) {
				Log.e(e.getMessage(), true);
			}
		} // end main vision startup loop
		while(true) {
			if(Main.data.size() > 0) {
				Main.lock.lock();
				VisionData data = Main.data.get(0);
				Main.data.remove(0);
				Main.lock.unlock();
				System.out.println("Data ("+data.params.getByName("name")+"):");
				System.out.println("FPS: "+data.fps);
				System.out.println("Number of targets found: "+data.targetsFound.size());
				System.out.println("Preffered target found: "+(data.preferredTarget != null));
				if(data.preferredTarget != null) {
					System.out.println("Preffered target: ");
					System.out.println("Distance: "+data.preferredTarget.distance);
					System.out.println("X-Centre: "+data.preferredTarget.xCentreNorm);
					System.out.println("Y-Centre: "+data.preferredTarget.yCentreNorm);
					System.out.println("Area: "+data.preferredTarget.areaNorm);
				}
				for(int i = 0; i < data.targetsFound.size(); i++) {
					Target t = data.targetsFound.get(i);
					System.out.println("Target #"+(i+1)+": ");
					System.out.println("Distance: "+t.distance);
					System.out.println("X-Centre: "+t.xCentreNorm);
					System.out.println("Y-Centre: "+t.yCentreNorm);
					System.out.println("Area: "+t.areaNorm);
				}
				data = null;
			}
		} // end data collection loop
		
	}
}
