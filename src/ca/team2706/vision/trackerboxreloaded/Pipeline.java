package ca.team2706.vision.trackerboxreloaded;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import ca.team2706.vision.trackerboxreloaded.Main.VisionData;
import ca.team2706.vision.trackerboxreloaded.Main.VisionParams;

public class Pipeline {
	
	/** Numerical Constants **/
	private static final int NANOSECONDS_PER_SECOND = 1000000000;
	
	public static long fpsTimer = System.nanoTime();

	 /**
     * The vision pipeline!
     *
     * @param src Raw source image to process
     * @returns turn All the data!
     */
	public static VisionData process(Mat src, VisionParams visionParams) throws Exception {

		// If there's any data or intermediate images that you want to return,
		// add them to the VisionData class
		// For example, any numbers that we want to return to the roboRIO.
		VisionData visionData = new VisionData();

		// Colour threshold
		Mat hsvThreshold = new Mat();
		Core.inRange(src, new Scalar(visionParams.minHue, visionParams.minSaturation, visionParams.minValue),
				new Scalar(visionParams.maxHue, visionParams.maxSaturation, visionParams.maxValue), hsvThreshold);

		// Dilate - Erode
		Mat dilatedImg = new Mat();
		Mat erode = new Mat();
		Imgproc.dilate(hsvThreshold, dilatedImg, new Mat(), new Point(), visionParams.erodeDilateIterations,
				Core.BORDER_CONSTANT, new Scalar(0));
		Imgproc.erode(dilatedImg, erode, new Mat(), new Point(), visionParams.erodeDilateIterations,
				Core.BORDER_CONSTANT, new Scalar(0));

		visionData.outputImg = erode;
		long now = System.nanoTime();
		visionData.fps = ((double) NANOSECONDS_PER_SECOND) / (now - fpsTimer);
		fpsTimer = now;
		return visionData;
	}

}
