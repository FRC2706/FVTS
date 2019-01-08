package ca.team2706.vision.trackerboxreloaded;

import java.util.HashMap;
import java.util.Map;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class CameraServer {
	
	private static Map<Integer,VideoCapture> cameras = new HashMap<Integer,VideoCapture>();
	
	public static void initCamera(int id) throws Exception{
		
		VideoCapture capture = new VideoCapture(id);
		
		cameras.put(id,capture);
		
	}
	
	private static Map<Integer,Mat> frames = new HashMap<Integer,Mat>();
	
	public static Mat getFrame(int camera) {
		
		
		
	}
	
}
