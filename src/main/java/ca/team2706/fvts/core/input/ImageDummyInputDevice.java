package ca.team2706.fvts.core.input;

import java.util.HashMap;
import java.util.Map;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class ImageDummyInputDevice extends AbstractInputDevice{

	public ImageDummyInputDevice() {
		super("image",true);
	}

	private Map<String,Mat> frames = new HashMap<String,Mat>();
	@Override
	public void init(String identifier) throws Exception{
		if(!frames.containsKey(identifier)) {
			VideoCapture capture = new VideoCapture(identifier);
			Mat frame = new Mat();
			capture.read(frame);
			frames.put(identifier, frame);
		}
	}

	@Override
	public Mat getFrame(String identifier) {
		return frames.get(identifier);
	}
	
}
