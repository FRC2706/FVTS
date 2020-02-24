package ca.team2706.fvts.core.image;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import ca.team2706.fvts.core.MainThread;
import ca.team2706.fvts.core.params.AttributeOptions;

public class ImageCropPreprocessor extends AbstractImagePreprocessor {

	public ImageCropPreprocessor() {
		super("crop");
	}

	@Override
	public void init(MainThread thread) {
		
	}

	@Override
	public List<AttributeOptions> getOptions() {
		List<AttributeOptions> ret = new ArrayList<AttributeOptions>();
		
		ret.add(new AttributeOptions(getName()+"/"+"cX1", true));
		ret.add(new AttributeOptions(getName()+"/"+"cY1", true));
		ret.add(new AttributeOptions(getName()+"/"+"cX2", true));
		ret.add(new AttributeOptions(getName()+"/"+"cY2", true));
		
		return ret;
	}

	@Override
	public Mat process(Mat src, MainThread thread) {
		int cX1 = (int) (thread.visionParams.getByName(getName()+"/"+"cX1").getValueD() * src.cols());
		int cY1 = (int) (thread.visionParams.getByName(getName()+"/"+"cY1").getValueD() * src.rows());
		int cX2 = (int) (thread.visionParams.getByName(getName()+"/"+"cX2").getValueD() * src.cols());
		int cY2 = (int) (thread.visionParams.getByName(getName()+"/"+"cY2").getValueD() * src.rows());
		
		int width = (int) Math.abs(cX1-cX2);
		int height = (int) Math.abs(cY1-cY2);
		
		Rect cropArea = new Rect((cX1 < cX2 ? cX1 : cX2), (cY1 < cY2 ? cY1 : cY2),width,height);
		
		Mat cropped = new Mat(src,cropArea);
		return cropped;
	}

}
