package ca.team2706.vision.trackerboxreloaded;

import ca.team2706.vision.trackerboxreloaded.Main.VisionData;
import ca.team2706.vision.trackerboxreloaded.Main.VisionParams;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class Pipeline {
	
	/** Numerical Constants **/
	public static final int NANOSECONDS_PER_SECOND = 1000000000;
	
	public static long fpsTimer = System.nanoTime();

	 /**
     * The visionTable pipeline!
     *
     * @param src Raw source image to process
     * @param visionParams Parameters for visionTable
     * @return All the data!
     */
	@SuppressWarnings("unused")
	public static VisionData process(Mat src, VisionParams visionParams) {

		int imgArea = src.height() * src.width();

		// If there's any data or intermediate images that you want to return, add them to the VisionData class
		// For example, any numbers that we want to return to the roboRIO.
		VisionData visionData = new VisionData();


		// Colour threshold
		Mat hsvThreshold = new Mat();
		Core.inRange(src, new Scalar(visionParams.minHue, visionParams.minSaturation, visionParams.minValue),
				new Scalar(visionParams.maxHue, visionParams.maxSaturation, visionParams.maxValue), hsvThreshold);

		// Erode - Dilate*2 - Erode
		Mat dilated = new Mat();
		Mat erodeOne = new Mat();
		Mat erodeTwo = new Mat();
		Imgproc.erode(hsvThreshold, erodeOne, new Mat(), new Point(), visionParams.erodeDilateIterations, Core.BORDER_CONSTANT, new Scalar(0));
		Imgproc.dilate(erodeOne, dilated, new Mat(), new Point(), 2*visionParams.erodeDilateIterations, Core.BORDER_CONSTANT, new Scalar(0));
	    Imgproc.erode(dilated, erodeTwo, new Mat(), new Point(), visionParams.erodeDilateIterations, Core.BORDER_CONSTANT, new Scalar(0));

		visionData.outputImg = erodeTwo.clone();

		//Find contours
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(dilated, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);



		//Make Bounding Box
		for (MatOfPoint contour : contours)
		{
			Rect boundingRect = Imgproc.boundingRect(contour);

			// height * width for area (easier and less CPU cycles than contour.area)
			double areaNorm = ((double) boundingRect.width * boundingRect.height) / imgArea;

            if (areaNorm >= visionParams.minArea) {
				/**
				 * This code basically checks if the bounding box given corresponds to double boxes or a single box.
				 * if the x length of the rectangle is 2 times the Y length then it is safe to say there are 2 cubes
				 * this code also gives a 25% range for error (still detect if X length is 2.25 / 1.75 times the Y length)
				 */
				int target1CtrX, target1CtrY, target2CtrX, target2CtrY;
				double target1AreaNorm, target2AreaNorm;
                if ((boundingRect.width <= (2 + visionParams.aspectRatioThresh) * boundingRect.height) && (boundingRect.width >= (2 - visionParams.aspectRatioThresh) * boundingRect.height)) {

					// Detect 2 targets rather than 1 big bounding box

					// target1 is the left half of this contour
					VisionData.Target target1 = new VisionData.Target();
                	target1.boundingBox = new Rect(boundingRect.x, boundingRect.y,
							boundingRect.width/2, boundingRect.height);
                    target1.xCentre = target1.boundingBox.x + (target1.boundingBox.width / 2);
					target1.xCentreNorm = ((double) target1.xCentre - (src.width()/2)) / (src.width()/2);
                    target1.yCentre = target1.boundingBox.y + (target1.boundingBox.height / 2);
                    target1.yCentreNorm = ((double) target1.yCentre - (src.height()/2)) / (src.height()/2);
                    target1.areaNorm = (target1.boundingBox.height * target1.boundingBox.width) / ((double) imgArea);
                    visionData.targetsFound.add(target1);


                    // target2 is the right half of this contour
                    VisionData.Target target2 = new VisionData.Target();
					target2.boundingBox = new Rect(boundingRect.x + (boundingRect.width/2), boundingRect.y,
							boundingRect.width/2, boundingRect.height);
					target2.xCentre = target2.boundingBox.x + (target2.boundingBox.width / 2);
					target2.xCentreNorm = ((double) target2.xCentre - (src.width()/2)) / (src.width()/2);
                    target2.yCentre = target2.boundingBox.y + (target2.boundingBox.height / 2);
					target2.yCentreNorm = ((double) target2.yCentre - (src.height()/2)) / (src.height()/2);
					target2.areaNorm = (target2.boundingBox.height * target2.boundingBox.width) / ((double) imgArea);
					visionData.targetsFound.add(target2);

                } else {
					VisionData.Target target = new VisionData.Target();
					target.boundingBox = boundingRect;
					target.xCentre = target.boundingBox.x + (target.boundingBox.width / 2);
					target.xCentreNorm = ((double) target.xCentre - (src.width()/2)) / (src.width()/2);
					target.yCentre = target.boundingBox.y + (target.boundingBox.height / 2);
					target.yCentreNorm = ((double) target.yCentre - (src.height()/2)) / (src.height()/2);
					target.areaNorm = (target.boundingBox.height * target.boundingBox.width) / ((double) imgArea);
					visionData.targetsFound.add(target);
                }
            }
            // else
			// skip this contour because it's too small
		}

		long now = System.nanoTime();
		visionData.fps = ((double) NANOSECONDS_PER_SECOND) / (now - fpsTimer);
		fpsTimer = now;

		return visionData;
	}


	/**
	 * From all the targets found in visionData.targetsFound, select the one that we're going to send to the roboRIO.
	 *
	 * @param visionData
	 */
	public static void selectPreferredTarget(VisionData visionData, VisionParams visionParams) {

		if (visionData.targetsFound.size() == 0) {
			return;
		}

		// loop over the targets to find the largest area of any target found.
		// 	this is so we can give the largest a score of 1.0, and each other target a score that is a
		// 	percentage of the area of the largest.
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
							-  visionParams.distToCentreImportance * distFromCentrePenalty;

			if(bestScore < score) {
				visionData.preferredTarget = target;
				bestScore = score;
			}
		}
	}

	//Create Colour Values
	private static final Scalar BACKGROUND_TARGET_COLOUR = new Scalar(237, 19, 75); //Purple (Non-Preffered Target)
	private static final Scalar PREFERRED_TARGET_COLOUR = new Scalar(30, 180, 30);  //Green (Preffered Target)

	public static void drawPreferredTarget(Mat src, VisionData visionData) {

		// DRAW STUFF ONTO THE OUTPUT IMAGE
		// for each target found, draw the bounding box and centre

		for (VisionData.Target target : visionData.targetsFound)
		{
			Point centerTarget = new Point(target.xCentre, target.yCentre);
			Imgproc.circle(src, centerTarget, 6, BACKGROUND_TARGET_COLOUR, -1);
			Imgproc.rectangle(src, new Point(target.boundingBox.x, target.boundingBox.y),
					new Point(target.boundingBox.x + target.boundingBox.width,
							target.boundingBox.y + target.boundingBox.height), BACKGROUND_TARGET_COLOUR, 3);
		}

		// Draw the preferred target over it
        if (visionData.preferredTarget != null) {

            Point centerTarget = new Point(visionData.preferredTarget.xCentre, visionData.preferredTarget.yCentre);
            Imgproc.circle(src, centerTarget, 10, PREFERRED_TARGET_COLOUR, -1);
            Imgproc.rectangle(src, new Point(visionData.preferredTarget.boundingBox.x, visionData.preferredTarget.boundingBox.y),
                    new Point(visionData.preferredTarget.boundingBox.x + visionData.preferredTarget.boundingBox.width,
                            visionData.preferredTarget.boundingBox.y + visionData.preferredTarget.boundingBox.height),
                    PREFERRED_TARGET_COLOUR, 7);
        }
	}

}
