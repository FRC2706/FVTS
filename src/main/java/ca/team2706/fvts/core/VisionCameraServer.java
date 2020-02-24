package ca.team2706.fvts.core;

import java.util.HashMap;
import java.util.Map;

import org.opencv.core.Mat;

import ca.team2706.fvts.core.input.AbstractInputDevice;

public class VisionCameraServer extends Thread {
	
	public static void startServer() {
		new VisionCameraServer().start();
	}

	public static void initCamera(String type, String identifier) throws Exception {
		if(!inputs.containsKey(type+":"+identifier)) {
			AbstractInputDevice input = AbstractInputDevice.getByName(type);
			if(input == null) {
				Log.e("Invalid input device type "+type, true);
				System.exit(1);
			}
			input.init(identifier);
			inputs.put(type+":"+identifier, input);
		}
	}

	private static Map<String, Mat> frames = new HashMap<String, Mat>();
	private static Map<String,AbstractInputDevice> inputs = new HashMap<String,AbstractInputDevice>();
	
	public static Mat getFrame(String type, String identifier) {
		return frames.get(type+":"+identifier);
	}

	@Override
	public void run() {

		while (true) {
			
			update();
			
		}

	}

	public static void update() {

		for (String id : inputs.keySet()) {

			AbstractInputDevice device = inputs.get(id);
			Mat frame = device.getFrame(id.split(":",2)[1]);
			frames.put(id, frame);
			
		}

		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			Log.e(e.getMessage(), true);
		}
	}

}
