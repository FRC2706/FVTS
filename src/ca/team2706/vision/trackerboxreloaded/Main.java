package ca.team2706.vision.trackerboxreloaded;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Main {

	// Camera Type
	// Set to 1 for USB camera, set to 0 for webcam, I think 0 is USB if
	// there is no webcam :/

	/**
	 * A class to hold calibration parameters for the image processing algorithm
	 */
	public static class VisionParams {
		public int minHue;
		public int maxHue;
		public int minSaturation;
		public int maxSaturation;
		public int minValue;
		public int maxValue;
		public int erodeDilateIterations;
		public int CameraSelect;
		public double aspectRatioThresh;
		public int minArea;
	}

	public static VisionParams visionParams = new VisionParams();

	/**
	 * A class to hold any vision data returned by process()
	 */

	public static class VisionData {

		public static class Target {
			int xCenter;
			int yCenter;

			public Target(int x, int y) {
				xCenter = x;
				yCenter = y;
			}
		}
		Mat outputImg = new Mat();

		double fps;

		ArrayList<Target> targetsFound = new ArrayList<Target>();
	}

	/*** Helper Functions ***/

	private static void loadVisionParams() {
		Properties properties = new Properties();
		try {
			FileInputStream in = new FileInputStream("visionParams.properties");
			properties.load(in);

			visionParams.CameraSelect = Integer.valueOf(properties.getProperty("CameraSelect"));
			visionParams.minHue = Integer.valueOf(properties.getProperty("minHue"));
			visionParams.maxHue = Integer.valueOf(properties.getProperty("maxHue"));
			visionParams.minSaturation = Integer.valueOf(properties.getProperty("minSaturation"));
			visionParams.maxSaturation = Integer.valueOf(properties.getProperty("maxSaturation"));
			visionParams.minValue = Integer.valueOf(properties.getProperty("minValue"));
			visionParams.maxValue = Integer.valueOf(properties.getProperty("maxValue"));
			visionParams.erodeDilateIterations = Integer.valueOf(properties.getProperty("erodeDilateIterations"));
			visionParams.aspectRatioThresh = Double.valueOf(properties.getProperty("aspectRatioThresh"));
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(1);
		}
	}

	private static BufferedImage Mat2BufferedImage(Mat matrix) throws Exception {
		MatOfByte mob = new MatOfByte();
		Imgcodecs.imencode(".jpg", matrix, mob);
		byte ba[] = mob.toArray();

		BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
		return bi;
	}

	public static void main(String[] args) {
		// Must be included!
		System.loadLibrary("opencv_java310");

		// Connect NetworkTables, and get access to the publishing table
		NetworkTable.setClientMode();
		// Set your team number here
		NetworkTable.setTeam(2706);

		NetworkTable.initialize();

		// This is the network port you want to stream the raw received image to
		// By rules, this has to be between 1180 and 1190, so 1185 is a good
		// choice
		// int streamPort = 1185;

		// read the vision calibration values from file.
		loadVisionParams();

		// Open a connection to the camera
		VideoCapture camera = new VideoCapture(visionParams.CameraSelect);

		// Read the camera's supported frame-rate
		double cameraFps = camera.get(Videoio.CAP_PROP_FPS);

		Mat frame = new Mat();
		camera.read(frame);

		if (!camera.isOpened()) {
			System.err.println("Error: Can not connect to camera");
			System.exit(1);
		}

		// Set up the camera feed
		camera.read(frame);
		DisplayGui guiRawImg = null;
		DisplayGui guiProcessedImg = null;
		boolean use_GUI = false;
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) {
			use_GUI = true;
			System.out.println(use_GUI);
		}
		// Set up the GUI display windows
		if (use_GUI) {
			try {
				guiRawImg = new DisplayGui(Mat2BufferedImage(frame), "Raw Camera Image");
				guiProcessedImg = new DisplayGui(Mat2BufferedImage(frame), "Processed Image");
				new ParamsSelector();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Main video processing loop
		while (true) {
			if (camera.read(frame)) {

				// display the raw frame
				if (use_GUI) {
					try {
						// May throw a NullPointerException if initializing
						// the window failed
						guiRawImg.updateImage(Mat2BufferedImage(frame));
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("Window closed");
						Runtime.getRuntime().halt(0);
					}
				}

				// Process the frame!
				VisionData visionData;
				try {
					visionData = Pipeline.process(frame, visionParams);
				} catch (Exception e) {
					// frame failed to process .... do nothing and go to
					// next frame?
					System.err.println("Error: Frame failed to process. Skipping frame.");
					continue;
				}

				// display the processed frame in the GUI
				if (use_GUI) {
					try {
						// May throw a NullPointerException if initializing
						// the window failed
						guiProcessedImg.updateImage(Mat2BufferedImage(visionData.outputImg));
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("Window closed");
						Runtime.getRuntime().halt(0);
					}

					// Display the frame rate
					System.out.printf("Vision FPS: %3.2f, camera FPS: %3.2f \n", visionData.fps, cameraFps, "");
				}
				else {
					System.err.println("Error: Failed to get a frame from the camera");
				}
			}
		} // end main video processing loop
	}

	public static void save() {
		Properties properties = new Properties();
		try {
			properties.setProperty("CameraSelect", String.valueOf(visionParams.CameraSelect));
			properties.setProperty("minHue", String.valueOf(visionParams.minHue));
			properties.setProperty("maxHue", String.valueOf(visionParams.maxHue));
			properties.setProperty("minSaturation", String.valueOf(visionParams.minSaturation));
			properties.setProperty("maxSaturation", String.valueOf(visionParams.maxSaturation));
			properties.setProperty("minValue", String.valueOf(visionParams.minValue));
			properties.setProperty("maxValue", String.valueOf(visionParams.maxValue));
			properties.setProperty("erodeDilateIterations", String.valueOf(visionParams.erodeDilateIterations));
			properties.setProperty("minArea", String.valueOf(visionParams.minArea));
			FileOutputStream out = new FileOutputStream("visionParams.properties");
			properties.store(out, "");
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(1);
		}
	}
}
