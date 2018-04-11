package ca.team2706.vision.trackerboxreloaded;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

import edu.wpi.first.wpilibj.networktables.NetworkTable;


public class Main {

	public static VisionParams visionParams = new VisionParams();
	public static NetworkTable visionTable;
	public static ParamsSelector selector;
	public static int seconds_between_img_dumps;
	public static int timestamp = 0;
	public static File timestampfile;
	public static long current_time_seconds;
	public static String outputPath;
	public static BufferedImage currentImage;
	public static VideoCapture camera;
	public static VisionData lastData;
	public static boolean process = true;
	public static boolean showMiddle = false;

	// Camera Type (set in visionParams.properties)
	// Set to 1 for USB camera, set to 0 for webcam, I think 0 is USB if
	// there is no webcam :/

	/**
	 * A class to hold calibration parameters for the image processing algorithm
	 */
	public static class VisionParams {
		int minHue;
		int maxHue;
		int minSaturation;
		int maxSaturation;
		int minValue;
		int maxValue;
		int erodeDilateIterations;
		int cameraSelect;
		double aspectRatioThresh;
		double minArea;
		double distToCentreImportance;
		int width;
		int height;
		Size sz;
		String imageFile;
	}

	/**
	 * A class to hold any visionTable data returned by process() :) :) :} :] :]
	 */

	public static class VisionData {

		public static class Target {
			int xCentre;
			double xCentreNorm;
			int yCentre;
			double yCentreNorm;
			double areaNorm; // [0,1] representing how much of the screen it
								// occupies
			Rect boundingBox;
		}

		ArrayList<Target> targetsFound = new ArrayList<Target>();
		Target preferredTarget;
		public Mat binMask = new Mat();
		public double fps;
	}

	/**
	 * Initilizes the Network Tables WARNING! Change 127.0.0.1 to the robot ip
	 * before it is on master or it will not be fun :)
	 */
	private static void initNetworkTables() {
		NetworkTable.setClientMode();
		NetworkTable.setUpdateRate(0.02);
		NetworkTable.setTeam(2706); // Use this for the robit
		NetworkTable.setDSClientEnabled(true); // and this for the robit
		// NetworkTable.setIPAddress("10.27.6.67"); //Use this for testing
		NetworkTable.initialize();
		visionTable = NetworkTable.getTable("vision");
	}

