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
	public static int minHue;
	public static int maxHue;
	public static int minSaturation;
	public static int maxSaturation;
	public static int minValue;
	public static int maxValue;
	public static int iterations;

	/**
	 * A class to hold any vision data returned by process()
	 */
	private static class VisionData {
		public Mat outputImg = new Mat();

	}

	public static void main(String[] args) {

		Properties properties = new Properties();
		
		try {
			FileInputStream in = new FileInputStream("config.properties");
			properties.load(in);
			minHue = Integer.valueOf(properties.getProperty("minHue"));
			maxHue = Integer.valueOf(properties.getProperty("maxHue"));
			minSaturation = Integer.valueOf(properties.getProperty("minSaturation"));
			maxSaturation = Integer.valueOf(properties.getProperty("maxSaturation"));
			minValue = Integer.valueOf(properties.getProperty("minValue"));
			maxValue = Integer.valueOf(properties.getProperty("maxValue"));
			iterations = Integer.valueOf(properties.getProperty("iterations"));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		
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

		boolean windows = false;
		if (System.getProperty("os.name").startsWith("Windows")) {
			windows = true;
		}

		// Set to 1 for USB camera, set to 0 for webcam, i think 0 is USB if
		// there is no webcam :/
		VideoCapture camera = new VideoCapture(0);
		Mat frame = new Mat();
		VisionData img = new VisionData();
		camera.read(frame);

		if (!camera.isOpened()) {
			System.out.println("Error");
		} else {

			camera.read(frame);
			DisplayGui guiRawImg = null;
			DisplayGui guiProcessedImg = null;

			if (windows) {
				try {
					guiRawImg = new DisplayGui(Mat2BufferedImage(frame), "Raw Camera Image");
					guiProcessedImg = new DisplayGui(Mat2BufferedImage(frame), "Processed Image");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			while (true) {
				if (camera.read(frame)) {

					// display the raw image
					if (windows) {
						try {
							// May throw a NullPointerException if initializing the window failed
							guiRawImg.updateImage(Mat2BufferedImage(frame));
						} catch (Exception e) {
							e.printStackTrace();
							System.out.println("Window closed");
							Runtime.getRuntime().halt(0);
						}
					}


					try {
						img = process(frame);
					}
					catch (Exception e) {
						// frame failed to process .... do nothing and go to next frame?
						continue;
					}
					// display the processed frame in the GUI
					if (windows) {
						try {
							// May throw a NullPointerException if initializing the window failed
							guiProcessedImg.updateImage(Mat2BufferedImage(img.outputImg));
						} catch (Exception e) {
							e.printStackTrace();
							System.out.println("Window closed");
							Runtime.getRuntime().halt(0);
						}
					}
				}
			}
		}
		camera.release();
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
		Mat hsvThreshold = new Mat();
		Core.inRange(src, new Scalar(minHue,minSaturation,minValue), new Scalar(maxHue,maxSaturation,maxValue), hsvThreshold);
		//Mat mask = new Mat();
		//src.copyTo(mask, hsvThreshold);
		Mat dilate = new Mat();
		Imgproc.dilate(hsvThreshold, dilate,new Mat(),new Point(), iterations, Core.BORDER_CONSTANT, new Scalar(0));
		Mat erode = new Mat();
		Imgproc.erode(dilate, erode,new Mat(),new Point(), iterations, Core.BORDER_CONSTANT, new Scalar(0));
		VisionData visionData = new VisionData();
		visionData.outputImg = erode.clone();
		return visionData;
	}
}