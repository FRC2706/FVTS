package ca.team2706.vision.trackerboxreloaded;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Main {

	// Camera Type
	// Set to 1 for USB camera, set to 0 for webcam, I think 0 is USB if
	// there is no webcam :/

	/**
	 * A class to hold calibration parameters for the image processing algorithm
	 */
	private static class VisionParams {
		public int minHue;
		public int maxHue;
		public int minSaturation;
		public int maxSaturation;
		public int minValue;
		public int maxValue;
		public int erodeDilateIterations;
		public int CameraSelect;
	}

	private static VisionParams visionParams = new VisionParams();

	/**
	 * A class to hold any vision data returned by process()
	 */
	private static class VisionData {
		public Mat outputImg = new Mat();
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
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(1);
		}
	}

	private static void saveProperties() {
		// TODO -- basically the opposite of loadProperties()
		// See guide: https://www.mkyong.com/java/java-properties-file-examples/
	}

	private static BufferedImage Mat2BufferedImage(Mat matrix) throws Exception {
		MatOfByte mob = new MatOfByte();
		Imgcodecs.imencode(".jpg", matrix, mob);
		byte ba[] = mob.toArray();

		BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
		return bi;
	}

	public static VisionData process(Mat src) throws Exception {

		// If there's any data or intermediate images that you want to return, add them to the VisionData class
		// For example, any numbers that we want to return to the roboRIO.
		VisionData visionData = new VisionData();


		// Colour threshold
		Mat hsvThreshold = new Mat();
		Core.inRange(src, new Scalar(visionParams.minHue, visionParams.minSaturation, visionParams.minValue),
				new Scalar(visionParams.maxHue, visionParams.maxSaturation, visionParams.maxValue), hsvThreshold);

		// Dilate - Erode
		Mat dilatedImg = new Mat();
		Mat erode = new Mat();
		Imgproc.dilate(hsvThreshold, dilatedImg, new Mat(), new Point(), visionParams.erodeDilateIterations, Core.BORDER_CONSTANT, new Scalar(0));
		Imgproc.erode(dilatedImg, erode, new Mat(), new Point(), visionParams.erodeDilateIterations, Core.BORDER_CONSTANT, new Scalar(0));


		visionData.outputImg = erode;

		return visionData;
	}



	/*** Main() ***/

	public static void main(String[] args) {
		// Loads our OpenCV library. This MUST be included
		System.loadLibrary("opencv_java310");

		// Connect NetworkTables, and get access to the publishing table
		NetworkTable.setClientMode();
		// Set your team number here
		NetworkTable.setTeam(2706);

		NetworkTable.initialize();

		// This is the network port you want to stream the raw received image to
		// By rules, this has to be between 1180 and 1190, so 1185 is a good
		// choice
		//int streamPort = 1185;

		boolean use_GUI = false;
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) {
			use_GUI = true;
			System.out.println(use_GUI);
		}

		// read the vision calibration values from file.
		loadVisionParams();

		VideoCapture camera = new VideoCapture(visionParams.CameraSelect);
		Mat frame = new Mat();
		camera.read(frame);

		if (!camera.isOpened()) {
			System.out.println("Error: Can not connect to camera");
		} else {

			// Set up the camera feed
			camera.read(frame);
			DisplayGui guiRawImg = null;
			DisplayGui guiProcessedImg = null;

			// Set up the GUI display windows
			if (use_GUI) {
				try {
					guiRawImg = new DisplayGui(Mat2BufferedImage(frame), "Raw Camera Image");
					guiProcessedImg = new DisplayGui(Mat2BufferedImage(frame), "Processed Image");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// Main video processing loop
			while (true) {
				if (camera.read(frame)) {

					// If we're using the GUI, then re-load the params on each frame so we can tune it.
					// ... if running on the Pi, then we can be a little more efficient and only load them once at the beginning.
					if(use_GUI) {
						loadVisionParams();
					}

					// display the raw frame
					if (use_GUI) {
						try {
							// May throw a NullPointerException if initializing the window failed
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
						visionData = process(frame);
					}
					catch (Exception e) {
						// frame failed to process .... do nothing and go to next frame?
						System.err.println("Error: Frame failed to process. Skipping frame.");
						continue;
					}

					// display the processed frame in the GUI
					if (use_GUI) {
						try {
							// May throw a NullPointerException if initializing the window failed
							guiProcessedImg.updateImage(Mat2BufferedImage(visionData.outputImg));
						} catch (Exception e) {
							e.printStackTrace();
							System.out.println("Window closed");
							Runtime.getRuntime().halt(0);
						}
					}
				}
				else {
					System.err.println("Error: Failed to get a frame from the camera");
				}
			} // end main video processing loop
		}
		camera.release();
	}
}