	/**
	 * Loads the visionTable params! :]
	 **/
	public static void loadVisionParams() {
		Properties properties = new Properties();
		try {
			FileInputStream in = new FileInputStream("visionParams.properties");
			properties.load(in);

			visionParams.cameraSelect = Integer.valueOf(properties.getProperty("CameraSelect"));
			visionParams.minHue = Integer.valueOf(properties.getProperty("minHue"));
			visionParams.maxHue = Integer.valueOf(properties.getProperty("maxHue"));
			visionParams.minSaturation = Integer.valueOf(properties.getProperty("minSaturation"));
			visionParams.maxSaturation = Integer.valueOf(properties.getProperty("maxSaturation"));
			visionParams.minValue = Integer.valueOf(properties.getProperty("minValue"));
			visionParams.maxValue = Integer.valueOf(properties.getProperty("maxValue"));
			visionParams.minArea = Double.valueOf(properties.getProperty("minArea"));
			visionParams.erodeDilateIterations = Integer.valueOf(properties.getProperty("erodeDilateIterations"));
			visionParams.aspectRatioThresh = Double.valueOf(properties.getProperty("aspectRatioThresh"));
			visionParams.distToCentreImportance = Double.valueOf(properties.getProperty("distToCentreImportance"));

			outputPath = properties.getProperty("imgDumpPath");
			seconds_between_img_dumps = Integer.valueOf(properties.getProperty("imgDumpWait"));
			visionParams.imageFile = properties.getProperty("imageFile");
			if (outputPath.endsWith("/") || outputPath.endsWith("\\")) {
				timestampfile = new File(outputPath + "time.stamp");
			} else {
				timestampfile = new File(outputPath + "/" + "time.stamp");
			}
			// if the file, take the timestamp from there
			if (timestampfile.exists()) {
				Scanner s = new Scanner(timestampfile);
				timestamp = Integer.valueOf(s.nextLine()).intValue();
				timestamp++;
				s.close();
			}

			String resolution = properties.getProperty("resolution");
			if (resolution.equals("320x240")) {
				visionParams.width = 320;
				visionParams.height = 240;
			} else if (resolution.equals("640x480")) {
				visionParams.width = 640;
				visionParams.height = 480;
			} else if (resolution.equals("160x120")) {
				visionParams.width = 160;
				visionParams.height = 120;
			} else if (resolution.equals("80x60")) {
				visionParams.width = 80;
				visionParams.height = 60;
			} else {
				throw new IllegalArgumentException("Error: " + properties.getProperty("resolution")
						+ " is not a supported resolution.\n" + "Allowed: 80x60, 160x120, 320x240, 640x480.");
			}
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (Exception e1) {
			e1.printStackTrace();
			System.err.println("\n\nError reading the params file, check if the file is corrupt?");
			System.exit(1);
		}
	}

	public static void saveVisionParams() {
		Properties properties = new Properties();
		try {
			properties.setProperty("CameraSelect", String.valueOf(visionParams.cameraSelect));
			properties.setProperty("minHue", String.valueOf(visionParams.minHue));
			properties.setProperty("maxHue", String.valueOf(visionParams.maxHue));
			properties.setProperty("minSaturation", String.valueOf(visionParams.minSaturation));
			properties.setProperty("maxSaturation", String.valueOf(visionParams.maxSaturation));
			properties.setProperty("minValue", String.valueOf(visionParams.minValue));
			properties.setProperty("maxValue", String.valueOf(visionParams.maxValue));
			properties.setProperty("erodeDilateIterations", String.valueOf(visionParams.erodeDilateIterations));
			properties.setProperty("minArea", String.valueOf(visionParams.minArea));
			properties.setProperty("aspectRatioThresh", String.valueOf(visionParams.aspectRatioThresh));
			properties.setProperty("distToCentreImportance", String.valueOf(visionParams.distToCentreImportance));
			properties.setProperty("imageFile", visionParams.imageFile);
			properties.setProperty("resolution", visionParams.width + "x" + visionParams.height);
			properties.setProperty("imgDumpWait", String.valueOf(seconds_between_img_dumps));
			properties.setProperty("imgDumpPath", outputPath);
			FileOutputStream out = new FileOutputStream("visionParams.properties");
			properties.store(out, "");
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Turns all the vision data into packets that kno da wae to get to the robo
	 * rio :]
	 *
	 * @param visionData
	 */
	private static void sendVisionDataOverNetworkTables(VisionData visionData) {

		// Sends the data
		visionTable.putNumber("fps", visionData.fps);
		visionTable.putNumber("numTargetimestampfileound", visionData.targetsFound.size());

		if (visionData.preferredTarget != null) {
			visionTable.putNumber("ctrX", visionData.preferredTarget.xCentreNorm);
			visionTable.putNumber("area", visionData.preferredTarget.areaNorm);
		}
	}

	/**
	 * Converts a OpenCV Matrix to a BufferedImage :)
	 *
	 * @param matrix
	 *            Matrix to be converted
	 * @return Generated from the matrix
	 * @throws IOException
	 * @throws Exception
	 */
	private static BufferedImage matToBufferedImage(Mat matrix) throws IOException {
		MatOfByte mob = new MatOfByte();
		Imgcodecs.imencode(".jpg", matrix, mob);
		byte ba[] = mob.toArray();

		BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
		return bi;
	}

	public static Mat bufferedImageToMat(BufferedImage bi) {
		Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		mat.put(0, 0, data);
		return mat;
	}

	public static void imgDump(BufferedImage image, String suffix, int timestamp) throws IOException {
		// prepend the file name with the tamestamp integer, left-padded with
		// zeros so it sorts properly
		File output = new File(outputPath + String.format("%05d", timestamp) + "_" + suffix + ".png");
		try {
			ImageIO.write(image, "PNG", output);
		} catch (IOException e) {
			throw new IOException(e.getMessage());
		}
		timestampfile.delete();
		timestampfile.createNewFile();
		PrintWriter out = new PrintWriter(timestampfile);
		out.println(timestamp);
		out.close();
	}

	/**
	 * The main method! Very important Do not delete! :] :]
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		// Must be included!
		System.loadLibrary("opencv_java310");

		// Connect NetworkTables, and get access to the publishing table
		initNetworkTables();

		// read the vision calibration values from file.
		loadVisionParams();

		try {
			Files.copy(Paths.get("visionParams.properties"),
					Paths.get(outputPath + "/visionParams-" + timestamp + ".properties"),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		Mat frame = new Mat();

		// Open a connection to the camera
		VideoCapture camera = null;

		// Whether to use a camera, or load an image file from disk.
		boolean useCamera = true;
		if (visionParams.cameraSelect == -1) {
			useCamera = false;
		}

		if (useCamera) {
			camera = new VideoCapture(visionParams.cameraSelect);

			int fourcc = VideoWriter.fourcc('M', 'J', 'P', 'G');
			camera.set(Videoio.CAP_PROP_FOURCC, fourcc);
			camera.set(Videoio.CAP_PROP_FRAME_WIDTH, visionParams.width);
			camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, visionParams.height);

			camera.read(frame);

			if (!camera.isOpened()) {
				System.err.println("Error: Can not connect to camera");
				System.exit(1);
			}

			// Set up the camera feed
			camera.read(frame);
		} else {
			// load the image from file.
			try {
				frame = bufferedImageToMat(ImageIO.read(new File(visionParams.imageFile)));
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		DisplayGui guiRawImg = null;
		DisplayGui guiProcessedImg = null;
		boolean use_GUI = false;
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") != -1) {
			use_GUI = true;
		}

		visionParams.sz = new Size(visionParams.width, visionParams.height);
		Imgproc.resize(frame, frame, visionParams.sz);

		// Set up the GUI display windows
		if (use_GUI) {
			try {
				guiRawImg = new DisplayGui(matToBufferedImage(frame), "Raw Camera Image");
				guiProcessedImg = new DisplayGui(matToBufferedImage(frame), "Processed Image");
				new ParamsSelector();
			} catch (IOException e) {
				// means mat2BufferedImage broke
				// non-fatal error, let the program continue
			}
		}

		// Main video processing loop
		while (true) {
			if (useCamera) {
				if (!camera.read(frame)) {
					System.err.println("Error: Failed to get a frame from the camera");
					continue;
				}
			} // else use the image from disk that we loaded above

			if (use_GUI)
				Imgproc.resize(frame, frame, visionParams.sz);

			// Process the frame!
			long pipelineStart = System.nanoTime();
			VisionData visionData = Pipeline.process(frame, visionParams, use_GUI);
			long pipelineEnd = System.nanoTime();

			Pipeline.selectPreferredTarget(visionData, visionParams);

			Mat outputTargetImg = frame.clone();
			Pipeline.drawPreferredTarget(outputTargetImg, visionData);

			sendVisionDataOverNetworkTables(visionData);
			lastData = visionData;
			// display the processed frame in the GUI
			if (use_GUI) {
				try {
					// May throw a NullPointerException if initializing
					// the window failed
					BufferedImage raw = matToBufferedImage(frame);
					currentImage = raw;
					if (showMiddle) {
						Graphics g = raw.getGraphics();
						g.setColor(Color.RED);
						g.fillOval(raw.getWidth() / 2 - 8, raw.getHeight() / 2 - 8, 8, 8);
						g.dispose();
					}
					guiRawImg.updateImage(raw);
					guiProcessedImg.updateImage(matToBufferedImage(visionData.binMask));
				} catch (IOException e) {
					// means mat2BufferedImage broke
					// non-fatal error, let the program continue
					continue;
				} catch (NullPointerException e) {
					e.printStackTrace();
					System.out.println("Window closed");
					Runtime.getRuntime().halt(0);
				} catch (Exception e) {
					// just in case
					e.printStackTrace();
					continue;
				}
			}
			// log images to file once every seconds_between_img_dumps
			long elapsedTime = (System.currentTimeMillis() / 1000) - current_time_seconds;
			if (elapsedTime >= seconds_between_img_dumps) {
				current_time_seconds = (System.currentTimeMillis() / 1000);

				Mat finalFrame = frame.clone();
				new Thread(new Runnable() {
					public void run() {
						try {
							imgDump(matToBufferedImage(finalFrame), "raw", timestamp);
							imgDump(matToBufferedImage(visionData.binMask), "binMask", timestamp);
							imgDump(matToBufferedImage(outputTargetImg), "output", timestamp);
							timestamp++;
						} catch (IOException e) {
							e.printStackTrace();
							return;
						}
					}
				}).start();
			}
			// Display the frame rate onto the console
			double pipelineTime = (((double) (pipelineEnd - pipelineStart)) / Pipeline.NANOSECONDS_PER_SECOND) * 1000;
			System.out.printf("Vision FPS: %3.2f, pipeline took: %3.2f ms\n", visionData.fps, pipelineTime);
		}
	} // end main video processing loop

	public void hideMiddle() {
		showMiddle = false;
	}

	public void showMiddle() {
		showMiddle = true;
	}

	public static VisionData forceProcess() {
		Mat frame = new Mat();
		camera.read(frame);
		Imgproc.resize(frame, frame, visionParams.sz);

		VisionData visionData = Pipeline.process(frame, visionParams, false);

		Pipeline.selectPreferredTarget(visionData, visionParams);

		return visionData;
	}

	public static VisionData forceProcess(Mat frame) {
		Imgproc.resize(frame, frame, visionParams.sz);

		VisionData visionData = Pipeline.process(frame, visionParams, false);

		Pipeline.selectPreferredTarget(visionData, visionParams);

		return visionData;
	}

	public static Mat getFrame(){
    	Mat frame = new Mat();
    	camera.read(frame);
    	return frame;
    }
}
