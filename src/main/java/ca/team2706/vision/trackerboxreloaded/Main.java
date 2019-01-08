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
	
	public static String filename = "";
	public static ParamsSelector selector;
	public static int timestamp = 0;
	public static File timestampfile;
	public static BufferedImage currentImage;
	public static VisionData lastData;
	public static boolean process = true;
	public static boolean showMiddle = false;
	public static boolean useCamera = true;
	public static Mat frame;

	public static void setFrame(Mat f) {
		frame = f;
	}

	// Camera Type (set in visionParams.properties)
	// Set to 1 for USB camera, set to 0 for webcam, I think 0 is USB if
	// there is no webcam :/
	/** The vision parameters, this is used by the vision pipeline **/
	public static VisionParams visionParams = new VisionParams();
	/**
	 * The vision NetworkTable, this is used to publish vision data to the
	 * RoboRIO
	 **/
	public static NetworkTable visionTable;
	/**
	 * This is the number of seconds between dumping images to a usb stick, this
	 * is taken from the vision parameters
	 **/
	public static double seconds_between_img_dumps;
	/**
	 * This is the current number of seconds, this resets every time it is over
	 * the seconds_between_img_dumps
	 **/
	public static double current_time_seconds;
	/** The directory that images are dumped to **/
	public static String outputPath;

	/**
	 * A class to hold calibration parameters for the image processing algorithm
	 */
	public static class VisionParams {
		/** This is the minimum hue that the pipeline will recognize **/
		int minHue;
		/** This is the maximum hue that the pipeline will recognize **/
		int maxHue;
		/** This is the minimum saturation that the pipeline will recognize **/
		int minSaturation;
		/** This is the maximum saturation that the pipeline will recognize **/
		int maxSaturation;
		/** This is the minimum value that the pipeline will recognize **/
		int minValue;
		/** This is the maximum value that the pipeline will recognize **/
		int maxValue;
		/**
		 * This is how many times the pipeline will erode dilate the camera
		 * image
		 **/
		int erodeDilateIterations;
		/** This is the id of the camera that will be used to get images **/
		int cameraSelect;
		/**
		 * The threshold to detect one large cube as 2 cubes, this is a value
		 * between 0 and 1
		 **/
		double aspectRatioThresh;
		/** The minimum area that a target can have and still be recognized **/
		double minArea;
		/**
		 * How important it is for a target to be close to the center of the
		 * image, this will change depending on how well we can turn
		 **/
		double distToCentreImportance;
		/** The width to resize the image from the camera to **/
		int width;
		/** The height to resize the image from the camera to **/
		int height;
		/**
		 * The size to resize the image from the camera to, this is just the
		 * width and the height values
		 **/
		Size sz;
		/** This is the image to be processed if the selected camera is -1 **/
		String imageFile;
	}

	/**
	 * A class to hold any visionTable data returned by process() :) :) :} :] :]
	 */

	public static class VisionData {

		public static class Target {
			/** The x center of the target in the image **/
			int xCentre;
			/**
			 * The normalized x center of the target that is between 0 and 1
			 **/
			double xCentreNorm;
			/** The y center of the target in the image **/
			int yCentre;
			/**
			 * The normalized y center of the target that is between 0 and 1
			 **/
			double yCentreNorm;
			/**
			 * A value between 0 and 1 representing the percentage of the image
			 * the target takes up
			 **/
			double areaNorm; // [0,1] representing how much of the screen it
								// occupies
			/** The rectangle made from x and y centers **/
			Rect boundingBox;
		}

		/** The List of all the targets in the image **/
		ArrayList<Target> targetsFound = new ArrayList<Target>();
		/**
		 * The target that is the most appealing, how it is chosen depends on
		 * the distToCenterImportance value in the vision parameters
		 **/
		Target preferredTarget;
		/** The image that contains the targets **/
		public Mat outputImg = new Mat();
		/** The frames per second **/
		public Mat binMask = new Mat();
		public double fps;
	}

	/**
	 * Initilizes the Network Tables WARNING! Change 127.0.0.1 to the robot ip
	 * before it is on master or it will not be fun :)
	 */
	public static void initNetworkTables() {
		// Tells the NetworkTable class that this is a client
		NetworkTable.setClientMode();
		// Sets the interval for updating NetworkTables
		NetworkTable.setUpdateRate(0.02);
		// Sets the team number
		//NetworkTable.setTeam(2706); // Use this for the robit
		// Enables DSClient
		NetworkTable.setDSClientEnabled(true); // and this for the robit
		// Sets the IP adress to connect to
		NetworkTable.setIPAddress("localhost"); //Use this for testing
		// Initilizes NetworkTables
		NetworkTable.initialize();
		// Sets the vision table to the "vision" table that is in NetworkTables
		visionTable = NetworkTable.getTable("vision");
	}

	/**
	 * Loads the visionTable params! :]
	 **/

	public static void loadVisionParams() {
		// Initilizes the properties
		Properties properties = new Properties();
		try {
			// The file input stream that contains the vision parameters data
			// from visionParams.properties
			FileInputStream in = new FileInputStream("visionParams.properties");
			// Tells the properties to use the file input stream
			properties.load(in);
			// Sets the selected camera to the selected camera in the properties
			// file
			visionParams.cameraSelect = Integer.valueOf(properties.getProperty("CameraSelect"));
			// Sets the minimum hue to the minimum hue in the properties file
			visionParams.minHue = Integer.valueOf(properties.getProperty("minHue"));
			// Sets the maximum hue to the maximum hue in the properties file
			visionParams.maxHue = Integer.valueOf(properties.getProperty("maxHue"));
			// Sets the minimum saturation to the minimum saturation in the
			// properties file
			visionParams.minSaturation = Integer.valueOf(properties.getProperty("minSaturation"));
			// Sets the maximum saturation to the maximum saturation in the
			// properties file
			visionParams.maxSaturation = Integer.valueOf(properties.getProperty("maxSaturation"));
			// Sets the minimum value to the minimum value in the properties
			// file
			visionParams.minValue = Integer.valueOf(properties.getProperty("minValue"));
			// Sets the maximum value to the maximum value in the properties
			// file
			visionParams.maxValue = Integer.valueOf(properties.getProperty("maxValue"));
			// Sets the minimum area to the minimum area in the properties file
			visionParams.minArea = Double.valueOf(properties.getProperty("minArea"));
			// Sets the erode dilate iterations to the erode dilation iterations
			// in the properties file
			visionParams.erodeDilateIterations = Integer.valueOf(properties.getProperty("erodeDilateIterations"));

			// Sets the aspect ratio threshold to the aspect ratio threshold in
			// the properties file
			visionParams.aspectRatioThresh = Double.valueOf(properties.getProperty("aspectRatioThresh"));
			// Sets the distance to center importance to the distance to center
			// importance in the properties file
			visionParams.distToCentreImportance = Double.valueOf(properties.getProperty("distToCentreImportance"));
			// Sets the output path for dumping images to the image dump path in
			// the properties file
			outputPath = properties.getProperty("imgDumpPath");
			// Sets the seconds between image dumps to the seconds between image
			// dumps in the properties file
			seconds_between_img_dumps = Double.valueOf(properties.getProperty("imgDumpWait"));
			// Sets the image file path to the image file path in the properties
			// file, this is only used if the selected camera is -1
			visionParams.imageFile = properties.getProperty("imageFile");
			if (outputPath.endsWith("/") || outputPath.endsWith("\\")) {
				timestampfile = new File(outputPath + "time.stamp");
			} else {
				timestampfile = new File(outputPath + "/" + "time.stamp");
			}
			// if the file, take the timestamp from there
			if (timestampfile.exists()) {
				Scanner s = new Scanner(timestampfile);
				try{
					timestamp = Integer.valueOf(s.nextLine()).intValue();
				}catch(Exception e){
					e.printStackTrace();
				}
				timestamp++;
				s.close();
			}

			// Gets the resolution from the properties file
			String resolution = properties.getProperty("resolution");
			if (resolution.equals("320x240")) {
				// Sets the resolution to 320 by 240
				visionParams.width = 320;
				visionParams.height = 240;
			} else if (resolution.equals("640x480")) {
				// Sets the resolution to 640 by 480
				visionParams.width = 640;
				visionParams.height = 480;
			} else if (resolution.equals("160x120")) {
				// Sets the resolution to 160 by 120
				visionParams.width = 160;
				visionParams.height = 120;
			} else if (resolution.equals("80x60")) {
				// Sets the resolution to 80 by 60
				visionParams.width = 80;
				visionParams.height = 60;
			} else {
				// Throws a error that the the resolution is invalid
				throw new IllegalArgumentException("Error: " + properties.getProperty("resolution")
						+ " is not a supported resolution.\n" + "Allowed: 80x60, 160x120, 320x240, 640x480.");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			System.err.println("\n\nError reading the params file, check if the file is corrupt?");
			System.exit(1);
		}
	}

	/**
	 * Saves the vision parameters to a file
	 * 
	 **/
	public static void saveVisionParams() {
		// Initilizes the properties object
		Properties properties = new Properties();
		try {
			// Sets the camera select property in the file to the camera select
			// value
			properties.setProperty("CameraSelect", String.valueOf(visionParams.cameraSelect));
			// Sets the minimum hue property in the file to the minimum hue
			// value
			properties.setProperty("minHue", String.valueOf(visionParams.minHue));
			// Sets the maximum hue property in the file to the maximum hue
			// value
			properties.setProperty("maxHue", String.valueOf(visionParams.maxHue));
			// Sets the minimum saturation property in the file to the minimum
			// saturation value
			properties.setProperty("minSaturation", String.valueOf(visionParams.minSaturation));
			// Sets the maximum saturation property in the file to the maximum
			// saturation value
			properties.setProperty("maxSaturation", String.valueOf(visionParams.maxSaturation));
			// Sets the minimum value property in the file to the minimum value
			// value
			properties.setProperty("minValue", String.valueOf(visionParams.minValue));
			// Sets the maximum value property in the file to the maximum value
			// value
			properties.setProperty("maxValue", String.valueOf(visionParams.maxValue));
			// Sets the erode dilate iterations property in the file to the
			// erode dilate iterations value
			properties.setProperty("erodeDilateIterations", String.valueOf(visionParams.erodeDilateIterations));
			// Sets the minimum area property in the file to the minimum area
			// value
			properties.setProperty("minArea", String.valueOf(visionParams.minArea));
			// Sets the aspect ratio threshold property in the file to the
			// aspect ratio threshold value
			properties.setProperty("aspectRatioThresh", String.valueOf(visionParams.aspectRatioThresh));
			// Sets the distance to center importance property in the file to
			// the distance to center importance value
			properties.setProperty("distToCentreImportance", String.valueOf(visionParams.distToCentreImportance));
			// Sets the image file property in the file to the image file value
			properties.setProperty("imageFile", visionParams.imageFile);
			// Sets the resolution property in the file to the resolution value
			properties.setProperty("resolution", visionParams.width + "x" + visionParams.height);
			// Sets the image dumping interval property in the file to the image
			// dumping interval value
			properties.setProperty("imgDumpWait", String.valueOf(seconds_between_img_dumps));
			// Sets the image dumping path property in the file to the image
			// dumping path value
			properties.setProperty("imgDumpPath", outputPath);
			// Initilizes the output stream to the vision parameters file
			FileOutputStream out = new FileOutputStream("visionParams.properties");
			// Dumps the properties to the output stream
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
	public static void sendVisionDataOverNetworkTables(VisionData visionData) {

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
	public static BufferedImage matToBufferedImage(Mat matrix) throws IOException {
		MatOfByte mob = new MatOfByte();
		Imgcodecs.imencode(".jpg", matrix, mob);
		byte ba[] = mob.toArray();

		BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
		return bi;
	}

	/**
	 * Converts a Buffered Image to a OpenCV Matrix
	 * 
	 * @param Buffered
	 *            Image to convert to matrix
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
	 * @param The
	 *            image to dump to a file
	 * @param image
	 *            the image to be dumped
	 * @param suffix
	 *            the suffix to put on the file name
	 * @throws IOException
	 */

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

	public static boolean b = true;
	
	/**
	 * The main method! Very important Do not delete! :] :]
	 *
	 * 
	 * @param The
	 *            command line arguments
	 */
	public static void main(String[] args) {
		// Must be included!
		// Loads OpenCV
		System.loadLibrary("opencv_java310");

		// Connect NetworkTables, and get access to the publishing table
		initNetworkTables();

		// read the vision calibration values from file.
		loadVisionParams();

		try {
			// Copys the vision parameters to a usb flash drive
			Files.copy(Paths.get("visionParams.properties"),
					Paths.get(outputPath + "/visionParams-" + timestamp + ".properties"),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		// Initilizes a Matrix to hold the frame

		frame = new Mat();

		// Whether to use a camera, or load an image file from disk.
		if (visionParams.cameraSelect == -1) {
			useCamera = false;
		}

		if (useCamera) {
			VideoCapture camera = CameraServer.cameras.get(visionParams.cameraSelect);
			
			if(camera == null) {
				System.err.println("Failed to connect to camera!");
				System.exit(1);
			}
			
			// Sets camera parameters
			int fourcc = VideoWriter.fourcc('M', 'J', 'P', 'G');
			camera.set(Videoio.CAP_PROP_FOURCC, fourcc);
			camera.set(Videoio.CAP_PROP_FRAME_WIDTH, visionParams.width);
			camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, visionParams.height);

			camera.read(frame);

			if (!camera.isOpened()) {
				// If the camera didn't open throw an error
				System.err.println("Error: Can not connect to camera");
				// Exit
				System.exit(1);
			}
		} else {
			// load the image from file.
			try {
				frame = bufferedImageToMat(ImageIO.read(new File(visionParams.imageFile)));
			} catch (IOException e) {
				e.printStackTrace();
				frame = new Mat();
			}
		}
		// The window to display the raw image
		DisplayGui guiRawImg = null;
		// The window to display the processed image
		DisplayGui guiProcessedImg = null;
		// Wether to open the guis
		boolean use_GUI = true;
		// If on Linux don't use guis
		if (System.getProperty("os.name").toLowerCase().indexOf("raspbian") != -1) {
			use_GUI = false;
		}
		// Set the vision parameters size
		visionParams.sz = new Size(visionParams.width, visionParams.height);
		// Set up the GUI display windows
		if (use_GUI) {
			try {
				// Initilizes the window to display the raw image
				guiRawImg = new DisplayGui(matToBufferedImage(frame), "Raw Camera Image",true);
				// Initilizes the window to display the processed image
				guiProcessedImg = new DisplayGui(matToBufferedImage(frame), "Processed Image",true);
				// Initilizes the parameters selector
				ParamsSelector selector = new ParamsSelector(true,true);
				guiRawImg.addKeyListener(selector);
				guiProcessedImg.addKeyListener(selector);
			} catch (IOException e) {
				// means mat2BufferedImage broke
				// non-fatal error, let the program continue
			}
		}
		ImageDumpScheduler.start();
		// Main video processing loop
		while (b) {
			if (useCamera) {
				// Read the frame from the camera, if it fails try again
				frame = CameraServer.getFrame(visionParams.cameraSelect);
				if(frame == null) {
					System.err.println("Couldn't get frame from camera!");
					continue;
				}
			} // else use the image from disk that we loaded above
			else{
				// load the image from file.
	            try {
        	        frame = bufferedImageToMat(ImageIO.read(new File(visionParams.imageFile)));
                } catch (IOException e) {
                	e.printStackTrace();
                    frame = new Mat();
                }

			}
			if (use_GUI) {
				// Resize the frame
				Imgproc.resize(frame, frame, visionParams.sz);
			}
			// Process the frame!
			// Log when the pipeline starts
			long pipelineStart = System.nanoTime();
			// Process the frame
			VisionData visionData = Pipeline.process(frame, visionParams, use_GUI);
			// Log when the pipeline stops
			long pipelineEnd = System.nanoTime();
			// Selects the prefered target
			Pipeline.selectPreferredTarget(visionData, visionParams);
			// Creates the raw output image object
			Mat rawOutputImg;
			if (use_GUI) {
				// If use gui then draw the prefered target
				// Sets the raw image to the frame
				rawOutputImg = frame.clone();
				// Draws the preffered target
				Pipeline.drawPreferredTarget(rawOutputImg, visionData);
			} else {
				// Sets the raw image to the frame
				rawOutputImg = frame;
			}
			// Sends the data to the vision table
			sendVisionDataOverNetworkTables(visionData);
			lastData = visionData;
			// display the processed frame in the GUI
			if (use_GUI) {
				try {
					// May throw a NullPointerException if initializing
					// the window failed
					BufferedImage raw = matToBufferedImage(rawOutputImg);
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
			if (useCamera) {
				// log images to file once every seconds_between_img_dumps
				double elapsedTime = ((double) System.currentTimeMillis() / 1000) - current_time_seconds;
				// If the elapsed time is more that the seconds between image
				// dumps
				// then dump images asynchronously
				if (elapsedTime >= seconds_between_img_dumps) {
					// Sets the current number of seconds
					current_time_seconds = (((double) System.currentTimeMillis()) / 1000);
					// Clones the frame
					Mat finalFrame = frame.clone();
					try {
						Mat draw = finalFrame.clone();
						Pipeline.drawPreferredTarget(draw, visionData);
						Bundle b = new Bundle(matToBufferedImage(finalFrame), matToBufferedImage(visionData.binMask),
								matToBufferedImage(draw), timestamp);
						ImageDumpScheduler.schedule(b);
						timestamp++;
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}
				}
			}
			// Display the frame rate onto the console
			double pipelineTime = (((double) (pipelineEnd - pipelineStart)) / Pipeline.NANOSECONDS_PER_SECOND) * 1000;
			System.out.printf("Vision FPS: %3.2f, pipeline took: %3.2f ms\n", visionData.fps, pipelineTime);
		}
	} // end main video processing loop

	public static void hideMiddle() {
		showMiddle = false;
	}

	public static void showMiddle() {
		showMiddle = true;
	}
}
