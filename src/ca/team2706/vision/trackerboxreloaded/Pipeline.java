package ca.team2706.vision.trackerboxreloaded;

import ca.team2706.vision.trackerboxreloaded.Main.VisionData;
import ca.team2706.vision.trackerboxreloaded.Main.VisionParams;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class Pipeline {
	
	/** Numerical Constants **/
	private static final int NANOSECONDS_PER_SECOND = 1000000000;
	
	public static long fpsTimer = System.nanoTime();


	/**
	 * The vision pipeline!
	 *
	 * @param src Raw source image to process
	 * @return All the data!
	 */
	public static VisionData process(Mat src, VisionParams visionParams) {

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
			if ( (rect.width <= (2 + visionParams.aspectRatioThresh)*rect.height) && (rect.width >= (2 - visionParams.aspectRatioThresh)*rect.height) ) {
//			if (rect.width <= ((rect.height*2) + (rect.height*visionParams.aspectRatioThresh)) && rect.width >= ((rect.height*2) - (rect.height*visionParams.aspectRatioThresh))) {
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

		// DRAW STUFF ONTO THE OUTPUT IMAGE
		// for each target found, draw the bounding box and centre

		for (VisionData.Target targetCenter : visionData.targetsFound)
		{
			Point centerTarget = new Point(targetCenter.xCenter, targetCenter.yCenter);
			Scalar color = new Scalar(237, 19, 75);
			Imgproc.circle(src, centerTarget, 8, color);
		}

		long now = System.nanoTime();
		visionData.fps = ((double) NANOSECONDS_PER_SECOND) / (now - fpsTimer);
		fpsTimer = now;

		return visionData;
	}

}
