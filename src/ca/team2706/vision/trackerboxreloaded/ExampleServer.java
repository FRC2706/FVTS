package ca.team2706.vision.trackerboxreloaded;

import java.util.HashMap;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableValue;

public class ExampleServer {
	/**
	 * the vision table
	 */
	public static NetworkTable vision;
	
	/**
	 * the fps table
	 */
	public static NetworkTable fps;
	
	/**
	 * the data table
	 */
	public static NetworkTable data;

	
	public static void main(String[] args) {
		//Inits stuff
		NetworkTableInstance instance = NetworkTableInstance.getDefault();
		instance.startServer();
		vision = instance.getTable("vision");
		data = vision.getSubTable("data");
		fps = vision.getSubTable("fps");
		while (true) {
			NetworkTableValue value = data.getEntry("data").getValue();
			if (value.isString()) {
				HashMap<String, String> data = DataUtils.decodeData(value.getString());
				for (String key : data.keySet()) {
					System.out.println(key + " : " + data.get(key));
				}
			}
			NetworkTableValue fpsValue = fps.getEntry("fps").getValue();
			if(fpsValue.isDouble()){
				System.out.println("fps: "+fpsValue.getDouble());
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
