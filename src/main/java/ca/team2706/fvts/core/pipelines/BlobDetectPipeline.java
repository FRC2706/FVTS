package ca.team2706.fvts.core.pipelines;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.VisionData;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.core.params.VisionParams;

public class BlobDetectPipeline extends AbstractPipeline {

	public BlobDetectPipeline() {
		super("blobdetect");
	}

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
	public VisionData process(Mat src, VisionParams visionParams) {

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

		Core.inRange(src, new Scalar(visionParams.getByName("minHue").getValueI(),
				visionParams.getByName("minSaturation").getValueI(), visionParams.getByName("minValue").getValueI()),
				new Scalar(visionParams.getByName("maxHue").getValueI(),
						visionParams.getByName("maxSaturation").getValueI(),
						visionParams.getByName("maxValue").getValueI()),
				hsvThreshold);

		// Erode - Dilate*2 - Erode
		Imgproc.erode(hsvThreshold, erodeOne, new Mat(), new Point(),
				visionParams.getByName("erodeDilateIterations").getValueI(), Core.BORDER_CONSTANT, new Scalar(0));
		Imgproc.dilate(erodeOne, dilated, new Mat(), new Point(),
				2 * visionParams.getByName("erodeDilateIterations").getValueI(), Core.BORDER_CONSTANT, new Scalar(0));
		Imgproc.erode(dilated, erodeTwo, new Mat(), new Point(),
				visionParams.getByName("erodeDilateIterations").getValueI(), Core.BORDER_CONSTANT, new Scalar(0));

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
				target.areaNorm = areaNorm;
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

	// Create Colour Values
	private static final Scalar BACKGROUND_TARGET_COLOUR = new Scalar(237, 19, 75); // Purple (Non-Preffered Target)
	private static final Scalar PREFERRED_TARGET_COLOUR = new Scalar(30, 180, 30); // Green (Preffered Target)

	public void drawPreferredTarget(Mat src, VisionData visionData) {

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

	@Override
	public List<AttributeOptions> getOptions() {
		List<AttributeOptions> ret = new ArrayList<AttributeOptions>();
		AttributeOptions minHue = new AttributeOptions("minHue", true);
		AttributeOptions maxHue = new AttributeOptions("maxHue", true);
		AttributeOptions minSat = new AttributeOptions("minSaturation", true);
		AttributeOptions maxSat = new AttributeOptions("maxSaturation", true);
		AttributeOptions minVal = new AttributeOptions("minValue", true);
		AttributeOptions maxVal = new AttributeOptions("maxValue", true);

		AttributeOptions imageFile = new AttributeOptions("imageFile", true);

		AttributeOptions minArea = new AttributeOptions("minArea", true);

		AttributeOptions erodeDilateIterations = new AttributeOptions("erodeDilateIterations", true);

		ret.add(minHue);
		ret.add(maxHue);
		ret.add(minSat);
		ret.add(maxSat);
		ret.add(minVal);
		ret.add(maxVal);
		ret.add(imageFile);
		ret.add(minArea);
		ret.add(erodeDilateIterations);

		return ret;
	}

	@Override
	public void init(MainThread thread) {

	}

}
