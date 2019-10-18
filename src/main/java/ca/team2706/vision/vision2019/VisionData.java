package ca.team2706.vision.vision2019;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;

import ca.team2706.vision.vision2019.params.VisionParams;

/**
 * A class to hold any visionTable data returned by process() :) :) :} :] :]
 */

public class VisionData {

	public static class Target {
		double distance;

		MatOfPoint contour;

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
		 * A value between 0 and 1 representing the percentage of the image the target
		 * takes up
		 **/
		double areaNorm; // [0,1] representing how much of the screen it
							// occupies
		/** The rectangle made from x and y centers **/
		Rect boundingBox;
	}

	/** The List of all the targets in the image **/

	ArrayList<Target> targetsFound = new ArrayList<Target>();
	/**
	 * The target that is the most appealing, how it is chosen depends on the
	 * distToCenterImportance value in the vision parameters
	 **/
	Target preferredTarget;
	/** The image that contains the targets **/
	public Mat outputImg = new Mat();
	/** The frames per second **/
	public Mat binMask = new Mat();
	public double fps;
	public VisionParams params;
}