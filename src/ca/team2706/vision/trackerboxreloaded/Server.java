package ca.team2706.vision.trackerboxreloaded;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Server {
	public static NetworkTable table;
	public static void main(String[] args){
		NetworkTable.setServerMode();
		
		// Sets the team number to 2706
		NetworkTable.setTeam(2706);

		//Initilizes the NetworkTable! Very important!
		NetworkTable.initialize();
		
		//Gets the vision NetworkTable
		table = NetworkTable.getTable("vision");
		while(true){
			if(table.containsKey("data")){
				
			}
		}
	}
}
