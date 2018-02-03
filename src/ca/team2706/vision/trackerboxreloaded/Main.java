package ca.team2706.vision.trackerboxreloaded;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class Main {
	public static final int SECONDS_BETWEEN_DUMPS = 100;
	public static int current_time_seconds = 0;
	public static String outputPath;
	public static final SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd-hh-mm-ss");
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
		public int minArea;
	}

	public static VisionParams visionParams = new VisionParams();

	/**
	 * A class to hold any vision data returned by process()
	 */
	public static class VisionData {
		public Mat outputImg = new Mat();
		double fps;
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
			outputPath = properties.getProperty("dumpPath");
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
		new Main();
	}

	public Main() {
		// Must be included!
		System.loadLibrary("opencv_java310");

		// read the vision calibration values from file.
		loadVisionParams();

		// Open a connection to the camera
		VideoCapture camera = new VideoCapture(visionParams.CameraSelect);

		// Read the camera's supported frame-rate
		double cameraFps = camera.get(Videoio.CAP_PROP_FPS);

		Mat frame = new Mat();
		camera.read(frame);

		if (!camera.isOpened()) {
			System.out.println("Error: Can not connect to camera");
		} else {

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

			// Starts the timer
			new Timer();
			
			// Main video processing loop
			while (true) {
				if (camera.read(frame)) {

					// display the raw frame
					if (use_GUI) {
						try {
							BufferedImage image = Mat2BufferedImage(frame);
							if(current_time_seconds >= SECONDS_BETWEEN_DUMPS){
								dump(image,true);
							}
							// May throw a NullPointerException if initializing
							// the window failed
							guiRawImg.updateImage(image);
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
							BufferedImage image = Mat2BufferedImage(visionData.outputImg);
							if(current_time_seconds >= SECONDS_BETWEEN_DUMPS){
								dump(image,false);
							}
							// May throw a NullPointerException if initializing
							// the window failed
							guiProcessedImg.updateImage(image);
						} catch (Exception e) {
							e.printStackTrace();
							System.out.println("Window closed");
							Runtime.getRuntime().halt(0);
						}

					}
					// Display the frame rate
					System.out.printf("Vision FPS: %3.2f, camera FPS: %3.2f\n", visionData.fps, cameraFps);

				} // end main video processing loop
			}
		}
		camera.release();
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

	public static void dump(BufferedImage image,boolean raw) {
		File output;
		if(raw){
			output = new File(outputPath + "imageraw" + format.format(Calendar.getInstance().getTime()) + ".png");
		}else{
			output = new File(outputPath + "imageprocessed" + format.format(Calendar.getInstance().getTime()) + ".png");
		}
		if (!output.getParentFile().exists()) {
			output.getParentFile().mkdirs();
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ImageIO.write(image, "PNG", output);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
