package ca.team2706.vision.vision2019;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import ca.team2706.vision.core.ConfigParser;
import ca.team2706.vision.core.ImageDumpScheduler;
import ca.team2706.vision.core.Log;
import ca.team2706.vision.core.MainThread;
import ca.team2706.vision.core.NetworkTablesManager;
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
	private static File visionParamsFile;
	public static boolean developmentMode = false;
	public static List<AttributeOptions> options;

	public static List<MainThread> threads = new ArrayList<MainThread>();

	public static void reloadConfig() {
		visionParamsList.clear();
		loadVisionParams();
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

	/**
	 * Loads the visionTable params! :]
	 **/

	public static void loadVisionParams() {
		try {
			AttributeOptions name = new AttributeOptions("name", true);

			AttributeOptions minHue = new AttributeOptions("minHue", true);
			AttributeOptions maxHue = new AttributeOptions("maxHue", true);
			AttributeOptions minSat = new AttributeOptions("minSaturation", true);
			AttributeOptions maxSat = new AttributeOptions("maxSaturation", true);
			AttributeOptions minVal = new AttributeOptions("minValue", true);
			AttributeOptions maxVal = new AttributeOptions("maxValue", true);

			AttributeOptions aspectRatioThresh = new AttributeOptions("aspectRatioThresh", true);

			AttributeOptions distToCentreImportance = new AttributeOptions("distToCentreImportance", true);

			AttributeOptions imageFile = new AttributeOptions("imageFile", true);

			AttributeOptions minArea = new AttributeOptions("minArea", true);

			AttributeOptions erodeDilateIterations = new AttributeOptions("erodeDilateIterations", true);

			AttributeOptions resolution = new AttributeOptions("resolution", true);

			AttributeOptions imgDumpPath = new AttributeOptions("imgDumpPath", true);

			AttributeOptions imgDumpTime = new AttributeOptions("imgDumpTime", true);

			AttributeOptions slope = new AttributeOptions("slope", true);

			AttributeOptions yIntercept = new AttributeOptions("yIntercept", true);

			AttributeOptions group = new AttributeOptions("group", true);

			AttributeOptions type = new AttributeOptions("type", true);

			AttributeOptions identifier = new AttributeOptions("identifier", true);

			AttributeOptions enabled = new AttributeOptions("enabled", false);

			options = new ArrayList<AttributeOptions>();
			options.add(name);
			options.add(minHue);
			options.add(maxHue);
			options.add(minSat);
			options.add(maxSat);
			options.add(minVal);
			options.add(maxVal);
			options.add(aspectRatioThresh);
			options.add(distToCentreImportance);
			options.add(imageFile);
			options.add(minArea);
			options.add(erodeDilateIterations);
			options.add(resolution);
			options.add(imgDumpPath);
			options.add(imgDumpTime);
			options.add(slope);
			options.add(yIntercept);
			options.add(group);
			options.add(type);
			options.add(identifier);
			options.add(enabled);
			List<String> lists = ConfigParser.listLists(visionParamsFile);

			for (String s : lists) {

				Map<String, String> data = ConfigParser.getProperties(visionParamsFile, s);

				List<Attribute> attribs = new ArrayList<Attribute>();
				attribs.add(new Attribute("name", s));
				for (String s1 : data.keySet()) {
					attribs.add(new Attribute(s1, data.get(s1)));
				}
				VisionParams params = new VisionParams(attribs, options);
				String resolution1 = params.getByName("resolution").getValue();
				int width = Integer.valueOf(resolution1.split("x")[0]);
				int height = Integer.valueOf(resolution1.split("x")[1]);
				params.getAttribs().add(new Attribute("width", width + ""));
				params.getAttribs().add(new Attribute("height", height + ""));
				NetworkTable visionTable = NetworkTable
						.getTable("vision-" + params.getByName("name").getValue() + "/");
				NetworkTablesManager.tables.put(s, visionTable);
				// The parameters are now valid, because it didnt throw an error
				visionParamsList.add(params);
			}

			sendVisionParams();

		} catch (Exception e1) {
			Log.e(e1.getMessage(), true);
			Log.e("\n\nError reading the params file, check if the file is corrupt?", true);
			System.exit(1);
		}
	}

	private static void sendVisionParams() {

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
	 * Saves the vision parameters to a file
	 * 
	 **/
	public static void saveVisionParams() {
		try {

			for (VisionParams params : visionParamsList) {
				saveVisionParams(params);
			}

		} catch (Exception e1) {
			Log.e(e1.getMessage(), true);
		}
	}

	public static void saveVisionParams(VisionParams params) throws Exception {
		Map<String, String> data = new HashMap<String, String>();

		for (Attribute a : params.getAttribs()) {
			if (!a.getName().equals("name")) {
				data.put(a.getName(), a.getValue());
			}
		}

		ConfigParser.saveList(visionParamsFile, params.getByName("name").getValue(), data);
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

	/**
	 * Converts a OpenCV Matrix to a BufferedImage :)
	 * 
	 * @param matrix Matrix to be converted
	 * @return Generated from the matrix
	 * @throws IOException
	 * @throws Exception
	 */
	public static BufferedImage matToBufferedImage(Mat matrix) throws IOException {
		MatOfByte mob = new MatOfByte();
		Imgcodecs.imencode(".jpg", matrix, mob);
		byte ba[] = mob.toArray();

		BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
		matrix.release();
		return bi;
	}

	/**
	 * Converts a Buffered Image to a OpenCV Matrix
	 * 
	 * @param Buffered Image to convert to matrix
	 * @return The matrix from the buffered image
	 */

	public static Mat bufferedImageToMat(BufferedImage bi) {
		Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		mat.put(0, 0, data);
		return mat;
	}

	/**
	 * 
	 * @param The    image to dump to a file
	 * @param image  the image to be dumped
	 * @param suffix the suffix to put on the file name
	 * @throws IOException
	 */

	public static void imgDump(BufferedImage image, String suffix, int timestamp, String outputPath)
			throws IOException {
		// prepend the file name with the tamestamp integer, left-padded with
		// zeros so it sorts properly
		@SuppressWarnings("deprecation")
		String match = loggingTable.getString("match");
		if (match.equals("")) {
			match = "practice";
		}

		File output = new File(outputPath + match + "-" + String.format("%05d", timestamp) + "_" + suffix + ".png");
		ImageIO.write(image, "png", output);
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

		// Must be included!
		// Loads OpenCV
		System.loadLibrary("opencv_java310");

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
		loadVisionParams();

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
				
				MainThread thread = new MainThread(params);
				if (enabled) {
					thread.start();
				}
				threads.add(thread);
			} catch (Exception e) {
				Log.e(e.getMessage(), true);
			}

		} // end main video processing loop
	}
}
