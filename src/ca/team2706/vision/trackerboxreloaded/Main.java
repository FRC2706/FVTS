package ca.team2706.vision.trackerboxreloaded;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Main {
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
		int streamPort = 1185;

		// Set to 1 for USB camera, set to 0 for webcam, i think 0 is USB if
		// there is no webcam :/
		VideoCapture camera = new VideoCapture(0);
		Mat frame = new Mat();
		camera.read(frame);

		if (!camera.isOpened()) {
			System.out.println("Error");
		} else {

			camera.read(frame);
			DisplayGui gui = null;
			boolean windows = false;
			if (System.getProperty("os.name").startsWith("Windows")) {
				windows = true;
			}
			if (windows) {
				try {
					gui = new DisplayGui(Mat2BufferedImage(frame));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			while (true) {
				if (camera.read(frame)) {
					try {
						BufferedImage img = process(frame);
						if (windows) {
							gui.updateImage(img);
						}
					} catch (Exception e) {
						if (windows) {
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

	public static BufferedImage process(Mat src) throws Exception {
		
		return Mat2BufferedImage(src);
	}
}