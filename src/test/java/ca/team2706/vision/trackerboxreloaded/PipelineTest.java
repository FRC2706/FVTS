package ca.team2706.vision.trackerboxreloaded;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import ca.team2706.vision.trackerboxreloaded.Main.VisionData;
import ca.team2706.vision.trackerboxreloaded.Main.VisionParams;

public class PipelineTest {

	static {
		// Loads OpenCV
		System.loadLibrary("opencv_java310");
	}
	
	@SuppressWarnings("unused")
	@Test
	public void process() {
		try {
			VisionParams params = new VisionParams();
			params.aspectRatioThresh = 0;
			params.distToCentreImportance = 0;
			params.height = 480;
			params.width = 640;
			params.maxHue = 0;
			params.minHue = 0;
			params.maxSaturation = 0;
			params.minSaturation = 0;
			params.maxValue = 0;
			params.minValue = 0;
			VisionData data1 = Pipeline.process(new Mat(240, 320, CvType.CV_8U), params, false);
			VisionData data2 = Pipeline.process(new Mat(240, 320, CvType.CV_8U), params, true);

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void selectPrefferedTarget() {
		try {
			VisionParams params = new VisionParams();
			params.aspectRatioThresh = 0;
			params.distToCentreImportance = 0;
			params.height = 480;
			params.width = 640;
			params.maxHue = 0;
			params.minHue = 0;
			params.maxSaturation = 0;
			params.minSaturation = 0;
			params.maxValue = 0;
			params.minValue = 0;
			VisionData data1 = Pipeline.process(new Mat(240, 320, CvType.CV_8U), params, false);
			Pipeline.selectPreferredTarget(data1, params);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void drawPrefferedTarget() {
		try {
			VisionParams params = new VisionParams();
			params.aspectRatioThresh = 0;
			params.distToCentreImportance = 0;
			params.height = 480;
			params.width = 640;
			params.maxHue = 0;
			params.minHue = 0;
			params.maxSaturation = 0;
			params.minSaturation = 0;
			params.maxValue = 0;
			params.minValue = 0;
			VisionData data1 = Pipeline.process(new Mat(240, 320, CvType.CV_8U), params, false);
			Pipeline.selectPreferredTarget(data1, params);
			Pipeline.drawPreferredTarget(new Mat(240, 320, CvType.CV_8U), data1);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
