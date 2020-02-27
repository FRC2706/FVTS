package ca.team2706.fvts.core.image;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.params.AttributeOptions;

public class ImageResizingProcessor extends AbstractImagePreprocessor{

	public ImageResizingProcessor() {
		super("resize");
	}

	@Override
	public void init(MainThread thread) {
		
	}

	@Override
	public List<AttributeOptions> getOptions() {
		List<AttributeOptions> ret = new ArrayList<AttributeOptions>();
		
		AttributeOptions width = new AttributeOptions(getName()+"/"+"width", true);
		AttributeOptions height = new AttributeOptions(getName()+"/"+"height", true);
		
		ret.add(width);
		ret.add(height);
		
		return ret;
	}

	@Override
	public Mat process(Mat src, MainThread thread) {
		int width = thread.visionParams.getByName(getName()+"/"+"width").getValueI();
		int height = thread.visionParams.getByName(getName()+"/"+"height").getValueI();
		Mat dst = new Mat(height,width,src.type());
		// Resize the frame
		Size sz = new Size(width,height);
		Imgproc.resize(src,dst, sz);
		return dst;
	}

}
