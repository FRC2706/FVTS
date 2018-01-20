package ca.team2706.vision.trackerboxreloaded;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import edu.wpi.cscore.HttpCamera;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.tables.ITable;

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

		// This stores our reference to our mjpeg server for streaming the input
		// image
		MjpegServer inputStream = new MjpegServer("MJPEG Server", streamPort);

		// Selecting a Camera
		// Uncomment one of the 2 following camera options
		// The top one receives a stream from another device, and performs
		// operations based on that
		// On windows, this one must be used since USB is not supported
		// The bottom one opens a USB camera, and performs operations on that,
		// along with streaming
		// the input image so other devices can see it.

		// HTTP Camera
		/*
		 * // This is our camera name from the robot. this can be set in your
		 * robot code with the following command //
		 * CameraServer.getInstance().startAutomaticCapture("YourCameraNameHere"
		 * ); // "USB Camera 0" is the default if no string is specified String
		 * cameraName = "USB Camera 0"; HttpCamera camera =
		 * setHttpCamera(cameraName, inputStream); // It is possible for the
		 * camera to be null. If it is, that means no camera could // be found
		 * using NetworkTables to connect to. Create an HttpCamera by giving a
		 * specified stream // Note if this happens, no restream will be created
		 * if (camera == null) { camera = new HttpCamera("CoprocessorCamera",
		 * "YourURLHere"); inputStream.setSource(camera); }
		 */

		/***********************************************/

		// USB Camera

		// This gets the image from a USB camera
		// Usually this will be on device 0, but there are other overloads
		// that can be used
		/*
		 * THIS DOESNT WORK ON WINDOWS UsbCamera camera = setUsbCamera(0,
		 * inputStream); // Set the resolution for our camera, since this is
		 * over USB camera.setResolution(640,480);
		 * 
		 * 
		 * // This creates a CvSink for us to use. This grabs images from our
		 * selected camera, // and will allow us to use those images in opencv
		 * CvSink imageSink = new CvSink("CV Image Grabber");
		 * imageSink.setSource(camera);
		 * 
		 * // This creates a CvSource to use. This will take in a Mat image that
		 * has had OpenCV operations // operations CvSource imageSource = new
		 * CvSource("CV Image Source", VideoMode.PixelFormat.kMJPEG, 640, 480,
		 * 30); MjpegServer cvStream = new MjpegServer("CV Image Stream", 1186);
		 * cvStream.setSource(imageSource);
		 * 
		 * // All Mats and Lists should be stored outside the loop to avoid
		 * allocations // as they are expensive to create Mat inputImage = new
		 * Mat(); Mat hsv = new Mat();
		 * 
		 * // Infinitely process image while (true) { // Grab a frame. If it has
		 * a frame time of 0, there was an error. // Just skip and continue long
		 * frameTime = imageSink.grabFrame(inputImage); if (frameTime == 0)
		 * continue;
		 * 
		 * // Below is where you would do your OpenCV operations on the provided
		 * image // The sample below just changes color source to HSV
		 * Imgproc.cvtColor(inputImage, hsv, Imgproc.COLOR_BGR2HSV);
		 * 
		 * // Here is where you would write a processed image that you want to
		 * restreams // This will most likely be a marked up image of what the
		 * camera sees // For now, we are just going to stream the HSV image
		 * imageSource.putFrame(hsv); }
		 */

		VideoCapture camera = new VideoCapture(0);
		Mat frame = new Mat();
		camera.read(frame);

		if (!camera.isOpened()) {
			System.out.println("Error");
		} else {

			camera.read(frame);

			DisplayGui gui = null;
			try {
				gui = new DisplayGui(Mat2BufferedImage(frame));
			} catch (Exception e) {
				e.printStackTrace();
			}
			while (true) {
				if (camera.read(frame)) {
					try {

						BufferedImage img = process(frame);
						gui.updateImage(img);
					} catch (Exception e) {
						System.out.println("Window closed");
						Runtime.getRuntime().halt(0);
					}
				}
			}
		}
		camera.release();
	}

	private static HttpCamera setHttpCamera(String cameraName, MjpegServer server) {
		// Start by grabbing the camera from NetworkTables
		NetworkTable publishingTable = NetworkTable.getTable("CameraPublisher");
		// Wait for robot to connect. Allow this to be attempted indefinitely
		while (true) {
			try {
				if (publishingTable.getSubTables().size() > 0) {
					break;
				}
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		HttpCamera camera = null;
		if (!publishingTable.containsSubTable(cameraName)) {
			return null;
		}
		ITable cameraTable = publishingTable.getSubTable(cameraName);
		String[] urls = cameraTable.getStringArray("streams", null);
		if (urls == null) {
			return null;
		}
		ArrayList<String> fixedUrls = new ArrayList<String>();
		for (String url : urls) {
			if (url.startsWith("mjpg")) {
				fixedUrls.add(url.split(":", 2)[1]);
			}
		}
		camera = new HttpCamera("CoprocessorCamera", fixedUrls.toArray(new String[0]));
		server.setSource(camera);
		return camera;
	}

	private static UsbCamera setUsbCamera(int cameraId, MjpegServer server) {
		// This gets the image from a USB camera
		// Usually this will be on device 0, but there are other overloads
		// that can be used
		UsbCamera camera = new UsbCamera("CoprocessorCamera", cameraId);
		server.setSource(camera);
		return camera;
	}

	private static BufferedImage Mat2BufferedImage(Mat matrix) throws Exception {
		MatOfByte mob = new MatOfByte();
		Imgcodecs.imencode(".jpg", matrix, mob);
		byte ba[] = mob.toArray();

		BufferedImage bi = ImageIO.read(new ByteArrayInputStream(ba));
		return bi;
	}

	public static BufferedImage process(Mat src) throws Exception {
		// Pyramid down and back up
		//Imgproc.pyrDown(src, src, new Size(src.cols() / 2, src.rows() / 2), Core.BORDER_DEFAULT);
		//Imgproc.pyrUp(src, src, new Size(src.cols() * 2, src.rows() * 2), Core.BORDER_DEFAULT);

		// Canny edge detection
		/*
		Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
		Imgproc.threshold(src, src, 120, 255, Imgproc.THRESH_BINARY);
		Imgproc.Canny(src, src, 60, 60 * 3);
		
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		//src.convertTo(src, CvType.CV_32SC1);
		Imgproc.findContours(src, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		
		Mat contourImg = new Mat(src.size(), CvType.CV_8U);
		for (int i = 0; i < contours.size(); i++) {
		    Imgproc.drawContours(contourImg, contours, i, new Scalar(255, 255, 255));
		}
		return Mat2BufferedImage(contourImg);
		*/
		return Mat2BufferedImage(src);
	}
}