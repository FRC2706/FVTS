package ca.team2706.vision.trackerboxreloaded;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Main {

	public static NetworkTable vision;
	public static NetworkTable fps;
	public static NetworkTable data;
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
	 * A class to hold any vision data returned by process() :) :) :} :] :]
	 */
	public static class VisionData {
		public Mat outputImg = new Mat();
		public double fps;
		public HashMap<String, String> data = new HashMap<String, String>();

		/**
		 * This method converts the vision data into a nice and tidy string :]
		 * 
		 * @return the data
		 */
		public void encode(NetworkTable table) {
			// TODO: add code
			// basic networktable code: table.getEntry("data").setString("foo);
			table.getEntry("data").setString(DataUtils.encodeData(data));
		}

		/**
		 * Decodes the NetworkTable stuff into a VisionData
		 * 
		 * @param the
		 *            data table
		 * @return the data
		 */
		@SuppressWarnings("unused")
		public static VisionData decode(NetworkTable table) {
			// TODO: add code
			// basic networktable code:
			// table.getEntry("data").getValue().getString();
			// dont forget: if(table.getEntry("data").getValue().isString())
			if (table.getEntry("data").getValue().isString()) {
				HashMap<String, String> data = DataUtils.decodeData(table.getEntry("data").getValue().getString());
			}
			return null;
		}
	}

	/**
	 * Initilizes the Network Tables WARNING! Change 127.0.0.1 to the robot ip
	 * before it is on master or it will not be fun :)
	 */
	private static void initNetworkTables() {
		NetworkTableInstance instance = NetworkTableInstance.getDefault();
		instance.startClient("127.0.0.1");
		vision = instance.getTable("vision");
		fps = vision.getSubTable("fps");
		data = vision.getSubTable("data");
	}

	/**
	 * 
	 * Loads the vision params! :]
	 * 
	 **/

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

	/**
	 * The main method!
	 * Very important
	 * Do not delete!
	 * :]
	 * :]
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) {
		// Must be included!
		System.loadLibrary("opencv_java310");

		// Connect NetworkTables, and get access to the publishing table
		initNetworkTables();
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
				if (camera.read(frame)) {
					// display the raw frame
					if (use_GUI) {
						try {
							// May throw a NullPointerException if initializing
							// the window failed
							guiRawImg.updateImage(matToBufferedImage(frame));
						} catch (IOException e) {
							// means mat2BufferedImage broke
							// non-fatal error, let the program continue
							continue;
						} catch (NullPointerException e) {
							e.printStackTrace();
							System.out.println("Window closed");
							Runtime.getRuntime().halt(0);
						}
					}

					// Process the frame!
					VisionData visionData = Pipeline.process(frame, visionParams);
					
					
					//Sends the data
					visionData.encode(data);
					fps.getEntry("fps").setDouble(visionData.fps);
					
					// display the processed frame in the GUI
					if (use_GUI) {
						try {
							// May throw a NullPointerException if initializing
							// the window failed
							guiProcessedImg.updateImage(matToBufferedImage(visionData.outputImg));
						} catch (IOException e) {
							// means mat2BufferedImage broke
							// non-fatal error, let the program continue
							continue;
						} catch (NullPointerException e) {
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

	/**
	 * Saves the properties :]
	 */
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
