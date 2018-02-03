package ca.team2706.vision.trackerboxreloaded;

import java.util.HashMap;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class ExampleServer {
	public static NetworkTable table;
	public static void main(String[] args){
		NetworkTable.setServerMode();
		
		// Sets the team number to 2706
		NetworkTable.setTeam(2706);
		
		//Initilizes the NetworkTable! Very important!
		NetworkTable.initialize();
		
		//Gets the vision NetworkTable
		table = NetworkTable.getTable("vision");
		table.putString("data", "start");
		while(true){
			HashMap<String,String> values = DataDecoder.decodeMessage(table.getString("data"));
			for(String key : values.keySet()){
				System.out.println(key+" : "+values.get(key));
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
