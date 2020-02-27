package ca.team2706.fvts.core.input;

import java.util.HashMap;
import java.util.Map;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import ca.team2706.fvts.core.Log;

public class VideoDummyInputDevice extends AbstractInputDevice{
	public VideoDummyInputDevice() {
		super("video");
	}

	public static Map<String, VideoCapture> videos = new HashMap<String, VideoCapture>();
	
	private Mat frame = new Mat();
	
	@Override
	public void init(String identifier) throws Exception{

		if(videos.containsKey(identifier)) {
			return;
		}
		
		VideoCapture capture = new VideoCapture(identifier);
		
		Log.i("Waiting for file to be read!",true);
		
		while(!capture.isOpened()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		Log.i("Video successfully opened",true);

		if(!capture.read(frame)) {
			
			Log.e("Failed to open video file "+identifier,true);
			
			System.exit(1);
		}
		videos.put(identifier, capture);
	}

	@Override
	public Mat getFrame(String identifier) {
		videos.get(identifier).read(frame);
		return frame;
	}
}
