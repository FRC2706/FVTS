package ca.team2706.fvts.core.pipelines;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import ca.team2706.fvts.core.Constants;
import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.VisionData;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.core.params.VisionParams;

public class FaceDetectPipeline extends AbstractPipeline {

	public FaceDetectPipeline() {
		super("facedetect");
	}

	/** Numerical Constants **/
	public static final int NANOSECONDS_PER_SECOND = 1000000000;

	/** The fps timer **/
	public static long fpsTimer = System.nanoTime();

	private CascadeClassifier faceDetector = new CascadeClassifier();

	@Override
	public VisionData process(Mat src, VisionParams visionParams) {
		VisionData ret = new VisionData();
		ret.params = visionParams;
		Mat binMask = new Mat();
		Imgproc.cvtColor(src, binMask, Imgproc.COLOR_BGR2GRAY);
		Imgproc.equalizeHist(binMask, binMask);
		ret.binMask = binMask;
		double faceSize = binMask.rows() * visionParams.getByName("minFaceSize").getValueD();
		MatOfRect faces = new MatOfRect();
		faceDetector.detectMultiScale(binMask, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
				new Size(faceSize, faceSize), new Size());
		Rect[] facesArray = faces.toArray();

		for (Rect rect : facesArray) {
			// height * width for area (easier and less CPU cycles than contour.area)
			int imageArea = binMask.rows() * binMask.cols();

			VisionData.Target target = new VisionData.Target();
			target.boundingBox = rect;
			target.xCentre = target.boundingBox.x + (target.boundingBox.width / 2);
			target.xCentreNorm = ((double) target.xCentre - (src.width() / 2)) / (src.width() / 2);
			target.yCentre = target.boundingBox.y + (target.boundingBox.height / 2);
			target.yCentreNorm = ((double) target.yCentre - (src.height() / 2)) / (src.height() / 2);
			target.areaNorm = (target.boundingBox.height * target.boundingBox.width) / ((double) imageArea);
			ret.targetsFound.add(target);
		}
		long now = System.nanoTime();
		ret.fps = ((double) NANOSECONDS_PER_SECOND) / (now - fpsTimer);
		ret.fps = ((int) (ret.fps * 10)) / 10.0; // round to 1 decimal place
		fpsTimer = now;
		return ret;
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
		ret.add(new AttributeOptions("minFaceSize", true));
		return ret;
	}

	private boolean setup = false;
	private Lock lock = new ReentrantLock();

	@Override
	public void init(MainThread thread) {
		lock.lock();
		if (!setup) {
			faceDetector.load(
					new File(Constants.RESOURCE_FOLDER, "haarcascades/haarcascade_frontalface_alt.xml").getPath());
			setup = true;
		}
		lock.unlock();
	}

}
