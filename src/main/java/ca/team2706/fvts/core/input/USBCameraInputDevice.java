package ca.team2706.fvts.core.input;

import java.util.HashMap;
import java.util.Map;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import ca.team2706.fvts.core.Log;

public class USBCameraInputDevice extends AbstractInputDevice{
	public USBCameraInputDevice() {
		super("usb");
	}

	public static Map<Integer, VideoCapture> cameras = new HashMap<Integer, VideoCapture>();
	
	private Mat frame = new Mat();
	
	@Override
	public void init(String identifier) throws Exception{
		int id = Integer.valueOf(identifier);

		if(cameras.containsKey(id) || id == -1) {
			return;
		}
		
		VideoCapture capture = new VideoCapture(id);
		
		Log.i("Waiting for camera to respond!",true);
		
		while(!capture.isOpened()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		Log.i("Camera successfully connected",true);

		if(!capture.read(frame)) {
			
			Log.e("Failed to connect to camera #"+identifier,true);
			
			System.exit(1);
			
		}
	}

	@Override
	public Mat getFrame(String identifier) {
		cameras.get(Integer.valueOf(identifier)).read(frame);
		return frame;
	}
}
