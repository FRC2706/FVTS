package ca.team2706.vision.trackerboxreloaded;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import ca.team2706.vision.trackerboxreloaded.Main.VisionData;
import ca.team2706.vision.trackerboxreloaded.Main.VisionParams;

public class Pipeline {
	public static void start(){
		System.loadLibrary("opencv_java310");
	}
	
	public static VisionData process(Mat src,VisionParams visionParams) throws Exception {

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
	
}
