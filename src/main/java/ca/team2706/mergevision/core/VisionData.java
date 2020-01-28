package ca.team2706.mergevision.core;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;

import ca.team2706.mergevision.core.params.VisionParams;

/**
 * A class to hold any visionTable data returned by process() :) :) :} :] :]
 */

public class VisionData {

	public static class Target {
		public double distance;

		public MatOfPoint contour;

		/** The x center of the target in the image **/
		public int xCentre;
		/**
		 * The normalized x center of the target that is between 0 and 1
		 **/
		public double xCentreNorm;
		/** The y center of the target in the image **/
		public int yCentre;
		/**
		 * The normalized y center of the target that is between 0 and 1
		 **/
		public double yCentreNorm;
		/**
		 * A value between 0 and 1 representing the percentage of the image the target
		 * takes up
		 **/
		public double areaNorm; // [0,1] representing how much of the screen it
							// occupies
		/** The rectangle made from x and y centers **/
		public Rect boundingBox;
	}

	/** The List of all the targets in the image **/

	public ArrayList<Target> targetsFound = new ArrayList<Target>();
	/**
	 * The target that is the most appealing, how it is chosen depends on the
	 * distToCenterImportance value in the vision parameters
	 **/
	public Target preferredTarget;
	/** The image that contains the targets **/
	public Mat outputImg = new Mat();
	/** The frames per second **/
	public Mat binMask = new Mat();
	public double fps;
	public VisionParams params;
}