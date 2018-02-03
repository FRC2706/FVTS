package ca.team2706.vision.trackerboxreloaded;

import java.util.HashMap;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class ExampleServer {
	static NetworkTable table;
	public static void main(String[] args){
		NetworkTableInstance instance = NetworkTableInstance.getDefault();
		table = instance.getTable("vision");
		while(true){
			HashMap<String,String> data = DataUtils.decodeData(table.getEntry("data").getString(""));
			for(String key : data.keySet()){
				System.out.println(key+" : "+data.get(key));
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
