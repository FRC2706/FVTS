package ca.team2706.vision.vision2019;

import java.util.HashMap;
import java.util.Map;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class CameraServer extends Thread {

	public static Map<Integer, VideoCapture> cameras = new HashMap<Integer, VideoCapture>();

	public static void startServer() {
		new CameraServer().start();
	}

	public static void initCamera(int id) throws Exception {

		if (cameras.keySet().contains(id)) {
			return;
		}

		VideoCapture capture = new VideoCapture(id);

		cameras.put(id, capture);
		capture.read(frame1);
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

				capture.read(frame);

				frames.put(i, frame);

			}

			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
