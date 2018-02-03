package ca.team2706.vision.trackerboxreloaded;

import ca.team2706.vision.trackerboxreloaded.Main.VisionData;
import ca.team2706.vision.trackerboxreloaded.Main.VisionParams;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class Pipeline {
	
	/** Numerical Constants **/
	public static final int NANOSECONDS_PER_SECOND = 1000000000;
	
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
			Rect boundingRect = Imgproc.boundingRect(contour);

			// height * width for area (easier and less CPU cycles than contour.area)
			int area = boundingRect.width * boundingRect.height;

            if (area >= visionParams.minArea) {
                // TODO Matt to write an explanation of the formula below
                int target1CtrX, target1CtrY, target2CtrX, target2CtrY;
                if ((boundingRect.width <= (2 + visionParams.aspectRatioThresh) * boundingRect.height) && (boundingRect.width >= (2 - visionParams.aspectRatioThresh) * boundingRect.height)) {

                    target1CtrX = boundingRect.x + (boundingRect.width / 4);
                    target1CtrY = boundingRect.y + (boundingRect.height / 2);
                    target2CtrX = boundingRect.x + ((3 * boundingRect.width) / 4);
                    target2CtrY = boundingRect.y + (boundingRect.height / 2);

                    visionData.targetsFound.add(new VisionData.Target(target1CtrX, target1CtrY, boundingRect));
                    visionData.targetsFound.add(new VisionData.Target(target2CtrX, target2CtrY, boundingRect));
                } else {
                    target1CtrX = boundingRect.x + (boundingRect.width / 2);
                    target1CtrY = boundingRect.y + (boundingRect.height / 2);

                    visionData.targetsFound.add(new VisionData.Target(target1CtrX, target1CtrY, boundingRect));
                }
            }


			//system.out.println("area: ", area, "xCenter: ", xCenter, "yCenter", yCenter);
		}

		visionData.outputImg = erode;




		// DRAW STUFF ONTO THE OUTPUT IMAGE
		// for each target found, draw the bounding box and centre

		for (VisionData.Target target : visionData.targetsFound)
		{
			Point centerTarget = new Point(target.xCenter, target.yCenter);
			Scalar color = new Scalar(237, 19, 75);
			Imgproc.circle(src, centerTarget, 8, color, -1);
			Imgproc.rectangle(src, new Point(target.boundingBox.x, target.boundingBox.y),
					new Point(target.boundingBox.x + target.boundingBox.width, target.boundingBox.y + target.boundingBox.height), color, 5);
		}

		long now = System.nanoTime();
		visionData.fps = ((double) NANOSECONDS_PER_SECOND) / (now - fpsTimer);
		fpsTimer = now;

		return visionData;
	}

}
