package ca.team2706.fvts.core.math;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.imgproc.Imgproc;

import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.VisionData;
import ca.team2706.fvts.core.VisionData.Target;
import ca.team2706.fvts.core.params.AttributeOptions;

public class GroupProcessor extends AbstractMathProcessor {

	public GroupProcessor() {
		super("group");
	}

	@Override
	public void process(VisionData visionData, MainThread main) {
		if(visionData.targetsFound.size() == 0)
			return;
		
		int groupAngle = visionData.params.getByName(getName()+"/"+"groupAngle").getValueI();
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

			double x = target.boundingBox.x < minTarget.boundingBox.x ? target.boundingBox.x : minTarget.boundingBox.x;
			double y = target.boundingBox.y < minTarget.boundingBox.y ? target.boundingBox.y : minTarget.boundingBox.y;

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

	@Override
	public void init(MainThread main) {
		
	}

	@Override
	public List<AttributeOptions> getOptions() {
		List<AttributeOptions> ret = new ArrayList<AttributeOptions>();

		AttributeOptions angle = new AttributeOptions(getName()+"/"+"groupAngle", true);

		ret.add(angle);

		return ret;
	}

}
