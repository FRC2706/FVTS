package ca.team2706.vision.trackerboxreloaded;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class NetworkTablesServer {

	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		//Inits stuff
		//Tells Network Tables that this is a server
		NetworkTable.setServerMode();
		//Sets the update interval
		NetworkTable.setUpdateRate(0.2);
		//Initilizes the network tables
		NetworkTable.initialize();
		//Gets the vision table
		NetworkTable visionTable = NetworkTable.getTable("vision");

		System.out.println("NetworkTables server started.\nOutputting table /vision");

		while (true) {
			//
			if(visionTable.containsKey("fps"))
				//Print fps if exists
				System.out.println("fps: "+visionTable.getDouble("fps")+", ");
			
			if(visionTable.containsKey("ctrX"))
				//Print center x if exists
				System.out.println("ctrX: "+visionTable.getDouble("ctrX")+", ");
			
			if(visionTable.containsKey("numTargetsFound"))
				//Prints the number of targets found if exists
				System.out.println("numTargetsFound: "+visionTable.getDouble("numTargetsFound"));
			
			//Prints an end line
			System.out.println();
			try {
				//Sleep for 1ms
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
