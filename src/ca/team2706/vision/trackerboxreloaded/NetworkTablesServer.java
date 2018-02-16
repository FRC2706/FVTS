package ca.team2706.vision.trackerboxreloaded;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class NetworkTablesServer {

	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		//Inits stuff
		NetworkTable.setServerMode();
		NetworkTable.setUpdateRate(0.2);
		NetworkTable.initialize();
		NetworkTable visionTable = NetworkTable.getTable("vision");

		System.out.println("NetworkTables server started.\nOutputting table /vision");

		while (true) {
			if(visionTable.containsKey("fps"))
				System.out.println("fps: "+visionTable.getDouble("fps")+", ");
			
			if(visionTable.containsKey("ctrX"))
				System.out.println("ctrX: "+visionTable.getDouble("ctrX")+", ");
			
			if(visionTable.containsKey("numTargetsFound"))
				System.out.println("numTargetsFound: "+visionTable.getDouble("numTargetsFound"));
			
			System.out.println();
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
