package ca.team2706.vision.vision2019;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import ca.team2706.vision.vision2019.Main.VisionData;
import ca.team2706.vision.vision2019.Main.VisionData.Target;
import ca.team2706.vision.vision2019.Main.VisionParams;

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
		// If using the guis

		if (use_GUI) {
			// Make new Mats
			dilated = new Mat();
			erodeOne = new Mat();
			erodeTwo = new Mat();
		} else {
			// Else re use them
			dilated = new Mat();
			erodeOne = dilated;
			erodeTwo = dilated;
		}
		// Calculate the image area
		int imgArea = src.height() * src.width();

		// If there's any data or intermediate images that you want to return, add them
		// to the VisionData class
		// For example, any numbers that we want to return to the roboRIO.
		VisionData visionData = new VisionData();

		visionData.params = visionParams;

		// Colour threshold
		Mat hsvThreshold = new Mat();
		Core.inRange(src, new Scalar(visionParams.minHue, visionParams.minSaturation, visionParams.minValue),
				new Scalar(visionParams.maxHue, visionParams.maxSaturation, visionParams.maxValue), hsvThreshold);

		// Erode - Dilate*2 - Erode
		Imgproc.erode(hsvThreshold, erodeOne, new Mat(), new Point(), visionParams.erodeDilateIterations,
				Core.BORDER_CONSTANT, new Scalar(0));
		Imgproc.dilate(erodeOne, dilated, new Mat(), new Point(), 2 * visionParams.erodeDilateIterations,
				Core.BORDER_CONSTANT, new Scalar(0));
		Imgproc.erode(dilated, erodeTwo, new Mat(), new Point(), visionParams.erodeDilateIterations,
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

			if (areaNorm >= visionParams.minArea) {

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

	private static List<OrderedPoint> sortTargetPoints(Target in) {

		Rect boundingRect = in.boundingBox;

		double a = Double.POSITIVE_INFINITY, b = Double.POSITIVE_INFINITY, c = Double.POSITIVE_INFINITY,
				d = Double.POSITIVE_INFINITY;

		double x1, x2, x3, x4, y1, y2, y3, y4;

		x1 = boundingRect.x;
		y1 = boundingRect.y;
		x2 = boundingRect.width + boundingRect.x;
		y2 = boundingRect.y;
		x3 = boundingRect.width + boundingRect.x;
		y3 = boundingRect.height + boundingRect.y;
		x4 = boundingRect.x;
		y4 = boundingRect.height + boundingRect.y;

		for (Point point : in.contour.toArray()) {

			double a1 = Math.sqrt(Math.pow(point.x - x1, 2) + Math.pow(point.y - y1, 2));
			double b1 = Math.sqrt(Math.pow(point.x - x2, 2) + Math.pow(point.y - y2, 2));
			double c1 = Math.sqrt(Math.pow(point.x - x3, 2) + Math.pow(point.y - y3, 2));
			double d1 = Math.sqrt(Math.pow(point.x - x4, 2) + Math.pow(point.y - y4, 2));

			if (a1 < a) {
				a = a1;
				continue;
			}
			if (b1 < b) {
				b = b1;
				continue;
			}
			if (c1 < c) {
				c = c1;
				continue;
			}
			if (d1 < d) {
				d = d1;
				continue;
			}

		}
		List<OrderedPoint> orderedPoints = new ArrayList<OrderedPoint>();
		for (Point point : in.contour.toArray()) {

			double a1 = Math.sqrt(Math.pow(point.x - x1, 2) + Math.pow(point.y - y1, 2));
			double b1 = Math.sqrt(Math.pow(point.x - x2, 2) + Math.pow(point.y - y2, 2));
			double c1 = Math.sqrt(Math.pow(point.x - x3, 2) + Math.pow(point.y - y3, 2));
			double d1 = Math.sqrt(Math.pow(point.x - x4, 2) + Math.pow(point.y - y4, 2));

			if (a1 == a) {
				orderedPoints.add(new OrderedPoint(point, 1));
			}
			if (b1 == b) {
				orderedPoints.add(new OrderedPoint(point, 2));
			}
			if (c1 == c) {
				orderedPoints.add(new OrderedPoint(point, 3));
			}
			if (d1 == d) {
				orderedPoints.add(new OrderedPoint(point, 4));
			}
		}

		Collections.sort(orderedPoints);

		return orderedPoints;

	}
	
	private static boolean isMissingPair(List<OrderedPoint> orderedPoints1, double slope, double minDist, Target target, Target target2) {
		
		double slope1 = 0;
		double b21 = 0;
		
		boolean missingPair = false;

		try {
			Point A = orderedPoints1.get(0).getP();
			Point D = orderedPoints1.get(orderedPoints1.size() - 1).getP();

			double rise = (A.y - D.y);
			double run = (A.x - D.x);

			if (rise == 0) {
				rise = 1;
			}
			if (run == 0) {
				run = 1;
			}

			slope1 = rise / run;
			b21 = slope * A.x;
			b21 = A.y - b21;

			double dist = Math.sqrt(Math.pow(Math.abs(target.xCentre - target2.xCentre), 2)
					+ Math.pow(Math.abs(target.yCentre - target2.yCentre), 2));

			if (!(slope1 < 0) && !(slope > 0) && dist < minDist) {
				missingPair = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return missingPair;
		
	}
	
	private static class TargetContainer{
		
		private double dist;
		private Target target;
		
		public TargetContainer(double dist, Target target) {
			this.target = target;
			this.dist = dist;
		}

		public double getDist() {
			return dist;
		}

		public Target getTarget() {
			return target;
		}
		
	}
	
	private static TargetContainer calcMinDist(Target target, Target target2, List<OrderedPoint> orderedPoints1, double slope, double minDist) {
		
		double slope1 = 0;
		double b21 = 0;
		
		double minDist1 = Double.MAX_VALUE;
		Target minTarget1 = null;

		try {

			Point A = orderedPoints1.get(0).getP();
			Point D = orderedPoints1.get(orderedPoints1.size() - 1).getP();

			slope1 = (A.y - D.y) / (A.x - D.x);
			b21 = slope * A.x;
			b21 = A.y - b21;

			double dist = Math.sqrt(Math.pow(Math.abs(target.xCentre - target2.xCentre), 2)
					+ Math.pow(Math.abs(target.yCentre - target2.yCentre), 2));

			boolean toHigh = target.yCentre+target.yCentre*0.2 < target2.yCentre ? true : false || target.yCentre-target.yCentre*0.2 > target2.yCentre ? true : false;
			
			if (slope1 < 0 && slope > 0 && dist < minDist && !toHigh) {

				minTarget1 = target2;

				minDist1 = dist;

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new TargetContainer(minDist1, minTarget1);
		
	}
	
	private static class SlopeYIntPair{
		
		private double m,b;

		public SlopeYIntPair(double m, double b) {
			super();
			this.m = m;
			this.b = b;
		}

		public double getM() {
			return m;
		}

		@SuppressWarnings("unused")
		public double getB() {
			return b;
		}
		
	}
	
	private static SlopeYIntPair calcSlope(List<OrderedPoint> orderedPoints) {
		
		double slope = 0;
		double b2 = 0;

		try {

			Point A = orderedPoints.get(0).getP();
			Point D = orderedPoints.get(orderedPoints.size() - 1).getP();

			double rise = (A.y - D.y);
			double run = (A.x - D.x);

			if (rise == 0) {
				rise = 1;
			}
			if (run == 0) {
				run = 1;
			}

			slope = rise / run;
			b2 = slope * A.x;
			b2 = A.y - b2;

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new SlopeYIntPair(slope, b2);
		
	}

	/**
	 * From all the targets found in visionData.targetsFound, select the one that
	 * we're going to send to the roboRIO.
	 *
	 * @param visionData
	 */
	public static void selectPreferredTarget(VisionData visionData, VisionParams visionParams, boolean bool) {

		if (visionData.targetsFound.size() == 0) {
			return;
		}

		if (bool) {
			
			List<Target> newTargets = new ArrayList<Target>();
			
			for (Target target : visionData.targetsFound) {

				
				
				List<OrderedPoint> orderedPoints = sortTargetPoints(target);

				SlopeYIntPair slopePair = calcSlope(orderedPoints);
				
				double slope = slopePair.getM();

				if (slope < 0) {
					continue;
				}

				Target minTarget = null;
				double minDist = Double.MAX_VALUE;

				for (Target target2 : visionData.targetsFound) {

					if (target2 != target) {

						List<OrderedPoint> orderedPoints1 = sortTargetPoints(target2);

						TargetContainer targetContainer = calcMinDist(target, target2, orderedPoints1, slope, minDist);
						
						if(targetContainer.getDist() < minDist) {
							
							minDist = targetContainer.getDist();
							minTarget = targetContainer.getTarget();
							
						}
						
					}

				}

				boolean missingPair = false;

				for (Target target2 : visionData.targetsFound) {

					if (target2 != target && !missingPair) {

						List<OrderedPoint> orderedPoints1 = sortTargetPoints(target2);

						boolean missingPair1 = isMissingPair(orderedPoints1, slope, minDist, target, target2);

						if(missingPair1 == true) {
							missingPair = true;
						}
						
					}

				}

				if (minTarget == null || missingPair) {

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

			double score = (1 - visionParams.distToCentreImportance) * areaScore
					- visionParams.distToCentreImportance * distFromCentrePenalty;

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

			double x = (y - visionParams.yIntercept) / visionParams.slope;

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
