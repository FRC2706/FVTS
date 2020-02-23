package ca.team2706.fvts.core.image;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.params.Attribute;
import ca.team2706.fvts.core.params.AttributeOptions;
import ca.team2706.fvts.core.params.VisionParams;

public class ImageResizingProcessor extends AbstractImagePreprocessor{

	public ImageResizingProcessor() {
		super("resize");
	}

	@Override
	public void init(MainThread thread) {
		VisionParams params = thread.visionParams;
		String resolution = params.getByName("resolution").getValue();
		int width = Integer.valueOf(resolution.split("x")[0]);
		int height = Integer.valueOf(resolution.split("x")[1]);
		params.getAttribs().add(new Attribute("width", width + ""));
		params.getAttribs().add(new Attribute("height", height + ""));
	}

	@Override
	public List<AttributeOptions> getOptions() {
		List<AttributeOptions> ret = new ArrayList<AttributeOptions>();
		
		AttributeOptions resolution = new AttributeOptions("resolution", true);
		
		ret.add(resolution);
		
		return ret;
	}

	@Override
	public Mat process(Mat src, MainThread thread) {
		int width = thread.visionParams.getByName("width").getValueI();
		int height = thread.visionParams.getByName("height").getValueI();
		Mat dst = new Mat(height,width,src.type());
		// Resize the frame
		Size sz = new Size(width,height);
		Imgproc.resize(src,dst, sz);
		return dst;
	}

}
