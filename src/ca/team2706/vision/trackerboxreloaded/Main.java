package ca.team2706.vision.trackerboxreloaded;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Main {

	// Camera Type
	// Set to 1 for USB camera, set to 0 for webcam, I think 0 is USB if
	// there is no webcam :/


	/** Numerical Constants **/
	private static final int NANOSECONDS_PER_SECOND = 1000000000;

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
		public double dBoxBuffer;
	}

	private static VisionParams visionParams = new VisionParams();

	/**
	 * A class to hold any vision data returned by process()
	 */
	private static class VisionData {

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

		ArrayList<Target> targetsFound = new ArrayList<>();
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
			visionParams.dBoxBuffer = Double.valueOf(properties.getProperty("dBoxBuffer"));
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

    public static long fpsTimer = System.nanoTime();

    /**
     * The vision pipeline!
     *
     * @param src Raw source image to process
     * @return All the data!
     */
	public static VisionData process(Mat src) {

		// If there's any data or intermediate images that you want to return, add them to the VisionData class
		// For example, any numbers that we want to return to the roboRIO.
		VisionData visionData = new VisionData();


		// Colour threshold
		Mat hsvThreshold = new Mat();
		Core.inRange(src, new Scalar(visionParams.minHue, visionParams.minSaturation, visionParams.minValue),
				new Scalar(visionParams.maxHue, visionParams.maxSaturation, visionParams.maxValue), hsvThreshold);

		// Erode - Dilate
		Mat dilatedImg = new Mat();
		Mat erode = new Mat();
		Imgproc.erode(hsvThreshold, erode, new Mat(), new Point(), visionParams.erodeDilateIterations, Core.BORDER_CONSTANT, new Scalar(0));
		Imgproc.dilate(erode, dilatedImg, new Mat(), new Point(), visionParams.erodeDilateIterations, Core.BORDER_CONSTANT, new Scalar(0));

		//Find contours
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(dilatedImg, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		//Make Bounding Box
		for (MatOfPoint contour : contours)
		{
    	Rect rect = Imgproc.boundingRect(contour);

			// height * width for area (easier and less CPU cycles than contour.area)
			int area = rect.width * rect.height;

			// TODO Matt to write an explanation of the formula below
			int x,y,xt,yt;
		if (rect.width <= ((rect.height*2) + (rect.height*visionParams.dBoxBuffer)) && rect.width >= ((rect.height*2) - (rect.height*visionParams.dBoxBuffer))) {
			x = rect.x + (rect.width/4);
			y = rect.y + (rect.height/2);
			xt = rect.x + ((3*rect.width)/4);
			yt = rect.y + (rect.height/2);

			visionData.targetsFound.add(new VisionData.Target(x,y));
			visionData.targetsFound.add(new VisionData.Target(xt,yt));
		} else {
			x = rect.x + (rect.width/2);
			y = rect.y + (rect.height/2);

			visionData.targetsFound.add(new VisionData.Target(x,y));
		}


			//system.out.println("area: ", area, "xCenter: ", xCenter, "yCenter", yCenter);
}

		visionData.outputImg = erode;

        long now = System.nanoTime();
		visionData.fps = ((double) NANOSECONDS_PER_SECOND) / (now - fpsTimer);
        fpsTimer = now;

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

                    // Display the frame rate
                    System.out.printf("Vision FPS: %3.2f, camera FPS: %3.2f \n", visionData.fps, cameraFps, "");
				}
				else {
					System.err.println("Error: Failed to get a frame from the camera");
				}
			} // end main video processing loop
		}
		camera.release();
	}
	/**Normalises points from a frame from the camera to a value 
	 * between -1,-1 and 1,1 where the center of the image is 0,0
	 * this is very usefull
	 * 
	 * formula:
	 * 
	 * (x - width/2)/(width/2)
	 * 
	 * and the same with y but use height instead!
	 * 
	 * @param The point to be normalised
	 * @return The normalised point
	 */
	public NormalisedPoint normalisePoint(ca.team2706.vision.trackerboxreloaded.Point point){
		int midW = point.getFrameData().getWidth()/2;
		int midH = point.getFrameData().getHeight()/2;
		int x = Integer.valueOf(point.getX());
		int y = Integer.valueOf(point.getY());
		int newX;
		int newY;
		newX = (x - midW) / midW;
		newY = (y - midH) / midH;
		return new NormalisedPoint(newX,newY);
	}
}
