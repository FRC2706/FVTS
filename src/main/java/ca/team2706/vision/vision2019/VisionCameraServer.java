package ca.team2706.vision.vision2019;

import java.util.HashMap;
import java.util.Map;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class VisionCameraServer extends Thread {

	public static Map<Integer, VideoCapture> cameras = new HashMap<Integer, VideoCapture>();

	public static void startServer() {
		new VisionCameraServer().start();
	}

	public static void initCamera(int id) throws Exception {

		if (cameras.keySet().contains(id) || id == -1) {
			return;
		}

		VideoCapture capture = new VideoCapture(id);
		
		if(frame1 == null) {
			frame1 = new Mat();
		}
		
		while(!capture.read(frame1)) {
			Thread.sleep(40);
		}
		
		cameras.put(id, capture);
		
		frames.put(id, frame1);

	}

	private static Mat frame1;
	private static Map<Integer, Mat> frames = new HashMap<Integer, Mat>();

	public static Mat getFrame(int camera) {

		return frames.get(camera);

	}

	private Mat frame;

	@Override
	public void run() {

		while (true) {
			
			for (int i : cameras.keySet()) {
				
				VideoCapture capture = cameras.get(i);

				if(frame == null) {
					frame = new Mat();
				}
				
				if(capture.read(frame)) {
					
					frames.put(i, frame);
					
				}else {
					System.err.println("Failed to read from camera "+i);
				}
			}

			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	
	public static void update() {
		
		Mat frame = null;
		
		for (int i : cameras.keySet()) {
			
			VideoCapture capture = cameras.get(i);

			if(frame == null) {
				frame = new Mat();
			}
			
			if(capture.read(frame)) {
				
				frames.put(i, frame);
				
			}else {
				System.err.println("Failed to read from camera "+i);
			}
			

		}

		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
