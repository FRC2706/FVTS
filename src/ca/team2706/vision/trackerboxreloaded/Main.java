package ca.team2706.vision.trackerboxreloaded;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import edu.wpi.first.networktables.NetworkTable;

public class Main {

	public NetworkTable table;
	
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
		
		/**
		 * This method converts the vision data into a nice and tidy string
		 * @return data
		 */
		@Override
		public String toString(){
			String s = "";
			//TODO: return all the datas to send over network tables
			//TODO: come up with syntax for seperating data, like s = "x:"+x+"#y:"+y
			s += "fps:"+fps;
			return s;
		}
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

	/** Converts an OpenCV Matrix to a BufferedImage
	 * 
	 * @param The OpenCV Matrix
	 * @return The image generated from the OpenCV Matrix
	 * @throws Exception
	 */
	private static BufferedImage Mat2BufferedImage(Mat matrix) throws Exception {
		MatOfByte mob = new MatOfByte();
		Imgcodecs.imencode(".jpg", matrix, mob);
		byte ba[] = mob.toArray();

		BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
		return bi;
	}
	
	/** The main method! :)
	 * 
	 * @param Command line arguments
	 * @throws Somethings gone wrong :]
	 */
	public static void main(String[] args) throws Exception {
		new Main();
	}
	
	
	/** The constructor! its cool, and everything is here
	 * 
	 * @throws Somethings up! :] Have fun debugging future self!
	 */
	public Main() throws Exception {
		// Must be included! dont remove the next 5 lines of code ;}
		// This loads opencv
		System.loadLibrary("opencv_java310");

		// Connect NetworkTables, and get access to the publishing table
		//
		NetworkTable.setClientMode();
		
		// Sets the team number to 2706
		NetworkTable.setTeam(2706);

		//Initilizes the NetworkTable! Very important!
		NetworkTable.initialize();
		
		//Gets the vision NetworkTable
		table = NetworkTable.getTable("vision");
		
		// read the vision calibration values from file.
		loadVisionParams();

		// Open a connection to the camera
		VideoCapture camera = new VideoCapture(visionParams.CameraSelect);

		// Read the camera's supported frame-rate
		// TODO: Get camera fps to read properly
		double cameraFps = camera.get(Videoio.CAP_PROP_FPS);

		//Gets the frame matrix ready to be written to
		Mat frame = new Mat();
		
		//Reads the first frame
		camera.read(frame);

		//Make sure the camera is open!
		if (!camera.isOpened()) {
			System.out.println("Error: Can not connect to camera");
		} else {
			
			// Set up the camera feed
			camera.read(frame);
			//Sets up the gui for the raw image
			DisplayGui guiRawImg = null;
			//Sets up the gui for the processed image
			DisplayGui guiProcessedImg = null;
			//The next 5 lines of code check if the OS is windows
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
					table.putString("data", visionData.toString());
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

					}else{
						table.putString("data", visionData.toString());
					}
					// Display the frame rate
					System.out.printf("Vision FPS: %3.2f, camera FPS: %3.2f\n", visionData.fps, cameraFps);
					
				} // end main video processing loop
			}
		}
		camera.release();
	}

	/**
	 * Saves the current vision parameters!
	 * Very important!
	 * :]
	 * :]
	 * Should only be called on windows
	 */
	public static void save() {
		//Saves params! Should only be called on Windows
		Properties properties = new Properties();
		try {
			//Sets properties
			properties.setProperty("CameraSelect", String.valueOf(visionParams.CameraSelect));
			properties.setProperty("minHue", String.valueOf(visionParams.minHue));
			properties.setProperty("maxHue", String.valueOf(visionParams.maxHue));
			properties.setProperty("minSaturation", String.valueOf(visionParams.minSaturation));
			properties.setProperty("maxSaturation", String.valueOf(visionParams.maxSaturation));
			properties.setProperty("minValue", String.valueOf(visionParams.minValue));
			properties.setProperty("maxValue", String.valueOf(visionParams.maxValue));
			properties.setProperty("erodeDilateIterations", String.valueOf(visionParams.erodeDilateIterations));
			properties.setProperty("minArea", String.valueOf(visionParams.minArea));
			//Creates output stream for properties file
			FileOutputStream out = new FileOutputStream("visionParams.properties");
			//Writes the properties data to the properties file
			properties.store(out, "");
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(1);
		}
	}
}
