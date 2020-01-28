package ca.team2706.mergevision.core;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import ca.team2706.mergevision.core.VisionData.Target;
import ca.team2706.mergevision.core.params.VisionParams;

public class Pipeline {

	/** Numerical Constants **/
	public static final int NANOSECONDS_PER_SECOND = 1000000000;

	/** The fps timer **/
	public static long fpsTimer = System.nanoTime();

	/**
	 * The visionTable pipeline!
	 *
	 * @param src          Raw source image to process
	 * @param visionParams Parameters for visionTable
	 * @return All the data!
	 */
	public static VisionData process(Mat src, VisionParams visionParams, boolean use_GUI) {

		// As a memory footprint optimization, when running on a Pi, re-use one working
		// image in memory
		Mat dilated, erodeOne, erodeTwo;

		dilated = new Mat();
		erodeOne = dilated;
		erodeTwo = dilated;
		// Calculate the image area
		int imgArea = src.height() * src.width();

		// If there's any data or intermediate images that you want to return, add them
		// to the VisionData class
		// For example, any numbers that we want to return to the roboRIO.
		VisionData visionData = new VisionData();

		visionData.params = visionParams;

		// Colour threshold
		Mat hsvThreshold = new Mat();

		Core.inRange(src, new Scalar(visionParams.getByName("minHue").getValueI(), visionParams.getByName("minSaturation").getValueI(), visionParams.getByName("minValue").getValueI()),
				new Scalar(visionParams.getByName("maxHue").getValueI(), visionParams.getByName("maxSaturation").getValueI(), visionParams.getByName("maxValue").getValueI()), hsvThreshold);

		// Erode - Dilate*2 - Erode
		Imgproc.erode(hsvThreshold, erodeOne, new Mat(), new Point(), visionParams.getByName("erodeDilateIterations").getValueI(),
				Core.BORDER_CONSTANT, new Scalar(0));
		Imgproc.dilate(erodeOne, dilated, new Mat(), new Point(), 2 * visionParams.getByName("erodeDilateIterations").getValueI(),
				Core.BORDER_CONSTANT, new Scalar(0));
		Imgproc.erode(dilated, erodeTwo, new Mat(), new Point(), visionParams.getByName("erodeDilateIterations").getValueI(),
				Core.BORDER_CONSTANT, new Scalar(0));

		visionData.binMask = erodeTwo.clone();

		// Find contours
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(dilated, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		// Make Bounding Box
		for (MatOfPoint contour : contours) {

			Rect boundingRect = Imgproc.boundingRect(contour);

			// height * width for area (easier and less CPU cycles than contour.area)
			double areaNorm = ((double) boundingRect.width * boundingRect.height) / imgArea;

			if (areaNorm >= visionParams.getByName("minArea").getValueD()) {

				VisionData.Target target = new VisionData.Target();
				target.boundingBox = boundingRect;
				target.contour = contour;
				target.xCentre = target.boundingBox.x + (target.boundingBox.width / 2);
				target.xCentreNorm = ((double) target.xCentre - (src.width() / 2)) / (src.width() / 2);
				target.yCentre = target.boundingBox.y + (target.boundingBox.height / 2);
				target.yCentreNorm = ((double) target.yCentre - (src.height() / 2)) / (src.height() / 2);
				target.areaNorm = (target.boundingBox.height * target.boundingBox.width) / ((double) imgArea);
				visionData.targetsFound.add(target);
			}
			// else
			// skip this contour because it's too small
		}

		long now = System.nanoTime();
		visionData.fps = ((double) NANOSECONDS_PER_SECOND) / (now - fpsTimer);
		visionData.fps = ((int) (visionData.fps * 10)) / 10.0; // round to 1 decimal place
		fpsTimer = now;

		return visionData;
	}

	/**
	 * From all the targets found in visionData.targetsFound, select the one that
	 * we're going to send to the roboRIO.
	 *
	 * @param visionData
	 */
	public static void selectPreferredTarget(VisionData visionData, VisionParams visionParams, boolean group, int groupAngle) {

		if (visionData.targetsFound.size() == 0) {
			return;
		}

		if (group) {

			ArrayList<Target> newTargets = new ArrayList<Target>();

			for (Target target : visionData.targetsFound) {

				MatOfPoint2f contour = new MatOfPoint2f(target.contour.toArray());

				RotatedRect rect = Imgproc.minAreaRect(contour);

				rect.angle = rect.angle + groupAngle;

				if (rect.angle > 0) {
					continue;
				}

				Target minTarget = null;
				double minDist = Double.MAX_VALUE;

				for (Target target2 : visionData.targetsFound) {

					if (target2 != target) {

						if (target2.xCentre < target.xCentre) {
							continue;
						}

						MatOfPoint2f contour2 = new MatOfPoint2f(target2.contour.toArray());

						RotatedRect rect2 = Imgproc.minAreaRect(contour2);
						rect2.angle = rect2.angle + groupAngle;

						if (rect2.angle < 0) {
							continue;
						}

						double w = Math.abs(target.xCentre - target2.xCentre);
						double h = Math.abs(target.yCentre - target2.yCentre);

						double dist = Math.sqrt(Math.pow(w, 2) + Math.pow(h, 2));

						if (dist < minDist) {

							minDist = dist;
							minTarget = target2;

						}

					}

				}

				boolean missing = false;

				for (Target target2 : visionData.targetsFound) {

					if (target2 != target) {

						if (target2.xCentre < target.xCentre) {
							continue;
						}

						MatOfPoint2f contour2 = new MatOfPoint2f(target2.contour.toArray());

						RotatedRect rect2 = Imgproc.minAreaRect(contour2);
						rect2.angle = rect2.angle + 40;

						if (rect2.angle > 0) {
							continue;
						}

						double w = Math.abs(target.xCentre - target2.xCentre);
						double h = Math.abs(target.yCentre - target2.yCentre);

						double dist = Math.sqrt(Math.pow(w, 2) + Math.pow(h, 2));

						if (dist < minDist) {

							missing = true;

						}

					}

				}

				if (minTarget == null || missing) {
					continue;
				}

				Target target3 = new Target();

				double x = target.boundingBox.x < minTarget.boundingBox.x ? target.boundingBox.x
						: minTarget.boundingBox.x;
				double y = target.boundingBox.y < minTarget.boundingBox.y ? target.boundingBox.y
						: minTarget.boundingBox.y;

				double width = Math.abs(target.boundingBox.x - minTarget.boundingBox.x) + minTarget.boundingBox.width;
				double height = Math.abs(target.boundingBox.y - minTarget.boundingBox.y) + minTarget.boundingBox.height;

				target3.boundingBox = new Rect((int) x, (int) y, (int) width, (int) height);
				target3.xCentre = (int) (x + (width / 2));
				target3.xCentreNorm = ((double) target3.xCentre - (visionData.binMask.width() / 2))
						/ (visionData.binMask.width() / 2);
				target3.yCentre = (int) (y + (height / 2));
				target3.yCentreNorm = ((double) target3.yCentre - (visionData.binMask.height() / 2))
						/ (visionData.binMask.height() / 2);
				target3.areaNorm = (target3.boundingBox.height * target3.boundingBox.width)
						/ ((double) visionData.binMask.width() * visionData.binMask.height());

				newTargets.add(target3);

			}

			visionData.targetsFound = newTargets;

		}

		// loop over the targets to find the largest area of any target found.
		// this is so we can give the largest a score of 1.0, and each other target a
		// score that is a
		// percentage of the area of the largest.
		double largestAreaNorm = Double.NEGATIVE_INFINITY;
		for (VisionData.Target target : visionData.targetsFound) {
			if (target.areaNorm > largestAreaNorm)
				largestAreaNorm = target.areaNorm;
		}

		double bestScore = Double.NEGATIVE_INFINITY;
		for (VisionData.Target target : visionData.targetsFound) {

			// Give each target a score, and select the one with the highest score.

			double areaScore = target.areaNorm / largestAreaNorm;
			double distFromCentrePenalty = Math.abs(target.xCentreNorm);

			double score = (1 - visionParams.getByName("distToCentreImportance").getValueD()) * areaScore
					- visionParams.getByName("distToCentreImportance").getValueD() * distFromCentrePenalty;

			if (bestScore < score) {
				visionData.preferredTarget = target;
				bestScore = score;
			}
		}
		/*
		 * 
		 * Time to math the distance y = height of cube x = distance from cube | | | | |
		 * -------------------
		 * 
		 * using y = mx+b we can determine that the formula to calculate x from y is x =
		 * (y-b)/m
		 * 
		 */

		if (visionData.preferredTarget != null) {

			double y = visionData.preferredTarget.boundingBox.height;

			double x = (y - visionParams.getByName("yIntercept").getValueD()) / visionParams.getByName("slope").getValueD();

			// Now we have the distance in cm!!!

			visionData.preferredTarget.distance = x;

		}

	}

	// Create Colour Values
	private static final Scalar BACKGROUND_TARGET_COLOUR = new Scalar(237, 19, 75); // Purple (Non-Preffered Target)
	private static final Scalar PREFERRED_TARGET_COLOUR = new Scalar(30, 180, 30); // Green (Preffered Target)

	public static void drawPreferredTarget(Mat src, VisionData visionData) {

		// DRAW STUFF ONTO THE OUTPUT IMAGE
		// for each target found, draw the bounding box and centre

		for (VisionData.Target target : visionData.targetsFound) {
			Point centerTarget = new Point(target.xCentre, target.yCentre);
			Imgproc.circle(src, centerTarget, 6, BACKGROUND_TARGET_COLOUR, -1);
			Imgproc.rectangle(src, new Point(target.boundingBox.x, target.boundingBox.y),
					new Point(target.boundingBox.x + target.boundingBox.width,
							target.boundingBox.y + target.boundingBox.height),
					BACKGROUND_TARGET_COLOUR, 3);
		}

		// Draw the preferred target over it
		if (visionData.preferredTarget != null) {

			Point centerTarget = new Point(visionData.preferredTarget.xCentre, visionData.preferredTarget.yCentre);
			Imgproc.circle(src, centerTarget, 10, PREFERRED_TARGET_COLOUR, -1);
			Imgproc.rectangle(src,
					new Point(visionData.preferredTarget.boundingBox.x, visionData.preferredTarget.boundingBox.y),
					new Point(visionData.preferredTarget.boundingBox.x + visionData.preferredTarget.boundingBox.width,
							visionData.preferredTarget.boundingBox.y + visionData.preferredTarget.boundingBox.height),
					PREFERRED_TARGET_COLOUR, 7);
		}
	}

}
